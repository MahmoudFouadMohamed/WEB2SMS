package com.edafa.web2sms.service.campaign.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.service.enums.CampaignValidationStatus;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.ResultStatus;

@XmlType(name = "CampaignResult", namespace = "http://www.edafa.com/service/campaign/model/")
public class CampaignResult extends ResultStatus {
	@XmlElement(name = "CampaignValidationStatus", required = true, nillable = false)
	List<CampaignValidationStatus> validationStatusList;

	public CampaignResult() {
		super();

	}

	public CampaignResult(ResponseStatus status, String errorMessage) {
		super(status, errorMessage);

	}

	public CampaignResult(ResponseStatus status) {
		super(status);

	}

	public List<CampaignValidationStatus> getValidationStatusList() {
		return validationStatusList;
	}

	public void setValidationStatusList(List<CampaignValidationStatus> validationStatusList) {
		this.validationStatusList = validationStatusList;
	}
}
