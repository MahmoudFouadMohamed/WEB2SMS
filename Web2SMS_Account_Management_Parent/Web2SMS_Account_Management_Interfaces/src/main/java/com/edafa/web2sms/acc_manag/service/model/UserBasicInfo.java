package com.edafa.web2sms.acc_manag.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "UserBasicInfo", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class UserBasicInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6873627323401543200L;

	@XmlElement(required = true, nillable = false)
	String username;

	@XmlElement(required = true, nillable = false)
	String accountId;

	@XmlElement(required = true, nillable = false)
	String userId;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@XmlTransient
	public boolean isValid() {
		boolean valid = true;

		if (username == null || accountId == null)
			valid = false;

		return valid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserBasicInfo [");
		if (username != null)
			builder.append("username=").append(username).append(", ");
		if (accountId != null)
			builder.append("accountId=").append(accountId);
		builder.append("]");
		return builder.toString();
	}

}
