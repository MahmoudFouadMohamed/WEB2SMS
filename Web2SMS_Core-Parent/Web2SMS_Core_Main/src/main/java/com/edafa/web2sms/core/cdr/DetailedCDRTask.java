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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.csv.batchfile.CSVBatchFile;
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
import com.edafa.web2sms.dalayer.dao.interfaces.SMSLogDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.SMSLog;
import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Stateless
public class DetailedCDRTask implements DetailedCDRTaskRemote {

	private static final long serialVersionUID = -6040165377126324551L;

	Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());
	Logger cdrLogger = LogManager.getLogger(LoggersEnum.DETAILED_CDR.name());

	// DateFormat cdrDf = new SimpleDateFormat((String)
	// Configs.DETAILED_CDR_TIMESTAMP_FORMAT.getValue());
	DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS z");

	@EJB
	SMSLogDaoLocal smsLogDao;

	@EJB
	AppErrorManagerAdapter appErrorManagerAdapter;

	// Configs
	int cdrChunkSize;
	String delimiter;
	String filePath;
	String fileExtension;
	boolean eofNewLine;

	// Timestamps
	Calendar startTimestamp;
	Calendar endTimestamp;

	// CSV
	SMSLogCSV csvRecord = new SMSLogCSV();
	CSVBatchFile batchFile;

	int campaignNameSeqNum;
	int billingMSISDNSeqNum;
	int receiverSeqNum;
	int senderSeqNum;
	int processingDateSeqNum;
	int statusSeqNum;
	int segCountSeqNum;

	void updateSeqNums() {
		campaignNameSeqNum = (Integer) Configs.CAMPAIGN_NAME_SEQ_NUM.getValue();
		billingMSISDNSeqNum = (Integer) Configs.DETAILED_BILLING_MSISDN_SEQ_NUM.getValue();
		receiverSeqNum = (Integer) Configs.RECEIVER_ID_SEQ_NUM.getValue();
		senderSeqNum = (Integer) Configs.DETAILED_SENDER_SEQ_NUM.getValue();
		processingDateSeqNum = (Integer) Configs.PROCESSING_DATE_SEQ_NUM.getValue();
		statusSeqNum = (Integer) Configs.STATUS_SEQ_NUM.getValue();
		segCountSeqNum = (Integer) Configs.SEG_COUNT_SEQ_NUM.getValue();
	}

	// Fields

	void updateConfigs() {
		cdrLogger.info("Updating configuration values");

		cdrChunkSize = (Integer) Configs.DETAILED_CDR_CHUNK_SIZE.getValue();
		cdrLogger.debug("DETAILED_CDR_CHUNK_SIZE=" + cdrChunkSize);

		delimiter = (String) Configs.DETAILED_CDR_DELIMITER.getValue();
		cdrLogger.debug("DETAILED_CDR_DELIMITER=" + delimiter);

		eofNewLine = (boolean) Configs.DETAILED_CDR_EOF_NEW_LINE.getValue();
		cdrLogger.debug("DETAILED_CDR_EOF_NEW_LINE=" + eofNewLine);

		filePath = AppSettings.BaseDir.getEnvEntryValue() + (String) Configs.DETAILED_CDR_PATH.getValue();
		cdrLogger.debug("File path=" + filePath);

		fileExtension = (String) Configs.DETAILED_CDR_EXTENTION.getValue();

	}

	@Override
	public ScheduledTaskStatus execute(ScheduledTask task) {
		appLogger.info("Starting Detailed CDR task");
		cdrLogger.info("Starting Detailed CDR task");
		ScheduledTaskStatus finalStatus = ScheduledTaskStatus.SUCCESS;
		int calUnit = getCalenderUnit(task.getSchedulingPeriodicalUnit());
		updateConfigs();

		Date lastStart = task.getLastStartTime();
		Calendar thisMinTimestamp = Calendar.getInstance();
		thisMinTimestamp.clear(Calendar.SECOND);
		thisMinTimestamp.clear(Calendar.MILLISECOND);
		try {
			if (lastStart != null) {
				cdrLogger.info("Last started timestamp=" + df.format(lastStart));
				Calendar lastStartTimestamp = Calendar.getInstance();
				lastStartTimestamp.setTime(lastStart);
				lastStartTimestamp.clear(Calendar.SECOND);
				lastStartTimestamp.clear(Calendar.MILLISECOND);
				startTimestamp = (Calendar) lastStartTimestamp.clone();
			} else {
				startTimestamp = (Calendar) thisMinTimestamp.clone();
				startTimestamp.add(calUnit, -task.getSchedulingPeriod());
			}

			while (startTimestamp.compareTo(thisMinTimestamp) < 0) {
				endTimestamp = (Calendar) startTimestamp.clone();
				endTimestamp.add(calUnit, task.getSchedulingPeriod());

				generateCDR();

				startTimestamp.add(calUnit, task.getSchedulingPeriod());
			}


		} catch (DBException e) {
			cdrLogger.error(e.getMessage());
			appLogger.error("Agg CDR DBException:" + e.getMessage(), e);
			finalStatus = ScheduledTaskStatus.FAILED;
			if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
				reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
			} else {
				reportAppError(AppErrors.DATABASE_ERROR, "DB error");
			}

		} catch (IOException e) {
			cdrLogger.error(e.getMessage());
			appLogger.error("IOException " + e.getMessage(), e);
			finalStatus = ScheduledTaskStatus.FAILED;
			reportAppError(AppErrors.IO_ERROR, "Error in read/write agg CDR");

		} catch (Exception e) {
			cdrLogger.error(e.getMessage());
			appLogger.error("Unexpected Exception " + e.getMessage(), e);
			finalStatus = ScheduledTaskStatus.FAILED;
			reportAppError(AppErrors.GENERAL_ERROR, "failed to handle request \"generating Agg CDR\". ");
		}

		clear();

		appLogger.info("Finished Detailed CDR task, status: " + finalStatus);
		cdrLogger.info("Finished Detailed CDR task, status: " + finalStatus);

		return finalStatus;
	}

	private void clear() {
		startTimestamp = endTimestamp = null;
		csvRecord = new SMSLogCSV();
		batchFile = null;
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

		cdrLogger.trace(
				"Detailed CDR task is scheduled as " + schedulePeriodicalUnit + ", equivalent calender unit=" + result);
		return result;
	}

	private void generateCDR() throws Exception {
		appLogger.info("Generating new detailed CDR");
		int writtenCDRCount = 0;
		try {
			// startTime.setValue(cdrDf.format(startTimestamp.getTime()));
			cdrLogger.info("Generating new detailed CDR, start timestamp=" + df.format(startTimestamp.getTime())
					+ ", endTimestamp=" + df.format(endTimestamp.getTime()) + " (exclusive)");

			cdrLogger.debug("Getting CDRs count");
			int startIndex = 0;
			int cdrsCount = smsLogDao.countSMSLogs(startTimestamp.getTime(), endTimestamp.getTime());

			if (cdrsCount > 0) {
				cdrLogger.info("Total CDRs for period count=" + cdrsCount);
				cdrLogger.debug("Preparing CSV record");
				prepareSMSLogCSVRecord();

				cdrLogger.debug("Opening CSV record");
				openCDRFile();

				int iterCount = 1;
				List<SMSLog> cdrs = null;
				for (startIndex = 0; startIndex < cdrsCount; startIndex += cdrChunkSize, iterCount++) {
					cdrLogger.debug("Getting the logs by chunks, index=" + startIndex + ", chunk size=" + cdrChunkSize
							+ ", iter no. " + (iterCount));
					cdrs = smsLogDao.findSMSLogs(startTimestamp.getTime(), endTimestamp.getTime(), startIndex,
							cdrChunkSize);
					writeCDRs(cdrs);
					writtenCDRCount += cdrs.size();
				}

				if (eofNewLine) {
					writeNewLine();
				}

				cdrLogger.debug("Closing the file");
				closeCDRFile();
				cdrLogger.debug("All CDRs written to the file, total count=" + cdrsCount + ", written count="
						+ writtenCDRCount + " CDR file closed");
			} else {
				cdrLogger.info("There are no CDRs for this period");
			}
		} catch (IOException e) {
			appLogger.error("IOException while handling CDR ", e);
			cdrLogger.error("IOException while handling CDR ", e);
			throw e;
		} catch (DBException e) {
			appLogger.error("Database error while handling CDR ", e);
			cdrLogger.error("Database error while handling CDR ", e);
			throw e;
		} catch (Exception e) {
			appLogger.error("Error while handling CDR ", e);
			cdrLogger.error("Error while handling CDR ", e);
			throw e;
		}

	}

	private void prepareSMSLogCSVRecord() {
		csvRecord.unregisterAllFields();
		csvRecord.setTimestampFormat((String) Configs.DETAILED_CDR_TIMESTAMP_FORMAT.getValue());

		updateSeqNums();
		csvRecord.getCampaignName().setFieldSeq(campaignNameSeqNum);
		csvRecord.registerField(csvRecord.getCampaignName());

		csvRecord.getBillingMSISDN().setFieldSeq(billingMSISDNSeqNum);
		csvRecord.registerField(csvRecord.getBillingMSISDN());

		csvRecord.getReceiver().setFieldSeq(receiverSeqNum);
		csvRecord.registerField(csvRecord.getReceiver());

		csvRecord.getSender().setFieldSeq(senderSeqNum);
		csvRecord.registerField(csvRecord.getSender());

		csvRecord.getProcessingDate().setFieldSeq(processingDateSeqNum);
		csvRecord.registerField(csvRecord.getProcessingDate());

		csvRecord.getStatus().setFieldSeq(statusSeqNum);
		csvRecord.registerField(csvRecord.getStatus());

		csvRecord.getSegCount().setFieldSeq(segCountSeqNum);
		csvRecord.registerField(csvRecord.getSegCount());

	}

	private void openCDRFile() throws IOException {
		cdrLogger.debug("Opening CDR CSV file");
		DateFormat df = new SimpleDateFormat((String) Configs.DETAILED_CDR_FILENAME_TIMESTAMP_FORMAT.getValue());
		String fileName = (String) Configs.DETAILED_CDR_FILENAME_PREFIX.getValue()
				+ df.format(startTimestamp.getTime());
		File file = new File(filePath + fileName);
		Charset charset = StandardCharsets.UTF_8;
		cdrLogger.info("Opening CDR CSV file with paramerters: fileName=" + fileName + ", delimiter=\"" + delimiter
				+ "\", charset=" + charset);
		batchFile = new CSVBatchFile(csvRecord, file, delimiter, "", charset, StandardOpenOption.CREATE);
		cdrLogger.debug("CDR file opened");
	}

	private void updateSMSLogCSV(SMSLog log) {
		Campaign camp = log.getCampaign();
		// TODO: there always should be an owner account and campaign of the log
		if (camp != null) {
			// was campaign Name, and changed in 24 Dec based on business requirements.
			csvRecord.setCampaignName(camp.getCampaignId());
			try {
				String formattedMSISDN = SMSUtils.formatAddress(camp.getAccountUser().getAccount().getBillingMsisdn(),
						MsisdnFormat.INTER);
				csvRecord.setBillingMSISDN(formattedMSISDN);
			} catch (InvalidMSISDNFormatException e) {
				if (cdrLogger.isTraceEnabled())
					cdrLogger.error("Cannot format MSISDN (" + camp.getAccountUser().getAccount().getBillingMsisdn()
							+ ") " + e);
				csvRecord.setBillingMSISDN(camp.getAccountUser().getAccount().getBillingMsisdn());
			} catch (InvalidAddressFormattingException e) {
				if (cdrLogger.isTraceEnabled())
					cdrLogger.error("Cannot format MSISDN (" + camp.getAccountUser().getAccount().getBillingMsisdn()
							+ ") " + e);
				csvRecord.setBillingMSISDN(camp.getAccountUser().getAccount().getBillingMsisdn());
			}
		} else {
			csvRecord.setCampaignName("");
			try {
				String formattedMSISDN = SMSUtils.formatAddress(log.getAccount().getBillingMsisdn(),
						MsisdnFormat.INTER);
				csvRecord.setBillingMSISDN(formattedMSISDN);
			} catch (InvalidMSISDNFormatException e) {
				if (cdrLogger.isTraceEnabled())
					cdrLogger.error("Cannot format MSISDN (" + log.getAccount().getBillingMsisdn() + ") " + e);
				csvRecord.setBillingMSISDN(log.getAccount().getBillingMsisdn());
			} catch (InvalidAddressFormattingException e) {
				if (cdrLogger.isTraceEnabled())
					cdrLogger.error("Cannot format MSISDN (" + log.getAccount().getBillingMsisdn() + ") " + e);
				csvRecord.setBillingMSISDN(log.getAccount().getBillingMsisdn());
			}
		}
		csvRecord.setSender(log.getSender());
		csvRecord.setReceiver(log.getReceiver());
		csvRecord.setStatus(log.getSMSStatus().getName());
		csvRecord.setProcessingDate(log.getProcessingDate());
		csvRecord.setSegCount(log.getSegmentsCount());
		//		csvRecord.setComments(log.getComments());
		//		csvRecord.setSendDate(log.getSendReceiveDate());
		//		csvRecord.setDeliveryDate(log.getDeliveryDate());

	}

	void writeCDRs(List<SMSLog> cdrs) throws IOException {
		cdrLogger.debug("Write CDRs to the file, count=" + cdrs.size());
		for (SMSLog aggCDR : cdrs) {
                    if (aggCDR.getAccount() != null) { // to avoid generate system-account cdrs for temp passwords SMSs
                        updateSMSLogCSV(aggCDR);
                        batchFile.writeCSVRecord(csvRecord);
                    }
		}
		cdrLogger.debug("CDRs written to the file");
	}

	void writeNewLine() throws IOException {
		batchFile.getCsvRandomAccessFile().newLine();
	}

	void closeCDRFile() throws IOException {
		Path filePath = batchFile.getPath();
		batchFile.close();
		Files.move(filePath, filePath.resolveSibling(filePath.getFileName() + fileExtension));
	}

	private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, ErrorsSource.CAMPAIGN_MANAGEMENT_SERVICE.name(), msg);
	}
}
