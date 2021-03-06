package com.edafa.web2sms.acc_manag.service.account.group.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.GroupBasicInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

/**
 * @author memad
 *
 */
@XmlRootElement(name = "AccountGroupsResultSet", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class AccountGroupsResultSet extends ResultStatus {

	@XmlElementWrapper(name = "AccountGroups")
	@XmlElement(name = "Group")
	List<GroupBasicInfo> groupModels;

	public AccountGroupsResultSet() {
		// zero argument constructor
	}

	public AccountGroupsResultSet(ResponseStatus status) {
		super(status);
	}

	public List<GroupBasicInfo> getGroupModels() {
		return groupModels;
	}

	public void setGroupModels(List<GroupBasicInfo> groupModels) {
		this.groupModels = groupModels;
	}

}
