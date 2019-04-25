package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.service.enums.ProvResponseStatus;

@XmlType(name = "ProvResultStatus", namespace = "http://www.edafa.com/web2sms/prov/model/")
public class ProvResultStatus {
	@XmlElement(required = true, nillable = false)
	ProvResponseStatus status;

	@XmlElement(required = true, nillable = true)
	String errorMessage;

	public ProvResultStatus() {
		this.status = null;
		this.errorMessage = "";
	}

	public ProvResultStatus(ProvResponseStatus status) {
		this.status = status;
	}

	public ProvResultStatus(ProvResponseStatus status, String errorMessage) {
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public ProvResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ProvResponseStatus status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
