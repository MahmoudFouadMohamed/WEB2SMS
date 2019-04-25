package com.edafa.web2sms.acc_manag.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Privilege", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class PrivilegeModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1345026894863360792L;

	@XmlElement(required = true, nillable = false)
	String privilegeName;
	@XmlElement(required = false, nillable = false)
	String privilegeId;

	public PrivilegeModel() {

	}

	public PrivilegeModel(String privilegeName, String privilegeId) {
		this.privilegeName = privilegeName;
		this.privilegeId = privilegeId;
	}

	public String getPrivilegeName() {
		return privilegeName;
	}
	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}

	public String getPrivilegeId() {
		return privilegeId;
	}
	public void setPrivilegeId(String privilegeId) {
		this.privilegeId = privilegeId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrivilegeModel [");
		if (privilegeName != null)
			builder.append("privilegeName=").append(privilegeName).append(", ");
		if (privilegeId != null)
			builder.append("privilegeId=").append(privilegeId);
		builder.append("]");
		return builder.toString();
	}

}
