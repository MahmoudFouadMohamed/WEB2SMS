package com.edafa.web2sms.service.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.edafa.web2sms.service.campaign.DateTimeAdapter;

@XmlType(name = "HistoryReportSearch", namespace = "http://www.edafa.com/web2sms/service/model/")
public class HistoryReportSearch {
	
	@XmlElement(required = false, nillable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	Date dateFrom;
	@XmlElement(required = false, nillable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	Date dateTo;
	@XmlElement(required = false, nillable = false)
	String senderName;
	@XmlElement(required = false, nillable = false)
	String smsText;
	
	HistoryReportSearch(){

	}
	
	public Date getDateFrom() {
		return dateFrom;
	}
	
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	@Override
	public String toString() {
		return "HistoryReportSearch [dateFrom=" + dateFrom + ", dateTo=" + dateTo + ", senderName=" + senderName
				+ ", smsText=" + smsText + "]";
	}
	
	
}
