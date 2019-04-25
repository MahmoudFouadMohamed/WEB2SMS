package com.edafa.web2sms.sms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(name = "EnquireSMSByIdListDetailedRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EnquireSMSByIdListDetailedRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class EnquireSMSByIdListDetailedRequest extends EnquireSMSByIdListRequest {

	@XmlElement(name = "IP")
	protected String ip;
	
	@XmlElement(name = "TRXID")
	protected String trxId;

	
	public EnquireSMSByIdListDetailedRequest() {
		
	}

	public EnquireSMSByIdListDetailedRequest(EnquireSMSByIdListRequest request, String ip, String trxId) {
		
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash=request.secureHash;
		this.smsIdList = request.smsIdList;
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
		builder.append("EnquireSMSByIdListDetailedRequest [");
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
		if (smsIdList != null) {
			builder.append("smsIdList=");
			builder.append(smsIdList);
		}
		builder.append("]");
		return builder.toString();
	}

	

  
}
