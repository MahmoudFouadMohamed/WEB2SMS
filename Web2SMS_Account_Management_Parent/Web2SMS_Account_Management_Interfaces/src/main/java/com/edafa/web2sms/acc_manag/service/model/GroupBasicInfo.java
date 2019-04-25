package com.edafa.web2sms.acc_manag.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "GroupBasicInfo", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class GroupBasicInfo {

	@XmlElement(required = false, nillable = false)
	public String groupId;

	@XmlElement(required = true, nillable = false)
	public String groupName;

	@XmlElement(name = "default", required = false, nillable = false)
	public boolean defaultGroup;

	public GroupBasicInfo() {
		// constructor 
	}

	public GroupBasicInfo(String groupId, String groupName, Boolean defaultFlag) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.defaultGroup = defaultFlag;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isDefaultGroup() {
		return defaultGroup;
	}

	public void setDefaultGroup(boolean defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GroupBasicInfo [");
		if (groupId != null)
			builder.append("groupId=").append(groupId).append(", ");
		if (groupName != null)
			builder.append("groupName=").append(groupName);
		builder.append("]");
		return builder.toString();
	}
}