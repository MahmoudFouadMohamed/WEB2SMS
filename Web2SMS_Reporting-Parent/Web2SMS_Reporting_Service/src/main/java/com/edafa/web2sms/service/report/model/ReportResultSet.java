/**
 * 
 */
package com.edafa.web2sms.service.report.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.edafa.web2sms.dalayer.model.DateTimeAdapter;
import com.edafa.web2sms.service.model.CampaignAggregationReport;
import com.edafa.web2sms.service.model.ResultStatus;

/**
 * @author khalid
 *
 */
public class ReportResultSet extends ResultStatus {

	List<CampaignAggregationReport> reports;

	SummaryReport summary;

	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	Date dateFrom;
	@XmlElement(required = true, nillable = true)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	Date dateTo;

	public List<CampaignAggregationReport> getReports() {
		return reports;
	}

	public void setReports(List<CampaignAggregationReport> reports) {
		this.reports = reports;
	}

	public SummaryReport getSummary() {
		return summary;
	}

	public void setSummary(SummaryReport summary) {
		this.summary = summary;
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

}
