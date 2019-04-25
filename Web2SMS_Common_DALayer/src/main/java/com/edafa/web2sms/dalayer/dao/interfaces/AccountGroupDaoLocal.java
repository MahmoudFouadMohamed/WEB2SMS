package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountUser;

@Local
public interface AccountGroupDaoLocal {

	void create(AccountGroup acctGroup) throws DBException;
	void remove(AccountGroup acctGroup) throws DBException;
	void edit(AccountGroup defaultGroup) throws DBException;

	List<AccountGroup> findAccountGroups(String acctId);
	AccountGroup findAccountGroup(String acctId, String groupName);
	AccountGroup findAccountGroup(String groupId);

	List<AccountUser> findAccountGroupUsers(String acctId, String groupName);

	void addUserToGroup(String acctId, String groupName, String userName) throws DBException;
	void removeUserFromGroup(String acctId, String groupName, String userName) throws DBException;

	int countAccountGroups(String accountId, String groupName, String userName) throws DBException;
	void removeUsersFromGroup(String accountId, AccountGroup accountGroup, List<AccountUser> accountUsers)
			throws DBException;
	void addUsersToGroup(String accountId, AccountGroup accountGroup, List<AccountUser> accountUsers)
			throws DBException;
	AccountGroup findAccountDefaultGroup(String accountId);

	AccountUser findAccountGroupAdmin(String accountId, String groupId) throws DBException;

	List<AccountGroup> searchGroups(String accountId, String groupName, String userName, int first, int max)
			throws DBException;
	void removeAccountGroupAdmins(String accountId, List<AccountUser> users) throws DBException;

}
