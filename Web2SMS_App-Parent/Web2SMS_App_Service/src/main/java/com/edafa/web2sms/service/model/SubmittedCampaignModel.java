package com.edafa.web2sms.service.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.edafa.web2sms.dalayer.enums.CampaignTypeName;
import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;
import com.edafa.web2sms.service.campaign.DateTimeAdapter;

@XmlType(name = "SubmittedCampaign", namespace = "http://www.edafa.com/web2sms/service/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubmittedCampaignModel {

	@XmlElement(required = true, nillable = true)
	String campaignId;

	@XmlElement(required = true, nillable = false)
	private String campaignName;

	@XmlElement(required = true, nillable = false)
	private String smsText;

	@XmlElement(required = true, nillable = false)
	private LanguageNameEnum language;

	@XmlElement(required = true, nillable = false)
	private String senderName;

	@XmlElement(required = true, nillable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date scheduleStartTimestamp;

	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date scheduleEndTimestamp;

	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date scheduleStopTime;

	@XmlElement(required = true, nillable = false)
	private ScheduleFrequencyName scheduleFrequency;

	@XmlElement(required = true, nillable = false)
	private boolean registeredDelivery;

	@XmlElement(required = false, nillable = true)
	private List<ContactModel> individualContact;

	@XmlElement(required = true, nillable = true)
	private List<Integer> contactList;

	@XmlElement(required = true, nillable = true)
	private Boolean scheduledFlag;
	
	@XmlElement(required = true, nillable = false)
	private CampaignTypeName campaignType;

	public SubmittedCampaignModel() {
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public List<ContactModel> getIndividualContacts() {
		return individualContact;
	}

	public void setIndividualContacts(List<ContactModel> individualContacts) {
		this.individualContact = individualContacts;
	}

	public void setContactLists(List<Integer> contactList) {
		this.contactList = contactList;
	}

	public List<Integer> getContactLists() {
		return contactList;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Date getScheduleEndDate() {
		return scheduleEndTimestamp;
	}

	public void setScheduleEndTimestamp(Date scheduleEndTimestamp) {
		this.scheduleEndTimestamp = scheduleEndTimestamp;
	}

	public Date getScheduleStopTime() {
		return scheduleStopTime;
	}

	public void setScheduleStopTime(Date scheduleStopTime) {
		this.scheduleStopTime = scheduleStopTime;
	}

	public Date getScheduleStartTimestamp() {
		return scheduleStartTimestamp;
	}

	public void setScheduleStartTimestamp(Date scheduleStartTimestamp) {
		this.scheduleStartTimestamp = scheduleStartTimestamp;
	}

	public ScheduleFrequencyName getScheduleFrequency() {
		return scheduleFrequency;
	}

	public void setScheduleFrequency(ScheduleFrequencyName scheduleFrequency) {
		this.scheduleFrequency = scheduleFrequency;
	}

	public LanguageNameEnum getLanguage() {
		return language;
	}

	public void setLanguage(LanguageNameEnum language) {
		this.language = language;
	}

	public boolean isRegisteredDelivery() {
		return registeredDelivery;
	}

	public void setRegisteredDelivery(boolean registeredDelivery) {
		this.registeredDelivery = registeredDelivery;
	}

	public Boolean getScheduledFlag() {
		return scheduledFlag;
	}

	public void setScheduledFlag(Boolean scheduledFlag) {
		this.scheduledFlag = scheduledFlag;
	}

	public CampaignTypeName getCampaignType() {
		return campaignType;
	}

	public void setCampaignType(CampaignTypeName campaignType) {
		this.campaignType = campaignType;
	}

	@XmlTransient
	public boolean isValid() {
		boolean result = true;
		// validate nullable
		if (campaignName == null
				|| campaignName.isEmpty()
				|| smsText == null
				|| scheduleStartTimestamp == null
				|| ((individualContact == null || individualContact.isEmpty()) && (contactList == null || contactList
						.isEmpty()))) {
			result = false;
		}

		// Validate language and schedule frequency
		// try {
		// Enum.valueOf(LanguageNameEnum.class, language);
		// Enum.valueOf(ScheduleFrequencyName.class, scheduleFrequency);
		// } catch (Exception e) {
		// result = false;
		// }

		return result;
	}

	@Override
	public String toString() {
		return "SubmittedCampaign (campaignId=" + campaignId + ", campaignName=" + campaignName + ", language="
				+ language + ", smsText=" + smsText + ", senderName=" + senderName + ", scheduleStartTimestamp="
				+ scheduleStartTimestamp + ", scheduleEndTimestamp=" + scheduleEndTimestamp + ", scheduleStopTime="
				+ scheduleStopTime + ", scheduleFrequency=" + scheduleFrequency + ", registeredDelivery="
				+ registeredDelivery + ", individualContact=" + individualContact + ", contactList=" + contactList
				+ ", scheduledFlag=" + scheduledFlag + ", campaignType="+ campaignType+ ")";
	}

}
