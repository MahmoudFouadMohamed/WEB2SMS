package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignAggregationReportResult", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
public class CampaignAggregationReportResult extends ResultStatus {

	@XmlElement(required = true, nillable = false)
	private CampaignAggregationReport campaignAggregationReport;

	public CampaignAggregationReportResult() {}

	public CampaignAggregationReport getCampaignAggregationReport() {
		return campaignAggregationReport;
	}

	public void setCampaignAggregationReport(CampaignAggregationReport campaignAggregationReport) {
		this.campaignAggregationReport = campaignAggregationReport;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{campaignAggregationReport=").append(campaignAggregationReport).append("}");
		return builder.toString();
	}

}
