package com.edafa.web2sms.sms.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.edafa.web2sms.sms.enums.ResultStatus;



@XmlType(name = "EnquireSMSsByDatesAndMSISDNResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EnquireSMSsByDatesAndMSISDNResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class EnquireSMSsByDatesAndMSISDNResponse {

	@XmlElement(name = "EnquireSMSsByDatesAndMSISDNResponseList")
	protected EnquireSMSsByDatesAndMSISDNResponseList responseList;
	
	
	@XmlElement(name = "ResultStatus")
	protected ResultStatus resultStatus;
	
	@XmlElement(name = "Description")
	protected String description;

	public EnquireSMSsByDatesAndMSISDNResponse() {
	}

	public EnquireSMSsByDatesAndMSISDNResponse(EnquireSMSsByDatesAndMSISDNResponseList responseList,ResultStatus resultStatus,
			String description) {
		this.responseList = responseList;
		this.resultStatus = resultStatus;
		this.description = description;
	}

	public EnquireSMSsByDatesAndMSISDNResponseList getResponseList() {
		return responseList;
	}

	public void setResponseList(EnquireSMSsByDatesAndMSISDNResponseList responseList) {
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
		builder.append("EnquireSMSsByDatesAndMSISDNResponse [responseList=");
		builder.append(responseList);
		builder.append(", resultStatus=");
		builder.append(resultStatus);
            if (description != null) {
                builder.append(", description=");
                builder.append(description);
            }
		builder.append("]");
		return builder.toString();
	}
	
	public String getResponse() {
		StringBuilder builder = new StringBuilder();
		builder.append("[resultStatus=");
		builder.append(resultStatus);
            if (description != null) {
                builder.append(", description=");
                builder.append(description);
            }
		builder.append("]");
		return builder.toString();
	}


}
