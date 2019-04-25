package com.edafa.web2sms.sms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignInquiryRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CampaignInquiryRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class CampaignInquiryRequest {

	@XmlElement(name = "AccountId")
	protected String accountId;
	@XmlElement(name = "Password")
	protected String password;
	@XmlElement(name = "SecureHash")
	protected String secureHash;
	@XmlElement(name = "CampaignId")
	protected String campaignId;

	

	public CampaignInquiryRequest() {
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

	
	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	@XmlTransient
	public String logId() {
		return "Account(" + accountId + ") | ";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CampaignInquiryRequest [");
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
		if (campaignId != null) {
			builder.append("campaignId=");
			builder.append(campaignId);
			builder.append(", ");
		}
			return builder.toString();
	}
	

	
	

}
