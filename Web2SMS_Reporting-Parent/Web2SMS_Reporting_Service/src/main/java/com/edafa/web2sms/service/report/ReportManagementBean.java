package com.edafa.web2sms.service.report;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.edafa.csv.batchfile.CSVBatchFile;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ReportsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSLogDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SmsApiStatsDaoLocal;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.enums.ReportStatus;
import com.edafa.web2sms.dalayer.enums.ReportType;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignStatsReport;
import com.edafa.web2sms.dalayer.model.Reports;
import com.edafa.web2sms.dalayer.model.SMSLog;
import com.edafa.web2sms.dalayer.model.SmsApiDailySmsStats;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.conversoin.ReportConversionBean;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.CampaignAggregationReport;
import com.edafa.web2sms.service.model.HistoryReportSearch;
import com.edafa.web2sms.service.model.SMSLogCSV;
import com.edafa.web2sms.service.model.SMSReport;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.report.exception.CampaginReportNotFound;
import com.edafa.web2sms.service.report.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.report.exception.NoLogsForCampaignException;
import com.edafa.web2sms.service.report.exception.ReportException;
import com.edafa.web2sms.service.report.exception.ReportNotFound;
import com.edafa.web2sms.service.report.exception.SMSAPIViewLogNotFoundException;
import com.edafa.web2sms.service.report.interfaces.ReportManagementBeanLocal;
import com.edafa.web2sms.service.report.model.SummaryReport;
import com.edafa.web2sms.utils.FileNameUtils;
import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Stateless
@LocalBean
public class ReportManagementBean implements ReportManagementBeanLocal {

	private Logger campLogger = LogManager.getLogger(LoggersEnum.CAMP_MNGMT.name());
	private Logger smsLogger = LogManager.getLogger(LoggersEnum.SMS_API_MNGT.name());
	private Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

	@EJB
	private AccountManegementFacingLocal accountManegement;

	@EJB
	private CampaignDaoLocal campaignDao;

	@EJB
	private ReportConversionBean reportConversionBean;

	/**/

	@EJB
	private CampaignStatsDaoLocal campaignStatsDao;

	@EJB
	private SmsApiStatsDaoLocal smsApiStatsDao;

	@EJB
	private ReportsDaoLocal reportsDao;

	@EJB
	private SMSLogDaoLocal smsLogDao;

	private DateFormat df = new SimpleDateFormat((String) Configs.LOG_TIMESTAMP_FORMAT.getValue());

	public ReportManagementBean() {}

	private String getUniqueFileName(String postfix) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
		return df.format(new Date()) + "_" + postfix;
	}

	private Sheet newSMSAPISheetWithHeader(SXSSFWorkbook wb) {
		Sheet sh = wb.createSheet();
		Row row = sh.createRow(0);
		Cell cell;
		int cellNum = 0;
		for (String header : getSMSAPIHeader()) {
			cell = row.createCell(cellNum++, Cell.CELL_TYPE_STRING);
			cell.setCellValue(header);
		}
		return sh;
	}

	private Sheet newSheetWithHeader(SXSSFWorkbook wb) {
		Sheet sh = wb.createSheet();
		Row row = sh.createRow(0);
		Cell cell;
		int cellNum = 0;
		for (String header : getHeader()) {
			cell = row.createCell(cellNum++, Cell.CELL_TYPE_STRING);
			cell.setCellValue(header);
		}
		return sh;
	}

	private List<String> getSMSAPIHeader() {
		List<String> header = new LinkedList<>();
		header.add("Sender");
		header.add("Receiver");
		header.add("SMS Text");
		header.add("Language");
		header.add("Status");
		header.add("Send Date");
		header.add("Sent Segments");
		header.add("Delivery Date");

		return header;
	}

	private void updateSMSLogCSV(Campaign campaign, SMSLog log, SMSLogCSV logCSV) {
		logCSV.setCampaignName(campaign.getName());
		logCSV.setScheduleFrequency(campaign.getCampaignScheduling().getScheduleFrequency().getScheduleFreqName());
		logCSV.setSender(log.getSender());
		logCSV.setReceiver(log.getReceiver());
		logCSV.setSmsText(log.getSmsText());
		logCSV.setLanguage(log.getSMSLanguage().getLanguageName());
		logCSV.setStatus(log.getSMSStatus().getName());
		logCSV.setSegCount(log.getSegmentsCount());
		logCSV.setComments(log.getComments());
		// logCSV.setSendDate(log.getSendReceiveDate());
		// logCSV.setDeliveryDate(log.getDeliveryDate());
	}

	private List<String> getLogArgs(Campaign campaign, SMSLog smsLog) {
		List<String> logArgs = new ArrayList<>(12);
		logArgs.add(campaign.getName());
		logArgs.add(campaign.getCampaignScheduling().getScheduleFrequency().getScheduleFreqName() != null
				? campaign.getCampaignScheduling().getScheduleFrequency().getScheduleFreqName().toString()
				: "");
		logArgs.add(smsLog.getSender() != null ? smsLog.getSender() : "");
		logArgs.add(smsLog.getReceiver());
		logArgs.add(smsLog.getSmsText());
		logArgs.add(
				smsLog.getSMSLanguage().getLanguageName() != null ? smsLog.getSMSLanguage().getLanguageName().toString()
						: "");
		logArgs.add(smsLog.getSMSStatus().getName() != null ? smsLog.getSMSStatus().getName().toString() : "");
		logArgs.add(smsLog.getSendReceiveDate() != null ? smsLog.getSendReceiveDate().toString() : "");
		logArgs.add(smsLog.getSegmentsCount() != null ? smsLog.getSegmentsCount().toString() : "");
		logArgs.add(smsLog.getDeliveryDate() != null ? smsLog.getDeliveryDate().toString() : "");
		logArgs.add(smsLog.getComments());

		return logArgs;
	}

	private List<String> getHeader() {
		List<String> header = new LinkedList<>();
		header.add("Campaign Name");
		header.add("Campaign Type");
		header.add("Sender");
		header.add("Receiver");
		header.add("SMS Text");
		header.add("Language");
		header.add("Status");
		header.add("Send Date");
		header.add("Sent Segments");
		header.add("Delivery Date");
		header.add("Comments");

		return header;
	}

	private CSVBatchFile openDetailedReportCSV(SMSLogCSV csvLog, String outPathName, String outFileBaseName,
			String delimiter, String quoteChar, Charset charset, boolean compressed) throws IOException {
		CSVBatchFile batchFile;
		String outFileName = null;

		if (compressed) {
			outFileName = outFileBaseName + ".zip";
			FileOutputStream fos = new FileOutputStream(outPathName + outFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			// add a new Zip Entry to the ZipOutputStream
			ZipEntry ze = new ZipEntry(outFileBaseName + ".csv");
			zos.putNextEntry(ze);
			batchFile = new CSVBatchFile(csvLog, null, zos, delimiter, quoteChar, charset);
		} else {
			outFileName = outFileBaseName + ".csv";
			Path outFilePath = Paths.get(outPathName + outFileName);
			batchFile = new CSVBatchFile(csvLog, outFilePath.toFile(), delimiter, quoteChar, charset,
					StandardOpenOption.CREATE);
		}
		batchFile.setFileName(outFileName);

		return batchFile;
	}

	private List<String> getSMSAPILogArgs(SMSLog smsLog) {
		List<String> logArgs = new ArrayList<>(10);

		logArgs.add(smsLog.getSender() != null ? smsLog.getSender() : "");
		logArgs.add(smsLog.getReceiver() != null ? smsLog.getReceiver() : "");
		logArgs.add(smsLog.getSmsText() != null ? smsLog.getSmsText() : "");
		logArgs.add(
				smsLog.getSMSLanguage().getLanguageName() != null ? smsLog.getSMSLanguage().getLanguageName().toString()
						: "");
		logArgs.add(smsLog.getSMSStatus().getName() != null ? smsLog.getSMSStatus().getName().toString() : "");
		logArgs.add(smsLog.getSendReceiveDate() != null ? smsLog.getSendReceiveDate().toString() : "");
		logArgs.add(smsLog.getSegmentsCount() != null ? smsLog.getSegmentsCount().toString() : "");
		logArgs.add(smsLog.getDeliveryDate() != null ? smsLog.getDeliveryDate().toString() : "");

		return logArgs;
	}

	@Override
	public List<CampaignAggregationReport> getReportsByCampName(UserTrxInfo userTrxInfo, String campName, int first,
			int max) throws IneligibleAccountException, DBException {
		userTrxInfo.addUserAction(ActionName.VIEW_REPORTS);
		campLogger.info(userTrxInfo.logInfo() + "Getting reports with name: " + campName + ". ");

		campLogger.debug(userTrxInfo.logId() + "Check account eligibility");
		accountManegement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		campLogger.debug(userTrxInfo.logId() + "Account is eligible for action " + userTrxInfo.getUserActions());

		String accountId = userTrxInfo.getUser().getAccountId();
		List<CampaignAggregationReport> campaignAggregationReport = new ArrayList<>();

		campLogger.debug(userTrxInfo.logId() + "Retrieving campaign reports by AccountId and Campaign name.");
		List<CampaignStatsReport> campaignStatsReport = campaignStatsDao.find(accountId, campName, first, max);

		if (campaignStatsReport != null && !campaignStatsReport.isEmpty()) {
			campLogger.debug(userTrxInfo.logId() + "Retrieving Campaigns aggregation reports list.");
			campaignAggregationReport = reportConversionBean.getCampaignAggregationReport(campaignStatsReport,
					userTrxInfo);
		}

		return campaignAggregationReport;
	}

	@Override
	public CampaignAggregationReport getReportByCampId(UserTrxInfo userTrxInfo, String campId)
			throws IneligibleAccountException, DBException, IneligibleAccountException, DBException,
			CampaginReportNotFound {
		userTrxInfo.addUserAction(ActionName.VIEW_REPORTS);
		campLogger.info(userTrxInfo.logInfo() + "Getting report with id: " + campId + ". ");

		campLogger.debug(userTrxInfo.logId() + "Check account eligibility");
		accountManegement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		campLogger.debug(userTrxInfo.logId() + "Account is eligible for action " + userTrxInfo.getUserActions());

		String accountId = userTrxInfo.getUser().getAccountId();
		CampaignAggregationReport campaignAggregationReport = null;

		campLogger.debug(userTrxInfo.logId() + "Retrieving campaign reports by AccountId and Campaign ID.");
		CampaignStatsReport campaignStatsReport = campaignStatsDao.find(accountId, campId);

		if (campaignStatsReport != null) {
			campLogger.debug(userTrxInfo.logId() + "Retrieving Campaign aggregation reports list.");
			campaignAggregationReport = reportConversionBean.getCampaignAggregationReport(campaignStatsReport,
					userTrxInfo);
		}

		return campaignAggregationReport;
	}

	@Override
	public List<CampaignAggregationReport> getReportsWithinDateRange(UserTrxInfo userTrxInfo, Date startDate,
			Date endDate, int first, int max) throws IneligibleAccountException, DBException {
		userTrxInfo.addUserAction(ActionName.VIEW_REPORTS);
		campLogger.info(userTrxInfo.logInfo() + "Getting reports in period between " + startDate + " and " + endDate
				+ ", first=" + first + ", count=" + max);

		campLogger.debug(userTrxInfo.logId() + "Check account eligibility");
		accountManegement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		campLogger.debug(userTrxInfo.logId() + "Account is eligible for action " + userTrxInfo.getUserActions());

		String accountId = userTrxInfo.getUser().getAccountId();
		List<CampaignAggregationReport> campaignAggregationReport = new ArrayList<>();

		campLogger.debug(userTrxInfo.logId() + "Retrieving campaign reports by AccountId and Campaign name.");
		List<CampaignStatsReport> campaignStatsReport = campaignStatsDao.find(accountId, startDate, endDate, first,
				max);

		if (campaignStatsReport != null && !campaignStatsReport.isEmpty()) {
			campLogger.debug(userTrxInfo.logId() + "Retrieving Campaigns aggregation reports list.");
			campaignAggregationReport = reportConversionBean.getCampaignAggregationReport(campaignStatsReport,
					userTrxInfo);
		}

		return campaignAggregationReport;
	}

	@Override
	public List<CampaignAggregationReport> getReportsWithinDateRangeAndCampName(UserTrxInfo userTrxInfo, Date startDate,
			Date endDate, String campName, int first, int max) throws IneligibleAccountException, DBException {
		userTrxInfo.addUserAction(ActionName.VIEW_REPORTS);
		campLogger.info(userTrxInfo.logInfo() + "Getting reports in period between " + startDate + " and " + endDate
				+ ", Campaign name= " + campName + ", first=" + first + ", count=" + max);

		campLogger.debug(userTrxInfo.logId() + "Check account eligibility");
		accountManegement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		campLogger.debug(userTrxInfo.logId() + "Account is eligible for action " + userTrxInfo.getUserActions());

		String accountId = userTrxInfo.getUser().getAccountId();
		List<CampaignAggregationReport> campaignAggregationReport = new ArrayList<>();

		campLogger.debug(userTrxInfo.logId() + "Retrieving campaign reports by AccountId and Campaign name.");
		List<CampaignStatsReport> campaignStatsReport = campaignStatsDao.find(accountId, campName, startDate, endDate,
				first, max);

		if (campaignStatsReport != null && !campaignStatsReport.isEmpty()) {
			campLogger.debug(userTrxInfo.logId() + "Retrieving Campaigns aggregation reports list.");
			campaignAggregationReport = reportConversionBean.getCampaignAggregationReport(campaignStatsReport,
					userTrxInfo);
		}

		return campaignAggregationReport;
	}

	@Override
	public int countAllReports(UserTrxInfo userTrxInfo) throws DBException {
		int result;
		result = campaignStatsDao.count(userTrxInfo.getUser().getAccountId());
		return result;
	}

	@Override
	public int countReportsWithinDateRangeForPDF(UserTrxInfo userTrxInfo, Date startDate, Date endDate)
			throws DBException {
		int result;
		result = campaignStatsDao.count(userTrxInfo.getUser().getAccountId(), startDate, endDate);
		return result;
	}

	@Override
	public int countReportsWithinDateRangeAndCampNameForPDF(UserTrxInfo userTrxInfo, Date startDate, Date endDate,
			String campName) throws DBException {
		int result;
		result = campaignStatsDao.count(userTrxInfo.getUser().getAccountId(), startDate, endDate, campName);
		return result;
	}

	@Override
	public int countReportsWithinDateRangeForReport(UserTrxInfo userTrxInfo, Date startDate, Date endDate)
			throws DBException {
		int result;
		result = campaignStatsDao.count(userTrxInfo.getUser().getAccountId(), startDate, endDate);
		return result;
	}

	@Override
	public int countReportsByCampName(UserTrxInfo userTrxInfo, String campName) throws DBException {
		int result;
		result = campaignStatsDao.count(userTrxInfo.getUser().getAccountId(), campName);
		return result;
	}

	@Override
	public List<SMSReport> getSMSReportsView(UserTrxInfo userTrxInfo, HistoryReportSearch params, int first, int max)
			throws IneligibleAccountException, DBException {
		smsLogger.info(userTrxInfo.logInfo() + params);
		userTrxInfo.addUserAction(ActionName.VIEW_REPORTS);
		smsLogger.debug(userTrxInfo.logId() + "Check account eligibility");
		accountManegement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		smsLogger.debug(userTrxInfo.logId() + "Account is eligible for action " + userTrxInfo.getUserActions());

		Map<String, SMSReport> reportMap = new HashMap<>();
		List<SmsApiDailySmsStats> stats = smsApiStatsDao.getSmsApiDailyStats(userTrxInfo.getUser().getAccountId(),
				params.getSenderName(), params.getDateFrom(), params.getDateTo(), first, max);

		for (SmsApiDailySmsStats stat : stats) {
			String sender = stat.getSmsApiDailySmsStatsPK().getSenderName();
			SMSReport report = reportMap.get(sender);

			if (report == null) {
				report = new SMSReport(sender);
				reportMap.put(sender, report);
			}

			report.incDeliveredSMS(stat.getSmsDelivered());
			report.incDeliveredSegments(stat.getSegmentsDelivered());

			report.incUnDeliveredSMS(stat.getSegmentsDelivered());
			report.incUnDeliveredSegments(stat.getSegmentsNotDelivered());

			report.incFailedSMS(stat.getSmsFailed() + stat.getSmsFailedToSend() + stat.getSmsTimedOut());
			report.incFailedSegments(
					stat.getSegmentsFailed() + stat.getSegmentsFailedToSend() + stat.getSegmentsTimedOut());

			report.incPendingSMS(stat.getSmsSent() + stat.getSmsSubmitted());
			report.incPendingSegments(stat.getSegmentsSent() + stat.getSegmentsSubmitted());

			report.incTotalSMS(stat.getSmsTotal());
			report.incTotalSegments(stat.getSegmentsTotal());

			report.setSentSms(stat.getVfSentSms(), stat.getOgSentSms(), stat.getEtSentSms(), stat.getWeSentSms(),
					stat.getInterSentSms());
			report.setDeliveredSms(stat.getVfDeliveredSms(), stat.getOgDeliveredSms(), stat.getEtDeliveredSms(),
					stat.getWeDeliveredSms(), stat.getInterDeliveredSms());
			report.setNotDeliveredSms(stat.getVfNotDeliveredSms(), stat.getOgNotDeliveredSms(),
					stat.getEtNotDeliveredSms(), stat.getWeNotDeliveredSms(), stat.getInterNotDeliveredSms());

			report.setSentSegments(stat.getVfSentSeg(), stat.getOgSentSeg(), stat.getEtSentSeg(), stat.getWeSentSeg(),
					stat.getInterSentSeg());
			report.setDeliveredSegments(stat.getVfDeliveredSeg(), stat.getOgDeliveredSeg(), stat.getEtDeliveredSeg(),
					stat.getWeDeliveredSeg(), stat.getInterDeliveredSeg());
			report.setNotDeliveredSegments(stat.getVfNotDeliveredSeg(), stat.getOgNotDeliveredSeg(),
					stat.getEtNotDeliveredSeg(), stat.getWeNotDeliveredSeg(), stat.getInterNotDeliveredSeg());

		}

		return new ArrayList<SMSReport>(reportMap.values());
	}

	@Override
	public int countSMSReportsView(UserTrxInfo userTrxInfo, HistoryReportSearch params) throws DBException {
		smsLogger.info(userTrxInfo.logInfo() + params);
		int result = smsApiStatsDao.countSmsApiDailyStats(userTrxInfo.getUser().getAccountId(), params.getSenderName(),
				params.getDateFrom(), params.getDateTo());
		smsLogger.debug(userTrxInfo.logId() + "Found " + result + " record.");

		return result;
	}

	@Override
	public String generateDetailedSMSAPIReportInExcel(UserTrxInfo userTrxInfo, HistoryReportSearch params)
			throws DBException, IOException, SMSAPIViewLogNotFoundException, IneligibleAccountException {
		smsLogger.info(userTrxInfo.logInfo() + params);
		userTrxInfo.addUserAction(ActionName.GENERATE_DETAILED_REPORT);
		smsLogger.debug(userTrxInfo.logId() + "Check account eligibility");
		accountManegement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		smsLogger.debug(userTrxInfo.logId() + "Account is eligible for action " + userTrxInfo.getUserActions());

		smsLogger.info(userTrxInfo.logInfo() + " | Account(" + userTrxInfo.getUser().getAccountId()
				+ ") Generating detailed report file in Excel, " + params);
		OutputStream outFileStream;
		List<SMSLog> logs;
		int startIndex = 0;
		Sheet sh;

		smsLogger.debug(userTrxInfo.logId() + "Getting configs");
		int chunkSize = (Integer) Configs.REPORT_EXPORT_CHUNK_SIZE.getValue();
		int excelWinSize = (Integer) Configs.EXCEL_WINDOW_SIZE.getValue();
		// XLSX max row numbers " can't exceed 1048576 ".
		int sheetMaxRowsNum = ((Integer) Configs.EXCEL_SHEET_MAX_ROWS_NUM.getValue() < 1048576)
				? (Integer) Configs.EXCEL_SHEET_MAX_ROWS_NUM.getValue()
				: 1048576;
		String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String outputPath = (String) Configs.REPORT_EXPORT_PATH.getValue();
		boolean zipCompressed = (Boolean) Configs.REPORT_EXPORT_FILE_COMPRESSED.getValue();

		long logsCount = countSMSReportsView(userTrxInfo, params);
		if (logsCount == 0) {
			throw new SMSAPIViewLogNotFoundException(userTrxInfo.getUser().getAccountId(), params);
		}

		String outFileBaseName = getUniqueFileName(userTrxInfo.getUser().getAccountId());
		String outPathName = basePath + outputPath;
		String outFileToken = null;

		// File variables
		smsLogger.debug(userTrxInfo.logId() + "Export with chunk size=" + chunkSize + ", outPathName: " + outPathName
				+ ", Excel Stream window size: (" + excelWinSize + ")");
		smsLogger.debug(userTrxInfo.logId() + "Logs count=" + logsCount);

		if (logsCount > 0) {
			SXSSFWorkbook wb = new SXSSFWorkbook(excelWinSize);
			Row row;
			Cell cell;
			int rownum = 0;
			int cellNum = 0;
			int iterCount = 1;
			sh = newSMSAPISheetWithHeader(wb);
			rownum++;

			for (iterCount = 1; startIndex < logsCount; startIndex += chunkSize, iterCount++) {
				smsLogger.debug(userTrxInfo.logId() + "Get logs by chunks, index=" + startIndex + ", chunk size="
						+ chunkSize + ", iter no. " + (iterCount));
				logs = smsLogDao.getSmsApiLogs(userTrxInfo.getUser().getAccountId(), params.getSenderName(),
						params.getDateFrom(), params.getDateTo(), startIndex, chunkSize);

				smsLogger.debug(userTrxInfo.logId() + "Write logs to the file, logs count=" + logs.size());

				// Writing the logs to the file
				for (SMSLog smsApiLog : logs) {
					if (rownum % sheetMaxRowsNum == 0) {
						sh = newSMSAPISheetWithHeader(wb);
						rownum++;
					}

					row = sh.createRow(rownum % sheetMaxRowsNum);
					cellNum = 0;
					for (String logArgs : getSMSAPILogArgs(smsApiLog)) {
						cell = row.createCell(cellNum++, Cell.CELL_TYPE_STRING);
						cell.setCellValue(logArgs);
					}
					rownum++;
				}

				smsLogger.debug(userTrxInfo.logId() + "Logs written to the file, count=" + logs.size());
			}

			String outFileName;
			if (zipCompressed) {
				outFileName = outFileBaseName + ".zip";
				outFileStream = new ZipOutputStream(
						new BufferedOutputStream(new FileOutputStream(outPathName + outFileName)));
				ZipEntry ze = new ZipEntry(outFileBaseName + ".xlsx");
				((ZipOutputStream) outFileStream).putNextEntry(ze);
			} else {
				outFileName = outFileBaseName + ".xlsx";
				outFileStream = new BufferedOutputStream(new FileOutputStream(outPathName + outFileName));
			}

			wb.write(outFileStream);
			smsLogger.debug(userTrxInfo.logId() + "Closing the file");
			outFileStream.close();
			smsLogger.debug(userTrxInfo.logId() + "XLSX file is closed, Dispose the workbook");
			wb.dispose();
			smsLogger.debug(userTrxInfo.logId() + "Workbook disposed");

			outFileToken = FileNameUtils.encodeFileToken(outputPath + outFileName);
			smsLogger.info(userTrxInfo.logId() + "Exported campaign logs into XLSX file [" + outFileName
					+ "], logs count=" + logsCount + ", iterCount=" + (iterCount - 1) + ", output file token=\""
					+ outFileToken + "\"");
		} else {
			smsLogger.info(userTrxInfo.logId() + "No logs for this campaign");
			throw new SMSAPIViewLogNotFoundException(userTrxInfo.getUser().getAccountId(), params);
		}

		return outFileToken;
	}

	@Override
	public String generateDetailedCampaignReport(UserTrxInfo userTrxInfo, String campaignId) throws DBException,
			IOException, CampaignNotFoundException, NoLogsForCampaignException, IneligibleAccountException {
		userTrxInfo.addUserAction(ActionName.GENERATE_DETAILED_REPORT);
		smsLogger.debug(userTrxInfo.logId() + "Check account eligibility");
		accountManegement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		smsLogger.debug(userTrxInfo.logId() + "Account is eligible for action " + userTrxInfo.getUserActions());

		campLogger.info(userTrxInfo.logInfo() + "Generating detailed report file, campaignId=" + campaignId);
		CSVBatchFile outFile;
		int startIndex = 0;
		List<SMSLog> logs;

		campLogger.info(userTrxInfo.logId() + "Getting configs");
		int chunkSize = (Integer) Configs.REPORT_EXPORT_CHUNK_SIZE.getValue();
		Charset charset = Charset.forName((String) Configs.REPORT_EXPORT_CHARSET.getValue());
		String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String outputPath = (String) Configs.REPORT_EXPORT_PATH.getValue();
		String delimiter = (String) Configs.REPORT_EXPORT_FILE_DELIM.getValue();
		String quoteChar = (String) Configs.REPORT_EXPORT_FILE_QUOTE_CHAR.getValue();
		boolean zipCompressed = (Boolean) Configs.REPORT_EXPORT_FILE_COMPRESSED.getValue();

		campLogger.info(userTrxInfo.logId() + "Retrieving the campaign with id=" + campaignId);
		Campaign campaign = campaignDao.find(campaignId);

		if (campaign == null) {
			throw new CampaignNotFoundException();
		}

		campLogger.info(userTrxInfo.logId() + "Campaign retrieved from database");
		String outFileBaseName = getUniqueFileName(campaign.getCampaignId());
		String outPathName = basePath + outputPath;
		SMSLogCSV csvLog = new SMSLogCSV();
		String outFileToken = null;

		// File variables
		campLogger.debug(userTrxInfo.logId() + "Export with chunk size=" + chunkSize + ", charset: " + charset
				+ ", outPathName: " + outPathName + ", delimeter: (" + delimiter + "), quoteChar: (" + quoteChar + ")");
		campLogger.debug(userTrxInfo.logId() + "Counting the logs");

		int logsCount = smsLogDao.countCampaignLogs(campaignId);
		campLogger.debug(userTrxInfo.logId() + "Logs count=" + logsCount);

		if (logsCount > 0) {
			campLogger.debug(userTrxInfo.logId() + "logs count=" + logsCount);

			outFile = openDetailedReportCSV(csvLog, outPathName, outFileBaseName, delimiter, quoteChar, charset,
					zipCompressed);

			campLogger.debug(userTrxInfo.logId() + "Opened the file");
			campLogger.debug(userTrxInfo.logId() + "Write CSV record header");
			outFile.writeCSVRecordHeader();

			// Getting the logs by chunks
			int iterCount = 1;
			for (iterCount = 1; startIndex < logsCount; startIndex += chunkSize, iterCount++) {
				campLogger.debug(userTrxInfo.logId() + "Get logs by chunks, index=" + startIndex + ", chunk size="
						+ chunkSize + ", iter no. " + (iterCount));
				logs = smsLogDao.findByCampaignId(campaign.getCampaignId(), startIndex, chunkSize);

				campLogger.debug(userTrxInfo.logId() + "Write logs to the file, logs count=" + logs.size());
				// Writing the logs to the file
				for (SMSLog smsLog : logs) {
					updateSMSLogCSV(campaign, smsLog, csvLog);
					outFile.writeCSVRecord(csvLog);
				}

				campLogger.debug(userTrxInfo.logId() + "Logs written to the file, count=" + logs.size());
			}

			campLogger.debug(userTrxInfo.logId() + "Closing the file");
			outFile.close();
			campLogger.debug(userTrxInfo.logId() + "CSV file is closed");

			outFileToken = FileNameUtils.encodeFileToken(outputPath + outFile.getFileName());
			campLogger.info(userTrxInfo.logId() + "Exported campaign logs into CSV file [" + outFile.getFileName()
					+ "], logs count=" + logsCount + ", iterCount=" + (iterCount - 1) + ", output file token=\""
					+ outFileToken + "\"");
		} else {
			campLogger.info(userTrxInfo.logId() + "No logs for this campaign");
			throw new NoLogsForCampaignException(campaignId);
		}

		return outFileToken;
	}

	@Override
	public String generateDetailedCampaignReportInExcel(UserTrxInfo userTrxInfo, String campaignId) throws DBException,
			IOException, CampaignNotFoundException, NoLogsForCampaignException, IneligibleAccountException {
		userTrxInfo.addUserAction(ActionName.GENERATE_DETAILED_REPORT);
		smsLogger.debug(userTrxInfo.logId() + "Check account eligibility");
		accountManegement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		smsLogger.debug(userTrxInfo.logId() + "Account is eligible for action " + userTrxInfo.getUserActions());

		campLogger.info(userTrxInfo.logInfo() + "Generating detailed report file in Excel, campaignId=" + campaignId);
		int startIndex = 0;
		List<SMSLog> logs;
		Sheet sh;

		campLogger.info(userTrxInfo.logId() + "Getting configs");
		int chunkSize = (Integer) Configs.REPORT_EXPORT_CHUNK_SIZE.getValue();
		int excelWinSize = (Integer) Configs.EXCEL_WINDOW_SIZE.getValue();
		// XLSX max row numbers " can't exceed 1048576 ".
		int sheetMaxRowsNum = ((Integer) Configs.EXCEL_SHEET_MAX_ROWS_NUM.getValue() < 1048576)
				? (Integer) Configs.EXCEL_SHEET_MAX_ROWS_NUM.getValue()
				: 1048576;
		String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String outputPath = (String) Configs.REPORT_EXPORT_PATH.getValue();
		boolean zipCompressed = (Boolean) Configs.REPORT_EXPORT_FILE_COMPRESSED.getValue();

		campLogger.info(userTrxInfo.logId() + "Retrieving the campaign with id=" + campaignId);
		Campaign campaign = campaignDao.find(campaignId);

		if (campaign == null) {
			throw new CampaignNotFoundException();
		}

		campLogger.info(userTrxInfo.logId() + "Campaign retrieved from database");
		OutputStream outFileStream;
		String outFileBaseName = getUniqueFileName(campaign.getCampaignId());
		String outPathName = basePath + outputPath;
		String outFileToken = null;

		// File variables
		campLogger.debug(userTrxInfo.logId() + "Export with chunk size=" + chunkSize + ", outPathName: " + outPathName
				+ ", Excel Stream window size: (" + excelWinSize + ")");
		campLogger.debug(userTrxInfo.logId() + "Counting the logs");
		int logsCount = smsLogDao.countCampaignLogs(campaignId);

		campLogger.debug(userTrxInfo.logId() + "Logs count=" + logsCount);
		if (logsCount > 0) {
			campLogger.debug(userTrxInfo.logId() + "logs count=" + logsCount);

			SXSSFWorkbook wb = new SXSSFWorkbook(excelWinSize);
			Row row;
			Cell cell;
			int rownum = 0;
			int cellNum = 0;
			int iterCount = 1;
			sh = newSheetWithHeader(wb);
			rownum++;

			for (iterCount = 1; startIndex < logsCount; startIndex += chunkSize, iterCount++) {
				campLogger.debug(userTrxInfo.logId() + "Get logs by chunks, index=" + startIndex + ", chunk size="
						+ chunkSize + ", iter no. " + (iterCount));
				logs = smsLogDao.findByCampaignId(campaign.getCampaignId(), startIndex, chunkSize);
				campLogger.debug(userTrxInfo.logId() + "Write logs to the file, logs count=" + logs.size());

				// Writing the logs to the file
				for (SMSLog smsLog : logs) {
					if (rownum % sheetMaxRowsNum == 0) {
						sh = newSheetWithHeader(wb);
						rownum++;
					}
					row = sh.createRow(rownum % sheetMaxRowsNum);
					cellNum = 0;
					for (String logArgs : getLogArgs(campaign, smsLog)) {
						cell = row.createCell(cellNum++, Cell.CELL_TYPE_STRING);
						cell.setCellValue(logArgs);
					}
					rownum++;
				}

				campLogger.debug(userTrxInfo.logId() + "Logs written to the file, count=" + logs.size());
			}

			String outFileName;
			if (zipCompressed) {
				outFileName = outFileBaseName + ".zip";
				outFileStream = new ZipOutputStream(
						new BufferedOutputStream(new FileOutputStream(outPathName + outFileName)));
				ZipEntry ze = new ZipEntry(outFileBaseName + ".xlsx");
				((ZipOutputStream) outFileStream).putNextEntry(ze);
			} else {
				outFileName = outFileBaseName + ".xlsx";
				outFileStream = new BufferedOutputStream(new FileOutputStream(outPathName + outFileName));
			}

			wb.write(outFileStream);
			campLogger.debug(userTrxInfo.logId() + "Closing the file");
			outFileStream.close();
			campLogger.debug(userTrxInfo.logId() + "XLSX file is closed, Dispose the workbook");
			wb.dispose();
			campLogger.debug(userTrxInfo.logId() + "Workbook disposed");

			outFileToken = FileNameUtils.encodeFileToken(outputPath + outFileName);
			campLogger.info(userTrxInfo.logId() + "Exported campaign logs into XLSX file [" + outFileName
					+ "], logs count=" + logsCount + ", iterCount=" + (iterCount - 1) + ", output file token=\""
					+ outFileToken + "\"");
		} else {
			campLogger.info(userTrxInfo.logId() + "No logs for this campaign");
			throw new NoLogsForCampaignException(campaignId);
		}

		return outFileToken;
	}

	@Override
	public String generateDetailedCampaignReportInExcel(AdminTrxInfo adminTrxInfo, String campaignId)
			throws DBException, IOException, CampaignNotFoundException, NoLogsForCampaignException {
		campLogger.info(adminTrxInfo.logInfo() + "Generating detailed report file in Excel, campaignId=" + campaignId);
		int startIndex = 0;
		List<SMSLog> logs;
		Sheet sh;

		campLogger.info(adminTrxInfo.logId() + "Getting configs");
		int chunkSize = (Integer) Configs.REPORT_EXPORT_CHUNK_SIZE.getValue();
		int excelWinSize = (Integer) Configs.EXCEL_WINDOW_SIZE.getValue();
		// XLSX max row numbers " can't exceed 1048576 ".
		int sheetMaxRowsNum = ((Integer) Configs.EXCEL_SHEET_MAX_ROWS_NUM.getValue() < 1048576)
				? (Integer) Configs.EXCEL_SHEET_MAX_ROWS_NUM.getValue()
				: 1048576;
		String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String outputPath = (String) Configs.REPORT_EXPORT_PATH.getValue();
		boolean zipCompressed = (Boolean) Configs.REPORT_EXPORT_FILE_COMPRESSED.getValue();

		campLogger.info(adminTrxInfo.logId() + "Retrieving the campaign with id=" + campaignId);
		Campaign campaign = campaignDao.find(campaignId);

		if (campaign == null) {
			throw new CampaignNotFoundException();
		}

		campLogger.info(adminTrxInfo.logId() + "Campaign retrieved from database");
		String outFileBaseName = getUniqueFileName(campaign.getCampaignId());
		// String outFileBaseName = campaign.getCampaignId();
		String outPathName = basePath + outputPath;
		String outFileToken = null;
		OutputStream outFileStream;

		// File variables
		campLogger.debug(adminTrxInfo.logId() + "Export with chunk size=" + chunkSize + ", outPathName: " + outPathName
				+ ", Excel Stream window size: (" + excelWinSize + ")");
		campLogger.debug(adminTrxInfo.logId() + "Counting the logs");

		int logsCount = smsLogDao.countCampaignLogs(campaignId);
		campLogger.debug(adminTrxInfo.logId() + "Logs count=" + logsCount);

		if (logsCount > 0) {
			campLogger.debug(adminTrxInfo.logId() + "logs count=" + logsCount);

			SXSSFWorkbook wb = new SXSSFWorkbook(excelWinSize);
			Row row;
			Cell cell;
			int rownum = 0;
			int cellNum = 0;
			int iterCount = 1;
			sh = newSheetWithHeader(wb);
			rownum++;

			for (iterCount = 1; startIndex < logsCount; startIndex += chunkSize, iterCount++) {
				campLogger.debug(adminTrxInfo.logId() + "Get logs by chunks, index=" + startIndex + ", chunk size="
						+ chunkSize + ", iter no. " + (iterCount));
				logs = smsLogDao.findByCampaignId(campaign.getCampaignId(), startIndex, chunkSize);
				campLogger.debug(adminTrxInfo.logId() + "Write logs to the file, logs count=" + logs.size());

				// Writing the logs to the file
				for (SMSLog smsLog : logs) {
					if (rownum % sheetMaxRowsNum == 0) {
						sh = newSheetWithHeader(wb);
						rownum++;
					}
					row = sh.createRow(rownum % sheetMaxRowsNum);
					cellNum = 0;
					for (String logArgs : getLogArgs(campaign, smsLog)) {
						cell = row.createCell(cellNum++, Cell.CELL_TYPE_STRING);
						cell.setCellValue(logArgs);
					}
					rownum++;
				}
				campLogger.debug(adminTrxInfo.logId() + "Logs written to the file, count=" + logs.size());
			}

			String outFileName;
			if (zipCompressed) {
				outFileName = outFileBaseName + ".zip";
				outFileStream = new ZipOutputStream(
						new BufferedOutputStream(new FileOutputStream(outPathName + outFileName)));
				ZipEntry ze = new ZipEntry(outFileBaseName + ".xlsx");
				((ZipOutputStream) outFileStream).putNextEntry(ze);
			} else {
				outFileName = outFileBaseName + ".xlsx";
				outFileStream = new BufferedOutputStream(new FileOutputStream(outPathName + outFileName));
			}

			wb.write(outFileStream);
			campLogger.debug(adminTrxInfo.logId() + "Closing the file");
			outFileStream.close();
			campLogger.debug(adminTrxInfo.logId() + "XLSX file is closed, Dispose the workbook");
			wb.dispose();
			campLogger.debug(adminTrxInfo.logId() + "Workbook disposed");

			outFileToken = FileNameUtils.encodeFileToken(outputPath + outFileName);
			campLogger.info(adminTrxInfo.logId() + "Exported campaign logs into XLSX file [" + outFileName
					+ "], logs count=" + logsCount + ", iterCount=" + (iterCount - 1) + ", output file token=\""
					+ outFileToken + "\"");
		} else {
			campLogger.info(adminTrxInfo.logId() + "No logs for this campaign");
			throw new NoLogsForCampaignException(campaignId);
		}

		return outFileToken;
	}

	@Override
	public void offlineGenerateDetailedCampaignReportForAdmin(AdminTrxInfo adminTrxInfo, String campaignId)
			throws DBException {
		Reports report = new Reports();

		report.setAdmin(true);
		report.setCampaign(campaignId);
		report.setTrx(adminTrxInfo.getTrxId());
		report.setUserName(adminTrxInfo.getAdmin().getUsername());
		report.setOwner(adminTrxInfo.getAdmin().getAdminId().toString());
		report.setType(ReportType.CAMPAIGN);

		reportsDao.create(report);
	}

	@Override
	public void offlineGenerateDetailedCampaignReport(UserTrxInfo userTrxInfo, String campaignId) throws DBException {
		Reports report = new Reports();

		report.setAdmin(false);
		report.setCampaign(campaignId);
		report.setTrx(userTrxInfo.getTrxId());
		report.setUserName(userTrxInfo.getUser().getUsername());
		report.setOwner(userTrxInfo.getUser().getAccountId());
		report.setType(ReportType.CAMPAIGN);

		reportsDao.create(report);
	}

	@Override
	public void offlineGenerateDetailedSMSAPIReport(UserTrxInfo userTrxInfo, HistoryReportSearch params)
			throws DBException {
		Reports report = new Reports();

		report.setAdmin(false);
		report.setOwner(userTrxInfo.getUser().getAccountId());
		report.setTrx(userTrxInfo.getTrxId());
		report.setUserName(userTrxInfo.getUser().getUsername());
		report.setFrom(params.getDateFrom());
		report.setTo(params.getDateTo());
		report.setSenderName(params.getSenderName());
		report.setType(ReportType.SMS_API);

		reportsDao.create(report);
	}

	@Override
	public List<Reports> getAllReports(UserTrxInfo userTrxInfo, int start, int count) throws DBException {
		List<Reports> reports = reportsDao.getUserReports(userTrxInfo.getUser().getAccountId(), start, count);

		for (Reports report : reports) {
			if (report.getType() == ReportType.CAMPAIGN) {
				Campaign campaign = campaignDao.find(report.getCampaign());
				report.setCampaignName(campaign.getName());
			}
		}

		return reports;
	}

	@Override
	public int getReportsCount(UserTrxInfo userTrxInfo) throws DBException {
		int count = -1;

		count = reportsDao.countUserReports(userTrxInfo.getUser().getAccountId());

		return count;
	}

	@Override
	public List<Reports> getAllAdminReports(AdminTrxInfo adminTrxInfo, int start, int count) throws DBException {
		List<Reports> reports = reportsDao.getAdminReports(start, count);

		for (Reports report : reports) {
			if (report.getType() == ReportType.CAMPAIGN) {
				Campaign campaign = campaignDao.find(report.getCampaign());
				report.setCampaignName(campaign.getName());
			}
		}

		return reports;
	}

	@Override
	public int getAdminReportsCount(AdminTrxInfo adminTrxInfo) throws DBException {
		int count = -1;

		count = reportsDao.countAdminReports();

		return count;
	}

	@Override
	public void cancelReport(UserTrxInfo userTrxInfo, long reportId) throws DBException, ReportException {
		Reports report = reportsDao.find(reportId);

		if (report != null && report.getOwner() != null
				&& report.getOwner().equals(userTrxInfo.getUser().getAccountId())) {
			ReportStatus status = report.getStatus();
			if (status == ReportStatus.IN_PROGRESS || status == ReportStatus.CANCELED
					|| status == ReportStatus.SUCCESS) {
				//cannot do it throw exception
				throw new ReportException(
						"Report state unchangable, reportId=" + reportId + ", reportStatus=" + status);
			} else if (status == ReportStatus.PENDING || status == ReportStatus.NA || status == ReportStatus.FAILED) {
				//change to canceled
				report.setStatus(ReportStatus.CANCELED);
				reportsDao.edit(report);
			}
		} else {
			throw new ReportNotFound(reportId);
		}
	}

	@Override
	public void cancelAdminReport(AdminTrxInfo adminTrxInfo, long reportId) throws DBException, ReportException {
		Reports report = reportsDao.find(reportId);

		if (report != null && report.getAdmin()) {
			ReportStatus status = report.getStatus();
			if (status == ReportStatus.IN_PROGRESS || status == ReportStatus.CANCELED
					|| status == ReportStatus.SUCCESS) {
				//cannot do it throw exception
				throw new ReportException(
						"Report state unchangable, reportId=" + reportId + ", reportStatus=" + status);
			} else if (status == ReportStatus.PENDING || status == ReportStatus.NA || status == ReportStatus.FAILED) {
				//change to canceled
				report.setStatus(ReportStatus.CANCELED);
				reportsDao.edit(report);
			}
		} else {
			throw new ReportNotFound(reportId);
		}
	}

	@Override
	public SummaryReport getSummaryReport(UserTrxInfo userTrxInfo, List<CampaignAggregationReport> campAggReportList) {
		campLogger.info(userTrxInfo.logInfo() + "Calculate Report summary for " + campAggReportList.size()
				+ " CampaignAggregationReport.");

		SummaryReport summaryReport = new SummaryReport();
		for (CampaignAggregationReport campaignAggregationReport : campAggReportList) {

			summaryReport.incDeliverdSMSCount(campaignAggregationReport.getDeliverdSMSCount());
			summaryReport.incDeliverdSMSSegCount(campaignAggregationReport.getDeliverdSMSSegCount());
			summaryReport.incUnDeliverdSMSCount(campaignAggregationReport.getUnDeliverdSMSCount());
			summaryReport.incUnDeliverdSMSSegCount(campaignAggregationReport.getUnDeliverdSMSSegCount());
			summaryReport.incFailedSMSCount(campaignAggregationReport.getFailedSMSCount());
			summaryReport.incFailedSMSSegCount(campaignAggregationReport.getFailedSMSSegCount());
			summaryReport.incPendingSMSCount(campaignAggregationReport.getPendingSMSCount());
			summaryReport.incPendingSMSSegCount(campaignAggregationReport.getPendingSMSSegCount());
			summaryReport.incTotalSMSCount(campaignAggregationReport.getSmsCount());
			summaryReport.incTotalSMSSegCount(campaignAggregationReport.getSmsSegCount());

			summaryReport.incSmsSent(campaignAggregationReport.getVfSentSms(), campaignAggregationReport.getOgSentSms(),
					campaignAggregationReport.getEtSentSms(), campaignAggregationReport.getWeSentSms(),
					campaignAggregationReport.getInterSentSms());
			summaryReport.incSmsDelivered(campaignAggregationReport.getVfDeliveredSms(),
					campaignAggregationReport.getOgDeliveredSms(), campaignAggregationReport.getEtDeliveredSms(),
					campaignAggregationReport.getWeDeliveredSms(), campaignAggregationReport.getInterDeliveredSms());
			summaryReport.incSmsNotDelivered(campaignAggregationReport.getVfNotDeliveredSms(),
					campaignAggregationReport.getOgNotDeliveredSms(), campaignAggregationReport.getEtNotDeliveredSms(),
					campaignAggregationReport.getWeNotDeliveredSms(),
					campaignAggregationReport.getInterNotDeliveredSms());

			summaryReport.incSegmentsSent(campaignAggregationReport.getVfSentSegments(),
					campaignAggregationReport.getOgSentSegments(), campaignAggregationReport.getEtSentSegments(),
					campaignAggregationReport.getWeSentSegments(), campaignAggregationReport.getInterSentSegments());
			summaryReport.incSegmentsDelivered(campaignAggregationReport.getVfDeliveredSegments(),
					campaignAggregationReport.getOgDeliveredSegments(),
					campaignAggregationReport.getEtDeliveredSegments(),
					campaignAggregationReport.getWeDeliveredSegments(),
					campaignAggregationReport.getInterDeliveredSegments());
			summaryReport.incSegmentsNotDelivered(campaignAggregationReport.getVfNotDeliveredSegments(),
					campaignAggregationReport.getOgNotDeliveredSegments(),
					campaignAggregationReport.getEtNotDeliveredSegments(),
					campaignAggregationReport.getWeNotDeliveredSegments(),
					campaignAggregationReport.getInterNotDeliveredSegments());

		}

		return summaryReport;
	}

	@Override
	public SummaryReport getSMSSummaryReport(UserTrxInfo userTrxInfo, List<SMSReport> smsReportList) {
		campLogger.info(userTrxInfo.logInfo() + "Getting Summary for " + smsReportList.size() + " Report(s).");

		SummaryReport summaryReport = new SummaryReport();
		for (SMSReport smsReport : smsReportList) {
			summaryReport.incDeliverdSMSCount(smsReport.getDeliveredSMS());
			summaryReport.incDeliverdSMSSegCount(smsReport.getDeliveredSMSSegment());
			summaryReport.incUnDeliverdSMSCount(smsReport.getUnDeliveredSMS());
			summaryReport.incUnDeliverdSMSSegCount(smsReport.getUnDeliveredSMSSegment());
			summaryReport.incPendingSMSCount(smsReport.getPendingSMS());
			summaryReport.incPendingSMSSegCount(smsReport.getPendingSMSSegment());
			summaryReport.incFailedSMSCount(smsReport.getFailedSMS());
			summaryReport.incFailedSMSSegCount(smsReport.getFailedSMSSegment());
			summaryReport.incTotalSMSCount(smsReport.getTotalSMS());
			summaryReport.incTotalSMSSegCount(smsReport.getTotalSMSSegment());

			summaryReport.incSmsSent(smsReport.getVfSentSms(), smsReport.getOgSentSms(), smsReport.getEtSentSms(),
					smsReport.getWeSentSms(), smsReport.getInterSentSms());
			summaryReport.incSmsDelivered(smsReport.getVfDeliveredSms(), smsReport.getOgDeliveredSms(),
					smsReport.getEtDeliveredSms(), smsReport.getWeDeliveredSms(), smsReport.getInterDeliveredSms());
			summaryReport.incSmsNotDelivered(smsReport.getVfNotDeliveredSms(), smsReport.getOgNotDeliveredSms(),
					smsReport.getEtNotDeliveredSms(), smsReport.getWeNotDeliveredSms(),
					smsReport.getInterNotDeliveredSms());

			summaryReport.incSegmentsSent(smsReport.getVfSentSegments(), smsReport.getOgSentSegments(),
					smsReport.getEtSentSegments(), smsReport.getWeSentSegments(), smsReport.getInterSentSegments());
			summaryReport.incSegmentsDelivered(smsReport.getVfDeliveredSegments(), smsReport.getOgDeliveredSegments(),
					smsReport.getEtDeliveredSegments(), smsReport.getWeDeliveredSegments(),
					smsReport.getInterDeliveredSegments());
			summaryReport.incSegmentsNotDelivered(smsReport.getVfNotDeliveredSegments(),
					smsReport.getOgNotDeliveredSegments(), smsReport.getEtNotDeliveredSegments(),
					smsReport.getWeNotDeliveredSegments(), smsReport.getInterNotDeliveredSegments());
		}

		return summaryReport;
	}
}
