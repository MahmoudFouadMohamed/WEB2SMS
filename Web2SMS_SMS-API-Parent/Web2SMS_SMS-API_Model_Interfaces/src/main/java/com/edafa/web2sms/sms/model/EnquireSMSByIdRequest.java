package com.edafa.web2sms.sms.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "EnquireSMSByIdRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EnquireSMSByIdRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class EnquireSMSByIdRequest {
	
	@XmlElement(name = "AccountId")
	protected String accountId;
	@XmlElement(name = "Password")
	protected String password;
	
	@XmlElement(name = "SecureHash")
	protected String secureHash;
	
	@XmlElement(name = "smsId")
	protected String smsId;
	@XmlTransient
	boolean plainToString = false;
	
	
	public boolean isPlainToString() {
		return plainToString;
	}

	public void setPlainToString(boolean plainToString) {
		this.plainToString = plainToString;
	}

	public EnquireSMSByIdRequest() {
	}

	public EnquireSMSByIdRequest(String accountId, String password, String secureHash,String smsId) {
		this.accountId = accountId;
		this.password = password;
		this.secureHash=secureHash;
		this.smsId = smsId;
	}

	public EnquireSMSByIdRequest(EnquireSMSByIdRequest request) {
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash=request.secureHash;
		this.smsId = request.smsId;
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

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
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
		builder.append("EnquireSMSByIdRequest [");
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
		if (smsId != null) {
			builder.append("smsId=");
			builder.append(smsId);
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
