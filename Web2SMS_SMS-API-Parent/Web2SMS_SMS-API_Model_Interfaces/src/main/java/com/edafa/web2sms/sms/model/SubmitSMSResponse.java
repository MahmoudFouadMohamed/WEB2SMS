package com.edafa.web2sms.sms.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.enums.SMSResponseStatus;

@XmlType(name = "SubmitSMSResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitSMSResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitSMSResponse {
	@XmlElement(name = "SMSStatus")
	protected List<SMSResponseStatus> smsStatus;
	
	@XmlElement(name = "ResultStatus")
	protected ResultStatus resultStatus;
	
	@XmlElement(name = "Description")
	protected String description;
		
	public String getDescription() {
		return description;
	}
	public List<SMSResponseStatus> getSmsStatus() {
		return smsStatus;
	}
	public void setSmsStatus(List<SMSResponseStatus> smsStatus) {
		this.smsStatus = smsStatus;
	}
	public void setToSmsStatus(SMSResponseStatus smsStatus) {
		if(this.smsStatus == null){
			this.smsStatus =  new ArrayList<SMSResponseStatus>();
		}
		
		this.smsStatus.add(smsStatus);
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

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SubmitSMSResponse{smsStatus=");
        str = str.append(smsStatus)
        .append(", resultStatus=")
        .append(resultStatus);
        if (description != null) {
            str = str.append(", description=")
            .append(description);
        }
        str = str.append('}');
        return str.toString();
    }
        
}
