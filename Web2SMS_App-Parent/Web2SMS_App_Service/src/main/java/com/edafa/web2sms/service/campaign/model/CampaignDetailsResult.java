package com.edafa.web2sms.service.campaign.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.CampaignDetails;
import com.edafa.web2sms.service.model.ResultStatus;

@XmlType(name = "CampaignDetailsResult", namespace = "http://www.edafa.com/web2sms/service/campaign/model/")
public class CampaignDetailsResult extends ResultStatus {

	@XmlElement(required = true, nillable = false)
	CampaignDetails campaignDetails;
	
	public CampaignDetailsResult() {
		super();
	}

	public CampaignDetailsResult(ResponseStatus status, String errorMessage) {
		super(status, errorMessage);
	}

	public CampaignDetailsResult(ResponseStatus status) {
		super(status);
	}

	public CampaignDetails getCampaignDetails() {
		return campaignDetails;
	}

	public void setCampaignDetails(CampaignDetails campaignDetails) {
		this.campaignDetails = campaignDetails;
	}

}
