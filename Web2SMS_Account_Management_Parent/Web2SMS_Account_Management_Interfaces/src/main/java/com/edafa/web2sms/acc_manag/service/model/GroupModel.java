package com.edafa.web2sms.acc_manag.service.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Group", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class GroupModel extends GroupBasicInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4277915420614611968L;

	@XmlElement(required = false, nillable = false)
	public String groupAdminId;

	@XmlElementWrapper(name = "GroupUsers")
	@XmlElement(name = "User", required = false, nillable = false)
	List<GroupUserModel> groupUsers;

	@XmlElementWrapper(name = "GroupPrivileges")
	@XmlElement(name = "Privilege", required = false, nillable = false)
	List<PrivilegeModel> privileges;

	public GroupModel(String groupId, String groupName, Boolean defaultFlag) {
		super(groupId, groupName, defaultFlag);
	}

	public GroupModel() {
		// TODO Auto-generated constructor stub
	}

	public List<GroupUserModel> getGroupUsers() {
		return groupUsers;
	}

	public void setGroupUsers(List<GroupUserModel> groupUsers) {
		this.groupUsers = groupUsers;
	}

	public List<PrivilegeModel> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<PrivilegeModel> privileges) {
		this.privileges = privileges;
	}

	public void setGroupAdminId(String groupAdminId) {
		this.groupAdminId = groupAdminId;
	}

	public String getGroupAdminId() {
		return groupAdminId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		if (groupId != null)
			builder.append("groupId=").append(groupId).append(", ");
		if (groupName != null)
			builder.append("groupName=").append(groupName).append(", ");
		if (groupUsers != null)
			builder.append("groupUsers=").append(groupUsers).append(", ");
		if (privileges != null)
			builder.append("privileges=").append(privileges);
		builder.append("}");
		return builder.toString();
	}

}
