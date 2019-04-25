package com.edafa.web2sms.service.campaign.exception;

public class InvalidCampaignStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2142768628379125931L;

	public InvalidCampaignStateException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidCampaignStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCampaignStateException(String message) {
		super(message);
	}

	public InvalidCampaignStateException(Throwable cause) {
		super(cause);
	}

}
