package com.edafa.web2sms.campaign.execution.exception;

public class NoMoreContactsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3859819394504917410L;
	String campaignId;

	public NoMoreContactsException(String campaignId) {
		super("Campaign(" + campaignId + ") has no more contacts to send SMS to.");
		this.campaignId = campaignId;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
}
