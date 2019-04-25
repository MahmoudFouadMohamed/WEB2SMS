package com.edafa.web2sms.acc_manag.service.account.group.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

/**
 * @author memad
 *
 */
@XmlRootElement(name = "AccountGroupResultSet", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class AccountGroupResultSet extends ResultStatus {

	@XmlElement(name = "Group")
	GroupModel groupModel;

	public AccountGroupResultSet() {
		// zero argument constructor
	}

	public AccountGroupResultSet(ResponseStatus status) {
		super(status);
	}

	public GroupModel getGroupModel() {
		return groupModel;
	}

	public void setGroupModel(GroupModel groupModel) {
		this.groupModel = groupModel;
	}

}
