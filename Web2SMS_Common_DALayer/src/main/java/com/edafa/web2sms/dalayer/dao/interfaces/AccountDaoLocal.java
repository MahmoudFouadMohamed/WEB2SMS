/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountStatus;

/**
 * 
 * @author yyaseen
 */
@Local
public interface AccountDaoLocal {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	void create(Account account) throws DBException;

	void edit(Account account) throws DBException;

	void remove(Account account) throws DBException;

	Account find(Object id) throws DBException;

	List<Account> findAll() throws DBException;

	List<Account> findRange(int[] range) throws DBException;

	public List<Account> findRange(int frist, int max, String order) throws DBException;

	int count() throws DBException;

	int count(Object id) throws DBException;

	int updateAccountStatus(String acctId, AccountStatus newStatus) throws DBException;

	Account findByCompanyName(String companyName) throws DBException;

	String getAccountIdByCompanyId(String companyId);

	Account findByAccountId(String accountId) throws DBException;

    Account findWithSMSAPIByAccountId(String accountId, int timeOut) throws DBException;
    
    Account findWithSMSAPICampByAccountId(String accountId, int timeOut) throws DBException;

	List<Account> findByStatuses(List<AccountStatus> statusList) throws DBException;

	Account findByBillingMSISDN(String msisdn) throws DBException;

	List<Account> searchAccount(String accountID, String companyName, String billingMSISDN,
			List<AccountStatus> statuses, int first, int max) throws DBException;

	long countSearchAccount(String accountID, String companyName, String billingMSISDN, List<AccountStatus> statuses)throws DBException;

	List<Account> searchAccount(String accountID, String companyName, String billingMSISDN, List<AccountStatus> statuses)
			throws DBException;
    public Account findByIdAndUserNameAndAction(String accountId, String userName, ActionName actionName) throws DBException;

    public Account findWithSmsApiByIdAndAction(String accountId, ActionName actionName, int timeOut) throws DBException;

    public Account findWithSmsApiCampByIdAndAction(String accountId, List<ActionName> actionName, int timeOut) throws DBException;

//	boolean isValidAccount(String accountId) throws DBException;

}
