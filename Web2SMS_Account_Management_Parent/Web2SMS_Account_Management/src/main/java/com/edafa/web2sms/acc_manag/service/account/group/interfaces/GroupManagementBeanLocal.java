package com.edafa.web2sms.acc_manag.service.account.group.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupAlreadyExistException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupCreationException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.SetGroupAdminException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.LastAdminNotRemovableException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotEditableException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotRemovableException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupBasicInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.dalayer.exception.DBException;

/**
 * @author memad
 *
 */

@Local
public interface GroupManagementBeanLocal {

	List<GroupBasicInfo> getAccountGroups(AccountUserTrxInfo userTrxInfo, String search, int first, int max)
			throws DBException, IneligibleAccountException;

	List<GroupModel> getAccountGroupsDetailed(AccountUserTrxInfo userTrxInfo, String search, int first, int max)
			throws DBException, IneligibleAccountException;

	GroupModel getAccountGroupFullInfo(AccountUserTrxInfo userTrxInfo, GroupBasicInfo groupBasicInfo)
			throws DBException, IneligibleAccountException, GroupNotFoundException;

	GroupModel createGroup(AccountUserTrxInfo userTrxInfo, GroupModel groupModel)
			throws DBException, IneligibleAccountException, GroupAlreadyExistException, UserNotFoundException,
			GroupCreationException, LastAdminNotRemovableException;

	void deleteGroup(AccountUserTrxInfo userTrxInfo, GroupBasicInfo groupModel)
			throws DBException, IneligibleAccountException, GroupNotFoundException, NotRemovableException;

	void updateGroup(AccountUserTrxInfo userTrxInfo, GroupModel groupModel)
			throws DBException, IneligibleAccountException, GroupNotFoundException, NotEditableException,
			UserNotFoundException, LastAdminNotRemovableException, SetGroupAdminException;

	int countAccountGroups(AccountUserTrxInfo userTrxInfo, String groupName)
			throws DBException, IneligibleAccountException;

}
