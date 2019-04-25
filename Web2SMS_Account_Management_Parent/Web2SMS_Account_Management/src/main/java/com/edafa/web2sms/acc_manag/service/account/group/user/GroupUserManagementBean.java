package com.edafa.web2sms.acc_manag.service.account.group.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.group.user.interfaces.GroupUserManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountManegementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.conversoin.GroupConversionBean;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.dalayer.dao.AccountGroupDao;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserDaoLocal;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.constants.AccountGroupConst;

/**
 * Session Bean implementation class GroupManagementBean
 */
@Stateless
@LocalBean
public class GroupUserManagementBean implements GroupUserManagementBeanLocal {

	Logger acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());

	@EJB
	private AccountGroupDao accountGroupDao;

	@EJB
	AccountManegementBeanLocal accountManegementBean;

	@EJB
	GroupUserManagementBeanLocal groupUserManagementBean;

	@EJB
	GroupConversionBean groupConversionBean;

	@EJB
	AccountUserDaoLocal accountUserDao;

	@Override
	public List<AccManagUserModel> getGroupUsers(AccountUserTrxInfo userTrxInfo, GroupModel groupModel)
			throws DBException, IneligibleAccountException {

		acctLogger.info(userTrxInfo.logInfo());

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.VIEW_USERS);
		userTrxInfo.setUserActions(actionList);

		//check eligibility
		accountManegementBean.checkAccountAndUserEligibility(userTrxInfo);

		//TODO validate arguments

		String accountId = userTrxInfo.getUser().getAccountId();

		List<AccountUser> groupUsers = accountGroupDao.findAccountGroupUsers(accountId, groupModel.getGroupName());

		List<AccManagUserModel> userModels = groupConversionBean.getAccountGroupUsers(groupUsers);
		acctLogger.info(userTrxInfo.logInfo() + "groups{" + Arrays.toString(userModels.toArray()) + "}");

		return userModels;

	}

	@Override
	public void assignUserToGroup(AccountUserTrxInfo userTrxInfo, GroupModel groupModel, String userName)
			throws DBException, IneligibleAccountException, GroupNotFoundException {

		acctLogger.info(userTrxInfo.logInfo());

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.EDIT_GROUP_USERS);
		userTrxInfo.setUserActions(actionList);

		//check eligibility
		accountManegementBean.checkAccountAndUserEligibility(userTrxInfo);

		//TODO validate arguments

		String accountId = userTrxInfo.getUser().getAccountId();

		accountGroupDao.addUserToGroup(accountId, groupModel.getGroupName(), userName);

		acctLogger.info(
				userTrxInfo.logInfo() + "user[" + userName + "] added to group[" + groupModel.getGroupName() + "]");

	}

	@Override
	public void removeUserFromGroup(AccountUserTrxInfo userTrxInfo, GroupModel groupModel, String userName)
			throws DBException, IneligibleAccountException, GroupNotFoundException {
		acctLogger.info(userTrxInfo.logInfo());

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.EDIT_GROUP_USERS);
		userTrxInfo.setUserActions(actionList);

		//check eligibility
		accountManegementBean.checkAccountAndUserEligibility(userTrxInfo);

	}

	@Override
	public void setUserAsGroupAdmin(AccountUserTrxInfo userTrxInfo, String userId, String groupId)
			throws DBException, IneligibleAccountException, UserNotFoundException, GroupNotFoundException {

		userTrxInfo.getUserActions().add(ActionName.MARK_GROUP_ADMIN);
		acctLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());
		accountManegementBean.checkAccountAndUserEligibility(userTrxInfo);
                setUserAsGroupAdminWithoutEligibilityCheck(userTrxInfo, userId, groupId);
	}
        
        @Override
	public void setUserAsGroupAdminWithoutEligibilityCheck(AccountUserTrxInfo userTrxInfo, String userId, String groupId)
			throws DBException, UserNotFoundException, GroupNotFoundException {

		String accountId = userTrxInfo.getUser().getAccountId();

		acctLogger.info(userTrxInfo.logInfo() + "Retriving user by id(" + userId + ") from data base.");
		AccountUser newGroupAdminUser = accountUserDao.findAccountUserById(userId);

		if (newGroupAdminUser == null) {
			acctLogger.error(userTrxInfo.logInfo() + "User with id(" + userId + ") not found");
			throw new UserNotFoundException(userTrxInfo.logInfo(), userId);
		}

		List<AccountGroup> userGroups = newGroupAdminUser.getAccountGroups();
		AccountGroup userGroup = null;
		if (userGroups != null) {
			for (AccountGroup group : userGroups) {
				if (group.getAccountGroupId().equals(groupId) && !group.getHiddenFlag()) {
					userGroup = group;
				}
			}
			if (userGroup == null) {
				acctLogger.error(userTrxInfo.logInfo() + "User (" + userId + ") doesn't belonge to group with id("
						+ groupId + ").");
				throw new UserNotFoundException(userTrxInfo.logInfo(), userId);
			}
		} else {
			acctLogger.error(
					userTrxInfo.logInfo() + "User (" + userId + ") doesn't belonge to group with id(" + groupId + ").");
			throw new UserNotFoundException(userTrxInfo.logInfo(), userId);
		}

		acctLogger.info(userTrxInfo.logInfo() + "Retriving group (" + AccountGroupConst.GROUPS_ADMINS_GROUP_NAME
				+ ") from data base.");

		AccountGroup groupAdminsGroup = accountGroupDao.findAccountGroup(accountId,
				AccountGroupConst.GROUPS_ADMINS_GROUP_NAME);
		if (groupAdminsGroup == null) {
			acctLogger.error(
					userTrxInfo.logInfo() + "Group(" + AccountGroupConst.GROUPS_ADMINS_GROUP_NAME + ") not found");
			throw new GroupNotFoundException(accountId, AccountGroupConst.GROUPS_ADMINS_GROUP_NAME);
		}

		AccountUser oldGroupAdmin = accountGroupDao.findAccountGroupAdmin(accountId, groupId);

		if (oldGroupAdmin != null) {
			acctLogger.info(userTrxInfo.logInfo() + "Remove old group admin User (" + oldGroupAdmin.getUsername()
					+ ") from (" + AccountGroupConst.GROUPS_ADMINS_GROUP_NAME + ") group.");
			Iterator<AccountGroup> oldGroupAdminGroup = oldGroupAdmin.getAccountGroups().iterator();
			while (oldGroupAdminGroup.hasNext()) {
				AccountGroup accountGroup = oldGroupAdminGroup.next();
				if (accountGroup.getGroupName().equals(AccountGroupConst.GROUPS_ADMINS_GROUP_NAME)) {
					oldGroupAdminGroup.remove();
				}
			}
			acctLogger.info(userTrxInfo.logInfo() + "Update old group admin (" + oldGroupAdmin.getUsername() + ").");
			accountUserDao.edit(oldGroupAdmin);
		}

		userGroups.add(groupAdminsGroup);
		acctLogger.info(userTrxInfo.logInfo() + "Update user (" + newGroupAdminUser.getUsername()
				+ ") to be group admin of group(" + userGroup.getGroupName() + ") with group id(" + groupId + ").");
		accountUserDao.edit(newGroupAdminUser);
	}
}
