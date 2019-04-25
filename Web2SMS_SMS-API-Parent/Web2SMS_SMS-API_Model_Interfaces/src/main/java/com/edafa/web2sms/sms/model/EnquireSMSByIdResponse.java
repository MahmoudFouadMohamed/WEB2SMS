package com.edafa.web2sms.sms.model;



import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.ResultStatus;



@XmlType(name = "EnquireSMSByIdResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EnquireSMSByIdResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class EnquireSMSByIdResponse {

	
	@XmlElement(name = "EnquireSMSByIdResponseList")
	protected List<EnquireSMSByIdResponseList> smsStatusWithSmsId;
	
	@XmlElement(name = "ResultStatus")
	protected ResultStatus resultStatus;
	
	@XmlElement(name = "Description")
	protected String description;

	public EnquireSMSByIdResponse() {
	}
	
	public List<EnquireSMSByIdResponseList> getSmsStatusWithSmsId() {
		return smsStatusWithSmsId;
	}

	public void setSmsStatusWithSmsId(List<EnquireSMSByIdResponseList> smsStatusWithSmsId) {
		this.smsStatusWithSmsId = smsStatusWithSmsId;
	}
	
	public void setToSmsStatusWithSmsId(EnquireSMSByIdResponseList smsStatuswithId) {
		if(this.smsStatusWithSmsId == null){
			this.smsStatusWithSmsId =  new ArrayList<EnquireSMSByIdResponseList>();
		}
		
		this.smsStatusWithSmsId.add(smsStatuswithId);
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
		builder.append("EnquireSMSByIdResponse [smsStatusWithSmsId=");
		builder.append(smsStatusWithSmsId);
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
