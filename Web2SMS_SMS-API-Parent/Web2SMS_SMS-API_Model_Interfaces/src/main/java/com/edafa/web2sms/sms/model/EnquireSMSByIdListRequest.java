package com.edafa.web2sms.sms.model;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "EnquireListOfSMSByIdRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EnquireListOfSMSByIdRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class EnquireSMSByIdListRequest {
	
	@XmlElement(name = "AccountId")
	protected String accountId;
	@XmlElement(name = "Password")
	protected String password;
	
	@XmlElement(name = "SecureHash")
	protected String secureHash;
	
	@XmlElement(name = "smsId")
	protected ArrayList<String> smsIdList;

	@XmlTransient
	boolean plainToString = false;
	
	
	public boolean isPlainToString() {
		return plainToString;
	}

	public void setPlainToString(boolean plainToString) {
		this.plainToString = plainToString;
	}
	
	public EnquireSMSByIdListRequest() {
	}

	public EnquireSMSByIdListRequest(String accountId, String password, String secureHash,ArrayList<String> smsId) {
		this.accountId = accountId;
		this.password = password;
		this.secureHash=secureHash;
		this.smsIdList = smsId;
	}

	public EnquireSMSByIdListRequest(EnquireSMSByIdListRequest request) {
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash=request.secureHash;
		this.smsIdList = request.smsIdList;
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

	public ArrayList<String> getSmsIdList() {
		return smsIdList;
	}

	public void setSmsIdList(ArrayList<String> smsId) {
		this.smsIdList = smsId;
	}

	public String getSecureHash() {
		return secureHash;
	}

	public void setSecureHash(String secureHash) {
		this.secureHash = secureHash;
	}

  
    


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnquireSMSByIdListRequest [");
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
		if (smsIdList != null) {
			builder.append("smsIdList=");
			builder.append(smsIdList);
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

	@XmlTransient
	public String logAccountId() {
		return "Account(" + accountId + ") | ";
	}
	
}
