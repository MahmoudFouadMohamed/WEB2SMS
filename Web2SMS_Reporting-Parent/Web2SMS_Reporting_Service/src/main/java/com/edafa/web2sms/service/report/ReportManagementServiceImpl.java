package com.edafa.web2sms.service.report;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Reports;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.CampaignAggregationReport;
import com.edafa.web2sms.service.model.CampaignAggregationReportResult;
import com.edafa.web2sms.service.model.FileTokenResult;
import com.edafa.web2sms.service.model.GetCountResult;
import com.edafa.web2sms.service.model.HistoryReportSearch;
import com.edafa.web2sms.service.model.ReportsResult;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.SMSReport;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.report.exception.CampaginReportNotFound;
import com.edafa.web2sms.service.report.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.report.exception.NoLogsForCampaignException;
import com.edafa.web2sms.service.report.exception.SMSAPIViewLogNotFoundException;
import com.edafa.web2sms.service.report.interfaces.ReportManagementBeanLocal;
import com.edafa.web2sms.service.report.interfaces.ReportManagementService;
import com.edafa.web2sms.service.report.model.ReportResultSet;
import com.edafa.web2sms.service.report.model.SMSReportResultSet;
import com.edafa.web2sms.service.report.model.SummaryReport;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Stateless
@WebService(name = "ReportManagementService", serviceName = "ReportManagementService", targetNamespace = "http://www.edafa.com/web2sms/service/report", endpointInterface = "com.edafa.web2sms.service.report.interfaces.ReportManagementService")
public class ReportManagementServiceImpl implements ReportManagementService {

	private Logger campLogger = LogManager.getLogger(LoggersEnum.CAMP_MNGMT.name());
	private Logger smsLogger = LogManager.getLogger(LoggersEnum.SMS_API_MNGT.name());

	@EJB
	private ReportManagementBeanLocal reportManagementBean;

	@EJB
	private AppErrorManagerAdapter appErrorManagerAdapter;

	public ReportManagementServiceImpl() {}

	@Override
	public ReportResultSet getReportsViewWithPagination(UserTrxInfo userTrxInfo, Date startDate, Date endDate,
			int first, int max) {
		ReportResultSet result = new ReportResultSet();
		SummaryReport summaryReport = null;
		try {

			if (!userTrxInfo.isValid() || startDate == null || endDate == null) {
				campLogger.error("Invalid request: userTrxInfo=" + userTrxInfo + ", startDate" + startDate
						+ ", endDate=" + endDate);
				result.setStatus(ResponseStatus.INVALID_REQUEST);
			}

			List<CampaignAggregationReport> reportsList = reportManagementBean.getReportsWithinDateRange(userTrxInfo,
					startDate, endDate, first, max);
			if (reportsList != null) {
				summaryReport = reportManagementBean.getSummaryReport(userTrxInfo, reportsList);
				result.setReports(reportsList);
				result.setSummary(summaryReport);
				result.setStatus(ResponseStatus.SUCCESS);
			} else {
				result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			}

		} catch (DBException e) {
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}
			campLogger.error(userTrxInfo.logId() + "Datebase error", e);
			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IneligibleAccountException e) {
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			campLogger.error(userTrxInfo.logId() + "Failed to get reports", e);
			result.setStatus(ResponseStatus.FAIL);
		}

		return result;
	}

	@Override
	public GetCountResult countReportsView(UserTrxInfo userTrxInfo, Date startDate, Date endDate) {
		GetCountResult result = new GetCountResult();
		try {
			int count = reportManagementBean.countReportsWithinDateRangeForReport(userTrxInfo, startDate, endDate);
			result.setCount(count);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error, return zero reports", e);

			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ReportResultSet getPDFReports(UserTrxInfo userTrxInfo, Date startDate, Date endDate) {
		ReportResultSet result = new ReportResultSet();
		SummaryReport summaryReport = null;

		if (!userTrxInfo.isValid() || startDate == null || endDate == null) {
			campLogger.error(
					"Invalid request: userTrxInfo=" + userTrxInfo + ", startDate" + startDate + ", endDate=" + endDate);
			result.setStatus(ResponseStatus.INVALID_REQUEST);
		}

		try {
			List<CampaignAggregationReport> reportsList = reportManagementBean.getReportsWithinDateRange(userTrxInfo,
					startDate, endDate, 0, 0);
			if (reportsList != null) {
				summaryReport = reportManagementBean.getSummaryReport(userTrxInfo, reportsList);
				result.setSummary(summaryReport);
				result.setReports(reportsList);
				result.setDateFrom(startDate);
				result.setDateTo(endDate);
				result.setStatus(ResponseStatus.SUCCESS);
			} else {
				result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			}

		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}
			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to get reports", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}

		return result;
	}

	@Override
	public ReportResultSet getPDFReportsPaginated(UserTrxInfo userTrxInfo, Date startDate, Date endDate, int first,
			int max) {
		ReportResultSet result = new ReportResultSet();
		SummaryReport summaryReport = null;

		if (!userTrxInfo.isValid() || startDate == null || endDate == null) {
			campLogger.error(
					"Invalid request: userTrxInfo=" + userTrxInfo + ", startDate" + startDate + ", endDate=" + endDate);
			result.setStatus(ResponseStatus.INVALID_REQUEST);
		}

		try {
			List<CampaignAggregationReport> reportsList = reportManagementBean.getReportsWithinDateRange(userTrxInfo,
					startDate, endDate, first, max);
			if (reportsList != null) {
				summaryReport = reportManagementBean.getSummaryReport(userTrxInfo, reportsList);
				result.setSummary(summaryReport);
				result.setReports(reportsList);
				result.setDateFrom(startDate);
				result.setDateTo(endDate);
				result.setStatus(ResponseStatus.SUCCESS);
			} else {
				result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			}

		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to get reports", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}

		return result;
	}

	@Override
	public GetCountResult countReportsWithinDateRangeForPDF(UserTrxInfo userTrxInfo, Date startDate, Date endDate) {
		GetCountResult result = new GetCountResult();

		try {
			int count = reportManagementBean.countReportsWithinDateRangeForPDF(userTrxInfo, startDate, endDate);
			result.setCount(count);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error, return zero reports", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ReportResultSet getReportsByCampName(UserTrxInfo userTrxInfo, String campName) {
		ReportResultSet result = new ReportResultSet();
		SummaryReport summaryReport = null;

		if (!userTrxInfo.isValid() || campName.isEmpty()) {
			campLogger.error("Invalid request: userTrxInfo=" + userTrxInfo + ", Campaign name" + campName);
			result.setStatus(ResponseStatus.INVALID_REQUEST);
		}

		try {
			List<CampaignAggregationReport> reportsList = reportManagementBean.getReportsByCampName(userTrxInfo,
					campName, 0, 0);
			if (reportsList != null) {
				summaryReport = reportManagementBean.getSummaryReport(userTrxInfo, reportsList);
				result.setSummary(summaryReport);
				result.setReports(reportsList);
				result.setStatus(ResponseStatus.SUCCESS);
			} else {
				result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			}

		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to get reports", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}

		return result;
	}

	@Override
	public ReportResultSet getReportsByCampNamePaginated(UserTrxInfo userTrxInfo, String campName, int first, int max) {
		ReportResultSet result = new ReportResultSet();
		SummaryReport summaryReport = null;

		if (!userTrxInfo.isValid() || campName.isEmpty()) {
			campLogger.error("Invalid request: userTrxInfo=" + userTrxInfo + ", Campaign name" + campName);
			result.setStatus(ResponseStatus.INVALID_REQUEST);
		}

		try {
			List<CampaignAggregationReport> reportsList = reportManagementBean.getReportsByCampName(userTrxInfo,
					campName, first, max);
			if (reportsList != null) {
				summaryReport = reportManagementBean.getSummaryReport(userTrxInfo, reportsList);
				result.setSummary(summaryReport);
				result.setReports(reportsList);
				result.setStatus(ResponseStatus.SUCCESS);
			} else {
				result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			}

		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to get reports", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}

		return result;
	}

	@Override
	public GetCountResult countReportsByCampName(UserTrxInfo userTrxInfo, String campName) {
		GetCountResult result = new GetCountResult();

		try {
			int count = reportManagementBean.countReportsByCampName(userTrxInfo, campName);
			result.setCount(count);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error, return zero reports", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ReportResultSet getPDFReportsWithinDateAndCampName(UserTrxInfo userTrxInfo, Date startDate, Date endDate,
			String campName) {
		ReportResultSet result = new ReportResultSet();
		SummaryReport summaryReport = null;

		if (!userTrxInfo.isValid() || startDate == null || endDate == null || campName.isEmpty()) {
			campLogger.error("Invalid request: userTrxInfo=" + userTrxInfo + ", startDate" + startDate + ", endDate="
					+ endDate + ", Campaign Name= " + campName);
			result.setStatus(ResponseStatus.INVALID_REQUEST);
		}

		try {
			List<CampaignAggregationReport> reportsList = reportManagementBean
					.getReportsWithinDateRangeAndCampName(userTrxInfo, startDate, endDate, campName, 0, 0);
			if (reportsList != null) {
				summaryReport = reportManagementBean.getSummaryReport(userTrxInfo, reportsList);
				result.setSummary(summaryReport);
				result.setReports(reportsList);
				result.setDateFrom(startDate);
				result.setDateTo(endDate);
				result.setStatus(ResponseStatus.SUCCESS);
			} else {
				result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			}

		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to get reports", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}

		return result;
	}

	@Override
	public ReportResultSet getPDFReportsWithinDateAndCampNamePaginated(UserTrxInfo userTrxInfo, Date startDate,
			Date endDate, String campName, int first, int max) {
		ReportResultSet result = new ReportResultSet();
		SummaryReport summaryReport = null;

		if (!userTrxInfo.isValid() || startDate == null || endDate == null || campName.isEmpty()) {
			campLogger.error("Invalid request: userTrxInfo=" + userTrxInfo + ", startDate" + startDate + ", endDate="
					+ endDate + ", Campaign Name= " + campName);
			result.setStatus(ResponseStatus.INVALID_REQUEST);
		}

		try {
			List<CampaignAggregationReport> reportsList = reportManagementBean
					.getReportsWithinDateRangeAndCampName(userTrxInfo, startDate, endDate, campName, first, max);
			if (reportsList != null) {
				summaryReport = reportManagementBean.getSummaryReport(userTrxInfo, reportsList);
				result.setSummary(summaryReport);
				result.setReports(reportsList);
				result.setDateFrom(startDate);
				result.setDateTo(endDate);
				result.setStatus(ResponseStatus.SUCCESS);
			} else {
				result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			}

		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to get reports", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}

		return result;
	}

	@Override
	public GetCountResult countReportsWithinDateRangeAndCampNameForPDF(UserTrxInfo userTrxInfo, Date startDate,
			Date endDate, String campName) {
		GetCountResult result = new GetCountResult();

		try {
			int count = reportManagementBean.countReportsWithinDateRangeAndCampNameForPDF(userTrxInfo, startDate,
					endDate, campName);
			result.setCount(count);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Datebase error, return zero reports", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public FileTokenResult generateDetailedCampaignReport(UserTrxInfo userTrxInfo, String campaignId) {
		FileTokenResult result = new FileTokenResult();

		try {
			String fileToken = reportManagementBean.generateDetailedCampaignReportInExcel(userTrxInfo, campaignId);
			result.setFileToken(fileToken);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IOException e) {
			campLogger.error(userTrxInfo.logId() + "IOException while generating the campaign report", e);
			reportAppError(AppErrors.IO_ERROR, "IO Failure");
			result.setStatus(ResponseStatus.FAIL);
		} catch (NoLogsForCampaignException e) {
			campLogger.error(userTrxInfo.logId() + e);
			reportAppError(AppErrors.INVALID_OPERATION, "No Logs For Campaign");
			result.setStatus(ResponseStatus.NO_LOGS_FOR_CAMPAIGN);
		} catch (CampaignNotFoundException e) {
			campLogger.error(userTrxInfo.logId() + e);
			reportAppError(AppErrors.INVALID_OPERATION, "Campaign NotFound");
			result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}

		return result;
	}

	@Override
	public FileTokenResult generateDetailedSMSAPIReport(UserTrxInfo userTrxInfo, HistoryReportSearch params) {
		FileTokenResult result = new FileTokenResult();

		try {
			String fileToken = reportManagementBean.generateDetailedSMSAPIReportInExcel(userTrxInfo, params);
			result.setFileToken(fileToken);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			smsLogger.error(userTrxInfo.logId() + "Database error: ", e);
			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IOException e) {
			smsLogger.error(userTrxInfo.logId() + "IOException while generating the campaign report", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}
			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (SMSAPIViewLogNotFoundException e) {
			smsLogger.error(userTrxInfo.logId() + e.getMessage(), e);
			reportAppError(AppErrors.INVALID_OPERATION, "SMSAPI View Log NotFound");
			result.setStatus(ResponseStatus.NO_LOGS_FOUND);
			result.setErrorMessage(e.getMessage());

		} catch (IneligibleAccountException e) {
			smsLogger.error(userTrxInfo.logId() + e.getMessage(), e);
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			smsLogger.error(userTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());

		}

		return result;
	}

	@Override
	public SMSReportResultSet getSMSReportsView(UserTrxInfo userTrxInfo, HistoryReportSearch params, int first,
			int max) {
		SMSReportResultSet result = new SMSReportResultSet();
		SummaryReport summary = null;
		List<SMSReport> reportList = null;

		try {
			reportList = reportManagementBean.getSMSReportsView(userTrxInfo, params, first, max);
			if (reportList != null && !reportList.isEmpty()) {
				summary = reportManagementBean.getSMSSummaryReport(userTrxInfo, reportList);
				result.setSummaryReport(summary);
				result.setReports(reportList);
			}
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public GetCountResult countSMSReportsView(UserTrxInfo userTrxInfo, HistoryReportSearch params) {
		GetCountResult result = new GetCountResult();

		try {
			int count = reportManagementBean.countSMSReportsView(userTrxInfo, params);
			result.setCount(count);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public FileTokenResult generateDetailedCampaignReportForAdmin(AdminTrxInfo adminTrxInfo, String campaignId) {
		FileTokenResult result = new FileTokenResult();

		try {
			String fileToken = reportManagementBean.generateDetailedCampaignReportInExcel(adminTrxInfo, campaignId);
			result.setFileToken(fileToken);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(adminTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (IOException e) {
			campLogger.error(adminTrxInfo.logId() + "IOException while generating the campaign report", e);
			reportAppError(AppErrors.IO_ERROR, "IO Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		} catch (NoLogsForCampaignException e) {
			campLogger.error(adminTrxInfo.logId() + e);
			reportAppError(AppErrors.INVALID_OPERATION, "No Logs For Campaign");
			result.setStatus(ResponseStatus.NO_LOGS_FOR_CAMPAIGN);
			result.setErrorMessage(e.getMessage());
		} catch (CampaignNotFoundException e) {
			campLogger.error(adminTrxInfo.logId() + e);
			reportAppError(AppErrors.INVALID_OPERATION, "Campaign NotFound");
			result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(adminTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.REPORT_MANAGEMENT);
	}

	@Override
	public CampaignAggregationReportResult getReportByCampaignId(UserTrxInfo userTrxInfo, String campaignId) {
		CampaignAggregationReportResult result = new CampaignAggregationReportResult();

		try {
			CampaignAggregationReport campaignAggregationReport = reportManagementBean.getReportByCampId(userTrxInfo,
					campaignId);
			result.setCampaignAggregationReport(campaignAggregationReport);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e);
			reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (CampaginReportNotFound e) {
			campLogger.error(userTrxInfo.logId() + e);
			reportAppError(AppErrors.INVALID_OPERATION, "Campagin Report NotFound");
			result.setStatus(ResponseStatus.CAMPAIGN_REPORT_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	/* Offline */

	@Override
	public ResultStatus offlineGenerateDetailedCampaignReportForAdmin(AdminTrxInfo adminTrxInfo, String campaignId) {
		ResultStatus result = new ResultStatus();

		try {
			reportManagementBean.offlineGenerateDetailedCampaignReportForAdmin(adminTrxInfo, campaignId);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(adminTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(adminTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ResultStatus offlineGenerateDetailedCampaignReport(UserTrxInfo userTrxInfo, String campaignId) {
		ResultStatus result = new ResultStatus();

		try {
			reportManagementBean.offlineGenerateDetailedCampaignReport(userTrxInfo, campaignId);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ResultStatus offlineGenerateDetailedSMSAPIReport(UserTrxInfo userTrxInfo, HistoryReportSearch params) {
		ResultStatus result = new ResultStatus();

		try {
			reportManagementBean.offlineGenerateDetailedSMSAPIReport(userTrxInfo, params);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ReportsResult getAllReports(UserTrxInfo userTrxInfo, int start, int count) {
		ReportsResult result = new ReportsResult();

		try {
			List<Reports> allReports = reportManagementBean.getAllReports(userTrxInfo, start, count);
			result.setReports(allReports);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public GetCountResult getReportsCount(UserTrxInfo userTrxInfo) {
		GetCountResult result = new GetCountResult();

		try {
			int reportsCount = reportManagementBean.getReportsCount(userTrxInfo);
			result.setCount(reportsCount);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ReportsResult getAllAdminReports(AdminTrxInfo adminTrxInfo, int start, int count) {
		ReportsResult result = new ReportsResult();

		try {
			List<Reports> allReports = reportManagementBean.getAllAdminReports(adminTrxInfo, start, count);
			result.setReports(allReports);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(adminTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(adminTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public GetCountResult getAdminReportsCount(AdminTrxInfo adminTrxInfo) {
		GetCountResult result = new GetCountResult();

		try {
			int reportsCount = reportManagementBean.getAdminReportsCount(adminTrxInfo);
			result.setCount(reportsCount);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(adminTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(adminTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ResultStatus cancelReport(UserTrxInfo userTrxInfo, int reportId) {
		ResultStatus result = new ResultStatus();

		try {
			reportManagementBean.cancelReport(userTrxInfo, reportId);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(userTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(userTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}

	@Override
	public ResultStatus cancelAdminReport(AdminTrxInfo adminTrxInfo, int reportId) {
		ResultStatus result = new ResultStatus();

		try {
			reportManagementBean.cancelAdminReport(adminTrxInfo, reportId);
			result.setStatus(ResponseStatus.SUCCESS);
		} catch (DBException e) {
			campLogger.error(adminTrxInfo.logId() + "Database error: ", e);
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

			result.setStatus(ResponseStatus.DB_ERROR);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			campLogger.error(adminTrxInfo.logId() + "Failed to handle the request: ", e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		}

		return result;
	}
}
