package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;

@Local
public interface AccountUserDaoLocal {

	public void create(AccountUser acctUser) throws DBException;
	public void remove(AccountUser acctUser) throws DBException;
	public void edit(AccountUser acctUser) throws DBException;

	public List<AccountUser> findAccountUsers(String acctId, List<AccountStatus> acctStatus);

	public AccountUser findAccountAdminUser(String acctId, List<AccountStatus> acctStatus);

	public AccountUser findAccountUser(String acctId, String username, List<AccountStatus> acctStatus);

	List<AccountUser> searchAccountUser(String userName, String accountID, String companyName, String billingMSISDN, String userMSISDN,
			List<AccountStatus> statuses, int first, int max) throws DBException;

	long countSearchAccountUser(String userName, String accountID, String companyName, String billingMSISDN, String userMSISDN,
			List<AccountStatus> statuses) throws DBException;

	public List<AccountUser> findRange(int frist, int max, String order) throws DBException;

	public AccountUser findAccountUserById(String id) throws DBException;
	int countAccountUsers(String acctId, List<AccountStatus> acctStatus);
	public int countActiveAccountUsers(String accountId, String search, String groupId) throws DBException;
	public List<AccountUser> searchActiveAccountUsers(String accountId, String search, String groupId, int first, int max)
			throws DBException;

}
