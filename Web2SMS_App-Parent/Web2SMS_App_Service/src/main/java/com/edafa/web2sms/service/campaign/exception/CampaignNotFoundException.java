package com.edafa.web2sms.service.campaign.exception;

public class CampaignNotFoundException extends Exception {

	String campaignId;

	private static final long serialVersionUID = -6501998843313975215L;

	public CampaignNotFoundException() {
		super();
	}

	public CampaignNotFoundException(String campaignId) {
		super((campaignId == null ? "Campaign not found, the field campaignId should not be null" : "Campaign with id("
				+ campaignId + ") not found"));
		this.campaignId = campaignId;
	}

	public CampaignNotFoundException(String campaignId, Throwable e) {
		super((campaignId == null ? "Campaign not found, the field campaignId should not be null" : "Campaign with id("
				+ campaignId + ") not found"), e);
		this.campaignId = campaignId;
	}

	public CampaignNotFoundException(Throwable cause) {
		super(cause);
	}

	public String getCampaignId() {
		return campaignId;
	}

}
