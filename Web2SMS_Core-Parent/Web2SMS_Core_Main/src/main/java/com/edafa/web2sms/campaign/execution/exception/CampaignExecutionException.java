package com.edafa.web2sms.campaign.execution.exception;

public class CampaignExecutionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6944915419613957475L;

	public CampaignExecutionException() {
		super();
	}

	public CampaignExecutionException(String message) {
		super(message);
	}

	public CampaignExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CampaignExecutionException(Throwable cause) {
		super(cause);
	}
}
