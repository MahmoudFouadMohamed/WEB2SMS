package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(namespace = "http://www.edafa.com/web2sms/service/model/")
public class FileTokenResult extends ResultStatus {

	@XmlElement(required = true, nillable = true)
	String fileToken;

	public String getFileToken() {
		return fileToken;
	}

	public void setFileToken(String fileToken) {
		this.fileToken = fileToken;
	}

}
