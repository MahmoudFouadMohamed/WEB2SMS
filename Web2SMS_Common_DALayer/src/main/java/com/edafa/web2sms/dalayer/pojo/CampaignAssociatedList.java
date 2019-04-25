package com.edafa.web2sms.dalayer.pojo;

public class CampaignAssociatedList {

	
	String campaignId;
	String listName;
	
	
	
	public CampaignAssociatedList(String campaignId, String listName) {
		this.campaignId = campaignId;
		this.listName = listName;
	}
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	
	
}
