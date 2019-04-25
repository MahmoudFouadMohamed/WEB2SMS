package com.edafa.web2sms.sms.model;

import javax.ejb.EJB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignInquiryDetailedRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CampaignInquiryDetailedRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class CampaignInquiryDetailedRequest extends CampaignInquiryRequest {
	@XmlElement(name = "IP")
	protected String ip;
	@XmlElement(name = "TRXID")
	protected String trxId;
	
	
	public CampaignInquiryDetailedRequest() {

	}

	public CampaignInquiryDetailedRequest(CampaignInquiryRequest request, String ip, String trxId) {
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash = request.secureHash;
		this.campaignId = request.campaignId;
		this.ip = ip;
		this.trxId = trxId;
	}

	
	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@XmlTransient
    public String logAccountId() {
        return "Account(" + accountId + ") | ";
    }

	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CampaignInquiryDetailedRequest [");
		if (ip != null) {
			builder.append("ip=");
			builder.append(ip);
			builder.append(", ");
		}
		if (trxId != null) {
			builder.append("trxId=");
			builder.append(trxId);
			builder.append(", ");
		}
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
		}
		builder.append("]");
		return builder.toString();
	}

	
	

}
