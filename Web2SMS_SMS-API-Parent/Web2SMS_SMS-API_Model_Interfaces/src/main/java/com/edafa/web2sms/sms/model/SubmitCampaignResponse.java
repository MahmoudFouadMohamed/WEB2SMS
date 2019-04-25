package com.edafa.web2sms.sms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.ResultStatus;

@XmlType(name = "SubmitCamaignResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitCamaignResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitCampaignResponse {

	@XmlElement(name = "campaignId")
	protected String campaignId;

	@XmlElement(name = "ResultStatus")
	protected ResultStatus resultStatus;

	@XmlElement(name = "Description")
	protected String description;

	public SubmitCampaignResponse() {
	}

	
	public String getDescription() {
		return description;
	}

	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubmitCampaignResponse [campaignId=");
		builder.append(campaignId);
		builder.append(", resultStatus=");
		builder.append(resultStatus);
            if (description != null) {
                builder.append(", description=");
                builder.append(description);
            }
		builder.append("]");
		return builder.toString();
	}
	
}
