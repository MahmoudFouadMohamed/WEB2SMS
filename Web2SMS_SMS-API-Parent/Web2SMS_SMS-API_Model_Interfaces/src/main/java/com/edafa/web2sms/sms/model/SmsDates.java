package com.edafa.web2sms.sms.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.smsgw.smshandler.sms.SMSId;


@XmlType(name = "SmsDates", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SmsDates implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5308785746440038235L;
	@XmlElement(name = "smsId")
	protected String smsId;
	
	@XmlElement(name = "SMSStatus")
	protected String smsStatus;

	
	@XmlElement(name = "SendDate")
	protected String sendDate;
	@XmlElement(name = "ReceivedDate")
	protected String receivedDate;
	@XmlElement(name = "DeliveryDate")
	protected String deliveryDate;
	
	
	public SmsDates() {
		this.smsId = SMSId.getSMSId().getId();
	}


	public SmsDates(String smsId, String smsStatus, String sendDate,
			String recievedDate, String deliveryDate) {
		super();
		this.smsId = smsId;
		this.smsStatus = smsStatus;
		this.sendDate = sendDate;
		this.receivedDate = recievedDate;
		this.deliveryDate = deliveryDate;
	}


	public String getSmsId() {
		return smsId;
	}


	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}


	public String getSmsStatus() {
		return smsStatus;
	}


	public void setSmsStatus(String smsStatus) {
		this.smsStatus = smsStatus;
	}


	public String getSendDate() {
		return sendDate;
	}


	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}


	public String getRecievedDate() {
		return receivedDate;
	}


	public void setRecievedDate(String recievedDate) {
		this.receivedDate = recievedDate;
	}


	public String getDeliveryDate() {
		return deliveryDate;
	}


	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	@Override
	public String toString() {
		return "SmsDates [smsId=" + smsId + ", smsStatus=" + smsStatus
				+ ", sendDate=" + sendDate + ", recievedDate=" + receivedDate
				+ ", deliveryDate=" + deliveryDate + "]";
	}
	
	
}
