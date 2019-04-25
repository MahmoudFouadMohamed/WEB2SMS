package com.edafa.web2sms.service.campaign.model;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.model.ActiveCampaignStats;
import com.edafa.web2sms.service.model.ResultStatus;

@XmlType(name = "ActiveCampaignStatsResult", namespace = "http://www.edafa.com/web2sms/service/campaign/model/")
public class ActiveCampaignStatsResult extends ResultStatus {

	List<ActiveCampaignStats> activeCampaignStats;

	public List<ActiveCampaignStats> getActiveCampaignStats() {
		return activeCampaignStats;
	}

	public void setActiveCampaignStats(List<ActiveCampaignStats> activeCampaignStats) {
		this.activeCampaignStats = activeCampaignStats;
	}
	
	

}
