package com.edafa.web2sms.acc_manag.service.account.group.user.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;

@XmlRootElement(name = "GroupUsersResultSet", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")

public class GroupUsersResultSet extends ResultStatus {

	@XmlElement
	List<AccManagUserModel> userModels;

	public GroupUsersResultSet() {
		// TODO Auto-generated constructor stub
	}

	public GroupUsersResultSet(ResponseStatus status) {
		super(status);
	}

	public List<AccManagUserModel> getUserModels() {
		return userModels;
	}

	public void setUserModels(List<AccManagUserModel> userModels) {
		this.userModels = userModels;
	}

}
