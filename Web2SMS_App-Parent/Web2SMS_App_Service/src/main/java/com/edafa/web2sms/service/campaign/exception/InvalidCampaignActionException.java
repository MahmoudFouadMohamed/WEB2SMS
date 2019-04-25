package com.edafa.web2sms.service.campaign.exception;

public class InvalidCampaignActionException extends Exception {


	private static final long serialVersionUID = 4176259173363651677L;

	public InvalidCampaignActionException(String message) {
		super(message);
	}

	public InvalidCampaignActionException(String message, Throwable cause) {
		super(message, cause);
	}
}
