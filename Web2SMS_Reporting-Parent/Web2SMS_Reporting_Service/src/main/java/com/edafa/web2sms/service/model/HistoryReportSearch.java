package com.edafa.web2sms.service.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.edafa.web2sms.dalayer.model.DateTimeAdapter;

@XmlType(name = "HistoryReportSearch", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
public class HistoryReportSearch {

	@XmlElement(required = false, nillable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date dateFrom;

	@XmlElement(required = false, nillable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date dateTo;

	@XmlElement(required = false, nillable = false)
	private String senderName;

	public HistoryReportSearch() {}

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{dateFrom=").append(dateFrom).append(", dateTo=").append(dateTo).append(", senderName=")
				.append(senderName).append("}");
		return builder.toString();
	}

}
