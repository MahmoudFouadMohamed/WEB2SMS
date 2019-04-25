package com.edafa.web2sms.acc_manag.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "UserDetails", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class UserDetailsModel extends UserBasicInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6873627323401543200L;

	@XmlElement
	String name;

	@XmlElement
	String phoneNumber;

	@XmlElement
	String email;

	@XmlElement
	String groupId;
        
        @XmlElement
	String groupName;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

    @XmlTransient
    public boolean isValid() {
        boolean valid = true;

        if (super.isValid()) {
            if (groupId == null || groupId.isEmpty()) {
                valid = false;
            }
        }
        return valid;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserDetailsModel [");
		if (username != null)
			builder.append("username=").append(username).append(", ");
		if (accountId != null)
			builder.append("accountId=").append(accountId).append(", ");
		if (userId != null)
			builder.append("userId=").append(userId).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (phoneNumber != null)
			builder.append("phoneNumber=").append(phoneNumber).append(", ");
		if (email != null)
			builder.append("email=").append(email).append(", ");
		if (groupId != null)
			builder.append("groupId=").append(groupId);
		builder.append("]");
		return builder.toString();
	}

}
