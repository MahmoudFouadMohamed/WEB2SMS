package com.edafa.web2sms.service.report.interfaces;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Reports;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.CampaignAggregationReport;
import com.edafa.web2sms.service.model.HistoryReportSearch;
import com.edafa.web2sms.service.model.SMSReport;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.report.exception.CampaginReportNotFound;
import com.edafa.web2sms.service.report.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.report.exception.NoLogsForCampaignException;
import com.edafa.web2sms.service.report.exception.ReportException;
import com.edafa.web2sms.service.report.exception.SMSAPIViewLogNotFoundException;
import com.edafa.web2sms.service.report.model.SummaryReport;

@Local
public interface ReportManagementBeanLocal {

	int countAllReports(UserTrxInfo userTrxInfo) throws DBException;

	int countReportsWithinDateRangeForPDF(UserTrxInfo userTrxInfo, Date startDate, Date endDate) throws DBException;

	int countReportsWithinDateRangeForReport(UserTrxInfo userTrxInfo, Date startDate, Date endDate) throws DBException;

	public List<CampaignAggregationReport> getReportsByCampName(UserTrxInfo userTrxInfo, String campName, int first,
			int max) throws IneligibleAccountException, DBException;

	List<CampaignAggregationReport> getReportsWithinDateRange(UserTrxInfo userTrxInfo, Date startDate, Date endDate,
			int first, int max) throws IneligibleAccountException, DBException;

	String generateDetailedCampaignReport(UserTrxInfo userTrxInfo, String campaignId) throws DBException, IOException,
			CampaignNotFoundException, NoLogsForCampaignException, IneligibleAccountException;

	String generateDetailedCampaignReportInExcel(UserTrxInfo userTrxInfo, String campaignId) throws DBException,
			IOException, CampaignNotFoundException, NoLogsForCampaignException, IneligibleAccountException;

	int countReportsByCampName(UserTrxInfo userTrxInfo, String campName) throws DBException;

	List<CampaignAggregationReport> getReportsWithinDateRangeAndCampName(UserTrxInfo userTrxInfo, Date startDate,
			Date endDate, String campName, int first, int max) throws IneligibleAccountException, DBException;

	int countReportsWithinDateRangeAndCampNameForPDF(UserTrxInfo userTrxInfo, Date startDate, Date endDate,
			String campName) throws DBException;

	List<SMSReport> getSMSReportsView(UserTrxInfo userTrxInfo, HistoryReportSearch params, int first, int max)
			throws IneligibleAccountException, DBException;

	String generateDetailedSMSAPIReportInExcel(UserTrxInfo userTrxInfo, HistoryReportSearch params)
			throws DBException, IOException, SMSAPIViewLogNotFoundException, IneligibleAccountException;

	String generateDetailedCampaignReportInExcel(AdminTrxInfo adminTrxInfo, String campaignId)
			throws DBException, IOException, CampaignNotFoundException, NoLogsForCampaignException;

	CampaignAggregationReport getReportByCampId(UserTrxInfo userTrxInfo, String campId)
			throws IneligibleAccountException, DBException, CampaginReportNotFound;

	List<Reports> getAllReports(UserTrxInfo userTrxInfo, int start, int count) throws DBException;

	int getReportsCount(UserTrxInfo userTrxInfo) throws DBException;

	List<Reports> getAllAdminReports(AdminTrxInfo adminTrxInfo, int start, int count) throws DBException;

	int getAdminReportsCount(AdminTrxInfo adminTrxInfo) throws DBException;

	void cancelReport(UserTrxInfo userTrxInfo, long reportId) throws DBException, ReportException;

	void cancelAdminReport(AdminTrxInfo adminTrxInfo, long reportId) throws DBException, ReportException;

	void offlineGenerateDetailedCampaignReportForAdmin(AdminTrxInfo adminTrxInfo, String campaignId) throws DBException;

	void offlineGenerateDetailedCampaignReport(UserTrxInfo userTrxInfo, String campaignId) throws DBException;

	void offlineGenerateDetailedSMSAPIReport(UserTrxInfo userTrxInfo, HistoryReportSearch params) throws DBException;

	SummaryReport getSummaryReport(UserTrxInfo userTrxInfo, List<CampaignAggregationReport> campAggReportList);

	SummaryReport getSMSSummaryReport(UserTrxInfo userTrxInfo, List<SMSReport> smsReportList);

	int countSMSReportsView(UserTrxInfo userTrxInfo, HistoryReportSearch params) throws DBException;

}
