package com.edafa.web2sms.core.cdr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.csv.batchfile.CSVBatchFile;
import com.edafa.csv.record.CSVRecord;
import com.edafa.csv.record.DefaultCSVRecord;
import com.edafa.csv.record.Field;
import com.edafa.jee.scheduler.api.ScheduledTask;
import com.edafa.jee.scheduler.model.ScheduledTaskFrequency;
import com.edafa.jee.scheduler.model.ScheduledTaskStatus;
import com.edafa.smsgw.smshandler.exceptions.InvalidAddressFormattingException;
import com.edafa.smsgw.smshandler.exceptions.InvalidMSISDNFormatException;
import com.edafa.smsgw.smshandler.sms.MsisdnFormat;
import com.edafa.smsgw.smshandler.sms.SMSUtils;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestsArchDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSLogDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;
import com.edafa.web2sms.dalayer.model.SMSStatus;
import com.edafa.web2sms.dalayer.pojo.AggCDR;
import com.edafa.web2sms.dalayer.pojo.ProvisioningEvent;
import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Stateless
public class AggCDRTask implements AggCDRTaskRemote {

	private static final long serialVersionUID = -8699378988663138070L;

	Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());
	Logger aggCdrLogger = LogManager.getLogger(LoggersEnum.AGG_CDR.name());

	DateFormat cdrDf = new SimpleDateFormat((String) Configs.AGG_CDR_TIMESTAMP_FORMAT.getValue());
	DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS z");

	@EJB
	SMSLogDaoLocal smsLogDao;

	@EJB
	SMSStatusDaoLocal SMSStatusDao;

	@EJB
	ProvRequestsArchDaoLocal provRequestsArchDao;

	@EJB
	ProvRequestStatusDaoLocal provRequestStatusDao;

	@EJB
	AppErrorManagerAdapter appErrorManagerAdapter;

	// Configs
	int cdrChunkSize;
	List<String> onNetPrefixes;
	List<String> mobNetPrefixes;
	List<String> etiNetPrefixes;
	List<String> masrNetPrefixes;

	Map<String, List<Date>> provisionEventHistory;
	String onNetStr;
	String mobNetStr;
	String etiNetStr;
	String masrNetStr;
	// String otherNetStr;
	String delimiter;
	String filePath;
	String fileExtension;
	boolean eofNewLine;
	boolean cdrFileOpened = false;

	// Timestamps
	Calendar startTimestamp;
	Calendar endTimestamp;

	// CSV
	CSVRecord csvRecord;
	CSVBatchFile batchFile;

	int billingMSISDNSeqNum;
	int dummy60202SeqNum;
	int companyIdSeqNum;
	int senderSeqNum;
	int startTimeSeqNum;
	int msisdnTypeSeqNum;
	int dummy13SeqNum;
	int aggSMSSegCountSeqNum;

	// Fields
	Field billingMSISDN = new Field("Sender_MSISDN", 0);
	Field dummy60202 = new Field("60202", 1, "60202");
	Field companyId = new Field("company_ID", 2);
	Field sender = new Field("Sender_Name", 3);
	Field startTime = new Field("Start_time", 4);
	Field msisdnType = new Field("B_Flag", 5);
	Field dummy13 = new Field("dummy", 13, "1");
	Field aggSMSSegCount = new Field("Aggregate_count", 15);

	void updateSeqNums() {
		billingMSISDNSeqNum = (Integer) Configs.BILLNG_MSISDN_SEQ_NUM.getValue();
		dummy60202SeqNum = (Integer) Configs.DUMMY_6020_SEQ_NUM.getValue();
		companyIdSeqNum = (Integer) Configs.COMPANY_ID_SEQ_NUM.getValue();
		senderSeqNum = (Integer) Configs.SENDER_SEQ_NUM.getValue();
		startTimeSeqNum = (Integer) Configs.START_TIME_SEQ_NUM.getValue();
		msisdnTypeSeqNum = (Integer) Configs.MSISDN_TYPE_SEQ_NUM.getValue();
		dummy13SeqNum = (Integer) Configs.DUMMY_13_SEQ_NUM.getValue();
		aggSMSSegCountSeqNum = (Integer) Configs.AGG_SMS_SEG_COUNT_SEQ_NUM.getValue();
	}

	void updateField() {
		updateSeqNums();
		billingMSISDN.setFieldSeq(billingMSISDNSeqNum);
		dummy60202.setFieldSeq(dummy60202SeqNum);
		companyId.setFieldSeq(companyIdSeqNum);
		sender.setFieldSeq(senderSeqNum);
		startTime.setFieldSeq(startTimeSeqNum);
		msisdnType.setFieldSeq(msisdnTypeSeqNum);
		dummy13.setFieldSeq(dummy13SeqNum);
		aggSMSSegCount.setFieldSeq(aggSMSSegCountSeqNum);
	}

	void updateConfigs() {
		aggCdrLogger.info("Updating configuration values");

		cdrChunkSize = (Integer) Configs.AGG_CDR_CHUNK_SIZE.getValue();
		aggCdrLogger.debug("AGG_CDR_CHUNK_SIZE=" + cdrChunkSize);

		onNetPrefixes = (List<String>) Configs.AGG_CDR_ON_NET_PREFIX.getValue();
		aggCdrLogger.debug("AGG_CDR_ON_NET_PREFIX=" + onNetPrefixes);

		mobNetPrefixes = (List<String>) Configs.AGG_CDR_MOB_NET_PREFIX.getValue();
		aggCdrLogger.debug("AGG_CDR_OFF_NET_PREFIX=" + mobNetPrefixes);

		etiNetPrefixes = (List<String>) Configs.AGG_CDR_ETI_NET_PREFIX.getValue();
		aggCdrLogger.debug("AGG_CDR_ETI_NET_PREFIX=" + etiNetPrefixes);

		masrNetPrefixes = (List<String>) Configs.AGG_CDR_MASR_NET_PREFIX.getValue();
		aggCdrLogger.debug("AGG_CDR_MASR_NET_PREFIX=" + masrNetPrefixes);

		onNetStr = (String) Configs.AGG_CDR_ON_NET_STR.getValue();
		aggCdrLogger.debug("AGG_CDR_ON_NET_STR=" + onNetStr);

		mobNetStr = (String) Configs.AGG_CDR_MOB_NET_STR.getValue();
		aggCdrLogger.debug("AGG_CDR_MOB_NET_STR=" + mobNetStr);

		etiNetStr = (String) Configs.AGG_CDR_ETI_NET_STR.getValue();
		aggCdrLogger.debug("AGG_CDR_ETI_NET_STR=" + etiNetStr);

		masrNetStr = (String) Configs.AGG_CDR_MASR_NET_STR.getValue();
		aggCdrLogger.debug("AGG_CDR_MASR_NET_STR=" + masrNetStr);

		delimiter = (String) Configs.AGG_CDR_DELIMITER.getValue();
		aggCdrLogger.debug("AGG_CDR_DELIMITER=" + delimiter);

		eofNewLine = (boolean) Configs.AGG_CDR_EOF_NEW_LINE.getValue();
		aggCdrLogger.debug("AGG_CDR_EOF_NEW_LINE=" + eofNewLine);

		filePath = AppSettings.BaseDir.getEnvEntryValue() + (String) Configs.AGG_CDR_PATH.getValue();
		aggCdrLogger.debug("File path=" + filePath);

		fileExtension = (String) Configs.AGG_CDR_EXTENTION.getValue();

	}

	@Override
	public ScheduledTaskStatus execute(ScheduledTask task) {
		appLogger.info("Starting Aggregated CDR task");
		aggCdrLogger.info("Starting Aggregated CDR task");
		ScheduledTaskStatus finalStatus = ScheduledTaskStatus.SUCCESS;
		Exception exc = null;
		int calUnit = getCalenderUnit(task.getSchedulingPeriodicalUnit());
		updateConfigs();

		Date lastStart = task.getLastStartTime();
		Calendar thisMinTimestamp = Calendar.getInstance();
		thisMinTimestamp.clear(Calendar.SECOND);
		thisMinTimestamp.clear(Calendar.MILLISECOND);

		try {
			if (lastStart != null) {
				aggCdrLogger.info("Last started timestamp=" + df.format(lastStart));
				Calendar lastStartTimestamp = Calendar.getInstance();
				lastStartTimestamp.setTime(lastStart);
				lastStartTimestamp.clear(Calendar.SECOND);
				lastStartTimestamp.clear(Calendar.MILLISECOND);
				startTimestamp = (Calendar) lastStartTimestamp.clone();
			} else {
				startTimestamp = (Calendar) thisMinTimestamp.clone();
				startTimestamp.add(calUnit, -task.getSchedulingPeriod());
			}

			aggCdrLogger.info("startTimestamp.compareTo(thisMinTimestamp)=" + startTimestamp.compareTo(thisMinTimestamp)
					+ ", startTimestamp:" + startTimestamp + ", thisMinTimestamp" + thisMinTimestamp);
			while (startTimestamp.compareTo(thisMinTimestamp) < 0) {
				endTimestamp = (Calendar) startTimestamp.clone();
				endTimestamp.add(calUnit, task.getSchedulingPeriod());

				fillProvisionEventHistory();
				handleCDRs();

				startTimestamp.add(calUnit, task.getSchedulingPeriod());
			}

			appLogger.info("Finshed aggregated CDR task, status: " + finalStatus);
			aggCdrLogger.info("Finshed aggregated CDR task, status: " + finalStatus);
		} catch (DBException e) {
			aggCdrLogger.error(e.getMessage());
			appLogger.error("Agg CDR DBException:" + e.getMessage(), e);
			finalStatus = ScheduledTaskStatus.FAILED;
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

		} catch (IOException e) {
			aggCdrLogger.error(e.getMessage());
			appLogger.error("IOException " + e.getMessage(), e);
			finalStatus = ScheduledTaskStatus.FAILED;
			reportAppError(AppErrors.IO_ERROR, "IOError in write agg CDR");

		} catch (Exception e) {
			aggCdrLogger.error(e.getMessage());
			appLogger.error("Unexpected Exception " + e.getMessage(), e);
			finalStatus = ScheduledTaskStatus.FAILED;
			reportAppError(AppErrors.GENERAL_ERROR, "failed to handle request \"generating Agg CDR\". ");
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

		aggCdrLogger.trace(
				"Agg CDR task is scheduled as " + schedulePeriodicalUnit + ", equivalent calender unit=" + result);
		return result;
	}

	private void handleCDRs() throws IOException, DBException, Exception {

		appLogger.info("Start handling aggregated CDR");
		try {

			aggCdrLogger.debug("Generating new aggregated CDR, start timestamp=" + df.format(startTimestamp.getTime())
					+ ", endTimestamp=" + df.format(endTimestamp.getTime()) + " (exclusive)");

			aggCdrLogger.debug("Preparing CSV record");
			prepareCSVRecord();

			List<String> customAccountCDRs = new ArrayList<String>(provisionEventHistory.keySet());
			aggCdrLogger.trace("customAccountCDRs: " + customAccountCDRs);
			if (!customAccountCDRs.isEmpty()) {
				aggCdrLogger.debug("Getting CDRs without following accountId: " + customAccountCDRs.toString());
			} else {
				aggCdrLogger.debug("Getting all CDRs");
			}

			// Start time is the time field in generated cdr.
			startTime.setValue(cdrDf.format(startTimestamp.getTime()));
			generateCDRs(startTimestamp.getTime(), endTimestamp.getTime(), onNetPrefixes, mobNetPrefixes,
					etiNetPrefixes, masrNetPrefixes, onNetStr, mobNetStr, etiNetStr, masrNetStr, customAccountCDRs,
					null);

			/*
			 * Generating CDR record depending on provisioning events
			 * "custom CDRs records"
			 */
			aggCdrLogger.trace("(provisionEventHistory != null && !provisionEventHistory.isEmpty()) is :"
					+ (provisionEventHistory != null && !provisionEventHistory.isEmpty()));

			if (provisionEventHistory != null && !provisionEventHistory.isEmpty()) {

				for (Iterator<Entry<String, List<Date>>> it = provisionEventHistory.entrySet().iterator(); it
						.hasNext();) {
					aggCdrLogger.trace("hasNext: " + provisionEventHistory.entrySet().iterator().hasNext());
					Entry<String, List<Date>> record = it.next();
					String accountId = record.getKey();
					List<Date> eventDate = new ArrayList<>(record.getValue());
					Date tempStartTimestamp;
					// Date tempEndTimestamp;

					aggCdrLogger.trace("record: " + record);
					tempStartTimestamp = startTimestamp.getTime(); // Initial
					for (Date eventTimestamp : eventDate) {

						aggCdrLogger.debug("Generate CDRs within StartTimestamp: " + tempStartTimestamp
								+ ", tempEndTimestamp:" + eventTimestamp);
						startTime.setValue(cdrDf.format(tempStartTimestamp));
						generateCDRs(tempStartTimestamp, eventTimestamp, onNetPrefixes, mobNetPrefixes, etiNetPrefixes,
								masrNetPrefixes, onNetStr, mobNetStr, etiNetStr, masrNetStr, null, accountId);

						tempStartTimestamp = eventTimestamp;

					}
					startTime.setValue(cdrDf.format(tempStartTimestamp));
					generateCDRs(tempStartTimestamp, endTimestamp.getTime(), onNetPrefixes, mobNetPrefixes,
							etiNetPrefixes, masrNetPrefixes, onNetStr, mobNetStr, etiNetStr, masrNetStr, null,
							accountId);
				}
			}
			aggCdrLogger.trace("Boolean cdrFileOpened: " + cdrFileOpened);
			if (cdrFileOpened) {
				aggCdrLogger.debug("Closing the file");
				closeCDRFile();
				aggCdrLogger.info("All CDRs written to the file, CDR file closed");
			} else {
				aggCdrLogger.info("There are no CDRs for this period");

			}
		} catch (IOException e) {
			appLogger.error("IOException while handling CDR ", e);
			aggCdrLogger.error("IOException while handling CDR ", e);
			throw e;
		} catch (DBException e) {
			appLogger.error("Database error while handling CDR ", e);
			aggCdrLogger.error("Database error while handling CDR ", e);
			throw e;
		} catch (Exception e) {
			appLogger.error("Error while handling CDR ", e);
			aggCdrLogger.error("Error while handling CDR ", e);
			throw e;
		}

	}

	private void generateCDRs(Date startTimestamp, Date endTimestamp, List<String> onNetPrefixes,
			List<String> mobNetPrefixes, List<String> etiNetPrefixes, List<String> masrNetPrefixes, String onNetStr,
			String mobNetStr, String etiNetStr, String masrNetStr, List<String> customAccountCDRs, String accountId)
			throws IOException, DBException {
		int startIndex = 0;
		int writtenCDRCount = 0;
		int cdrsCount = 0;

		List<SMSStatus> smsStatuses = new ArrayList<>();

		smsStatuses.add(SMSStatusDao.getCachedObjectByName(SMSStatusName.SENT));
		smsStatuses.add(SMSStatusDao.getCachedObjectByName(SMSStatusName.DELIVERED));
		smsStatuses.add(SMSStatusDao.getCachedObjectByName(SMSStatusName.NOT_DELIVERED));

		if (accountId == null) {
			aggCdrLogger.info("countAggCDRs is calling with customAccountCDRs: " + customAccountCDRs);
			cdrsCount = smsLogDao.countAggCDRs(startTimestamp, endTimestamp, onNetPrefixes, mobNetPrefixes,
					etiNetPrefixes, masrNetPrefixes, onNetStr, mobNetStr, etiNetStr, masrNetStr, customAccountCDRs,
					smsStatuses);
			aggCdrLogger.debug("Total CDRs count=" + cdrsCount);
		} else if (customAccountCDRs == null) {
			aggCdrLogger.info("countAggCDRsByAccountId is calling with accountId: " + accountId);
			cdrsCount = smsLogDao.countAggCDRsByAccountId(startTimestamp, endTimestamp, onNetPrefixes, mobNetPrefixes,
					etiNetPrefixes, masrNetPrefixes, onNetStr, mobNetStr, etiNetStr, masrNetStr, accountId,
					smsStatuses);
			aggCdrLogger.debug("Total CDRs for account (" + accountId + ") count=" + cdrsCount);
		}

		if (cdrsCount > 0) {
			ensureCDRFileOpen();

			int iterCount = 1;
			List<AggCDR> cdrs = null;
			for (startIndex = 0; startIndex < cdrsCount; startIndex += cdrChunkSize, iterCount++) {
				aggCdrLogger.debug("Getting the logs by chunks, index=" + startIndex + ", chunk size=" + cdrChunkSize
						+ ", iter no. " + (iterCount));
				if (accountId == null) {
					aggCdrLogger.trace("findAggCDRs is calling with parameter:(startTimestamp:" + startTimestamp
							+ ", endTimestamp:" + endTimestamp + ", customAccountCDRs:" + customAccountCDRs
							+ ", startIndex:" + startIndex + ", cdrChunkSize:" + cdrChunkSize + ")");
					cdrs = smsLogDao.findAggCDRs(startTimestamp, endTimestamp, onNetPrefixes, mobNetPrefixes,
							etiNetPrefixes, masrNetPrefixes, onNetStr, mobNetStr, etiNetStr, masrNetStr,
							customAccountCDRs, smsStatuses, startIndex, cdrChunkSize);
					aggCdrLogger.trace("cdrs:" + cdrs);
				} else if (customAccountCDRs == null) {
					aggCdrLogger.trace("findAggCDRsByAccountId is calling with parameter:(startTimestamp:"
							+ startTimestamp + ", endTimestamp:" + endTimestamp + ", accountId:" + accountId
							+ ", startIndex:" + startIndex + ", cdrChunkSize:" + cdrChunkSize + ")");
					cdrs = smsLogDao.findAggCDRsByAccountId(startTimestamp, endTimestamp, onNetPrefixes, mobNetPrefixes,
							etiNetPrefixes, masrNetPrefixes, onNetStr, mobNetStr, etiNetStr, masrNetStr, accountId,
							smsStatuses, startIndex, cdrChunkSize);
					aggCdrLogger.trace("cdrs:" + cdrs);
				}

				writeCDRs(cdrs);
				writtenCDRCount += cdrs.size();

				aggCdrLogger.info(
						"CDRs written to the file, total count=" + cdrsCount + ", written count=" + writtenCDRCount);

			}
		}
	}

	private void ensureCDRFileOpen() throws IOException {
		if (!cdrFileOpened) {
			openCDRFile();
			cdrFileOpened = true;
		}
	}

	private void openCDRFile() throws IOException {
		aggCdrLogger.debug("Opening CDR CSV file");
		DateFormat df = new SimpleDateFormat((String) Configs.AGG_CDR_FILENAME_TIMESTAMP_FORMAT.getValue());
		String fileName = (String) Configs.AGG_CDR_FILENAME_PREFIX.getValue() + df.format(startTimestamp.getTime());
		File file = new File(filePath + fileName);
		Charset charset = StandardCharsets.UTF_8;
		aggCdrLogger.info("Opening CDR CSV file with paramerters: fileName=" + fileName + ", delimiter=\"" + delimiter
				+ "\", charset=" + charset);
		batchFile = new CSVBatchFile(csvRecord, file, delimiter, "", charset, StandardOpenOption.CREATE);
		aggCdrLogger.debug("CDR file opened");
	}

	private void prepareCSVRecord() {
		csvRecord = new DefaultCSVRecord();
		DefaultCSVRecord csvRecord1 = (DefaultCSVRecord) csvRecord;
		// TODO: should update fields seq
		updateField();
		csvRecord1.registerField(billingMSISDN);
		csvRecord1.registerField(dummy60202);
		csvRecord1.registerField(companyId);
		csvRecord1.registerField(sender);
		csvRecord1.registerField(startTime);
		csvRecord1.registerField(msisdnType);
		csvRecord1.registerField(dummy13);
		csvRecord1.registerField(aggSMSSegCount);
	}

	void updateCSVValues(AggCDR cdr) {
		try {
			String formattedMSISDN = SMSUtils.formatAddress(cdr.getBillingMSISDN(), MsisdnFormat.INTER_CC_LOCAL);
			billingMSISDN.setValue(formattedMSISDN);
		} catch (InvalidMSISDNFormatException e) {
			billingMSISDN.setValue(cdr.getBillingMSISDN());
		} catch (InvalidAddressFormattingException e) {
			billingMSISDN.setValue(cdr.getBillingMSISDN());
		}
		companyId.setValue(cdr.getCompanyId());
		sender.setValue(cdr.getSender());
		msisdnType.setValue(cdr.getMsisdnType());
		aggSMSSegCount.setValue(cdr.getAggSMSSegCount().toString() + ";");
	}

	void writeCDRs(List<AggCDR> cdrs) throws IOException {
		aggCdrLogger.debug("Writing CDRs to the file, count=" + cdrs.size());
		for (AggCDR aggCDR : cdrs) {
			updateCSVValues(aggCDR);
			batchFile.writeCSVRecord(csvRecord);
		}
		aggCdrLogger.debug("CDRs written to the file");
	}

	void writeNewLine() throws IOException {
		batchFile.getCsvRandomAccessFile().newLine();
	}

	void closeCDRFile() throws IOException {
		if (eofNewLine) {
			writeNewLine();
		}
		Path filePath = batchFile.getPath();
		batchFile.close();
		cdrFileOpened = false;
		Files.move(filePath, filePath.resolveSibling(filePath.getFileName() + fileExtension));
	}

	void fillProvisionEventHistory() {

		provisionEventHistory = new HashMap<String, List<Date>>();

		aggCdrLogger.debug("filling ProvisionEventHistory");

		ProvRequestStatus status = provRequestStatusDao.getCachedObjectByName(ProvReqStatusName.SUCCESS);
		try {
			aggCdrLogger.debug("searching for ProvisioningEvent within the time and status: " + status);
			List<ProvisioningEvent> provEvents = provRequestsArchDao
					.findProvRequestByDateAndStatus(startTimestamp.getTime(), endTimestamp.getTime(), status);

			aggCdrLogger.debug("Found (" + provEvents.size() + ") ProvisioningEvent");
			for (ProvisioningEvent provisioningEvent : provEvents) {
				if (provisionEventHistory.containsKey(provisioningEvent.getAccountId())) {
					provisionEventHistory.get(provisioningEvent.getAccountId()).add(provisioningEvent.getEventDate());
				} else {
					List<Date> value = new ArrayList<Date>();
					value.add(provisioningEvent.getEventDate());

					provisionEventHistory.put(provisioningEvent.getAccountId(), value);
				}
			}
			if (!provisionEventHistory.isEmpty()) {
				aggCdrLogger.trace("provisionEventHistory: " + provisionEventHistory);
			}
		} catch (DBException e) {
			appLogger.error("Database error while handling CDR ", e);
			aggCdrLogger.error("Database error while handling CDR ", e);
		}
	}

	private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, ErrorsSource.CAMPAIGN_MANAGEMENT_SERVICE.name(), msg);
	}
}
