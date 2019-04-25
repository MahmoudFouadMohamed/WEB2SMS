package com.edafa.web2sms.dalayer.pojo;

import java.util.Date;

public class CampaignAggregation {

	private static final long serialVersionUID = 1L;

	private String campaignId;

	private String accountId;
	
	private String username;

	private String campaignName;

	private String senderName;
	private Integer submitted;
	private Integer total;

	private Date creationDate;
	private Integer listsNum;
	private Integer receipentsNum;

	private String campaignStatus;

	private Date startDate;

	private Date endDate;
	private Integer smsSegNum;
	private Integer smsDeliveredNum;

	private String sms;
	private Integer smsPendingNum;

	public CampaignAggregation() {
	}

	public CampaignAggregation(String campaignId, String accountId, String username, String campaignName, String senderName,
			Integer submitted, Integer total, Date creationDate, Integer listsNum, Integer receipentsNum,
			String campaignStatus, Date startDate, Date endDate, Integer smsSegNum, Integer smsDeliveredNum,
			String sms, Integer smsPendingNum) {
		this.campaignId = campaignId;
		this.accountId = accountId;
		this.username = username;
		this.campaignName = campaignName;
		this.senderName = senderName;
		this.submitted = submitted;
		this.total = total;
		this.creationDate = creationDate;
		this.listsNum = listsNum;
		this.receipentsNum = receipentsNum;
		this.campaignStatus = campaignStatus;
		this.startDate = startDate;
		this.endDate = endDate;
		this.smsSegNum = smsSegNum;
		this.smsDeliveredNum = smsDeliveredNum;
		this.sms = sms;
		this.smsPendingNum = smsPendingNum;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Integer getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Integer submitted) {
		this.submitted = submitted;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getListsNum() {
		return listsNum;
	}

	public void setListsNum(Integer listsNum) {
		this.listsNum = listsNum;
	}

	public Integer getReceipentsNum() {
		return receipentsNum;
	}

	public void setReceipentsNum(Integer receipentsNum) {
		this.receipentsNum = receipentsNum;
	}

	public String getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(String campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getSmsSegNum() {
		return smsSegNum;
	}

	public void setSmsSegNum(Integer smsSegNum) {
		this.smsSegNum = smsSegNum;
	}

	public Integer getSmsDeliveredNum() {
		return smsDeliveredNum;
	}

	public void setSmsDeliveredNum(Integer smsDeliveredNum) {
		this.smsDeliveredNum = smsDeliveredNum;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public Integer getSmsPendingNum() {
		return smsPendingNum;
	}

	public void setSmsPendingNum(Integer smsPendingNum) {
		this.smsPendingNum = smsPendingNum;
	}
}
