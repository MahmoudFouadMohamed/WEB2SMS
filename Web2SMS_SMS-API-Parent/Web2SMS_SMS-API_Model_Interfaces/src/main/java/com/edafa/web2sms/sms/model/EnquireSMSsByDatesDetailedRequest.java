package com.edafa.web2sms.sms.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "EnquireSMSsByDatesDetailedRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EnquireSMSsByDatesDetailedRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class EnquireSMSsByDatesDetailedRequest extends EnquireSMSsByDatesRequest {

	@XmlElement(name = "IP")
	protected String ip;

	@XmlElement(name = "TRXID")
	protected String trxId;
	
	
	public EnquireSMSsByDatesDetailedRequest() {
	}

	public EnquireSMSsByDatesDetailedRequest(EnquireSMSsByDatesRequest req,String ip, String trxId) {
		this.accountId=req.accountId;
		this.password=req.password;
		this.secureHash=req.secureHash;
		this.receiverMSISDN=req.receiverMSISDN;
		this.startDate=req.startDate;
		this.endDate=req.endDate;
		this.ip = ip;
		this.trxId=trxId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

 


		@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnquireSMSsByDatesDetailedRequest [");
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
		if (receiverMSISDN != null) {
			builder.append("receiverMSISDN=");
			builder.append(receiverMSISDN);
			builder.append(", ");
		}
		if (startDate != null) {
			builder.append("startDate=");
			builder.append(startDate);
			builder.append(", ");
		}
		if (endDate != null) {
			builder.append("endDate=");
			builder.append(endDate);
		}
		builder.append("]");
		return builder.toString();
	}

		@XmlTransient
    public String logAccountId() {
        return "Account(" + accountId + ") | ";
    }

}
