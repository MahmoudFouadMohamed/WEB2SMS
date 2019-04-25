package com.edafa.web2sms.service.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignAggregationView;
import com.edafa.web2sms.service.campaign.DateTimeAdapter;

@XmlType(name = "CampaignAggregationReport", namespace = "http://www.edafa.com/web2sms/service/model/")
public class CampaignAggregationReport implements Comparable<CampaignAggregationReport> {

	@XmlElement(required = true, nillable = false)
	private String campaignId;

	@XmlElement(required = true, nillable = false)
	private CampaignStatusName campaignStatus;

	@XmlElement(required = true, nillable = false)
	private String accountId;

	@XmlElement(required = true, nillable = false)
	private String campaignName;

	@XmlElement(required = true, nillable = false)
	private String username;

	@XmlElement(required = true, nillable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationTimestamp;

	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date startTimestamp;

	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date endTimestamp;

	@XmlElement(required = true, nillable = false)
	private String sender;

	@XmlElement(required = true, nillable = false)
	private String frequency;

	@XmlElement(required = true, nillable = false)
	private String smsText;

	@XmlElement(required = true, nillable = false)
	private List<String> contactListName;

	@XmlElement(required = true, nillable = false)
	private int listsCount;

	@XmlElement(required = true, nillable = false)
	private int recipientCount;

	@XmlElement(required = true, nillable = false)
	private int smsCount;

	@XmlElement(required = true, nillable = false)
	private int smsSegCount;

	@XmlElement(required = true, nillable = false)
	private int submittedSMSCount;

	@XmlElement(required = true, nillable = false)
	private int submittedSMSSegCount;

	@XmlElement(required = true, nillable = false)
	private int pendingSMSCount;

	@XmlElement(required = true, nillable = false)
	private int deliverdSMSCount;

	@XmlElement(required = true, nillable = false)
	private int unDeliverdSMSCount;

	@XmlElement(required = true, nillable = false)
	private int failedSMSCount;

	@XmlElement(required = true, nillable = false)
	private int pendingSMSSegCount;

	@XmlElement(required = true, nillable = false)
	private int deliverdSMSSegCount;

	@XmlElement(required = true, nillable = false)
	private int unDeliverdSMSSegCount;

	@XmlElement(required = true, nillable = false)
	private int failedSMSSegCount;

	@XmlElement(required = true, nillable = true)
	private int executionCount;

	@XmlElement(required = true, nillable = false)
	private boolean resentFailedFlag;

	@XmlTransient
	private boolean updatedCamp;

	public CampaignAggregationReport() {

	}

	public CampaignAggregationReport(CampaignAggregationView obj, Campaign camp) {
		this.accountId = obj.getAccountId().getAccount().getAccountId();
		this.username = obj.getAccountId().getUsername();
		this.campaignId = obj.getCampaignId();
		this.campaignName = obj.getCampaignName();
		this.campaignStatus = obj.getCampaignStatus();
		this.creationTimestamp = obj.getCreationTimestamp();
		this.endTimestamp = obj.getEndTimestamp();
		this.startTimestamp = obj.getStartTimestamp();
		this.executionCount = obj.getExecutionCount();
		this.listsCount = obj.getListsCount();
		this.sender = obj.getSender();
		this.smsCount = obj.getSmsCount();
		this.smsSegCount = obj.getSmsSegCount();
		this.smsText = obj.getSmsText();
		this.submittedSMSCount = obj.getSubmittedSMSCount();
		this.submittedSMSSegCount = obj.getSubmittedSMSSegCount();
		this.updatedCamp = obj.isUpdatedCamp();
		this.recipientCount = obj.getSubmittedSMSCount();
		this.resentFailedFlag = camp.isResendFailedFlag();

		this.deliverdSMSCount = 0;
		this.deliverdSMSSegCount = 0;
		this.unDeliverdSMSCount = 0;
		this.unDeliverdSMSSegCount = 0;
		this.pendingSMSCount = 0;
		this.pendingSMSSegCount = 0;
		this.failedSMSCount = 0;
		this.failedSMSSegCount = 0;

		this.contactListName = new ArrayList<String>();
	}

	public CampaignAggregationReport(CampaignAggregationView obj) {
		this.accountId = obj.getAccountId().getAccount().getAccountId();
		this.username = obj.getAccountId().getUsername();
		this.campaignId = obj.getCampaignId();
		this.campaignName = obj.getCampaignName();
		this.campaignStatus = obj.getCampaignStatus();
		this.creationTimestamp = obj.getCreationTimestamp();
		this.endTimestamp = obj.getEndTimestamp();
		this.startTimestamp = obj.getStartTimestamp();
		this.executionCount = obj.getExecutionCount();
		this.listsCount = obj.getListsCount();
		this.sender = obj.getSender();
		this.smsCount = obj.getSmsCount();
		this.smsSegCount = obj.getSmsSegCount();
		this.smsText = obj.getSmsText();
		this.submittedSMSCount = obj.getSubmittedSMSCount();
		this.submittedSMSSegCount = obj.getSubmittedSMSSegCount();
		this.updatedCamp = obj.isUpdatedCamp();
		this.recipientCount = obj.getSubmittedSMSCount();

		this.deliverdSMSCount = 0;
		this.deliverdSMSSegCount = 0;
		this.unDeliverdSMSCount = 0;
		this.unDeliverdSMSSegCount = 0;
		this.pendingSMSCount = 0;
		this.pendingSMSSegCount = 0;
		this.failedSMSCount = 0;
		this.failedSMSSegCount = 0;

		this.contactListName = new ArrayList<String>();
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public CampaignStatusName getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(CampaignStatusName campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public int getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(int smsCount) {
		this.smsCount = smsCount;
	}

	public int getSmsSegCount() {
		return smsSegCount;
	}

	public void setSmsSegCount(int smsSegCount) {
		this.smsSegCount = smsSegCount;
	}

	public int getSubmittedSMSCount() {
		return submittedSMSCount;
	}

	public void setSubmittedSMSCount(int submittedSMSCount) {
		this.submittedSMSCount = submittedSMSCount;
	}

	public int getSubmittedSMSSegCount() {
		return submittedSMSSegCount;
	}

	public void setSubmittedSMSSegCount(int submittedSMSSegCount) {
		this.submittedSMSSegCount = submittedSMSSegCount;
	}

	public int getPendingSMSCount() {
		return pendingSMSCount;
	}

	public void setPendingSMSCount(int pendingSMSCount) {
		this.pendingSMSCount = pendingSMSCount;
	}

	public int getDeliverdSMSCount() {
		return deliverdSMSCount;
	}

	public void setDeliverdSMSCount(int deliverdSMSCount) {
		this.deliverdSMSCount = deliverdSMSCount;
	}

	public int getFailedSMSCount() {
		return failedSMSCount;
	}

	public void setFailedSMSCount(int failedSMSCount) {
		this.failedSMSCount = failedSMSCount;
	}

	public int getListsCount() {
		return listsCount;
	}

	public void setListsCount(int listsCount) {
		this.listsCount = listsCount;
	}

	public int getRecipientCount() {
		return recipientCount;
	}

	public void setRecipientCount(int recipientCount) {
		this.recipientCount = recipientCount;
	}

	public Date getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Date startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Date getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(Date endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public int getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(int executionCount) {
		this.executionCount = executionCount;
	}

	public int getUnDeliverdSMSCount() {
		return unDeliverdSMSCount;
	}

	public void setUnDeliverdSMSCount(int unDeliverdSMSCount) {
		this.unDeliverdSMSCount = unDeliverdSMSCount;
	}

	public int getPendingSMSSegCount() {
		return pendingSMSSegCount;
	}

	public void setPendingSMSSegCount(int pendingSMSSegCount) {
		this.pendingSMSSegCount = pendingSMSSegCount;
	}

	public int getDeliverdSMSSegCount() {
		return deliverdSMSSegCount;
	}

	public void setDeliverdSMSSegCount(int deliverdSMSSegCount) {
		this.deliverdSMSSegCount = deliverdSMSSegCount;
	}

	public int getUnDeliverdSMSSegCount() {
		return unDeliverdSMSSegCount;
	}

	public void setUnDeliverdSMSSegCount(int unDeliverdSMSSegCount) {
		this.unDeliverdSMSSegCount = unDeliverdSMSSegCount;
	}

	public int getFailedSMSSegCount() {
		return failedSMSSegCount;
	}

	public void setFailedSMSSegCount(int failedSMSSegCount) {
		this.failedSMSSegCount = failedSMSSegCount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isUpdatedCamp() {
		return updatedCamp;
	}

	public void setUpdatedCamp(boolean updatedCamp) {
		this.updatedCamp = updatedCamp;
	}

	public List<String> getContactListName() {
		return contactListName;
	}

	public void setContactListName(List<String> contactListName) {
		this.contactListName = contactListName;
	}

	public void addToContactListName(String contactListName) {
		this.contactListName.add(contactListName);
	}

	public void resetCounters() {
		this.deliverdSMSCount = 0;
		this.deliverdSMSSegCount = 0;
		this.unDeliverdSMSCount = 0;
		this.unDeliverdSMSSegCount = 0;
		this.pendingSMSCount = 0;
		this.pendingSMSSegCount = 0;
		this.failedSMSCount = 0;
		this.failedSMSSegCount = 0;
	}

//	@Override
//	public String toString() {
//		return "CampaignAggregationReport [campaignId=" + campaignId + ", campaignStatus=" + campaignStatus
//				+ ", accountId=" + accountId + ", campaignName=" + campaignName + ", username=" + username
//				+ ", creationTimestamp=" + creationTimestamp + ", startTimestamp=" + startTimestamp + ", endTimestamp="
//				+ endTimestamp + ", sender=" + sender + ", smsText=" + smsText + ", contactListName=" + contactListName
//				+ ", listsCount=" + listsCount + ", recipientCount=" + recipientCount + ", smsCount=" + smsCount
//				+ ", smsSegCount=" + smsSegCount + ", submittedSMSCount=" + submittedSMSCount
//				+ ", submittedSMSSegCount=" + submittedSMSSegCount + ", pendingSMSCount=" + pendingSMSCount
//				+ ", deliverdSMSCount=" + deliverdSMSCount + ", unDeliverdSMSCount=" + unDeliverdSMSCount
//				+ ", failedSMSCount=" + failedSMSCount + ", pendingSMSSegCount=" + pendingSMSSegCount
//				+ ", deliverdSMSSegCount=" + deliverdSMSSegCount + ", unDeliverdSMSSegCount=" + unDeliverdSMSSegCount
//				+ ", failedSMSSegCount=" + failedSMSSegCount + ", executionCount=" + executionCount + ", updatedCamp="
//				+ updatedCamp + "]";
//	}

	
	
	// @Override
	// public int compare(CampaignAggregationReport o1,
	// CampaignAggregationReport o2) {
	// // TODO Auto-generated method stub
	// return o1.getStartTimestamp().compareTo(o2.getStartTimestamp());
	// }

	@Override
	public int compareTo(CampaignAggregationReport o) {
		// TODO Auto-generated method stub
		return this.startTimestamp.compareTo(o.getStartTimestamp());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CampaignAggregationReport [campaignId=");
		builder.append(campaignId);
		builder.append(", campaignStatus=");
		builder.append(campaignStatus);
		builder.append(", accountId=");
		builder.append(accountId);
		builder.append(", campaignName=");
		builder.append(campaignName);
		builder.append(", username=");
		builder.append(username);
		builder.append(", creationTimestamp=");
		builder.append(creationTimestamp);
		builder.append(", startTimestamp=");
		builder.append(startTimestamp);
		builder.append(", endTimestamp=");
		builder.append(endTimestamp);
		builder.append(", sender=");
		builder.append(sender);
		builder.append(", frequency=");
		builder.append(frequency);
		builder.append(", smsText=");
		builder.append(smsText);
		builder.append(", contactListName=");
		builder.append(contactListName);
		builder.append(", listsCount=");
		builder.append(listsCount);
		builder.append(", recipientCount=");
		builder.append(recipientCount);
		builder.append(", smsCount=");
		builder.append(smsCount);
		builder.append(", smsSegCount=");
		builder.append(smsSegCount);
		builder.append(", submittedSMSCount=");
		builder.append(submittedSMSCount);
		builder.append(", submittedSMSSegCount=");
		builder.append(submittedSMSSegCount);
		builder.append(", pendingSMSCount=");
		builder.append(pendingSMSCount);
		builder.append(", deliverdSMSCount=");
		builder.append(deliverdSMSCount);
		builder.append(", unDeliverdSMSCount=");
		builder.append(unDeliverdSMSCount);
		builder.append(", failedSMSCount=");
		builder.append(failedSMSCount);
		builder.append(", pendingSMSSegCount=");
		builder.append(pendingSMSSegCount);
		builder.append(", deliverdSMSSegCount=");
		builder.append(deliverdSMSSegCount);
		builder.append(", unDeliverdSMSSegCount=");
		builder.append(unDeliverdSMSSegCount);
		builder.append(", failedSMSSegCount=");
		builder.append(failedSMSSegCount);
		builder.append(", executionCount=");
		builder.append(executionCount);
		builder.append(", resentFailedFlag=");
		builder.append(resentFailedFlag);
		builder.append(", updatedCamp=");
		builder.append(updatedCamp);
		builder.append("]");
		return builder.toString();
	}

}
