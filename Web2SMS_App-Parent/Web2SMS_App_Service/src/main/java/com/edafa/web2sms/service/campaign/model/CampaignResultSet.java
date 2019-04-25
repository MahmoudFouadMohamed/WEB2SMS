package com.edafa.web2sms.service.campaign.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.CampaignModel;
import com.edafa.web2sms.service.model.ResultStatus;

@XmlType(name = "CampaignResultSet", namespace = "http://www.edafa.com/service/campaign/model/")
public class CampaignResultSet extends ResultStatus {
	@XmlElement(name = "Campaign", required = true, nillable = false)
	List<CampaignModel> campaigns;

	public CampaignResultSet() {
		super();
	}

	public CampaignResultSet(ResponseStatus status, String errorMessage) {
		super(status, errorMessage);
	}

	public CampaignResultSet(ResponseStatus status) {
		super(status);
	}

	public CampaignResultSet(ResponseStatus status, List<CampaignModel> campaigns) {
		super(status);
		this.campaigns = campaigns;
	}

	public List<CampaignModel> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<CampaignModel> campaigns) {
		this.campaigns = campaigns;
	}
}
