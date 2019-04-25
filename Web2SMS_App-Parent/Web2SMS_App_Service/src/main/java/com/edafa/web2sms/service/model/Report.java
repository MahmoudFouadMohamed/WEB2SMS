package com.edafa.web2sms.service.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="Report", namespace = "http://www.edafa.com/web2sms/service/model/")
public class Report {

	@XmlElement(required= true , nillable= false)
	String campaignName;
	@XmlElement(required= true , nillable= false)
	String senderName;
	@XmlElement(required= true , nillable= false)
	String status;
	
	@XmlElement(required= true , nillable= false)
	int sentSMS;
	@XmlElement(required= true , nillable= false)
	int totalSMS;
	@XmlElement(required= true , nillable= false)
	int numberOfLists;
	@XmlElement(required= true , nillable= false)
	int deliveredSMSs;
	@XmlElement(required= true , nillable= false)
	int numberOfRecipients;
	@XmlElement(required= true , nillable= false)
	int numberOfMessages;
	@XmlElement(required= true , nillable= false)
	int numberOfCharacters;
	@XmlElement(required= true , nillable= false)
	int pendingSMSs;
	
	@XmlElement(required= true , nillable= false)
	Date creationDate;
	@XmlElement(required= true , nillable= false)
	Date startDate;
	@XmlElement(required= true , nillable= false)
	Date endDate;

	public String getCampaignName() {
		return campaignName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getSentSMS() {
		return sentSMS;
	}

	public void setSentSMS(int sentSMS) {
		this.sentSMS = sentSMS;
	}

	public int getTotalSMS() {
		return totalSMS;
	}

	public void setTotalSMS(int totalSMS) {
		this.totalSMS = totalSMS;
	}

	public int getNumberOfLists() {
		return numberOfLists;
	}

	public void setNumberOfLists(int numberOfLists) {
		this.numberOfLists = numberOfLists;
	}

	public int getDeliveredSMSs() {
		return deliveredSMSs;
	}

	public void setDeliveredSMSs(int deliveredSMSs) {
		this.deliveredSMSs = deliveredSMSs;
	}

	public int getNumberOfRecipients() {
		return numberOfRecipients;
	}

	public void setNumberOfRecipients(int numberOfRecipients) {
		this.numberOfRecipients = numberOfRecipients;
	}

	public int getNumberOfMessages() {
		return numberOfMessages;
	}

	public void setNumberOfMessages(int numberOfMessages) {
		this.numberOfMessages = numberOfMessages;
	}

	public int getNumberOfCharacters() {
		return numberOfCharacters;
	}

	public void setNumberOfCharacters(int numberOfCharacters) {
		this.numberOfCharacters = numberOfCharacters;
	}

	public int getPendingSMSs() {
		return pendingSMSs;
	}

	public void setPendingSMSs(int pendingSMSs) {
		this.pendingSMSs = pendingSMSs;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
	
	

}
