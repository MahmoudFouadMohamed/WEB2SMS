/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountAlreadyActiveException;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountStateException;
import com.edafa.web2sms.acc_manag.service.account.exception.NoAttachedSendersException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameAlreadyAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountProvTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ProvisioningRequestInfo;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidSMSSender;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountSender;
import com.edafa.web2sms.dalayer.model.Tier;

/**
 *
 * @author mahmoud
 */
@Local
public interface AccountManegementFacingLocal {

	Account getAccount(String accountId);

	// AccountModel findAccount(String campAdmin) throws DBException,
	// AccountNotFoundException;
	Account findAccountById(AccountTrxInfo trxInfo, String accountId) throws DBException, AccountNotFoundException;

	public AccountModel findAccountByCoAdmin(AccountTrxInfo trxInfo, String accountAdmin)
			throws DBException, AccountNotFoundException;

	AccountModelFullInfo findAccountByCoAdminFullInfo(AccountTrxInfo trxInfo, String accountAdmin)
			throws DBException, AccountNotFoundException;

	AccountStatusName getAccountStatus(String accountId) throws DBException;

	public List<ActionName> checkAccountAndUserEligibility(AccountUserTrxInfo userTrxInfo)
			throws IneligibleAccountException, DBException;

	Account checkAccountEligibilitySMSAPI(String trxId, String accountId, ActionName userActionName, int timeOut)
			throws IneligibleAccountException, DBException;

	Account checkAccountEligibilitySMSAPICamp(String trxId, String accountId, ActionName userActionName, int timeOut)
			throws IneligibleAccountException, DBException;

	void checkAccountState(AccountProvTrxInfo trxInfo, AccountStatusName acctStatusName)
			throws InvalidAccountStateException;

	// boolean checkAccountExistance(String acctId) throws DBException;
	ProvisioningRequestInfo deactivateAccount(AccountProvTrxInfo trxInfo)
			throws DBException, AccountNotFoundException, InvalidAccountStateException;

	ProvisioningRequestInfo suspendAccount(AccountProvTrxInfo trxInfo)
			throws DBException, AccountNotFoundException, InvalidAccountStateException;

	ProvisioningRequestInfo reactivateAccountAfterSuspension(AccountProvTrxInfo trxInfo)
			throws DBException, AccountNotFoundException, InvalidAccountStateException;

	TierModel getRateplanTierMappingModel(AccountProvTrxInfo provTrxInfo, String ratePlan)
			throws TierNotFoundException, DBException;

	ProvisioningRequestInfo activateNewAccount(AccountProvTrxInfo provTrxInfo, Account acct, String acctAdmin)
			throws DBException, InvalidAccountException, AccountAlreadyActiveException;

	Tier getRateplanTierMapping(AccountProvTrxInfo provTrxInfo, String ratePlan)
			throws TierNotFoundException, DBException;

	ProvisioningRequestInfo migrateAccount(AccountProvTrxInfo provTrxInfo, Tier newTier)
			throws DBException, AccountNotFoundException, InvalidAccountStateException;

	/*
	 * AccountSender changeAccountSenderName(AccountProvTrxInfo provTrxInfo, String
	 * newSenderName) throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender,
	 * DBException;
	 */
	AccountSender addAccountSenderName(AccountProvTrxInfo provTrxInfo, String senderName)
			throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender, DBException, Exception;

	AccountSender changeAccountSenderName(AccountProvTrxInfo trxInfo, String oldSenderName, String newSenderName)
			throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender, DBException, SenderNameNotAttached;

	void deleteAccountSenderName(AccountProvTrxInfo provTrxInfo, String senderName)
			throws DBException, SenderNameNotAttached, IneligibleAccountException;

	String getAccountSenderName(AccountProvTrxInfo provTrxInfo, String acctId)
			throws NoAttachedSendersException, DBException;

	List<Account> findAllAccounts(AccountAdminTrxInfo trxInfo, int first, int count) throws DBException;

	int countAccounts(AccountTrxInfo trxInfo) throws DBException;

	void validateSMSSender(AccountTrxInfo trxInfo, String senderName) throws AccountManagInvalidSMSSender;

	List<AccountSender> getAccountSenderList(AccountTrxInfo trxInfo, String accountId)
			throws NoAttachedSendersException, DBException;

	AccountModelFullInfo findAccountByMSISDNFullInfo(AccountTrxInfo trxInfo, String msisdn)
			throws DBException, AccountNotFoundException;

	TierTypesEnum getTierTypeNameByTierId(AccountTrxInfo trxInfo, Integer integer);

	void removePrePaidAccount(AccountTrxInfo trxInfo, Account account) throws DBException;

	List<Account> findAllAccounts(AccountTrxInfo trxInfo) throws DBException;

	List<Account> searchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName, String billingMSISDN)
			throws DBException;

}
