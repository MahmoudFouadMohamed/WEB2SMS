package com.edafa.web2sms.sms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.ResultStatus;

@XmlType(name = "EnquireCampaignByIdResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EnquireCampaignByIdResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class EnquireCampaignByIdResponse {

	@XmlElement(name ="NumOfDeliveredSeg")
	protected Integer numOfDeliveredSeg;
	
	@XmlElement(name ="NumOfUnDeliveredSeg")
	protected Integer numOfUnDeliveredSeg;
	
	@XmlElement(name ="CampaignStatus")
	protected String campaignStatus;
	
	@XmlElement(name ="SubmittedRatio")
	protected Double submittedRatio;
	
	@XmlElement(name = "ResultStatus")
	protected ResultStatus resultStatus;

	@XmlElement(name = "Description")
	protected String description;

	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(String campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public double getSubmittedRatio() {
		return submittedRatio;
	}

	public void setSubmittedRatio(double submittedRatio) {
		this.submittedRatio = submittedRatio;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getNumOfDeliveredSeg() {
		return numOfDeliveredSeg;
	}

	public void setNumOfDeliveredSeg(Integer numOfDeliveredSeg) {
		this.numOfDeliveredSeg = numOfDeliveredSeg;
	}

	public Integer getNumOfUnDeliveredSeg() {
		return numOfUnDeliveredSeg;
	}

	public void setNumOfUnDeliveredSeg(Integer numOfUnDeliveredSeg) {
		this.numOfUnDeliveredSeg = numOfUnDeliveredSeg;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnquireCampaignByIdResponse [campaignStatus=");
		builder.append(campaignStatus);
		builder.append(", description=");
		builder.append(description);
		builder.append(", numOfDeliveredSeg=");
		builder.append(numOfDeliveredSeg);
		builder.append(", numOfUnDeliveredSeg=");
		builder.append(numOfUnDeliveredSeg);
		builder.append(", resultStatus=");
		builder.append(resultStatus);
		builder.append(", submittedRatio=");
		builder.append(submittedRatio);
		builder.append("]");
		return builder.toString();
	}



	
	
	
	
}
