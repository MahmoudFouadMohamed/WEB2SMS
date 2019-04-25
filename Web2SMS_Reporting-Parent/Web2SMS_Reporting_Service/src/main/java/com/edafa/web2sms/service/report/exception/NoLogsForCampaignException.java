package com.edafa.web2sms.service.report.exception;

public class NoLogsForCampaignException extends Exception {

	String campaignId;

	private static final long serialVersionUID = -6501998843313975215L;

	public NoLogsForCampaignException() {
		super();
	}

	public NoLogsForCampaignException(String campaignId) {
		super((campaignId == null ? "" : "No logs for the campaign with id(" + campaignId + ")"));
		this.campaignId = campaignId;
	}

	public NoLogsForCampaignException(String campaignId, Throwable e) {
		super((campaignId == null ? "" : "No logs for the campaign with id(" + campaignId + ")"), e);
		this.campaignId = campaignId;
	}

	public NoLogsForCampaignException(Throwable cause) {
		super(cause);
	}

	public String getCampaignId() {
		return campaignId;
	}

}
