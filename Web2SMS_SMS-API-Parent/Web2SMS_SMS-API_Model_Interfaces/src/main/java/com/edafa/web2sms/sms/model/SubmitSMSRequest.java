package com.edafa.web2sms.sms.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SubmitSMSRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitSMSRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitSMSRequest implements Serializable {

	private static final long serialVersionUID = 7728022522460509370L;

	@XmlElement(name = "AccountId")
	protected String accountId;
	@XmlElement(name = "Password")
	protected String password;
	@XmlElement(name = "SecureHash")
	protected String secureHash;
	@XmlElement(name = "SMSList")
	protected List<SMSDetails> SMSList;

	@XmlTransient
	boolean plainToString = false;
	
	
	public boolean isPlainToString() {
		return plainToString;
	}

	public void setPlainToString(boolean plainToString) {
		this.plainToString = plainToString;
	}

	public SubmitSMSRequest() {

	}

	public SubmitSMSRequest(String accountId, String password, String secureHash, List<SMSDetails> SMSList) {
		this.accountId = accountId;
		this.password = password;
		this.secureHash = secureHash;
		this.SMSList = SMSList;
	}

	public SubmitSMSRequest(SubmitSMSRequest request) {
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash = request.secureHash;
		this.SMSList = request.SMSList;
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

	public List<SMSDetails> getSMSs() {
		return SMSList;
	}

	public void setSMSs(List<SMSDetails> sMSs) {
		SMSList = sMSs;
	}

	@XmlTransient
	public String logAccountId() {
		return "Account(" + accountId + ") | ";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubmitSMSRequest [");
		if (accountId != null) {
			builder.append("accountId=");
			builder.append(accountId);
			builder.append(", ");
		}
		if (password != null) {
			builder.append("password=");
			builder.append(password);
			builder.append(", ");
		}
		if (secureHash != null) {
			builder.append("secureHash=");
			builder.append(secureHash);
			builder.append(", ");
		}
		if (SMSList != null) {
			builder.append("SMSList=");
			builder.append(SMSList);
			builder.append(", ");
		}
		builder.append("plainToString=");
		builder.append(plainToString);
		
		builder.append("]");
		if(!plainToString)
			return " ";
		else 
			return builder.toString();
	}

	
}
