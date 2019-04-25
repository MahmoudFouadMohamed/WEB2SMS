package com.edafa.web2sms.acc_manag.service.account.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupAlreadyExistException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupCreationException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.SetGroupAdminException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.LastAdminNotRemovableException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotEditableException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotRemovableException;
import com.edafa.web2sms.acc_manag.service.account.group.interfaces.GroupManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.group.user.interfaces.GroupUserManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountManegementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.conversoin.GroupConversionBean;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupBasicInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.GroupUserModel;
import com.edafa.web2sms.acc_manag.service.model.PrivilegeModel;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountGroupDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.Privilege;
import com.edafa.web2sms.dalayer.model.constants.AccountGroupConst;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Session Bean implementation class GroupManagementBean
 */
@Stateless
public class GroupManagementBean implements GroupManagementBeanLocal {

	Logger logger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());

	@EJB
	private AccountGroupDaoLocal acctGroupDao;

	@EJB
	private AccountUserDaoLocal acctUserDao;

	@EJB
	AccountStatusDaoLocal accountStatusDao;

	@EJB
	AccountManegementBeanLocal accountManagement;

	@EJB
	GroupConversionBean groupConversionBean;

	@EJB
	GroupUserManagementBeanLocal groupUserManagementBean;

	@Override
	public int countAccountGroups(AccountUserTrxInfo userTrxInfo, String groupName)
			throws DBException, IneligibleAccountException {

		String logId = userTrxInfo.logId();

		logger.debug(userTrxInfo.logInfo());

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.VIEW_GROUPS);
		actionList.add(ActionName.VIEW_OWN_GROUP);

		userTrxInfo.setUserActions(actionList);

		//check eligibility
		logger.debug(logId + "Check account eligibility for actions: " + userTrxInfo.getUserActions());
		List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);
		//check if should return all groups for "Admin" or single group for "Group admin" 
		String accountId = userTrxInfo.getUser().getAccountId();
		int count = 0;
		if (eligibileActions.contains(ActionName.VIEW_GROUPS)) {
			logger.debug(logId + "Count account groups with search group name("
					+ (groupName == null ? "NULL" : groupName) + ")");
			count = acctGroupDao.countAccountGroups(accountId, groupName, null);
		} else if (eligibileActions.contains(ActionName.VIEW_OWN_GROUP)) {
			logger.debug(logId + "Count user with name(" + userTrxInfo.getUser().getUsername()
					+ ") groups with search group name(" + (groupName == null ? "NULL" : groupName) + ")");
			count = acctGroupDao.countAccountGroups(accountId, groupName, userTrxInfo.getUser().getUsername());
		}

		logger.info(logId + "Count of groups(" + count + ")");

		return count;
	}

	@Override
	public List<GroupBasicInfo> getAccountGroups(AccountUserTrxInfo userTrxInfo, String groupName, int first, int max)
			throws DBException, IneligibleAccountException {

		String logId = userTrxInfo.logId();

		logger.debug(userTrxInfo.logInfo());

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.VIEW_GROUPS);
		actionList.add(ActionName.VIEW_OWN_GROUP);

		userTrxInfo.setUserActions(actionList);

		//check eligibility
		logger.debug(logId + "Check account eligibility for actions: " + userTrxInfo.getUserActions());
		List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);
		//check if should return all groups for "Admin" or single group for "Group admin" 

		String accountId = userTrxInfo.getUser().getAccountId();
		List<AccountGroup> accountGroups = null;
		if (eligibileActions.contains(ActionName.VIEW_GROUPS)) {
			//accountGroups = acctGroupDao.findAccountGroups(accountId);
			logger.debug(logId + "Get account groups with search group name(" + (groupName == null ? "NULL" : groupName)
					+ ")");
			accountGroups = acctGroupDao.searchGroups(accountId, groupName, null, first, max);

		} else if (eligibileActions.contains(ActionName.VIEW_OWN_GROUP)) {
			logger.debug(logId + "Get user with name(" + userTrxInfo.getUser().getUsername()
					+ ") groups with search group name(" + (groupName == null ? "NULL" : groupName) + ")");
			accountGroups = acctGroupDao.searchGroups(accountId, groupName, userTrxInfo.getUser().getUsername(), first,
					max);
		} else {
			throw new IneligibleAccountException("Ineligible");
		}

		List<GroupBasicInfo> groups = groupConversionBean.getAccountGroupsBasicInfo(accountGroups);
		logger.info(userTrxInfo.logInfo() + "Count of retrived groups(" + groups.size() + ")");
		logger.debug(logId + "groups{" + Arrays.toString(groups.toArray()) + "}");

		return groups;
	}

	@Override
	public List<GroupModel> getAccountGroupsDetailed(AccountUserTrxInfo userTrxInfo, String groupName, int first,
			int max) throws DBException, IneligibleAccountException {
		String logId = userTrxInfo.logId();

		logger.debug(userTrxInfo.logInfo());

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.VIEW_GROUPS);
		actionList.add(ActionName.VIEW_OWN_GROUP);

		userTrxInfo.setUserActions(actionList);

		//check eligibility
		logger.debug(logId + "Check account eligibility for actions: " + userTrxInfo.getUserActions());
		List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);
		//check if should return all groups for "Admin" or single group for "Group admin" 

		String accountId = userTrxInfo.getUser().getAccountId();
		List<AccountGroup> accountGroups = null;
		if (eligibileActions.contains(ActionName.VIEW_GROUPS)) {
			logger.debug(logId + "Get account groups with search group name(" + (groupName == null ? "NULL" : groupName)
					+ ")");
			accountGroups = acctGroupDao.searchGroups(accountId, groupName, null, first, max);

		} else if (eligibileActions.contains(ActionName.VIEW_OWN_GROUP)) {
			logger.debug(logId + "Get user with name(" + userTrxInfo.getUser().getUsername()
					+ ") groups with search group name(" + (groupName == null ? "NULL" : groupName) + ")");
			accountGroups = acctGroupDao.searchGroups(accountId, groupName, userTrxInfo.getUser().getUsername(), first,
					max);
		} else {
			throw new IneligibleAccountException("Ineligible");
		}

		List<GroupModel> groups = groupConversionBean.getAccountGroups(accountGroups);
		logger.info(userTrxInfo.logInfo() + "Count of groups(" + groups.size() + ")");
		logger.debug(logId + "groups{" + Arrays.toString(groups.toArray()) + "}");

		return groups;
	}

	@Override
	public GroupModel getAccountGroupFullInfo(AccountUserTrxInfo userTrxInfo, GroupBasicInfo groupBasicInfo)
			throws DBException, IneligibleAccountException, GroupNotFoundException {
		String logId = userTrxInfo.logId();

		logger.debug(userTrxInfo.logInfo());

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.VIEW_GROUPS);
		actionList.add(ActionName.VIEW_OWN_GROUP);

		userTrxInfo.setUserActions(actionList);

		//check eligibility
		logger.debug(logId + "Check account eligibility for actions: " + userTrxInfo.getUserActions());
		List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);

		String accountId = userTrxInfo.getUser().getAccountId();

		String groupId = groupBasicInfo.getGroupId();

		if (groupId == null) {
			throw new GroupNotFoundException("groupId is null");
		}

		logger.debug(logId + "Retriving group with id(" + groupId + ")");

		AccountGroup accountGroup = acctGroupDao.findAccountGroup(groupId);

		if (eligibileActions.contains(ActionName.VIEW_GROUPS)) {
			logger.debug(logId + "User is eligible to view all account groups");
		} else if (eligibileActions.contains(ActionName.VIEW_OWN_GROUP)) {
			//Ensure that the user is a member of this group
			logger.debug(logId + "User is eligible to view only his own group");
			List<AccountStatus> acctStatus = new ArrayList<AccountStatus>();
			acctStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));

			String username = userTrxInfo.getUser().getUsername();
			logger.info(userTrxInfo.logInfo() + "retrieving user by name(" + username
					+ ") from data base to check if belongs to group (" + accountGroup.getGroupName() + ")");

			AccountUser accountUser = acctUserDao.findAccountUser(accountId, username, acctStatus);

			if (!accountGroup.getAccountUsers().contains(accountUser)) {
				throw new IneligibleAccountException("Ineligible to view group with id " + groupId);
			}

		} else {
			throw new IneligibleAccountException("Ineligible to view group with id " + groupId);
		}

		logger.debug(logId + "Converting group with id (" + groupId + ") to group model");

		GroupModel group = groupConversionBean.getGroupModel(accountGroup);

		logger.info(userTrxInfo.logInfo() + "group{" + group + "}");

		return group;
	}

	@Override
	public GroupModel createGroup(AccountUserTrxInfo userTrxInfo, GroupModel groupModel) throws DBException,
			IneligibleAccountException, GroupAlreadyExistException, UserNotFoundException, GroupCreationException, LastAdminNotRemovableException {

		String logId = userTrxInfo.logId();

		logger.debug(userTrxInfo.logInfo() + groupModel);

		checkCreateEligibility(userTrxInfo, groupModel, logId);

		String groupName = groupModel.getGroupName();

		//validate arguments
		String accountId = userTrxInfo.getUser().getAccountId();
		validateCreateRequest(accountId, logId, groupName);

		//conversion
		AccountGroup group = groupConversionBean.getAccountGroup(groupModel, accountId);

		//Retrieving default group from db
		logger.debug(logId + "Retriving the default group from DB");
		AccountGroup defaultGroup = acctGroupDao.findAccountDefaultGroup(accountId);

		List<AccountUser> users = group.getAccountUsers();

		if (isNotEmpty(users)) {
			preventAllAdminsRemoval(logId, defaultGroup, users, 0);
		}

		logger.debug(logId + "Persist the new group to DB");

		acctGroupDao.create(group);

		String groupId = group.getAccountGroupId();
		groupModel.setGroupId(groupId);

		String groupAdminId = groupModel.getGroupAdminId();

		if (groupAdminId != null && !groupAdminId.isEmpty()) {
			try {
				logger.debug(logId + "Marking user with ID[" + groupAdminId + "] as group admin");
				groupUserManagementBean.setUserAsGroupAdmin(userTrxInfo, groupAdminId, groupId);
			} catch (GroupNotFoundException e) {
				//Should not happen
				throw new DBException("Unexpected error");
			}
		}

		logger.info(userTrxInfo.logInfo() + "group[" + group + " created successfully");
		//Remove users from default group
		removeUsersFromGroup(logId, users, defaultGroup);

		return groupModel;
	}

	private String validateCreateRequest(String accountId, String logId, String groupName)
			throws GroupAlreadyExistException, IneligibleAccountException {

		validateGroupName(groupName);

		logger.debug(logId + "Retriving group with name[" + groupName + "] for account id[" + accountId + "]");

		AccountGroup group = acctGroupDao.findAccountGroup(accountId, groupName);

		if (group != null) {
			logger.warn(
					logId + "Group with name[" + groupName + "] Already Exist for account with ID[" + accountId + "]");
			throw new GroupAlreadyExistException(
					"Group with name[" + groupName + "] Already Exist for account with ID[" + accountId + "]");
		}

		if (accountId == null) {
			throw new IneligibleAccountException("Account ID is null");
		}
		return accountId;
	}

	private void checkCreateEligibility(AccountUserTrxInfo userTrxInfo, GroupModel groupModel, String logId)
			throws DBException, IneligibleAccountException {
		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.CREATE_GROUP);

		if (isNotEmpty(groupModel.getGroupUsers())) {
			actionList.add(ActionName.EDIT_GROUP_USERS);
		}

		if (isNotEmpty(groupModel.getPrivileges())) {
			actionList.add(ActionName.EDIT_GROUP_PRIVILEGES);
		}

		userTrxInfo.setUserActions(actionList);

		//check eligibility
		logger.debug(logId + "Check account eligibility for actions: " + userTrxInfo.getUserActions());
		List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);

		checkEligibilityResult(actionList, eligibileActions);
	}

	private void checkEligibilityResult(List<ActionName> actionList, List<ActionName> eligibileActions)
			throws IneligibleAccountException {

		for (ActionName actionName : actionList) {
			if (!eligibileActions.contains(actionName) && actionName != ActionName.VIEW_GROUPS
					&& actionName != ActionName.VIEW_OWN_GROUP) {
				throw new IneligibleAccountException("Action[" + actionName + "] is not granted for user");
			}
		}

	}

	@Override
	public void deleteGroup(AccountUserTrxInfo userTrxInfo, GroupBasicInfo groupModel) throws DBException,
			IneligibleAccountException, GroupNotFoundException, IllegalArgumentException, NotRemovableException {

		String logId = userTrxInfo.logId();

		logger.debug(userTrxInfo.logInfo());

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.DELETE_GROUP);
		userTrxInfo.setUserActions(actionList);

		//check eligibility
		logger.debug(logId + "Check account eligibility for actions: " + userTrxInfo.getUserActions());
		accountManagement.checkAccountAndUserEligibility(userTrxInfo);

		String accountId = userTrxInfo.getUser().getAccountId();
		String groupName = groupModel.getGroupName();

		//validate arguments
		validateGroupForRemoval(groupName, accountId);

		//get AccountGroup
		logger.debug(logId + "Retriving group with name[" + groupName + "] from DB");
		AccountGroup group = acctGroupDao.findAccountGroup(accountId, groupName);

		if (group == null) {
			throw new GroupNotFoundException("No group found with name[" + groupModel.getGroupName()
					+ "] for account with ID[" + userTrxInfo.getUser().getAccountId() + "]");
		}

		//Move users to default group
		logger.debug(logId + "Retriving the default group from DB");

		AccountGroup defaultGroup = acctGroupDao.findAccountDefaultGroup(accountId);

		logger.debug(logId + "Adding users of the deleted group to the default group");
		acctGroupDao.addUsersToGroup(accountId, defaultGroup, group.getAccountUsers());

		//Remove admins of this group 
		logger.debug(logId + "Removing group admins from GroupAdmins group");
		acctGroupDao.removeAccountGroupAdmins(accountId, group.getAccountUsers());

		//Remove from DB
		acctGroupDao.remove(group);
		logger.info(userTrxInfo.logInfo() + "group[" + group + " deleted successfully");

	}

	@Override
        @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateGroup(AccountUserTrxInfo userTrxInfo, GroupModel groupModel)
			throws DBException, IneligibleAccountException, GroupNotFoundException, NotEditableException,
			UserNotFoundException, LastAdminNotRemovableException, SetGroupAdminException {

		String logId = userTrxInfo.logId();

		logger.debug(userTrxInfo.logInfo() + groupModel);

		String accountId = userTrxInfo.getUser().getAccountId();

		List<ActionName> eligibileActions = checkUpdateEligibility(userTrxInfo, accountId, groupModel);

		AccountGroup unUpdatedGroup = validateGroupForUpdate(groupModel, accountId);

		//Get users removed from group to be Moved to Default Group
		List<GroupUserModel> newGroupUsers = groupModel.getGroupUsers();
		List<AccountUser> removedUsers = getUsersToBeMoved(groupModel, newGroupUsers, unUpdatedGroup);

		checkForChangesEligibility(logId, groupModel, eligibileActions, unUpdatedGroup, removedUsers);

		logger.debug(logId + "Retriving the default group from DB");
		AccountGroup defaultGroup = acctGroupDao.findAccountDefaultGroup(accountId);

		boolean isDefaultGroup = groupModel.getGroupId().equals(defaultGroup.getAccountGroupId());
		if (isDefaultGroup) {

			if (isNotEmpty(removedUsers)) {
				throw new NotEditableException("Can not remove users from the Default group");
			}

			if (isDefaultGroupFlagChanged(groupModel)) {
				throw new NotEditableException("Can not change the default flag");
			}
		}

		//Conversion		
		if (logger.isDebugEnabled()) {
			logger.debug(logId + "Converting [" + groupModel + "] to an entity");
		}

		AccountGroup updatedGroup = groupConversionBean.updateAccountGroup(groupModel, accountId, unUpdatedGroup);

		if (!isDefaultGroup) {
			preventAllAdminsRemoval(logId, defaultGroup, updatedGroup.getAccountUsers(),
					unUpdatedGroup.getAccountUsers().size());
			updateDefaultGroup(updatedGroup, defaultGroup, accountId, removedUsers, logId);
		}

		//Persistence 
		logger.debug(logId + "Updating group with id[" + groupModel.getGroupId() + "] at DB");

		acctGroupDao.edit(updatedGroup);
		logger.info(userTrxInfo.logInfo() + "group[" + updatedGroup + " updated successfully");
        try {
            String groupAdminId = groupModel.getGroupAdminId();
            if (groupAdminId != null && !groupAdminId.isEmpty()) {

                logger.debug(logId + "Retriving admin user from DB");
                AccountUser adminAccountUser = acctGroupDao.findAccountGroupAdmin(accountId, groupModel.getGroupId());

                if (adminAccountUser == null || !groupAdminId.equals(adminAccountUser.getAccountUserId())) {
                    if (eligibileActions.contains(ActionName.MARK_GROUP_ADMIN)) {
                        logger.debug(logId + "Marking user with ID[" + groupAdminId + "] as group admin");
                        groupUserManagementBean.setUserAsGroupAdminWithoutEligibilityCheck(userTrxInfo, groupAdminId, updatedGroup.getAccountGroupId());
                    } else {
                        throw new IneligibleAccountException("Can't mark user as group admin");
                    }
                }
            }
        } catch (Exception e) {
            throw new SetGroupAdminException(logId + "Failed to set user as group admin(" + e.getMessage() + ")", e);
        }

    }

	private void checkForChangesEligibility(String logId, GroupModel groupModel, List<ActionName> eligibileActions,
			AccountGroup unUpdatedGroup, List<AccountUser> removedUsers) throws IneligibleAccountException {
		boolean usersChanged = isGroupUsersUpdated(logId, groupModel, unUpdatedGroup, removedUsers);

		if (usersChanged && !eligibileActions.contains(ActionName.EDIT_GROUP_USERS)) {
			throw new IneligibleAccountException("User is ineligible to edit group users");
		}

		if (isPrivilegesChanged(groupModel, unUpdatedGroup)
				&& !eligibileActions.contains(ActionName.EDIT_GROUP_PRIVILEGES)) {
			throw new IneligibleAccountException("User is ineligible to edit group privileges");
		}
	}

	private boolean isGroupUsersUpdated(String logId, GroupModel groupModel, AccountGroup unUpdatedGroup,
			List<AccountUser> removedUsers) {

		boolean usersChanged = false;

		if (unUpdatedGroup.getAccountUsers().size() != groupModel.getGroupUsers().size()) {
			usersChanged = true;
			logger.debug(logId + "isGroupUsersUpdated" + usersChanged + ", oldGroupSize="
					+ unUpdatedGroup.getAccountUsers().size() + ", newGroupSize=" + groupModel.getGroupUsers().size());

		}

		if (isNotEmpty(removedUsers)) {
			usersChanged = true;
		}

		return usersChanged;
	}

	private List<ActionName> checkUpdateEligibility(AccountUserTrxInfo userTrxInfo, String accountId,
			GroupModel groupModel) throws DBException, IneligibleAccountException {
		String logId = userTrxInfo.logId();

		List<ActionName> actionList = new ArrayList<>();
		actionList.add(ActionName.EDIT_GROUP);
		actionList.add(ActionName.VIEW_GROUPS);
		actionList.add(ActionName.VIEW_OWN_GROUP);
		actionList.add(ActionName.EDIT_GROUP_USERS);
		actionList.add(ActionName.EDIT_GROUP_PRIVILEGES);
                actionList.add(ActionName.MARK_GROUP_ADMIN);

		userTrxInfo.setUserActions(actionList);

		//check eligibility
		logger.debug(logId + "Check account eligibility for action: " + userTrxInfo.getUserActions());
		List<ActionName> eligibileActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo);

		//checkEligibilityResult(actionList, eligibileActions);

		if (!eligibileActions.contains(ActionName.EDIT_GROUP)) {
			throw new IneligibleAccountException("User is ineligible to edit groups");
		}

		if (eligibileActions.contains(ActionName.VIEW_GROUPS)) {
			logger.debug(logId + "User is eligible to view  and edit all account groups");
		} else if (eligibileActions.contains(ActionName.VIEW_OWN_GROUP)) {
			// make sure this is the group admin.
			logger.debug(logId + "Retriving admin user from DB");

			AccountUser adminAccountUser = acctGroupDao.findAccountGroupAdmin(accountId, groupModel.getGroupId());

			if (!adminAccountUser.getUsername().equals(userTrxInfo.getUser().getUsername())) {
				throw new IneligibleAccountException("Can't edit group without being admin of this group ");
			}
		}

		return eligibileActions;
	}

	private AccountGroup validateGroupForUpdate(GroupModel groupModel, String accountId)
			throws GroupNotFoundException, NotEditableException {
		String groupName = groupModel.getGroupName();
		validateGroupName(groupName);

		//get AccountGroup FROM DB
		String groupId = groupModel.getGroupId();
		AccountGroup group = acctGroupDao.findAccountGroup(groupId);

		if (group == null) {
			throw new GroupNotFoundException(
					"No group found with id[" + groupId + "] for account with ID[" + accountId + "]");
		}

		if (group.getGroupName().equals(AccountGroupConst.ADMINS_GROUP_NAME)) {

			if (!groupName.equals(AccountGroupConst.ADMINS_GROUP_NAME)) {
				throw new NotEditableException(
						"Can not edit the name of [" + AccountGroupConst.ADMINS_GROUP_NAME + "]");
			}

			if (!isExist(groupModel.getPrivileges()) || isPrivilegesChanged(groupModel, group)) {
				throw new NotEditableException(
						"Can not edit privileges of [" + AccountGroupConst.ADMINS_GROUP_NAME + "]");
			}

			List<GroupUserModel> users = groupModel.getGroupUsers();

			if (!isExist(users) || users.isEmpty()) {
				throw new NotEditableException(
						"Can not remove all users from [" + AccountGroupConst.ADMINS_GROUP_NAME + "]");
			}

		}

		return group;
	}

	private boolean isDefaultGroupFlagChanged(GroupModel groupModel) {

		return !groupModel.isDefaultGroup();
	}

	private boolean isPrivilegesChanged(GroupModel groupModel, AccountGroup group) {

		if (groupModel.getPrivileges().size() != group.getPrivileges().size()) {
			return true;
		}

		for (Iterator<?> iterator = group.getPrivileges().iterator(); iterator.hasNext();) {
			Privilege priv = (Privilege) iterator.next();
			if (isPrivilegeRemoved(groupModel.getPrivileges(), priv.getPrivilegeId().toString())) {
				return true;
			}

		}

		return false;
	}

	private boolean isPrivilegeRemoved(List<PrivilegeModel> newPrivileges, String pid) {

		for (PrivilegeModel pm : newPrivileges) {
			if (pm.getPrivilegeId().equals(pid))
				return false;
		}

		return true;
	}

	private void updateDefaultGroup(AccountGroup updatedGroup, AccountGroup defaultGroup, String accountId,
			List<AccountUser> removedUsers, String logId) throws DBException, NotEditableException {

		if (updatedGroup.equals(defaultGroup)) {
			return;
		}

		//Remove added users from default group
		removeUsersFromGroup(logId, updatedGroup.getAccountUsers(), defaultGroup);

		if (isNotEmpty(removedUsers)) {
			//Move the removed users to the default group
			addUsersToGroup(logId, removedUsers, defaultGroup);

			//If removed user is a group admin -> remove him from the  GROUPS_ADMINS_GROUP_NAME
			acctGroupDao.removeAccountGroupAdmins(accountId, removedUsers);
		}

		if (updatedGroup.getDefaultFlag()) {
			defaultGroup.setDefaultFlag(false);
			logger.info(logId + "Default group changed, new default group[" + updatedGroup.getGroupName() + "]");
		}

		acctGroupDao.edit(defaultGroup);
	}

	private void addUsersToGroup(String logId, List<AccountUser> users, AccountGroup group) {
		if (isNotEmpty(users)) {
			if (logger.isDebugEnabled()) {
				logger.debug(logId + "Updating the Default Group, adding [" + users + "]");
			}

			group.getAccountUsers().addAll(users);
		}
	}

	private void removeUsersFromGroup(String logId, List<AccountUser> usersToRemove, AccountGroup group) {

		if (isNotEmpty(usersToRemove)) {
			if (logger.isDebugEnabled()) {
				logger.debug(logId + "Updating the Default Group, removing [" + usersToRemove + "]");
			}

			group.getAccountUsers().removeAll(usersToRemove);
		}
	}

	private void preventAllAdminsRemoval(String logId, AccountGroup defaultGroup, List<AccountUser> newGroupUsers,
			int oldGroupSize) throws LastAdminNotRemovableException {
		//Should not remove all users from Admins group 
		if (defaultGroup.getGroupName().equals(AccountGroupConst.ADMINS_GROUP_NAME)) {
			int adminsGroupSize = defaultGroup.getAccountUsers().size();
			int newGroupSize = newGroupUsers.size();

			int usersRemovedFromAdminsGroup = newGroupSize - oldGroupSize;
			logger.debug(logId + "Prevent all admins removal, newGroupSize=" + newGroupSize + ", oldGroupSize="
					+ oldGroupSize + ", usersRemovedFromAdminsGroup=" + usersRemovedFromAdminsGroup
					+ ", adminsGroupSize=" + adminsGroupSize);

			if (usersRemovedFromAdminsGroup < adminsGroupSize) {
				int remainingAdmins = adminsGroupSize - usersRemovedFromAdminsGroup;
				logger.debug(logId + "remainingAdmins=" + remainingAdmins);
			} else {

				throw new LastAdminNotRemovableException(
						"Can not remove all users from [" + AccountGroupConst.ADMINS_GROUP_NAME + "]");
			}
		}
	}

	private List<AccountUser> getUsersToBeMoved(GroupModel groupModel, List<GroupUserModel> newGroupUsers,
			AccountGroup group) {
		List<AccountUser> groupUsers = group.getAccountUsers();
		List<AccountUser> removedUsers = new ArrayList<>();

		if (isExist(newGroupUsers) && isExist(groupUsers)) {

			for (AccountUser groupUser : groupUsers) {
				if (isUserRemoved(newGroupUsers, groupUser.getAccountUserId())) {
					removedUsers.add(groupUser);
				}
			}
		}
		return removedUsers;
	}

	private boolean isUserRemoved(List<GroupUserModel> newGroupUsers, String id) {

		for (GroupUserModel accountUser : newGroupUsers) {
			if (accountUser.getUserId().equals(id))
				return false;
		}

		return true;
	}

	private void validateGroupForRemoval(String groupName, String accountId) throws NotRemovableException {

		validateGroupName(groupName);

		if (groupName.equals(acctGroupDao.findAccountDefaultGroup(accountId).getGroupName())) {
			throw new NotRemovableException("Default group [" + groupName + "] can not be removed");
		}

		String agn = AccountGroupConst.ADMINS_GROUP_NAME;
		if (groupName.equals(agn)) {
			throw new NotRemovableException("group[" + groupName + "] can not be removed");
		}

	}

	private void validateGroupName(String groupName) throws IllegalArgumentException {
		if (groupName == null || groupName.isEmpty()) {
			throw new IllegalArgumentException("groupName[" + groupName + "]");
		}
	}

	private boolean isExist(List<?> list) {
		return list != null;
	}

	private boolean isNotEmpty(List<?> list) {
		return list != null && !list.isEmpty();
	}

}
