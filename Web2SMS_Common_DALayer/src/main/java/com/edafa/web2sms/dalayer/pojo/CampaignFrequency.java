package com.edafa.web2sms.dalayer.pojo;

import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;

public class CampaignFrequency {
	
	String campaignId;
		
	String freqName;
	

	public CampaignFrequency(String campaignId, ScheduleFrequencyName freqName) {
		this.campaignId = campaignId;
		this.freqName = freqName.name();
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getFreqName() {
		return freqName;
	}

	public void setFreqName(String freqName) {
		this.freqName = freqName;
	}
	
	

}
