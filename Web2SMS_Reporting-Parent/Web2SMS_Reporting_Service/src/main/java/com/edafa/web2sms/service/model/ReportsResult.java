package com.edafa.web2sms.service.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.model.Reports;

@XmlType(name = "ReportsResult", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
public class ReportsResult extends ResultStatus {

	@XmlElement(required = true, nillable = false)
	private List<Reports> reports;

	public ReportsResult() {
		reports = new ArrayList<>();
	}

	public void addReport(Reports report) {
		this.reports.add(report);
	}

	public List<Reports> getReports() {
		return reports;
	}

	public void setReports(List<Reports> reports) {
		this.reports = reports;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{reports=").append(reports).append("}");
		return builder.toString();
	}

}
