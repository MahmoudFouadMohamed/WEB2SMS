package com.edafa.web2sms.service.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.enums.CampaignTypeName;
import com.edafa.web2sms.service.campaign.DateTimeAdapter;

@XmlType(name = "Campaign", namespace = "http://www.edafa.com/web2sms/service/model/")
public class CampaignModel extends SubmittedCampaignModel {

	@XmlElement(required = true, nillable = false)
	private CampaignStatusName status;

	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationTimestamp;

	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date startTimestamp;

	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date endTimestamp;

	@XmlElement(required = true, nillable = false)
	private Integer recipientCount;

	@XmlElement(required = true, nillable = false)
	private Integer smsCount;

	@XmlElement(required = true, nillable = false)
	private Integer smsSegCount;

	@XmlElement(required = true, nillable = true)
	private Integer submittedSMSCount = 0;

	@XmlElement(required = true, nillable = true)
	private Integer submittedSMSSegCount = 0;
	
	@XmlElement(required = true, nillable = true)
	private Integer deliveredSMSCount = 0;

	@XmlElement(required = true, nillable = true)
	private Integer deliveredSMSSegCount = 0;
	
	@XmlElement(required = true, nillable = true)
	private Integer unDeliveredSMSCount = 0;

	@XmlElement(required = true, nillable = true)
	private Integer unDeliveredSMSSegCount = 0;
	
	@XmlElement(required = true, nillable = true)
	private Integer pendingSMSCount = 0;

	@XmlElement(required = true, nillable = true)
	private Integer pendingSMSSegCount = 0;
	
	@XmlElement(required = true, nillable = true)
	private Integer failedSMSCount = 0;

	@XmlElement(required = true, nillable = true)
	private Integer failedSMSSegCount = 0;

	@XmlElement(required = true, nillable = true)
	private String execusionComments;

	@XmlElement(required = true, nillable = false)
	private Integer expectedExecutionCount = 0;

	@XmlElement(required = true, nillable = true)
	private Integer executionCount = 0;

	@XmlElement(required = true, nillable = true)
	private CampaignActionName action;

	@XmlElement(required = true, nillable = true)
	private double submittedSMSRatio;
	
	@XmlElement(required = true, nillable = true)
	private CampaignTypeName type;
	
	@XmlElement(required = true, nillable = false)
	private boolean resendFailedFlag;

	public Integer getRecipientCount() {
		return recipientCount;
	}

	public void setRecipientCount(Integer recipientCount) {
		this.recipientCount = recipientCount;
	}

	public Integer getExpectedExecutionCount() {
		return expectedExecutionCount;
	}

	public void setExpectedExecutionCount(Integer expectedExecutionCount) {
		this.expectedExecutionCount = expectedExecutionCount;
	}

	public Integer getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(Integer executionCount) {
		this.executionCount = executionCount;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public CampaignStatusName getStatus() {
		return status;
	}

	public void setStatus(CampaignStatusName status) {
		this.status = status;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
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

	public Integer getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(Integer smsCount) {
		this.smsCount = smsCount;
	}

	public Integer getSmsSegCount() {
		return smsSegCount;
	}

	public void setSmsSegCount(Integer smsSegCount) {
		this.smsSegCount = smsSegCount;
	}

	public Integer getSubmittedSMSCount() {
		return submittedSMSCount;
	}

	public void setSubmittedSMSCount(Integer submittedSMSCount) {
		this.submittedSMSCount = submittedSMSCount;
	}

	public Integer getSubmittedSMSSegCount() {
		return submittedSMSSegCount;
	}

	public void setSubmittedSMSSegCount(Integer submittedSMSSegCount) {
		this.submittedSMSSegCount = submittedSMSSegCount;
	}

	public Integer getDeliveredSMSCount() {
		return deliveredSMSCount;
	}

	public void setDeliveredSMSCount(Integer deliveredSMSCount) {
		this.deliveredSMSCount = deliveredSMSCount;
	}

	public Integer getDeliveredSMSSegCount() {
		return deliveredSMSSegCount;
	}

	public void setDeliveredSMSSegCount(Integer deliveredSMSSegCount) {
		this.deliveredSMSSegCount = deliveredSMSSegCount;
	}

	public Integer getUnDeliveredSMSCount() {
		return unDeliveredSMSCount;
	}

	public void setUnDeliveredSMSCount(Integer unDeliveredSMSCount) {
		this.unDeliveredSMSCount = unDeliveredSMSCount;
	}

	public Integer getUnDeliveredSMSSegCount() {
		return unDeliveredSMSSegCount;
	}

	public void setUnDeliveredSMSSegCount(Integer unDeliveredSMSSegCount) {
		this.unDeliveredSMSSegCount = unDeliveredSMSSegCount;
	}

	public Integer getPendingSMSCount() {
		return pendingSMSCount;
	}

	public void setPendingSMSCount(Integer pendingSMSCount) {
		this.pendingSMSCount = pendingSMSCount;
	}

	public Integer getPendingSMSSegCount() {
		return pendingSMSSegCount;
	}

	public void setPendingSMSSegCount(Integer pendingSMSSegCount) {
		this.pendingSMSSegCount = pendingSMSSegCount;
	}

	public Integer getFailedSMSCount() {
		return failedSMSCount;
	}

	public void setFailedSMSCount(Integer failedSMSCount) {
		this.failedSMSCount = failedSMSCount;
	}

	public Integer getFailedSMSSegCount() {
		return failedSMSSegCount;
	}

	public void setFailedSMSSegCount(Integer failedSMSSegCount) {
		this.failedSMSSegCount = failedSMSSegCount;
	}

	public String getExecusionComments() {
		return execusionComments;
	}

	public void setExecusionComments(String execusionComments) {
		this.execusionComments = execusionComments;
	}

	public CampaignActionName getAction() {
		return action;
	}

	public void setAction(CampaignActionName action) {
		this.action = action;
	}

	public double getSubmittedSMSRatio() {
		return submittedSMSRatio;
	}

	public void setSubmittedSMSRatio(double submittedSMSRatio) {
		this.submittedSMSRatio = submittedSMSRatio;
	}

	public CampaignTypeName getType() {
		return type;
	}

	public void setType(CampaignTypeName type) {
		this.type = type;
	}

	public boolean isResendFailedFlag() {
		return resendFailedFlag;
	}

	public void setResendFailedFlag(boolean resendFailedFlag) {
		this.resendFailedFlag = resendFailedFlag;
	}
	
	
}
