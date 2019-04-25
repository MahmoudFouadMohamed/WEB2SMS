package com.edafa.web2sms.acc_manag.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "GroupUser", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class GroupUserModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5946959101760188253L;

	@XmlElement(name = "username", required = true, nillable = false)
	String username;

	@XmlElement(name = "userId", required = false, nillable = false)
	String userId;

	public GroupUserModel() {

	}

	public GroupUserModel(String username, String id) {
		this.username = username;
		this.userId = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String id) {
		this.userId = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GroupUser [");
		if (username != null)
			builder.append("username=").append(username).append(", ");
		if (userId != null)
			builder.append("userId=").append(userId);
		builder.append("]");
		return builder.toString();
	}

}
