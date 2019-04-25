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
import com.edafa.web2sms.dalayer.model.DateTimeAdapter;

@XmlType(name = "CampaignAggregationReport", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
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

	@XmlElement(required = true, nillable = false)
	private int vfSentSms;
	@XmlElement(required = true, nillable = false)
	private int ogSentSms;
	@XmlElement(required = true, nillable = false)
	private int etSentSms;
	@XmlElement(required = true, nillable = false)
	private int weSentSms;
	@XmlElement(required = true, nillable = false)
	private int interSentSms;
	@XmlElement(required = true, nillable = false)
	private int vfDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int ogDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int etDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int weDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int interDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int vfNotDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int ogNotDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int etNotDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int weNotDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int interNotDeliveredSms;

	@XmlElement(required = true, nillable = false)
	private int vfSentSegments;
	@XmlElement(required = true, nillable = false)
	private int ogSentSegments;
	@XmlElement(required = true, nillable = false)
	private int etSentSegments;
	@XmlElement(required = true, nillable = false)
	private int weSentSegments;
	@XmlElement(required = true, nillable = false)
	private int interSentSegments;
	@XmlElement(required = true, nillable = false)
	private int vfDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int ogDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int etDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int weDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int interDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int vfNotDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int ogNotDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int etNotDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int weNotDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int interNotDeliveredSegments;

	@XmlTransient
	private boolean updatedCamp;

	public CampaignAggregationReport() {}

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

	public boolean isResentFailedFlag() {
		return resentFailedFlag;
	}

	public void setResentFailedFlag(boolean resentFailedFlag) {
		this.resentFailedFlag = resentFailedFlag;
	}

	public void setSentSms(int vfSentSms, int ogSentSms, int etSentSms, int weSentSms, int interSentSms) {
		this.vfSentSms = vfSentSms;
		this.ogSentSms = ogSentSms;
		this.etSentSms = etSentSms;
		this.weSentSms = weSentSms;
		this.interSentSms = interSentSms;
	}

	public void setDeliveredSms(int vfDeliveredSms, int ogDeliveredSms, int etDeliveredSms, int weDeliveredSms,
			int interDeliveredSms) {
		this.vfDeliveredSms = vfDeliveredSms;
		this.ogDeliveredSms = ogDeliveredSms;
		this.etDeliveredSms = etDeliveredSms;
		this.weDeliveredSms = weDeliveredSms;
		this.interDeliveredSms = interDeliveredSms;
	}

	public void setNotDeliveredSms(int vfNotDeliveredSms, int ogNotDeliveredSms, int etNotDeliveredSms,
			int weNotDeliveredSms, int interNotDeliveredSms) {
		this.vfNotDeliveredSms = vfNotDeliveredSms;
		this.ogNotDeliveredSms = ogNotDeliveredSms;
		this.etNotDeliveredSms = etNotDeliveredSms;
		this.weNotDeliveredSms = weNotDeliveredSms;
		this.interNotDeliveredSms = interNotDeliveredSms;
	}

	public void setSentSegments(int vfSentSegments, int ogSentSegments, int etSentSegments, int weSentSegments,
			int interSentSegments) {
		this.vfSentSegments = vfSentSegments;
		this.ogSentSegments = ogSentSegments;
		this.etSentSegments = etSentSegments;
		this.weSentSegments = weSentSegments;
		this.interSentSegments = interSentSegments;
	}

	public void setDeliveredSegments(int vfDeliveredSegments, int ogDeliveredSegments, int etDeliveredSegments,
			int weDeliveredSegments, int interDeliveredSegments) {
		this.vfDeliveredSegments = vfDeliveredSegments;
		this.ogDeliveredSegments = ogDeliveredSegments;
		this.etDeliveredSegments = etDeliveredSegments;
		this.weDeliveredSegments = weDeliveredSegments;
		this.interDeliveredSegments = interDeliveredSegments;
	}

	public void setNotDeliveredSegments(int vfNotDeliveredSegments, int ogNotDeliveredSegments,
			int etNotDeliveredSegments, int weNotDeliveredSegments, int interNotDeliveredSegments) {
		this.vfNotDeliveredSegments = vfNotDeliveredSegments;
		this.ogNotDeliveredSegments = ogNotDeliveredSegments;
		this.etNotDeliveredSegments = etNotDeliveredSegments;
		this.weNotDeliveredSegments = weNotDeliveredSegments;
		this.interNotDeliveredSegments = interNotDeliveredSegments;
	}

	public int getVfSentSms() {
		return vfSentSms;
	}

	public void setVfSentSms(int vfSentSms) {
		this.vfSentSms = vfSentSms;
	}

	public int getOgSentSms() {
		return ogSentSms;
	}

	public void setOgSentSms(int ogSentSms) {
		this.ogSentSms = ogSentSms;
	}

	public int getEtSentSms() {
		return etSentSms;
	}

	public void setEtSentSms(int etSentSms) {
		this.etSentSms = etSentSms;
	}

	public int getWeSentSms() {
		return weSentSms;
	}

	public void setWeSentSms(int weSentSms) {
		this.weSentSms = weSentSms;
	}

	public int getInterSentSms() {
		return interSentSms;
	}

	public void setInterSentSms(int interSentSms) {
		this.interSentSms = interSentSms;
	}

	public int getVfDeliveredSms() {
		return vfDeliveredSms;
	}

	public void setVfDeliveredSms(int vfDeliveredSms) {
		this.vfDeliveredSms = vfDeliveredSms;
	}

	public int getOgDeliveredSms() {
		return ogDeliveredSms;
	}

	public void setOgDeliveredSms(int ogDeliveredSms) {
		this.ogDeliveredSms = ogDeliveredSms;
	}

	public int getEtDeliveredSms() {
		return etDeliveredSms;
	}

	public void setEtDeliveredSms(int etDeliveredSms) {
		this.etDeliveredSms = etDeliveredSms;
	}

	public int getWeDeliveredSms() {
		return weDeliveredSms;
	}

	public void setWeDeliveredSms(int weDeliveredSms) {
		this.weDeliveredSms = weDeliveredSms;
	}

	public int getInterDeliveredSms() {
		return interDeliveredSms;
	}

	public void setInterDeliveredSms(int interDeliveredSms) {
		this.interDeliveredSms = interDeliveredSms;
	}

	public int getVfNotDeliveredSms() {
		return vfNotDeliveredSms;
	}

	public void setVfNotDeliveredSms(int vfNotDeliveredSms) {
		this.vfNotDeliveredSms = vfNotDeliveredSms;
	}

	public int getOgNotDeliveredSms() {
		return ogNotDeliveredSms;
	}

	public void setOgNotDeliveredSms(int ogNotDeliveredSms) {
		this.ogNotDeliveredSms = ogNotDeliveredSms;
	}

	public int getEtNotDeliveredSms() {
		return etNotDeliveredSms;
	}

	public void setEtNotDeliveredSms(int etNotDeliveredSms) {
		this.etNotDeliveredSms = etNotDeliveredSms;
	}

	public int getWeNotDeliveredSms() {
		return weNotDeliveredSms;
	}

	public void setWeNotDeliveredSms(int weNotDeliveredSms) {
		this.weNotDeliveredSms = weNotDeliveredSms;
	}

	public int getInterNotDeliveredSms() {
		return interNotDeliveredSms;
	}

	public void setInterNotDeliveredSms(int interNotDeliveredSms) {
		this.interNotDeliveredSms = interNotDeliveredSms;
	}

	public int getVfSentSegments() {
		return vfSentSegments;
	}

	public void setVfSentSegments(int vfSentSegments) {
		this.vfSentSegments = vfSentSegments;
	}

	public int getOgSentSegments() {
		return ogSentSegments;
	}

	public void setOgSentSegments(int ogSentSegments) {
		this.ogSentSegments = ogSentSegments;
	}

	public int getEtSentSegments() {
		return etSentSegments;
	}

	public void setEtSentSegments(int etSentSegments) {
		this.etSentSegments = etSentSegments;
	}

	public int getWeSentSegments() {
		return weSentSegments;
	}

	public void setWeSentSegments(int weSentSegments) {
		this.weSentSegments = weSentSegments;
	}

	public int getInterSentSegments() {
		return interSentSegments;
	}

	public void setInterSentSegments(int interSentSegments) {
		this.interSentSegments = interSentSegments;
	}

	public int getVfDeliveredSegments() {
		return vfDeliveredSegments;
	}

	public void setVfDeliveredSegments(int vfDeliveredSegments) {
		this.vfDeliveredSegments = vfDeliveredSegments;
	}

	public int getOgDeliveredSegments() {
		return ogDeliveredSegments;
	}

	public void setOgDeliveredSegments(int ogDeliveredSegments) {
		this.ogDeliveredSegments = ogDeliveredSegments;
	}

	public int getEtDeliveredSegments() {
		return etDeliveredSegments;
	}

	public void setEtDeliveredSegments(int etDeliveredSegments) {
		this.etDeliveredSegments = etDeliveredSegments;
	}

	public int getWeDeliveredSegments() {
		return weDeliveredSegments;
	}

	public void setWeDeliveredSegments(int weDeliveredSegments) {
		this.weDeliveredSegments = weDeliveredSegments;
	}

	public int getInterDeliveredSegments() {
		return interDeliveredSegments;
	}

	public void setInterDeliveredSegments(int interDeliveredSegments) {
		this.interDeliveredSegments = interDeliveredSegments;
	}

	public int getVfNotDeliveredSegments() {
		return vfNotDeliveredSegments;
	}

	public void setVfNotDeliveredSegments(int vfNotDeliveredSegments) {
		this.vfNotDeliveredSegments = vfNotDeliveredSegments;
	}

	public int getOgNotDeliveredSegments() {
		return ogNotDeliveredSegments;
	}

	public void setOgNotDeliveredSegments(int ogNotDeliveredSegments) {
		this.ogNotDeliveredSegments = ogNotDeliveredSegments;
	}

	public int getEtNotDeliveredSegments() {
		return etNotDeliveredSegments;
	}

	public void setEtNotDeliveredSegments(int etNotDeliveredSegments) {
		this.etNotDeliveredSegments = etNotDeliveredSegments;
	}

	public int getWeNotDeliveredSegments() {
		return weNotDeliveredSegments;
	}

	public void setWeNotDeliveredSegments(int weNotDeliveredSegments) {
		this.weNotDeliveredSegments = weNotDeliveredSegments;
	}

	public int getInterNotDeliveredSegments() {
		return interNotDeliveredSegments;
	}

	public void setInterNotDeliveredSegments(int interNotDeliveredSegments) {
		this.interNotDeliveredSegments = interNotDeliveredSegments;
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

	@Override
	public int compareTo(CampaignAggregationReport o) {
		return this.startTimestamp.compareTo(o.getStartTimestamp());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{campaignId=").append(campaignId).append(", campaignStatus=").append(campaignStatus)
				.append(", accountId=").append(accountId).append(", campaignName=").append(campaignName)
				.append(", username=").append(username).append(", creationTimestamp=").append(creationTimestamp)
				.append(", startTimestamp=").append(startTimestamp).append(", endTimestamp=").append(endTimestamp)
				.append(", sender=").append(sender).append(", frequency=").append(frequency).append(", smsText=")
				.append(smsText).append(", contactListName=").append(contactListName).append(", listsCount=")
				.append(listsCount).append(", recipientCount=").append(recipientCount).append(", smsCount=")
				.append(smsCount).append(", smsSegCount=").append(smsSegCount).append(", submittedSMSCount=")
				.append(submittedSMSCount).append(", submittedSMSSegCount=").append(submittedSMSSegCount)
				.append(", pendingSMSCount=").append(pendingSMSCount).append(", deliverdSMSCount=")
				.append(deliverdSMSCount).append(", unDeliverdSMSCount=").append(unDeliverdSMSCount)
				.append(", failedSMSCount=").append(failedSMSCount).append(", pendingSMSSegCount=")
				.append(pendingSMSSegCount).append(", deliverdSMSSegCount=").append(deliverdSMSSegCount)
				.append(", unDeliverdSMSSegCount=").append(unDeliverdSMSSegCount).append(", failedSMSSegCount=")
				.append(failedSMSSegCount).append(", executionCount=").append(executionCount)
				.append(", resentFailedFlag=").append(resentFailedFlag).append(", vfSentSms=").append(vfSentSms)
				.append(", ogSentSms=").append(ogSentSms).append(", etSentSms=").append(etSentSms)
				.append(", weSentSms=").append(weSentSms).append(", interSentSms=").append(interSentSms)
				.append(", vfDeliveredSms=").append(vfDeliveredSms).append(", ogDeliveredSms=").append(ogDeliveredSms)
				.append(", etDeliveredSms=").append(etDeliveredSms).append(", weDeliveredSms=").append(weDeliveredSms)
				.append(", interDeliveredSms=").append(interDeliveredSms).append(", vfNotDeliveredSms=")
				.append(vfNotDeliveredSms).append(", ogNotDeliveredSms=").append(ogNotDeliveredSms)
				.append(", etNotDeliveredSms=").append(etNotDeliveredSms).append(", weNotDeliveredSms=")
				.append(weNotDeliveredSms).append(", interNotDeliveredSms=").append(interNotDeliveredSms)
				.append(", vfSentSegments=").append(vfSentSegments).append(", ogSentSegments=").append(ogSentSegments)
				.append(", etSentSegments=").append(etSentSegments).append(", weSentSegments=").append(weSentSegments)
				.append(", interSentSegments=").append(interSentSegments).append(", vfDeliveredSegments=")
				.append(vfDeliveredSegments).append(", ogDeliveredSegments=").append(ogDeliveredSegments)
				.append(", etDeliveredSegments=").append(etDeliveredSegments).append(", weDeliveredSegments=")
				.append(weDeliveredSegments).append(", interDeliveredSegments=").append(interDeliveredSegments)
				.append(", vfNotDeliveredSegments=").append(vfNotDeliveredSegments).append(", ogNotDeliveredSegments=")
				.append(ogNotDeliveredSegments).append(", etNotDeliveredSegments=").append(etNotDeliveredSegments)
				.append(", weNotDeliveredSegments=").append(weNotDeliveredSegments)
				.append(", interNotDeliveredSegments=").append(interNotDeliveredSegments).append(", updatedCamp=")
				.append(updatedCamp).append("}");
		return builder.toString();
	}

}
