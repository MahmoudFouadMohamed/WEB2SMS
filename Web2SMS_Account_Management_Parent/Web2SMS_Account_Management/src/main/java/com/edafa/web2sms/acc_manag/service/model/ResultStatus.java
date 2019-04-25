package com.edafa.web2sms.acc_manag.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;

@XmlType(name = "ResultStatus", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class ResultStatus {
	@XmlElement(required = true, nillable = false)
	ResponseStatus status;

	@XmlElement(required = true, nillable = true)
	String errorMessage;

	public ResultStatus() {
		this.status=null;
		this.errorMessage = "";
	}

	public ResultStatus(ResponseStatus status) {
		this.status = status;
	}

	public ResultStatus(ResponseStatus status, String errorMessage) {
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
