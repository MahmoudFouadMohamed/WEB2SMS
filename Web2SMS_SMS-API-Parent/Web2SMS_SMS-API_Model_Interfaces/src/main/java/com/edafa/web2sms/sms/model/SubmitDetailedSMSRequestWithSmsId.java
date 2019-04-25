package com.edafa.web2sms.sms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(name = "SubmitDetailedSMSRequestWithSmsId", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitDetailedSMSRequestWithSmsId", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitDetailedSMSRequestWithSmsId extends SubmitSMSRequestWithSmsId {

	@XmlElement(name = "IP")
	protected String ip;
	
	@XmlElement(name = "TRXID")
	protected String trxId;

	public SubmitDetailedSMSRequestWithSmsId() {
		
	}

	public SubmitDetailedSMSRequestWithSmsId(SubmitSMSRequestWithSmsId request,String ip,String trxId) {
		
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash=request.secureHash;
		this.smsId = request.smsId;
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
        StringBuilder str = new StringBuilder("SubmitDetailedSMSRequestWithSmsId[ip=");
        str = str.append(ip).append(", trxId=").append(trxId).append(", accountId=").append(accountId).append(", secureHash=").append(secureHash)
                .append(", smsId=").append(smsId).append("]");
        return str.toString();
    }

}
