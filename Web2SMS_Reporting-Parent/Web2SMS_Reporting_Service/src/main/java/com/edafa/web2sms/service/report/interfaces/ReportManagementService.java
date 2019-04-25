package com.edafa.web2sms.service.report.interfaces;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.CampaignAggregationReportResult;
import com.edafa.web2sms.service.model.FileTokenResult;
import com.edafa.web2sms.service.model.GetCountResult;
import com.edafa.web2sms.service.model.HistoryReportSearch;
import com.edafa.web2sms.service.model.ReportsResult;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.report.model.ReportResultSet;
import com.edafa.web2sms.service.report.model.SMSReportResultSet;

@WebService(name = "ReportManagementService", portName = "ReportManagementServicePort", targetNamespace = "http://www.edafa.com/web2sms/service/report")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ReportManagementService {

	@WebMethod(operationName = "getPDFReports")
	@WebResult(name = "ReportResultSet", partName = "reportResultSet")
	public ReportResultSet getPDFReports(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "StartDate", partName = "startDate") Date startDate,
			@WebParam(name = "EndDate", partName = "endDate") Date endDate);

	@WebMethod(operationName = "getPDFReportsPaginated")
	@WebResult(name = "ReportResultSet", partName = "reportResultSet")
	public ReportResultSet getPDFReportsPaginated(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "StartDate", partName = "startDate") Date startDate,
			@WebParam(name = "EndDate", partName = "endDate") Date endDate,
			@WebParam(name = "First", partName = "first") int first, @WebParam(name = "Max", partName = "max") int max);

	@WebMethod(operationName = "getReportsByCampName")
	@WebResult(name = "ReportResultSet", partName = "reportResultSet")
	public ReportResultSet getReportsByCampName(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignName", partName = "campaignName") String name);

	@WebMethod(operationName = "getReportsByCampNamePaginated")
	@WebResult(name = "ReportResultSet", partName = "reportResultSet")
	public ReportResultSet getReportsByCampNamePaginated(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignName", partName = "campaignName") String name,
			@WebParam(name = "First", partName = "first") int first, @WebParam(name = "Max", partName = "max") int max);

	@WebMethod(operationName = "getPDFReportsWithinDateAndCampName")
	@WebResult(name = "ReportResultSet", partName = "reportResultSet")
	public ReportResultSet getPDFReportsWithinDateAndCampName(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "StartDate", partName = "startDate") Date startDate,
			@WebParam(name = "EndDate", partName = "endDate") Date endDate,
			@WebParam(name = "CampaignName", partName = "campaignName") String name);

	@WebMethod(operationName = "getPDFReportsWithinDateAndCampNamePaginated")
	@WebResult(name = "ReportResultSet", partName = "reportResultSet")
	public ReportResultSet getPDFReportsWithinDateAndCampNamePaginated(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "StartDate", partName = "startDate") Date startDate,
			@WebParam(name = "EndDate", partName = "endDate") Date endDate,
			@WebParam(name = "CampaignName", partName = "campaignName") String name,
			@WebParam(name = "First", partName = "first") int first, @WebParam(name = "Max", partName = "max") int max);

	@WebMethod(operationName = "getReportsViewWithPagination")
	@WebResult(name = "ReportResultSet", partName = "reportResultSet")
	public ReportResultSet getReportsViewWithPagination(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "StartDate", partName = "startDate") Date startDate,
			@WebParam(name = "EndDate", partName = "endDate") Date endDate,
			@WebParam(name = "First", partName = "first") int first, @WebParam(name = "Max", partName = "max") int max);

	@WebMethod(operationName = "countReportsView")
	@WebResult(name = "GetCountResult", partName = "getCountResult")
	public GetCountResult countReportsView(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "StartDate", partName = "startDate") Date startDate,
			@WebParam(name = "EndDate", partName = "endDate") Date endDate);

	@WebMethod(operationName = "countReportsByCampName")
	@WebResult(name = "GetCountResult", partName = "getCountResult")
	public GetCountResult countReportsByCampName(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignName", partName = "campaignName") String campName);

	@WebMethod(operationName = "countReportsWithinDateRangeForPDF")
	@WebResult(name = "GetCountResult", partName = "getCountResult")
	public GetCountResult countReportsWithinDateRangeForPDF(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "StartDate", partName = "startDate") Date startDate,
			@WebParam(name = "EndDate", partName = "endDate") Date endDate);

	@WebMethod(operationName = "countReportsWithinDateRangeAndCampNameForPDF")
	@WebResult(name = "GetCountResult", partName = "getCountResult")
	public GetCountResult countReportsWithinDateRangeAndCampNameForPDF(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "StartDate", partName = "startDate") Date startDate,
			@WebParam(name = "EndDate", partName = "endDate") Date endDate,
			@WebParam(name = "CampaignName", partName = "campaignName") String campName);

	@WebMethod(operationName = "getSMSReportsView")
	@WebResult(name = "ReportResultSet", partName = "reportResultSet")
	public SMSReportResultSet getSMSReportsView(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "HistoryReportSearch", partName = "historyReportSearch") HistoryReportSearch params,
			@WebParam(name = "First", partName = "first") int first, @WebParam(name = "Max", partName = "max") int max);

	@WebMethod(operationName = "countSMSReportsView")
	@WebResult(name = "GetCountResult", partName = "getCountResult")
	public GetCountResult countSMSReportsView(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "HistoryReportSearch", partName = "historyReportSearch") HistoryReportSearch params);

	@WebMethod(operationName = "generateDetailedCampaignReport")
	@WebResult(name = "FileTokenResult", partName = "fileTokenResult")
	public FileTokenResult generateDetailedCampaignReport(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campaignId);

	@WebMethod(operationName = "generateDetailedSMSAPIReport")
	@WebResult(name = "FileTokenResult", partName = "fileTokenResult")
	public FileTokenResult generateDetailedSMSAPIReport(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "HistoryReportSearch", partName = "historyReportSearch") HistoryReportSearch params);

	@WebMethod(operationName = "generateDetailedCampaignReportForAdmin")
	@WebResult(name = "FileTokenResult", partName = "fileTokenResult")
	public FileTokenResult generateDetailedCampaignReportForAdmin(
			@WebParam(name = "AdminTrxInfo", partName = "adminTrxInfo") AdminTrxInfo adminTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campaignId);

	@WebMethod(operationName = "getReportByCampId")
	@WebResult(name = "CampaignAggregationReportResult", partName = "campaignAggregationReportResult")
	public CampaignAggregationReportResult getReportByCampaignId(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campaignId);

	/* offline */

	@WebMethod(operationName = "offlineGenerateDetailedCampaignReportForAdmin")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus offlineGenerateDetailedCampaignReportForAdmin(
			@WebParam(name = "AdminTrxInfo", partName = "adminTrxInfo") AdminTrxInfo adminTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campaignId);

	@WebMethod(operationName = "offlineGenerateDetailedCampaignReport")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus offlineGenerateDetailedCampaignReport(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campaignId);

	@WebMethod(operationName = "offlineGenerateDetailedSMSAPIReport")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus offlineGenerateDetailedSMSAPIReport(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "HistoryReportSearch", partName = "historyReportSearch") HistoryReportSearch params);

	@WebMethod(operationName = "getAllReports")
	@WebResult(name = "ReportsResult", partName = "reportsResult")
	public ReportsResult getAllReports(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "start", partName = "start") int start,
			@WebParam(name = "count", partName = "count") int count);

	@WebMethod(operationName = "getReportsCount")
	@WebResult(name = "GetCountResult", partName = "getCountResult")
	public GetCountResult getReportsCount(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo);

	@WebMethod(operationName = "getAllAdminReports")
	@WebResult(name = "ReportsResult", partName = "reportsResult")
	public ReportsResult getAllAdminReports(
			@WebParam(name = "AdminTrxInfo", partName = "adminTrxInfo") AdminTrxInfo adminTrxInfo,
			@WebParam(name = "start", partName = "start") int start,
			@WebParam(name = "count", partName = "count") int count);

	@WebMethod(operationName = "getAdminReportsCount")
	@WebResult(name = "GetCountResult", partName = "getCountResult")
	public GetCountResult getAdminReportsCount(
			@WebParam(name = "AdminTrxInfo", partName = "adminTrxInfo") AdminTrxInfo adminTrxInfo);

	@WebMethod(operationName = "cancelReport")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus cancelReport(@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			int reportId);

	@WebMethod(operationName = "cancelAdminReport")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus cancelAdminReport(
			@WebParam(name = "AdminTrxInfo", partName = "adminTrxInfo") AdminTrxInfo adminTrxInfo, int reportId);
}
