package com.edafa.web2sms.quota;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.jee.scheduler.api.ScheduledTask;
import com.edafa.jee.scheduler.model.ScheduledTaskFrequency;
import com.edafa.jee.scheduler.model.ScheduledTaskStatus;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.QuotaHistoryDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSLogDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.QuotaHistory;
import com.edafa.web2sms.dalayer.pojo.MonthQuota;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Stateless
public class QuotaAggTask implements QuotaAggTaskRemote {

	private static final long serialVersionUID = 5127218743321795180L;

	Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());
	Logger quotaAggLogger = LogManager.getLogger(LoggersEnum.QUOTA_AGG_TASK.name());

	DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS z");

	// Timestamps
	Calendar startTimestamp;
	Calendar endTimestamp;

	@EJB
	AccountDaoLocal accountDao;

	@EJB
	AccountStatusDaoLocal accountStatusDao;

	@EJB
	QuotaHistoryDaoLocal quotaHistoryDao;

	@EJB
	SMSLogDaoLocal smsLogDao;

	@Override
	public ScheduledTaskStatus execute(ScheduledTask task) {
		appLogger.info("Starting Quota Agg task");
		quotaAggLogger.info("Starting Quota Agg task");
		ScheduledTaskStatus finalStatus = ScheduledTaskStatus.SUCCESS;
		Exception exc = null;
		int calUnit = getCalenderUnit(task.getSchedulingPeriodicalUnit());
		DateFormat df = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSS a");

		Date lastStart = task.getLastStartTime();
		Calendar thisMinTimestamp = Calendar.getInstance();
		thisMinTimestamp.set(Calendar.DAY_OF_MONTH, 1);
		// thisMinTimestamp.set(Calendar.HOUR, 0);
		// thisMinTimestamp.set(Calendar.MINUTE, 0);
		thisMinTimestamp.set(Calendar.SECOND, 0);
		thisMinTimestamp.set(Calendar.MILLISECOND, 0);

		try {
			if (lastStart != null) {
				quotaAggLogger.info("Last started timestamp=" + df.format(lastStart));
				Calendar lastStartTimestamp = Calendar.getInstance();
				lastStartTimestamp.setTime(lastStart);
				lastStartTimestamp.set(Calendar.DAY_OF_MONTH, 1);
				lastStartTimestamp.set(Calendar.HOUR, 0);
				lastStartTimestamp.set(Calendar.MINUTE, 0);
				lastStartTimestamp.set(Calendar.SECOND, 0);
				lastStartTimestamp.set(Calendar.MILLISECOND, 0);
				startTimestamp = (Calendar) lastStartTimestamp.clone();
			} else {
				startTimestamp = (Calendar) thisMinTimestamp.clone();
				// to pass if condition
				// (startTimestamp.compareTo(thisMinTimestamp) < 0)
				startTimestamp.add(Calendar.MONTH, -2);
			}

			Date startDate = startTimestamp.getTime();
			Date endDate = thisMinTimestamp.getTime();
			quotaAggLogger
					.info("startTimestamp.compareTo(thisMinTimestamp)=" + startTimestamp.compareTo(thisMinTimestamp)
							+ ", startTimestamp=" + df.format(startDate) + ", thisMinTimestamp=" + df.format(endDate));
			if (startTimestamp.compareTo(thisMinTimestamp) < 0) {

				handleQuotaHistory();

			}

			appLogger.info("Finshed aggregated quota task, status: " + finalStatus);
			quotaAggLogger.info("Finshed aggregated quota task, status: " + finalStatus);
		} catch (DBException e) {
			quotaAggLogger.error(e.getMessage());
			appLogger.error("Agg quota DBException:" + e.getMessage(), e);
			finalStatus = ScheduledTaskStatus.FAILED;
			// reportAppError(AppErrors.DATABASE_ERROR, "Agg CDR DBException:" +
			// e.getMessage());

		} catch (Exception e) {
			quotaAggLogger.error(e.getMessage());
			appLogger.error("Unexpected Exception " + e.getMessage(), e);
			finalStatus = ScheduledTaskStatus.FAILED;
			// reportAppError(AppErrors.GENERAL_ERROR,
			// "failed to handle request \"generating Agg CDR\". ");
		}
		return finalStatus;
	}

	private int getCalenderUnit(ScheduledTaskFrequency schedulePeriodicalUnit) {
		int result = 1;
		switch (schedulePeriodicalUnit) {
			case MINUTE:
				result = Calendar.MINUTE;
				break;
			case HOUR:
				result = Calendar.HOUR;
				break;
			case DAY:
				result = Calendar.DATE;
				break;
			case FRIST_DAY_OF_MONTH:
			case MONTH:
				result = Calendar.MONTH;
				break;
			case WEEK:
				result = Calendar.WEEK_OF_MONTH;
				break;
			default:
				result = 1;
		}
		quotaAggLogger.trace(
				"Agg quota task is scheduled as " + schedulePeriodicalUnit + ", equivalent calender unit=" + result);
		return result;
	}

	public void handleQuotaHistory() throws DBException {

		appLogger.info("Start handling quota aggregation");
		DateFormat df = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSS a");

		// quotaAggLogger.debug("Aggregate quota for all Web2SMS account from Date:"
		// + df.format(startTimestamp.getTime())
		// + " to Date:" + df.format(endTimestamp.getTime()));

		List<AccountStatus> statusList = new ArrayList<>();
		statusList.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
		statusList.add(accountStatusDao.getCachedObjectByName(AccountStatusName.SUSPENDED));

		quotaAggLogger.debug("Getting all Web2SMS accounts having status:" + statusList);

		List<Account> accountList = accountDao.findByStatuses(statusList);

		quotaAggLogger.debug("retreived (" + accountList.size() + ") account(s).");

		for (Account account : accountList) {
			Calendar time = Calendar.getInstance();
			time.set(Calendar.DAY_OF_MONTH, 1);
			time.set(Calendar.MINUTE, 0);
			time.set(Calendar.SECOND, 0);
			time.set(Calendar.MILLISECOND, 0);
			time.set(Calendar.HOUR, 0);

			Date dateTo = time.getTime();
			Date dateFrom;

			quotaAggLogger.debug("account(" + account.getAccountId() + ") | Getting Quota History.");

			QuotaHistory quotaHistory = account.getQuotaHistory();

			if (quotaHistory == null) {
				quotaAggLogger.debug("account(" + account.getAccountId()
						+ ") | there is no Quota history for this account, will create new quota history");
				quotaHistory = new QuotaHistory(account.getAccountId());
				time.add(Calendar.YEAR, -1);
				dateFrom = time.getTime();
			} else {
				quotaAggLogger.debug("account(" + account.getAccountId()
						+ ") | Quota History found with last update timestamp: " + quotaHistory.getUpdateTimestamp());
				dateFrom = quotaHistory.getUpdateTimestamp();
				Calendar dateFromCal = Calendar.getInstance();
				dateFromCal.setTime(dateFrom);
				dateFromCal.set(Calendar.MINUTE, 0);
				dateFromCal.set(Calendar.SECOND, 0);
				dateFromCal.set(Calendar.MILLISECOND, 0);
				dateFromCal.set(Calendar.HOUR, 0);
			}

			quotaAggLogger.debug("account(" + account.getAccountId() + ") | Aggregate Quota history from ["
					+ df.format(dateFrom) + "] to [" + df.format(dateTo) + "].");
			List<MonthQuota> monthlyQuota = smsLogDao.findQuotaHistory(account.getAccountId(), df.format(dateFrom),
					df.format(dateTo));
			if (monthlyQuota != null) {
				quotaAggLogger
						.debug("account(" + account.getAccountId() + ") | " + monthlyQuota + " month(s) Aggregated");
			} else {
				quotaAggLogger.error("account(" + account.getAccountId() + ") | Aggregation return null");
			}
			for (MonthQuota monthQuota : monthlyQuota) {
				quotaHistory.setMonthValue(monthQuota.getMonth() - 1, monthQuota.getCount());
			}
			quotaAggLogger.debug("account(" + account.getAccountId() + ") | updating timestamp in quotaHistory to "
					+ dateTo + ", and persist changes in DB");
			quotaHistory.setUpdateTimestamp(dateTo);
			quotaHistoryDao.edit(quotaHistory);
		}

	}
}
