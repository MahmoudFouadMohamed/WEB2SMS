package com.edafa.web2sms.dalayer.pojo;

import java.util.Date;

public class ProvisioningEvent {
	
	private Date eventDate;
	private String accountId;
	
	public ProvisioningEvent(Date eventDate, String accountId) {
		this.eventDate = eventDate;
		this.accountId = accountId;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	@Override
	public String toString(){
		return "ProvEvent by accountId("+this.accountId+") in Date ("+this.eventDate+"). ";
	}

	
	
	
	

}
