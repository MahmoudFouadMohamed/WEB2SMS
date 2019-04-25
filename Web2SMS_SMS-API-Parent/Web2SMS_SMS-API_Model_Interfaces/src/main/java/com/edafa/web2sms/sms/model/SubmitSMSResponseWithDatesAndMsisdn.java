package com.edafa.web2sms.sms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.model.SMSResponseWithDatesAndMsisdn;



@XmlType(name = "SubmitSMSResponseWithDatesAndMsisdn", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitSMSResponseWithDatesAndMsisdn", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitSMSResponseWithDatesAndMsisdn {

	
	@XmlElement(name = "SMSResponseWithDatesAndMsisdn")
	protected SMSResponseWithDatesAndMsisdn responseList;
	
	
	@XmlElement(name = "ResultStatus")
	protected ResultStatus resultStatus;
	
	@XmlElement(name = "Description")
	protected String description;

	public SubmitSMSResponseWithDatesAndMsisdn() {
	}

	public SubmitSMSResponseWithDatesAndMsisdn(SMSResponseWithDatesAndMsisdn responseList,ResultStatus resultStatus,
			String description) {
		this.responseList = responseList;
		this.resultStatus = resultStatus;
		this.description = description;
	}

	public SMSResponseWithDatesAndMsisdn getResponseList() {
		return responseList;
	}

	public void setResponseList(SMSResponseWithDatesAndMsisdn responseList) {
		this.responseList = responseList;
	}

	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubmitSMSResponseWithDatesAndMsisdn [responseList=");
		builder.append(responseList);
		builder.append(", resultStatus=");
		builder.append(resultStatus);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
	
	public String getResponse() {
		StringBuilder builder = new StringBuilder();
		builder.append("[resultStatus=");
		builder.append(resultStatus);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}


}
