package com.edafa.web2sms.dalayer.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(name = "ActiveCampaignStats", namespace = "http://www.edafa.com/web2sms/service/campaign/model/")
public class ActiveCampaignStats {

	@XmlElement(required = true, nillable = false)
	String campaignId;

	@XmlElement(required = true, nillable = false)
	int pendingSMSCount;

	@XmlElement(required = true, nillable = false)
	int pendingSMSSegCount;

	@XmlElement(required = true, nillable = false)
	int deliveredSMSCount;

	@XmlElement(required = true, nillable = false)
	int deliveredSMSSegCount;

	@XmlElement(required = true, nillable = false)
	int unDeliveredSMSCount;

	@XmlElement(required = true, nillable = false)
	int unDeliveredSMSSegCount;

	@XmlElement(required = true, nillable = false)
	int failedSMSCount;

	@XmlElement(required = true, nillable = false)
	int failedSMSSegCount;

	public ActiveCampaignStats() {
		this.pendingSMSCount=0;
		this.pendingSMSSegCount=0;
		this.deliveredSMSCount=0;
		this.deliveredSMSSegCount=0;
		this.unDeliveredSMSCount=0;
		this.unDeliveredSMSSegCount=0;
		this.failedSMSCount=0;
		this.failedSMSSegCount=0;
	}

	public ActiveCampaignStats(String campaignId) {
		this.campaignId = campaignId;
		this.pendingSMSCount=0;
		this.pendingSMSSegCount=0;
		this.deliveredSMSCount=0;
		this.deliveredSMSSegCount=0;
		this.unDeliveredSMSCount=0;
		this.unDeliveredSMSSegCount=0;
		this.failedSMSCount=0;
		this.failedSMSSegCount=0;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public int getPendingSMSCount() {
		return pendingSMSCount;
	}

	public void setPendingSMSCount(int pendingSMSCount) {
		this.pendingSMSCount = pendingSMSCount;
	}

	public int getPendingSMSSegCount() {
		return pendingSMSSegCount;
	}

	public void setPendingSMSSegCount(int pendingSMSSegCount) {
		this.pendingSMSSegCount = pendingSMSSegCount;
	}

	public int getDeliveredSMSCount() {
		return deliveredSMSCount;
	}

	public void setDeliveredSMSCount(int deliveredSMSCount) {
		this.deliveredSMSCount = deliveredSMSCount;
	}

	public int getDeliveredSMSSegCount() {
		return deliveredSMSSegCount;
	}

	public void setDeliveredSMSSegCount(int deliveredSMSSegCount) {
		this.deliveredSMSSegCount = deliveredSMSSegCount;
	}

	public int getUnDeliveredSMSCount() {
		return unDeliveredSMSCount;
	}

	public void setUnDeliveredSMSCount(int unDeliveredSMSCount) {
		this.unDeliveredSMSCount = unDeliveredSMSCount;
	}

	public int getUnDeliveredSMSSegCount() {
		return unDeliveredSMSSegCount;
	}

	public void setUnDeliveredSMSSegCount(int unDeliveredSMSSegCount) {
		this.unDeliveredSMSSegCount = unDeliveredSMSSegCount;
	}

	public int getFailedSMSCount() {
		return failedSMSCount;
	}

	public void setFailedSMSCount(int failedSMSCount) {
		this.failedSMSCount = failedSMSCount;
	}

	public int getFailedSMSSegCount() {
		return failedSMSSegCount;
	}

	public void setFailedSMSSegCount(int failedSMSSegCount) {
		this.failedSMSSegCount = failedSMSSegCount;
	}

}


