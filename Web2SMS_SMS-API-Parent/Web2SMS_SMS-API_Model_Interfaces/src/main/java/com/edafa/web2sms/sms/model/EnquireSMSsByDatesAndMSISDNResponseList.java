package com.edafa.web2sms.sms.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(name = "EnquireSMSsByDatesAndMSISDNResponseList", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnquireSMSsByDatesAndMSISDNResponseList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4811971675632855421L;

	@XmlElement(name = "SmsDates")
	protected List<SmsDates> smsDatesList;

	public EnquireSMSsByDatesAndMSISDNResponseList() {
	}

	public EnquireSMSsByDatesAndMSISDNResponseList(List<SmsDates> smsDatesList) {
		this.smsDatesList = smsDatesList;
	}

	public List<SmsDates> getSmsDatesList() {
		return smsDatesList;
	}

	public void setSmsDatesList(List<SmsDates> smsDatesList) {
		this.smsDatesList = smsDatesList;
	}
	
	public void setToSmsDatesList(SmsDates smsDates){
		
		if(smsDates==null){
			
			this.smsDatesList= new ArrayList<SmsDates>();
		}
		this.smsDatesList.add(smsDates);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnquireSMSsByDatesAndMSISDNResponseList [smsDatesList=");
		builder.append(smsDatesList);
		builder.append("]");
		return builder.toString();
	}


}
