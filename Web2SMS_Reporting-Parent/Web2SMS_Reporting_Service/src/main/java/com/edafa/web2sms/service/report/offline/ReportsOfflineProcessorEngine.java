package com.edafa.web2sms.service.report.offline;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.ReportsDaoLocal;
import com.edafa.web2sms.dalayer.enums.ReportStatus;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Reports;
import com.edafa.web2sms.service.report.offline.interfaces.ReportsOfflineProcessorEngineLocal;
import com.edafa.web2sms.service.report.offline.interfaces.ReportsOfflineProcessorEngineTaskLocal;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Singleton
@Startup
public class ReportsOfflineProcessorEngine implements ReportsOfflineProcessorEngineLocal {

	private static final long DEFAULT_INTERVAL_DURATION = 60l * 1000l; //one minute
	private static final String LOG_ID = "ReportsOfflineProcessorEngine | ";
	private Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

	@Resource
	private TimerService timerService;

	@EJB
	private ReportsDaoLocal reportsDao;

	@EJB
	private ReportsOfflineProcessorEngineTaskLocal engineTask;

	public ReportsOfflineProcessorEngine() {}

	@PostConstruct
	public void onStartUp() {
		//set timer to start at next minute
		Calendar date = Calendar.getInstance();
		date.set(Calendar.MILLISECOND, 0);
		date.set(Calendar.SECOND, 0);
		date.add(Calendar.MINUTE, 1);

		this.timerService.createIntervalTimer(date.getTime(), DEFAULT_INTERVAL_DURATION, null);
	}

	@PreDestroy
	public void onDestroy() {
		try {
			if (!timerService.getTimers().isEmpty()) {
				for (Object obj : timerService.getTimers()) {
					Timer t = (Timer) obj;
					t.cancel();
				}
			}

		} catch (Exception e) {
			appLogger.error("Excepion: " + e.getMessage(), e);
		}
	}

	@Timeout
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void tick() {
		int concurrentMaxCount = (int) Configs.OFFLINE_REPORTS_CONCURRENT_MAX_COUNT.getValue();
		int activeTasksCount = engineTask.getActiveCount();

		appLogger.info(LOG_ID + "tick | start tick, concurrentMaxCount=" + concurrentMaxCount + ", activeTasksCount="
				+ activeTasksCount);

		if (activeTasksCount < concurrentMaxCount) {
			try {
				appLogger.info(LOG_ID + "tick | reportsDao.getPendingReports");
				List<Reports> pendingReports = reportsDao.getPendingReports();
				appLogger.info(LOG_ID + "tick | reportsDao.getPendingReports Success, pendingReports.size="
						+ pendingReports.size());
				processReports(concurrentMaxCount, pendingReports, false);

				appLogger.info(LOG_ID + "tick | reportsDao.getFailedReports");
				List<Reports> failedReports = reportsDao.getFailedReports();
				processReports(concurrentMaxCount, failedReports, true);
				appLogger.info(LOG_ID + "tick | reportsDao.getFailedReports Success, failedReports.size="
						+ failedReports.size());
			} catch (DBException e) {
				appLogger.error(LOG_ID + "tick | DB Error ", e);
			} catch (RuntimeException e) {
				appLogger.error(LOG_ID + "tick | Runtime Exception ", e);
			}
		} else {
			appLogger.info(LOG_ID + "reached concurrent limit no more reports to process, concurrentMaxCount="
					+ concurrentMaxCount);
		}
	}

	private void processReports(int concurrentMaxCount, List<Reports> reports, boolean failed) throws DBException {
		int maxRetryCount = (int) Configs.OFFLINE_REPORTS_MAX_RETRY_COUNT.getValue();
		Date processingDate = new Date();
		int activeTasksCount;

		for (Reports report : reports) {
			activeTasksCount = engineTask.getActiveCount();

			if (activeTasksCount < concurrentMaxCount) {
				report.setProcessingDate(processingDate);
				report.setStatus(ReportStatus.IN_PROGRESS);

				if (failed) {
					int retryCount = report.incrementAndGetRetryCount();

					if (retryCount > maxRetryCount) {
						report.setStatus(ReportStatus.OBSOLETE);
						reportsDao.edit(report);
						continue;
					}
				}
				reportsDao.edit(report);
				engineTask.processReport(report);
			} else {
				appLogger.info(LOG_ID
						+ "processReports | reached concurrent limit no more reports to process, concurrentMaxCount="
						+ concurrentMaxCount);
				break;
			}
		}
	}
}
