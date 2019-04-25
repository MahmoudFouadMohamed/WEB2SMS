package com.edafa.web2sms.acc_manag.service.account.user.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;

/**
 * @author memad
 *
 */
@XmlRootElement(name = "AccountGroupsResultSet", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class AccountUsersResultSet extends ResultStatus {

	@XmlElementWrapper(name = "AccountUsers")
	@XmlElement(name = "User")
	List<UserDetailsModel> userModels;

	public AccountUsersResultSet() {
		// zero argument constructor
	}

	public AccountUsersResultSet(ResponseStatus status) {
		super(status);
	}

	public List<UserDetailsModel> getUserModels() {
		return userModels;
	}

	public void setUserModels(List<UserDetailsModel> userModels) {
		this.userModels = userModels;
	}

}
