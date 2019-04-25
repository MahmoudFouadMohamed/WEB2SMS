/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountAlreadyActiveException;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountStateException;
import com.edafa.web2sms.acc_manag.service.account.exception.NoAttachedSendersException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameAlreadyAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountManegementBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AdminAccountManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountManegementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AdminAccountManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountProvTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ProvisioningRequestInfo;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidAddressFormattingException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidMSISDNFormatException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidSMSSender;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountSender;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.EJB;

/**
 *
 * @author mahmoud
 */
@Stateless
public class AccountManegementBeanWrapper implements AccountManegementBeanRemote, AdminAccountManagementBeanRemote {

    @EJB
    AccountManegementBeanLocal accountManegementBeanLocal;

    @EJB
    AdminAccountManagementBeanLocal adminAccountManagementBeanLocal;

    @Override
    public Account getAccount(String accountId) {
        return accountManegementBeanLocal.getAccount(accountId);
    }

    @Override
    public Account findAccountById(AccountTrxInfo trxInfo, String accountId) throws DBException, AccountNotFoundException {
        return accountManegementBeanLocal.findAccountById(trxInfo, accountId);
    }

    @Override
    public AccountModel findAccountByCoAdmin(AccountTrxInfo trxInfo, String accountAdmin) throws DBException, AccountNotFoundException {
        return accountManegementBeanLocal.findAccountByCoAdmin(trxInfo, accountAdmin);
    }

    @Override
    public AccountModelFullInfo findAccountByCoAdminFullInfo(AccountTrxInfo trxInfo, String accountAdmin) throws DBException, AccountNotFoundException {
        return accountManegementBeanLocal.findAccountByCoAdminFullInfo(trxInfo, accountAdmin);
    }

    @Override
    public AccountStatusName getAccountStatus(String accountId) throws DBException {
        return accountManegementBeanLocal.getAccountStatus(accountId);
    }

    @Override
    public List<ActionName> checkAccountAndUserEligibility(AccountUserTrxInfo userTrxInfo) throws DBException, IneligibleAccountException {
        return accountManegementBeanLocal.checkAccountAndUserEligibility(userTrxInfo);
    }

    @Override
    public Account checkAccountEligibilitySMSAPI(String trxId, String accountId, ActionName userActionName, int timeOut) throws IneligibleAccountException, DBException {
        return accountManegementBeanLocal.checkAccountEligibilitySMSAPI(trxId, accountId, userActionName, timeOut);
    }

    @Override
    public Account checkAccountEligibilitySMSAPICamp(String trxId, String accountId, ActionName userActionName, int timeOut) throws IneligibleAccountException, DBException {
        return accountManegementBeanLocal.checkAccountEligibilitySMSAPICamp(trxId, accountId, userActionName, timeOut);
    }

    @Override
    public void checkAccountState(AccountProvTrxInfo trxInfo, AccountStatusName acctStatusName) throws InvalidAccountStateException {
        accountManegementBeanLocal.checkAccountState(trxInfo, acctStatusName);
    }

    @Override
    public ProvisioningRequestInfo deactivateAccount(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException, InvalidAccountStateException {
        return accountManegementBeanLocal.deactivateAccount(trxInfo);
    }

    @Override
    public ProvisioningRequestInfo suspendAccount(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException, InvalidAccountStateException {
        return accountManegementBeanLocal.suspendAccount(trxInfo);
    }

    @Override
    public ProvisioningRequestInfo reactivateAccountAfterSuspension(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException, InvalidAccountStateException {
        return accountManegementBeanLocal.reactivateAccountAfterSuspension(trxInfo);
    }

    @Override
    public TierModel getRateplanTierMappingModel(AccountProvTrxInfo provTrxInfo, String ratePlan) throws TierNotFoundException, DBException {
        return accountManegementBeanLocal.getRateplanTierMappingModel(provTrxInfo, ratePlan);
    }

    @Override
    public ProvisioningRequestInfo activateNewAccount(AccountProvTrxInfo provTrxInfo, Account acct, String acctAdmin) throws DBException, InvalidAccountException, AccountAlreadyActiveException {
        return accountManegementBeanLocal.activateNewAccount(provTrxInfo, acct, acctAdmin);
    }

    @Override
    public Tier getRateplanTierMapping(AccountProvTrxInfo provTrxInfo, String ratePlan) throws TierNotFoundException, DBException {
        return accountManegementBeanLocal.getRateplanTierMapping(provTrxInfo, ratePlan);
    }

    @Override
    public ProvisioningRequestInfo migrateAccount(AccountProvTrxInfo provTrxInfo, Tier newTier) throws DBException, AccountNotFoundException, InvalidAccountStateException {
        return accountManegementBeanLocal.migrateAccount(provTrxInfo, newTier);
    }

    @Override
    public AccountSender addAccountSenderName(AccountProvTrxInfo provTrxInfo, String senderName) throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender, DBException, Exception {
        return accountManegementBeanLocal.addAccountSenderName(provTrxInfo, senderName);
    }

    @Override
    public AccountSender changeAccountSenderName(AccountProvTrxInfo trxInfo, String oldSenderName, String newSenderName) throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender, DBException, SenderNameNotAttached {
        return accountManegementBeanLocal.changeAccountSenderName(trxInfo, oldSenderName, newSenderName);
    }

    @Override
    public void deleteAccountSenderName(AccountProvTrxInfo provTrxInfo, String senderName) throws DBException, SenderNameNotAttached, IneligibleAccountException {
        accountManegementBeanLocal.deleteAccountSenderName(provTrxInfo, senderName);
    }

    @Override
    public String getAccountSenderName(AccountProvTrxInfo provTrxInfo, String acctId) throws NoAttachedSendersException, DBException {
        return accountManegementBeanLocal.getAccountSenderName(provTrxInfo, acctId);
    }

    @Override
    public List<Account> findAllAccounts(AccountAdminTrxInfo trxInfo, int first, int count) throws DBException {
        return accountManegementBeanLocal.findAllAccounts(trxInfo, first, count);
    }

    @Override
    public int countAccounts(AccountTrxInfo trxInfo) throws DBException {
        return accountManegementBeanLocal.countAccounts(trxInfo);
    }

    @Override
    public void validateSMSSender(AccountTrxInfo trxInfo, String senderName) throws AccountManagInvalidSMSSender {
        accountManegementBeanLocal.validateSMSSender(trxInfo, senderName);
    }

    @Override
    public List<AccountSender> getAccountSenderList(AccountTrxInfo trxInfo, String accountId) throws NoAttachedSendersException, DBException {
        return accountManegementBeanLocal.getAccountSenderList(trxInfo, accountId);
    }

    @Override
    public AccountModelFullInfo findAccountByMSISDNFullInfo(AccountTrxInfo trxInfo, String msisdn) throws DBException, AccountNotFoundException {
        return accountManegementBeanLocal.findAccountByMSISDNFullInfo(trxInfo, msisdn);
    }

    @Override
    public TierTypesEnum getTierTypeNameByTierId(AccountTrxInfo trxInfo, Integer integer) {
        return accountManegementBeanLocal.getTierTypeNameByTierId(trxInfo, integer);
    }

    @Override
    public void removePrePaidAccount(AccountTrxInfo trxInfo, Account account) throws DBException {
        accountManegementBeanLocal.removePrePaidAccount(trxInfo, account);
    }

    @Override
    public List<Account> findAllAccounts(AccountTrxInfo trxInfo) throws DBException {
        return accountManegementBeanLocal.findAllAccounts(trxInfo);
    }

    @Override
    public List<Account> searchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName, String billingMSISDN) throws DBException {
        return accountManegementBeanLocal.searchAccount(trxInfo, accountID, companyName, billingMSISDN);
    }

    @Override
    public QuotaInfo getQuotaInfoByMSISDN(AccountTrxInfo trxInfo, String billingMSISDN) throws DBException, AccountNotFoundException, InvalidCustomerForQuotaInquiry, QuotaInquiryFailed, AccountManagInvalidMSISDNFormatException, AccountManagInvalidAddressFormattingException {
        return adminAccountManagementBeanLocal.getQuotaInfoByMSISDN(trxInfo, billingMSISDN);
    }

    @Override
    public List<Account> searchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName, String billingMSISDN, int first, int max) throws DBException {
        return adminAccountManagementBeanLocal.searchAccount(trxInfo, accountID, companyName, billingMSISDN, first, max);
    }

    @Override
    public long countSearchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName, String billingMSISDN) throws DBException {
        return adminAccountManagementBeanLocal.countSearchAccount(trxInfo, accountID, companyName, billingMSISDN);
    }

}
