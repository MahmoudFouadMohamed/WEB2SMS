/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing;

import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
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
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountProvTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ProvisioningRequestInfo;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidSMSSender;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountSender;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.service.accountmanagement.remote.AccountManegementRemotePoolsLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import javax.ejb.Stateless;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author mahmoud
 */
@Stateless
public class AccountManegementFacing implements AccountManegementFacingLocal {

    org.apache.logging.log4j.Logger appLogger = LogManager.getLogger(LoggersEnum.PROV_UTILS.name());
    @EJB
    AccountManegementRemotePoolsLocal accountManegementRemotePools;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account findAccountById(AccountTrxInfo trxInfo, String accountId) throws DBException, AccountNotFoundException {
        Account account = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            account = accountManegementBeanRemote.findAccountById(trxInfo, accountId);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return account;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountModel findAccountByCoAdmin(AccountTrxInfo trxInfo, String accountAdmin) throws DBException, AccountNotFoundException {
        AccountModel account = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            account = accountManegementBeanRemote.findAccountByCoAdmin(trxInfo, accountAdmin);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return account;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountModelFullInfo findAccountByCoAdminFullInfo(AccountTrxInfo trxInfo, String accountAdmin) throws DBException, AccountNotFoundException {
        AccountModelFullInfo accountModelFullInfo = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            accountModelFullInfo = accountManegementBeanRemote.findAccountByCoAdminFullInfo(trxInfo, accountAdmin);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return accountModelFullInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void checkAccountState(AccountProvTrxInfo trxInfo, AccountStatusName acctStatusName) throws InvalidAccountStateException {
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            accountManegementBeanRemote.checkAccountState(trxInfo, acctStatusName);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProvisioningRequestInfo deactivateAccount(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException, InvalidAccountStateException {
        ProvisioningRequestInfo provisioningRequestInfo = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            provisioningRequestInfo = accountManegementBeanRemote.deactivateAccount(trxInfo);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return provisioningRequestInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProvisioningRequestInfo suspendAccount(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException, InvalidAccountStateException {
        ProvisioningRequestInfo provisioningRequestInfo = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            provisioningRequestInfo = accountManegementBeanRemote.suspendAccount(trxInfo);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return provisioningRequestInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProvisioningRequestInfo reactivateAccountAfterSuspension(AccountProvTrxInfo trxInfo) throws DBException, AccountNotFoundException, InvalidAccountStateException {
        ProvisioningRequestInfo provisioningRequestInfo = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            provisioningRequestInfo = accountManegementBeanRemote.reactivateAccountAfterSuspension(trxInfo);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return provisioningRequestInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TierModel getRateplanTierMappingModel(AccountProvTrxInfo provTrxInfo, String ratePlan) throws TierNotFoundException, DBException {
        TierModel tierModel = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            tierModel = accountManegementBeanRemote.getRateplanTierMappingModel(provTrxInfo, ratePlan);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return tierModel;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProvisioningRequestInfo activateNewAccount(AccountProvTrxInfo provTrxInfo, Account acct, String acctAdmin) throws DBException, InvalidAccountException, AccountAlreadyActiveException {
        ProvisioningRequestInfo provisioningRequestInfo = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            provisioningRequestInfo = accountManegementBeanRemote.activateNewAccount(provTrxInfo, acct, acctAdmin);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return provisioningRequestInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Tier getRateplanTierMapping(AccountProvTrxInfo provTrxInfo, String ratePlan) throws TierNotFoundException, DBException {
        Tier tier = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            tier = accountManegementBeanRemote.getRateplanTierMapping(provTrxInfo, ratePlan);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return tier;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProvisioningRequestInfo migrateAccount(AccountProvTrxInfo provTrxInfo, Tier newTier) throws DBException, AccountNotFoundException, InvalidAccountStateException {
        ProvisioningRequestInfo provisioningRequestInfo = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            provisioningRequestInfo = accountManegementBeanRemote.migrateAccount(provTrxInfo, newTier);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return provisioningRequestInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountSender addAccountSenderName(AccountProvTrxInfo provTrxInfo, String senderName) throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender, DBException, Exception {
        AccountSender accountSender = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            accountSender = accountManegementBeanRemote.addAccountSenderName(provTrxInfo, senderName);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return accountSender;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountSender changeAccountSenderName(AccountProvTrxInfo trxInfo, String oldSenderName, String newSenderName) throws SenderNameAlreadyAttached, AccountManagInvalidSMSSender, DBException, SenderNameNotAttached {
        AccountSender accountSender = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            accountSender = accountManegementBeanRemote.changeAccountSenderName(trxInfo, oldSenderName, newSenderName);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return accountSender;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteAccountSenderName(AccountProvTrxInfo provTrxInfo, String senderName) throws DBException, SenderNameNotAttached, IneligibleAccountException {
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            accountManegementBeanRemote.deleteAccountSenderName(provTrxInfo, senderName);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String getAccountSenderName(AccountProvTrxInfo provTrxInfo, String acctId) throws NoAttachedSendersException, DBException {
        String senderName = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            senderName = accountManegementBeanRemote.getAccountSenderName(provTrxInfo, acctId);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return senderName;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void validateSMSSender(AccountTrxInfo trxInfo, String senderName) throws AccountManagInvalidSMSSender {
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            accountManegementBeanRemote.validateSMSSender(trxInfo, senderName);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountModelFullInfo findAccountByMSISDNFullInfo(AccountTrxInfo trxInfo, String msisdn) throws DBException, AccountNotFoundException {
        AccountModelFullInfo accountModelFullInfo = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            accountModelFullInfo = accountManegementBeanRemote.findAccountByMSISDNFullInfo(trxInfo, msisdn);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return accountModelFullInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TierTypesEnum getTierTypeNameByTierId(AccountTrxInfo trxInfo, Integer integer) {
        TierTypesEnum tierTypesName = null;
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            tierTypesName = accountManegementBeanRemote.getTierTypeNameByTierId(trxInfo, integer);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

        return tierTypesName;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removePrePaidAccount(AccountTrxInfo trxInfo, Account account) throws DBException {
        AccountManegementBeanRemote accountManegementBeanRemote = null;
        try {
            accountManegementBeanRemote = accountManegementRemotePools.getAccountManegementBeanRemote();
        } catch (Exception e) {
            appLogger.error("Failed to get AccountManegementBeanRemote from AccountManegementBeanRemotePool", e);
        }
        try {
            accountManegementBeanRemote.removePrePaidAccount(trxInfo, account);
        } finally {
            try {
                accountManegementRemotePools.returnAccountManegementBeanRemote(accountManegementBeanRemote);
            } catch (Exception e) {
                appLogger.error("Failed to return AccountManegementBeanRemote to AccountManegementBeanRemotePool", e);
            }
        }

    }

}
