package com.edafa.web2sms.sms.model;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.smsgw.smshandler.sms.SMSId;
import com.edafa.web2sms.sms.enums.SMSResponseStatus;

@XmlType(name = "SMSResponseBySmsId", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMSResponseBySmsId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3983645507214408664L;
	
	@XmlElement(name = "ReceiverMSISDN")
	protected String receiverMSISDN;
	
	@XmlElement(name = "smsId")
	protected String smsId;
	
	@XmlElement(name = "SMSStatus")
	protected SMSResponseStatus smsStatus;
	
	
	public SMSResponseBySmsId(){
		this.smsId = SMSId.getSMSId().getId();
	}

	public SMSResponseBySmsId(String receiverMSISDN, String smsId,
			SMSResponseStatus smsStatus) {
		super();
		this.receiverMSISDN = receiverMSISDN;
		this.smsId = smsId;
		this.smsStatus = smsStatus;
	}
	
	
	public String getReceiverMSISDN() {
		return receiverMSISDN;
	}

	public void setReceiverMSISDN(String receiverMSISDN) {
		this.receiverMSISDN = receiverMSISDN;
	}

	public SMSResponseStatus getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(SMSResponseStatus smsStatus) {
		this.smsStatus = smsStatus;
	}

	public String getSMSId()
	{
		return smsId;
	}

	public void setSmsId(String smsId)
	{
		this.smsId = smsId;
	}

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SMSResponseBySmsId{");
        str = str.append("receiverMSISDN=").append(receiverMSISDN).append(", smsId=").append(smsId).append(", smsStatus=").append(smsStatus).append('}');
        return str.toString();
    }
        
}
