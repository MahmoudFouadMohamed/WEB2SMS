package com.edafa.web2sms.sms.model;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.model.SMSResponseBySmsId;



@XmlType(name = "SubmitSMSResponseBySmsId", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitSMSResponseBySmsId", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitSMSResponseBySmsId {

	@XmlElement(name = "SMSResponseBySmsId")
	protected List<SMSResponseBySmsId> smsResponseList;
	
	@XmlElement(name = "ResultStatus")
	protected ResultStatus resultStatus;
	
	@XmlElement(name = "Description")
	protected String description;

	public SubmitSMSResponseBySmsId() {
	}
	
	public List<SMSResponseBySmsId> getsmsResponseList() {
		return smsResponseList;
	}
	public void setsmsResponseList(List<SMSResponseBySmsId> smsStatus) {
		this.smsResponseList = smsStatus;
	}
	public void setToSmsStatus(SMSResponseBySmsId smsResponseList) {
		if(this.smsResponseList == null){
			this.smsResponseList =  new ArrayList<SMSResponseBySmsId>();
		}
		
		this.smsResponseList.add(smsResponseList);
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
	
	public String getDescription() {
		return description;
	}

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SubmitSMSResponseBySmsId{");
        str = str.append("smsResponseList=").append(smsResponseList).append(", resultStatus=").append(resultStatus);
        if (description != null) {
            str = str.append(", description=").append(description);
        }
        str = str.append('}');
        return str.toString();
    }
	
}
