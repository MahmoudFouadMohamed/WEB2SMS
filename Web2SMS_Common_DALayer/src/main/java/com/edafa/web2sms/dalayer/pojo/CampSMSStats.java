package com.edafa.web2sms.dalayer.pojo;

import com.edafa.web2sms.dalayer.enums.SMSStatusName;

public class CampSMSStats {

	String campaignId;
	SMSStatusName status;
	int smsCount;
	int smsSegCount;
	
	
	public CampSMSStats(String campaignId, SMSStatusName status, Long smsCount, Long smsSegCount) {
		this.campaignId = campaignId;
		this.status = status;
		this.smsCount = smsCount.intValue();
		this.smsSegCount = smsSegCount.intValue();
	}
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	public SMSStatusName getStatusId() {
		return status;
	}
	public void setStatusId(SMSStatusName status) {
		this.status = status;
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
	
	@Override
	public String toString() {
		return "CampSMSStats [campaignId=" + campaignId + ", status=" + status + ", smsCount=" + smsCount
				+ ", smsSegCount=" + smsSegCount + "]";
	}
	
	
}
