package com.edafa.web2sms.core.cdr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.edafa.csv.record.DefaultCSVRecord;
import com.edafa.csv.record.Field;
import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;

public class SMSLogCSV extends DefaultCSVRecord {

	private Field campaignName = new Field("Campaign Name", 0);
	private Field billingMSISDN = new Field("Billing MSISDN", 1);
	private Field receiver = new Field("Receiver", 2);
	private Field sender = new Field("Sender", 3);
	private Field processingDate = new Field("Processing Timestamp", 4);
	private Field status = new Field("Status", 5);
	private Field segCount = new Field("Sent Segments", 6);

	SimpleDateFormat dformatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

	public SMSLogCSV() {
		registerField(campaignName);
		registerField(billingMSISDN);
		registerField(receiver);
		registerField(sender);
		registerField(processingDate);
		registerField(status);
		registerField(status);
		registerField(segCount);
	}
	
	public void updateFieldRegistration(List<Field> fieldList){
		unregisterAllFields();
		for (Field field : fieldList) {
			registerField(field);
		}
	}

	@Override
	public boolean validateRecord(String[] record) {
		return true;
	}

	public void setTimestampFormat(String format) {
		dformatter = new SimpleDateFormat(format);
	}

	public Field getProcessingDate() {
		return processingDate;
	}

	public void setProcessingDate(Field processingDate) {
		this.processingDate = processingDate;
	}

	public void setProcessingDate(Date processingDate) {
		if (processingDate != null)
			this.processingDate.setValue(processingDate != null ? dformatter.format(processingDate) : "");
	}

	public Field getBillingMSISDN() {
		return billingMSISDN;
	}

	public void setBillingMSISDN(Field billingMSISDN) {
		this.billingMSISDN = billingMSISDN;
	}

	public void setBillingMSISDN(String billingMSISDN) {
		this.billingMSISDN.setValue(billingMSISDN);
	}

	/**
	 * @return the campaignName
	 */
	public Field getCampaignName() {
		return campaignName;
	}

	/**
	 * @param campaignName
	 *            the campaignName to set
	 */
	public void setCampaignName(Field campaignName) {
		this.campaignName = campaignName;
	}

	/**
	 * @return the sender
	 */
	public Field getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public void setSender(Field sender) {
		this.sender = sender;
	}

	/**
	 * @return the receiver
	 */
	public Field getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver
	 *            the receiver to set
	 */
	public void setReceiver(Field receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the status
	 */
	public Field getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Field status) {
		this.status = status;
	}

	/**
	 * @return the segCount
	 */
	public Field getSegCount() {
		return segCount;
	}

	/**
	 * @param segCount
	 *            the segCount to set
	 */
	public void setSegCount(Field segCount) {
		this.segCount = segCount;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName.setValue(campaignName != null ? campaignName : "");
	}

	public void setSender(String sender) {
		this.sender.setValue(sender);
	}

	public void setReceiver(String receiver) {
		this.receiver.setValue(receiver);
	}

	public void setStatus(SMSStatusName status) {
		if (status != null)
			this.status.setValue(status.name());
	}
		public void setSegCount(Integer segCount) {
		this.segCount.setValue(segCount.toString());
	}

}
