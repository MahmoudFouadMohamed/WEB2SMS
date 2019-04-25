package com.edafa.web2sms.acc_manag.service.account.group.user.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountGroup;

/**
 * @author memad
 *
 */

@Local
public interface GroupUserManagementBeanLocal {

	List<AccManagUserModel> getGroupUsers(AccountUserTrxInfo userTrxInfo, GroupModel groupModel)
			throws DBException, IneligibleAccountException;

	void assignUserToGroup(AccountUserTrxInfo userTrxInfo, GroupModel groupModel, String userName)
			throws DBException, IneligibleAccountException, GroupNotFoundException;

	void removeUserFromGroup(AccountUserTrxInfo userTrxInfo, GroupModel groupModel, String userName)
			throws DBException, IneligibleAccountException, GroupNotFoundException;

	public void setUserAsGroupAdmin(AccountUserTrxInfo userTrxInfo, String userId, String groupId)
			throws DBException, IneligibleAccountException, UserNotFoundException, GroupNotFoundException;

        public void setUserAsGroupAdminWithoutEligibilityCheck(AccountUserTrxInfo userTrxInfo, String userId, String groupId)
			throws DBException, UserNotFoundException, GroupNotFoundException;

        
}
