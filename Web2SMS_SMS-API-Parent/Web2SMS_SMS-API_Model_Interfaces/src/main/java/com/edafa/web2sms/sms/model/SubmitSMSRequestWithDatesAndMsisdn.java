package com.edafa.web2sms.sms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;



@XmlType(name = "SubmitSMSRequestWithDatesAndMsisdn", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitSMSRequestWithDatesAndMsisdn", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitSMSRequestWithDatesAndMsisdn {

	@XmlElement(name = "AccountId")
	protected String accountId;
	@XmlElement(name = "Password")
	protected String password;	
	@XmlElement(name = "SecureHash")
	protected String secureHash;
	
	@XmlElement(name = "ReceiverMSISDN")
	protected String receiverMSISDN;
	
	
	@XmlElement(name = "StartDate")
	protected String startDate;
	
	@XmlElement(name = "EndDate")
	protected String endDate;
	

	public SubmitSMSRequestWithDatesAndMsisdn() {
	}

	public SubmitSMSRequestWithDatesAndMsisdn(String accountId,String password, String secureHash, String receiverMSISDN,
			String startDate, String endDate) {
		this.accountId = accountId;
		this.password = password;
		this.secureHash = secureHash;
		this.receiverMSISDN = receiverMSISDN;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public SubmitSMSRequestWithDatesAndMsisdn(SubmitSMSRequestWithDatesAndMsisdn req) {

		this.accountId = req.accountId;
		this.password = req.password;
		this.secureHash=req.secureHash;
		this.receiverMSISDN=req.receiverMSISDN;
		this.startDate=req.startDate;
		this.endDate=req.endDate;
	}
	
	
	
	

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecureHash() {
		return secureHash;
	}

	public void setSecureHash(String secureHash) {
		this.secureHash = secureHash;
	}

	public String getReceiverMSISDN() {
		return receiverMSISDN;
	}

	public void setReceiverMSISDN(String receiverMSISDN) {
		this.receiverMSISDN = receiverMSISDN;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "SubmitSMSRequestWithDatesAndMsisdn [accountId=" + accountId
				+ ", password= *****" + ", secureHash=" + secureHash
				+ ", receiverMSISDN=" + receiverMSISDN + ", startDate="
				+ startDate + ", endDate=" + endDate + "]";
	}

	@XmlTransient
	public String logId() {
		return "Account(" + accountId + ") | ";
	}
	
	
	
	
}
