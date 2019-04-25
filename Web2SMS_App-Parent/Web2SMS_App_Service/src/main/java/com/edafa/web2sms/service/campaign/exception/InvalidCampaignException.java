package com.edafa.web2sms.service.campaign.exception;

import java.util.ArrayList;
import java.util.List;

import com.edafa.web2sms.service.enums.CampaignValidationStatus;

public class InvalidCampaignException extends Exception {

	protected List<CampaignValidationStatus> statusList;

	private static final long serialVersionUID = 4176259173363651677L;

	public InvalidCampaignException(List<CampaignValidationStatus> statusList) {
		super("Invalid campaign, validation status list:" + statusList);
		this.statusList = statusList;
	}

	public InvalidCampaignException(String message, List<CampaignValidationStatus> statusList) {
		super(message + "Validation status list:" + statusList);
		this.statusList = statusList;
	}

	public InvalidCampaignException(String message) {
		super(message);
		this.statusList = new ArrayList<>();
	}

	public InvalidCampaignException(String message, Throwable cause) {
		super(message, cause);
		this.statusList = new ArrayList<>();
	}

	public List<CampaignValidationStatus> getValidationStatusList() {
		return statusList;
	}
}
