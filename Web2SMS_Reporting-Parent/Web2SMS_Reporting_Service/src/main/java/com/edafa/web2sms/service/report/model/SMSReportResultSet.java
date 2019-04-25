package com.edafa.web2sms.service.report.model;

import java.util.List;

import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.SMSReport;

public class SMSReportResultSet extends ResultStatus {

	List<SMSReport> reports;

	SummaryReport summaryReport;

	public SMSReportResultSet() {

	}

	public List<SMSReport> getReports() {
		return reports;
	}

	public void setReports(List<SMSReport> reports) {
		this.reports = reports;
	}

	public SummaryReport getSummaryReport() {
		return summaryReport;
	}

	public void setSummaryReport(SummaryReport summaryReport) {
		this.summaryReport = summaryReport;
	}

}
