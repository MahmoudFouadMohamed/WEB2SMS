package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountSMSAPI;

@Local
public interface AccountSMSAPIDaoLocal {

	public void create(AccountSMSAPI entity) throws DBException;

	public AccountSMSAPI findByAccountId(String accountId);

	public void remove(AccountSMSAPI entity) throws DBException;

	public int CountByAccountId(String accountId);
	
	//added 12/7
	public List<Account> getAccountListPaginated (int first , int max);
	public int getAccountsCount();
	public void editSmsApiInfo (AccountSMSAPI accountSMSAPI) throws DBException;

    public com.edafa.web2sms.dalayer.pojo.AccountSMSAPI findPojoByAccountId(String accountId);

    public com.edafa.web2sms.dalayer.pojo.AccountSMSAPI findPojoByAccountId(Account account);

}
