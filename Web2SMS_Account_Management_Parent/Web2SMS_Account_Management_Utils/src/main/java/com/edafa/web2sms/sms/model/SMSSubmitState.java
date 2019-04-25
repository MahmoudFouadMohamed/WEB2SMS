package com.edafa.web2sms.sms.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.SMSResponseStatus;

@XmlType(name = "SMSSubmitState", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMSSubmitState implements Serializable
{
	private static final long serialVersionUID = -4098077934512632235L;
	
	@XmlTransient
	private String smsId;
	
	@XmlTransient
	private SMSResponseStatus smsResponseStatus;

	public SMSSubmitState()
	{
	}

	public SMSSubmitState(String smsId, SMSResponseStatus smsResponseStatus)
	{
		this.smsId = smsId;
		this.smsResponseStatus = smsResponseStatus;
	}

	public String getSmsId()
	{
		return smsId;
	}

	public void setSmsId(String smsId)
	{
		this.smsId = smsId;
	}

	public SMSResponseStatus getSmsResponseStatus()
	{
		return smsResponseStatus;
	}

	public void setSmsResponseStatus(SMSResponseStatus smsResponseStatus)
	{
		this.smsResponseStatus = smsResponseStatus;
	}

	@Override
	public String toString()
	{
		return "SMSStatus [smsId=" + smsId + ", smsResponseStatus=" + smsResponseStatus + "]";
	}
}// end of class SMSSubmitState