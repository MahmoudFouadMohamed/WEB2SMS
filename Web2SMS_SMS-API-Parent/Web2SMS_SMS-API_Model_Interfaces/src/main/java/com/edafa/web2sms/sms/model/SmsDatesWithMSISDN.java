package com.edafa.web2sms.sms.model;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.smsgw.smshandler.sms.SMSId;


@XmlType(name = "SmsDatesWithMSISDN", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SmsDatesWithMSISDN extends SmsDates implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6320206597048767415L;

	@XmlElement(name = "ReceiverMSISDN")
	protected String receiverMSISDN;

	public SmsDatesWithMSISDN() {
		this.smsId = SMSId.getSMSId().getId();
	}

	public SmsDatesWithMSISDN(String smsId,String receiverMSISDN , String smsStatus, String sendDate,
			String recievedDate, String deliveryDate) {
		super(smsId, smsStatus, sendDate, recievedDate, deliveryDate);
		
		this.smsId=smsId;
		this.receiverMSISDN=receiverMSISDN;
		this.smsStatus=smsStatus;
		this.sendDate=sendDate;
		this.receivedDate=recievedDate;
		this.deliveryDate=deliveryDate;
		// TODO Auto-generated constructor stub
	}

	public String getReceiverMSISDN() {
		return receiverMSISDN;
	}

	public void setReceiverMSISDN(String receiverMSISDN) {
		this.receiverMSISDN = receiverMSISDN;
	}

	@Override
	public String toString() {
		return "SmsDatesWithMSISDN [receiverMSISDN=" + receiverMSISDN
				+ ", smsId=" + smsId + ", smsStatus=" + smsStatus
				+ ", sendDate=" + sendDate + ", recievedDate=" + receivedDate
				+ ", deliveryDate=" + deliveryDate + "] \n";
	}
	
}
