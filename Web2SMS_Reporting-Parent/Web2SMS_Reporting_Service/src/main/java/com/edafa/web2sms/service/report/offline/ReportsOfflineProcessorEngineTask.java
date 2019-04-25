package com.edafa.web2sms.service.report.offline;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.dalayer.dao.interfaces.ReportsDaoLocal;
import com.edafa.web2sms.dalayer.enums.ReportStatus;
import com.edafa.web2sms.dalayer.enums.ReportType;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Admin;
import com.edafa.web2sms.dalayer.model.Reports;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.HistoryReportSearch;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.service.model.UserModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.report.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.report.exception.NoLogsForCampaignException;
import com.edafa.web2sms.service.report.exception.SMSAPIViewLogNotFoundException;
import com.edafa.web2sms.service.report.interfaces.ReportManagementBeanLocal;
import com.edafa.web2sms.service.report.offline.interfaces.ReportsOfflineProcessorEngineTaskLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Stateless
public class ReportsOfflineProcessorEngineTask implements ReportsOfflineProcessorEngineTaskLocal {

	private Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

	private static final AtomicInteger RUNNING_COUNT;

	static {
		RUNNING_COUNT = new AtomicInteger(0);
	}

	@EJB
	private ReportsDaoLocal reportsDao;

	@EJB
	private ReportManagementBeanLocal reportManagementBean;

	public ReportsOfflineProcessorEngineTask() {}

	@Override
	@Lock(LockType.READ)
	public int getActiveCount() {
		return RUNNING_COUNT.get();
	}

	@Override
	@Asynchronous
	@Lock(LockType.READ)
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void processReport(Reports report) {
		int executionNumber = RUNNING_COUNT.incrementAndGet();
		appLogger.info("start process report,executionNumber=" + executionNumber + ", report=" + report);

		TrxInfo trx = null;
		String fileToken = null;

		try {
			appLogger.info("create trxInfo, report.isAdmin=" + report.getAdmin());
			if (!report.getAdmin()) {
				UserModel user = new UserModel();
				user.setAccountId(report.getOwner());
				user.setUsername(report.getUserName());
				UserTrxInfo userTrx = new UserTrxInfo();
				userTrx.setTrxId(report.getTrx());
				userTrx.setUser(user);

				trx = userTrx;
			} else {
				Admin admin = new Admin(new BigDecimal(report.getOwner()));
				admin.setUsername(report.getUserName());

				AdminTrxInfo adminTrx = new AdminTrxInfo();
				adminTrx.setTrxId(report.getTrx());

				trx = adminTrx;
			}

			if (report.getType() == ReportType.CAMPAIGN) {
				String campaign = report.getCampaign();

				if (report.getAdmin()) {
					appLogger.info(trx.logId()
							+ "call reportManagementBean.generateDetailedCampaignReportInExcel, AdminTrxInfo=" + trx
							+ ", campaign=" + campaign);
					fileToken = reportManagementBean.generateDetailedCampaignReportInExcel((AdminTrxInfo) trx,
							campaign);
					appLogger.info(
							trx.logId() + "generateDetailedCampaignReportInExcel success, fileToken=" + fileToken);
				} else {
					appLogger.info(trx.logId()
							+ "call reportManagementBean.generateDetailedCampaignReportInExcel, UserTrxInfo=" + trx
							+ ", campaign=" + campaign);
					fileToken = reportManagementBean.generateDetailedCampaignReportInExcel((UserTrxInfo) trx, campaign);
					appLogger.info(
							trx.logId() + "generateDetailedCampaignReportInExcel success, fileToken=" + fileToken);
				}
			} else {
				HistoryReportSearch hrs = new HistoryReportSearch();
				hrs.setDateFrom(report.getFrom());
				hrs.setDateTo(report.getTo());
				hrs.setSenderName(report.getSenderName());

				appLogger.info(
						trx.logId() + "call reportManagementBean.generateDetailedSMSAPIReportInExcel, UserTrxInfo="
								+ trx + ", historyReportSearch=" + hrs);
				fileToken = reportManagementBean.generateDetailedSMSAPIReportInExcel((UserTrxInfo) trx, hrs);
				appLogger.info(trx.logId() + "generateDetailedCampaignReportInExcel success, fileToken=" + fileToken);
			}

			report.setFileName(fileToken);
			report.setStatus(ReportStatus.SUCCESS);
		} catch (DBException e) {
			appLogger.error(trx.logId() + "Datebase error ", e);
			report.setStatus(ReportStatus.FAILED);
		} catch (RuntimeException e) {
			appLogger.error(trx.logId() + "Runtime Exception ", e);
			report.setStatus(ReportStatus.FAILED);
		} catch (IOException e) {
			appLogger.error(trx.logId() + "IO Exception ", e);
			report.setStatus(ReportStatus.FAILED);
		} catch (CampaignNotFoundException e) {
			appLogger.error(trx.logId() + "Campaign Not Found ", e);
			report.setStatus(ReportStatus.FAILED);
		} catch (NoLogsForCampaignException e) {
			appLogger.error(trx.logId() + "No Logs For Campaign ", e);
			report.setStatus(ReportStatus.FAILED);
		} catch (IneligibleAccountException e) {
			appLogger.error(trx.logId() + "Ineligible Account ", e);
			report.setStatus(ReportStatus.FAILED);
		} catch (SMSAPIViewLogNotFoundException e) {
			appLogger.error(trx.logId() + "SMSAPI Log NotFound ", e);
			report.setStatus(ReportStatus.FAILED);
		}

		try {
			appLogger.info(trx.logId() + "reportsDao.edit, report=" + report);
			reportsDao.edit(report);
			appLogger.info(trx.logId() + "reportsDao.edit Success");
		} catch (DBException e) {
			appLogger.error(trx.logId() + "Failed to update report, report=" + report);
			appLogger.error(trx.logId() + "Datebase error ", e);
		}

		RUNNING_COUNT.decrementAndGet();
	}
}
