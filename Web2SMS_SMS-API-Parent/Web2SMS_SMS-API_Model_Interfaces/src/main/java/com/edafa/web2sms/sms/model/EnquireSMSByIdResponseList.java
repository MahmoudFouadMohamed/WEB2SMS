package com.edafa.web2sms.sms.model;



import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.smsgw.smshandler.sms.SMSId;



@XmlType(name = "EnquireSMSByIdResponseList", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnquireSMSByIdResponseList implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1895896399648559936L;

	@XmlElement(name = "smsId")
	protected String smsId;
	
	@XmlElement(name = "SMSStatus")
	protected String smsStatus;
	
	@XmlElement(name= "ReceiverMSISDN")
	protected String reciever;

	
	@XmlElement(name = "SendDate")
	protected String sendDate;
	@XmlElement(name = "ReceivedDate")
	protected String recievedDate;
	@XmlElement(name = "DeliveryDate")
	protected String deliveryDate;
	
	
	
	public EnquireSMSByIdResponseList() {
		this.smsId = SMSId.getSMSId().getId();
	}



	public EnquireSMSByIdResponseList(String smsId, String smsStatus,String reciever,
			String sendDate, String recievedDate, String deliveryDate) {
		super();
		this.smsId = smsId;
		this.smsStatus = smsStatus;
		this.reciever=reciever;
		this.sendDate = sendDate;
		this.recievedDate = recievedDate;
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
		return recievedDate;
	}



	public void setRecievedDate(String recievedDate) {
		this.recievedDate = recievedDate;
	}



	public String getDeliveryDate() {
		return deliveryDate;
	}



	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}



	public String getReciever() {
		return reciever;
	}



	public void setReciever(String reciever) {
		this.reciever = reciever;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnquireSMSByIdResponseList [smsId=");
		builder.append(smsId);
		builder.append(", smsStatus=");
		builder.append(smsStatus);
		builder.append(", reciever=");
		builder.append(reciever);
		builder.append(", sendDate=");
		builder.append(sendDate);
		builder.append(", recievedDate=");
		builder.append(recievedDate);
		builder.append(", deliveryDate=");
		builder.append(deliveryDate);
		builder.append("]");
		return builder.toString();
	}



	
	
	
}
