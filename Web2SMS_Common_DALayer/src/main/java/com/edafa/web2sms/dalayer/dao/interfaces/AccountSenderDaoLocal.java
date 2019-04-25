package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountSender;

@Local
public interface AccountSenderDaoLocal {

	void create(AccountSender sender) throws DBException;

	void edit(AccountSender sender) throws DBException;

	void remove(AccountSender sender) throws DBException;

	AccountSender find(Object id) throws DBException;

	List<AccountSender> findAll() throws DBException;

	List<AccountSender> findRange(int[] range) throws DBException;

	int count() throws DBException;

	List<AccountSender> findByAccountId(String accountId) throws DBException;

//	AccountSender findBySenderName(String sender) throws DBException;

	void removeAllByAccountId(String acctId) throws DBException;

	int CountByAccountId(String accountId) throws DBException;

//	int CountBySenderName(String sender) throws DBException;

	AccountSender findByAccountIdAndSenderName(String accountId, String sender) throws DBException;

}
