package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "GetCountResult", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
public class GetCountResult extends ResultStatus {

	@XmlElement(required = true, nillable = true)
	private int count;

	public GetCountResult() {}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{count=").append(count).append("}");
		return builder.toString();
	}

}
