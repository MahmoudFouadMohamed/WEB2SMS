package com.edafa.web2sms.sms.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignReceiverDetails", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class CampaignRecieverDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -999383164663964593L;

	@XmlElement(name = "ReceiverMSISDN")
	protected List<String> receiverMSISDN;

	public CampaignRecieverDetails() {
	}

	public List<String> getReceiverMSISDN() {
		return receiverMSISDN;
	}

	public void setReceiverMSISDN(List<String> receiverMSISDN) {
		this.receiverMSISDN = receiverMSISDN;
	}
	
	@Override
	public String toString() {
		return "CampaignReceiverDetails [receiverMSISDN=" + receiverMSISDN + "]";
	}

}
