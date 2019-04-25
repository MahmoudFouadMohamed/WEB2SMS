package com.edafa.web2sms.acc_manag.service.conversoin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.acc_manag.service.model.GroupBasicInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.GroupUserModel;
import com.edafa.web2sms.acc_manag.service.model.PrivilegeModel;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountGroupDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.Privilege;

/**
 * Session Bean implementation class GroupConversionBean
 */
@Stateless
@LocalBean
public class GroupConversionBean {

	@EJB
	private AccountGroupDaoLocal acctGroupDao;

	/**
	 * Default constructor. 
	 */
	public GroupConversionBean() {
		// zero argument constructor
	}

	public AccountGroup getAccountGroup(GroupModel groupModel, String accountId) {
		AccountGroup group = new AccountGroup();
		group.setAccount(new Account(accountId));
		group.setGroupName(groupModel.getGroupName());
		group.setCreationDate(new Date());
		group.setHiddenFlag(false);
		group.setDefaultFlag(groupModel.isDefaultGroup());

		//Users
		if (isExist(groupModel.getGroupUsers())) {
			List<AccountUser> accountUsers = new ArrayList<AccountUser>();
			for (Iterator<?> iterator = groupModel.getGroupUsers().iterator(); iterator.hasNext();) {
				GroupUserModel userModel = (GroupUserModel) iterator.next();
				accountUsers.add(new AccountUser(userModel.getUserId()));
			}

			group.setAccountUsers(accountUsers);
		}

		if (isExist(groupModel.getPrivileges())) {
			List<Privilege> privileges = new ArrayList<Privilege>();
			for (Iterator<?> iterator = groupModel.getPrivileges().iterator(); iterator.hasNext();) {
				PrivilegeModel pm = (PrivilegeModel) iterator.next();
				BigDecimal pId = new BigDecimal(pm.getPrivilegeId());
				privileges.add(new Privilege(pId));
			}

			group.setPrivileges(privileges);
		}

		return group;
	}

	public AccountGroup updateAccountGroup(GroupModel newGroupModel, String accountId, AccountGroup unupdatedGroup) {

		AccountGroup group = getAccountGroup(newGroupModel, accountId);
		group.setAccountGroupId(unupdatedGroup.getAccountGroupId());
		group.setCreationDate(unupdatedGroup.getCreationDate());
		return group;
	}

	public List<GroupModel> getAccountGroups(List<AccountGroup> accountGroups) throws DBException {

		List<GroupModel> groupModels = new ArrayList<GroupModel>();
		for (Iterator<AccountGroup> iterator = accountGroups.iterator(); iterator.hasNext();) {
			AccountGroup accountGroup = (AccountGroup) iterator.next();
			GroupModel groupModel = getGroupModel(accountGroup);
			groupModels.add(groupModel);
		}

		return groupModels;
	}

	public List<GroupBasicInfo> getAccountGroupsBasicInfo(List<AccountGroup> accountGroups) {

		List<GroupBasicInfo> groupModels = new ArrayList<GroupBasicInfo>();
		for (Iterator<AccountGroup> iterator = accountGroups.iterator(); iterator.hasNext();) {
			AccountGroup accountGroup = (AccountGroup) iterator.next();
			String groupId = accountGroup.getAccountGroupId();
			String groupName = accountGroup.getGroupName();
			Boolean defaultFlag = accountGroup.getDefaultFlag();
			GroupBasicInfo groupModel = new GroupBasicInfo(groupId, groupName, defaultFlag);
			groupModels.add(groupModel);
		}

		return groupModels;
	}

	public GroupModel getGroupModel(AccountGroup accountGroup) throws DBException {
		GroupModel groupModel = new GroupModel();

		groupModel.setGroupName(accountGroup.getGroupName());
		groupModel.setGroupId(accountGroup.getAccountGroupId());
		groupModel.setDefaultGroup(accountGroup.getDefaultFlag());

		//Users
		if (isExist(accountGroup.getAccountUsers())) {
			List<GroupUserModel> groupUserModels = new ArrayList<GroupUserModel>();
			for (Iterator<?> iterator = accountGroup.getAccountUsers().iterator(); iterator.hasNext();) {
				AccountUser accountUser = (AccountUser) iterator.next();
				GroupUserModel groupUserModel = new GroupUserModel(accountUser.getUsername(),
						accountUser.getAccountUserId());
				groupUserModels.add(groupUserModel);
			}

			groupModel.setGroupUsers(groupUserModels);
		}

		if (isExist(accountGroup.getPrivileges())) {
			List<PrivilegeModel> privileges = new ArrayList<PrivilegeModel>();
			for (Iterator<?> iterator = accountGroup.getPrivileges().iterator(); iterator.hasNext();) {
				Privilege privilege = (Privilege) iterator.next();
				PrivilegeModel pId = new PrivilegeModel(privilege.getPrivilegeName(),
						privilege.getPrivilegeId().toPlainString());
				privileges.add(pId);
			}

			groupModel.setPrivileges(privileges);
		}

		AccountUser groupAdmin = acctGroupDao.findAccountGroupAdmin(accountGroup.getAccount().getAccountId(),
				accountGroup.getAccountGroupId());

		if (groupAdmin != null) {
			groupModel.setGroupAdminId(groupAdmin.getAccountUserId());
		}

		return groupModel;
	}

	public List<AccManagUserModel> getAccountGroupUsers(List<AccountUser> groupUsers) {

		List<AccManagUserModel> userModels = new ArrayList<AccManagUserModel>();
		for (Iterator<AccountUser> iterator = groupUsers.iterator(); iterator.hasNext();) {
			AccountUser accountUser = (AccountUser) iterator.next();
			AccManagUserModel userModel = getUserModel(accountUser);
			userModels.add(userModel);
		}

		return userModels;
	}

	private AccManagUserModel getUserModel(AccountUser accountGroup) {
		AccManagUserModel userModel = new AccManagUserModel();
		userModel.setUsername(accountGroup.getUsername());
		userModel.setAccountId(accountGroup.getAccountId());

		return userModel;
	}

	private boolean isExist(List<?> list) {
		return list != null;
	}
}
