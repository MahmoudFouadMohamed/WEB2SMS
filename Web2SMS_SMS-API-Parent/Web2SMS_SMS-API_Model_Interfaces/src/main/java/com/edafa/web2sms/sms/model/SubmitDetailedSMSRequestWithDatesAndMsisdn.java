package com.edafa.web2sms.sms.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SubmitDetailedSMSRequestWithDatesAndMsisdn", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitDetailedSMSRequestWithDatesAndMsisdn", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitDetailedSMSRequestWithDatesAndMsisdn extends SubmitSMSRequestWithDatesAndMsisdn {

	@XmlElement(name = "IP")
	protected String ip;

	@XmlElement(name = "TRXID")
	protected String trxId;
	
	public SubmitDetailedSMSRequestWithDatesAndMsisdn() {
	}

	public SubmitDetailedSMSRequestWithDatesAndMsisdn(SubmitSMSRequestWithDatesAndMsisdn req,String ip, String trxId) {
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
        StringBuilder str = new StringBuilder("SubmitDetailedSMSRequestWithDatesAndMsisdn[ip=");
        str = str.append(ip).append(", trxId=").append(trxId).append(", accountId=").append(accountId)
                .append(", secureHash=").append(secureHash).append(", receiverMSISDN=").append(receiverMSISDN)
                .append(", startDate=").append(startDate).append(", endDate=").append(endDate).append("]");
        return str.toString();
    }

         @XmlTransient
    public String logAccountId() {
        return "Account(" + accountId + ") | ";
    }

}
