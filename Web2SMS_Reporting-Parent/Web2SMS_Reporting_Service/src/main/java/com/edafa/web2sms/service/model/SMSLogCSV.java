package com.edafa.web2sms.service.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.edafa.csv.record.DefaultCSVRecord;
import com.edafa.csv.record.Field;
import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;

public class SMSLogCSV extends DefaultCSVRecord {

	// private Field campaignId = new Field("Campaign Id", 1);
	private Field campaignName = new Field("Campaign Name", 0);
	private Field scheduleFrequency = new Field("Campaign Type", 1);
	// private Field smsId = new Field("SMSId", 2);
	private Field sender = new Field("Sender", 2);
	private Field receiver = new Field("Receiver", 3);
	private Field smsText = new Field("SMS Text", 4);
	private Field language = new Field("Language", 5);
	private Field status = new Field("Status", 6);
	private Field sendDate = new Field("Send Date", 7);
	private Field segCount = new Field("Sent Segments", 8);
	private Field deliveryDate = new Field("Delivery Date", 9);
	private Field comments = new Field("Comments", 10);

	SimpleDateFormat dformatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

	public SMSLogCSV() {
		// registerField(smsId);
		// registerField(campaignId);
		registerField(campaignName);
		registerField(scheduleFrequency);
		registerField(sender);
		registerField(receiver);
		registerField(smsText);
		registerField(language);
		registerField(status);
		registerField(sendDate);
		registerField(segCount);
		registerField(deliveryDate);
		registerField(comments);
	}

	public void setCampaignName(String campaignName) {

		this.campaignName.setValue(campaignName);
	}

	public void setSender(String sender) {
		this.sender.setValue(sender);
	}

	public void setReceiver(String receiver) {
		this.receiver.setValue(receiver);
	}

	public void setSmsText(String smsText) {
		this.smsText.setValue(smsText);
	}

	public void setSendDate(Date sendDate) {
		if (sendDate != null)
			this.sendDate.setValue(dformatter.format(sendDate));
	}

	public void setDeliveryDate(Date deliveryDate) {
		if (deliveryDate != null)
			this.deliveryDate.setValue(deliveryDate != null ? dformatter.format(deliveryDate) : "");
	}

	public void setStatus(SMSStatusName status) {
		if (status != null)
			this.status.setValue(status.name());
	}

	// public void setCampaignId(String campaignId) {
	// this.campaignId.setValue(campaignId;
	// }

	public void setLanguage(LanguageNameEnum language) {
		if (language != null)
			this.language.setValue(language.name());
	}

	public void setComments(String comments) {
		this.comments.setValue(comments != null ? comments : "");
	}

	public void setScheduleFrequency(ScheduleFrequencyName scheduleFrequency) {
		this.scheduleFrequency.setValue(scheduleFrequency.name());
	}

	public void setSegCount(Integer segCount) {
		this.segCount.setValue(segCount.toString());
	}

}
