package com.edafa.web2sms.ui.models;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AdminReportRequest", namespace = "http://www.edafa.com/web2sms/service/model/")
@XmlRootElement(name = "AdminReportRequest", namespace = "http://www.edafa.com/web2sms/service/model/")

public class AdminReportRequest implements Serializable {

	private static final long serialVersionUID = 2653443378372856425L;

	@XmlElement(required = true, nillable = false)
	private String trx;

	@XmlElement(required = true, nillable = false)
	private String fileToken;

	public AdminReportRequest() {}

	public String getTrx() {
		return trx;
	}

	public void setTrx(String trx) {
		this.trx = trx;
	}

	public String getFileToken() {
		return fileToken;
	}

	public void setFileToken(String fileToken) {
		this.fileToken = fileToken;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{trx=").append(trx).append(", fileToken=").append(fileToken).append("}");
		return builder.toString();
	}

}
