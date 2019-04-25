/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing;

import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AdminAccountManagementFacingLocal;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AdminAccountManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidAddressFormattingException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidMSISDNFormatException;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import com.edafa.web2sms.service.accountmanagement.remote.AccountManegementRemotePoolsLocal;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author mahmoud
 */
@Stateless
public class AdminAccountManagementFacing implements AdminAccountManagementFacingLocal {

    org.apache.logging.log4j.Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

    @EJB
    AccountManegementRemotePoolsLocal accountManegementRemotePools;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Account> findAllAccounts(AccountAdminTrxInfo trxInfo, int first, int count) throws DBException {
        List<Account> accounts = null;
            AdminAccountManagementBeanRemote adminAccountManagementBeanRemote = null;
            try {
                adminAccountManagementBeanRemote = accountManegementRemotePools.getAdminAccountManagementBeanRemote();
            } catch (Exception ex) {
                appLogger.error("Failed to get AdminAccountManagementBeanRemote from AccountManegementBeanRemotePool", ex);
            }
            try {
                accounts = adminAccountManagementBeanRemote.findAllAccounts(trxInfo, first, count);
            } finally {
                try {
                    accountManegementRemotePools.returnAdminAccountManagementBeanRemote(adminAccountManagementBeanRemote);
                } catch (Exception ex) {
                    appLogger.error("Failed to return AdminAccountManagementBeanRemote to AccountManegementBeanRemotePool", ex);
                }
            }

        return accounts;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public QuotaInfo getQuotaInfoByMSISDN(AccountTrxInfo trxInfo, String billingMSISDN) throws DBException, AccountNotFoundException, InvalidCustomerForQuotaInquiry, QuotaInquiryFailed, AccountManagInvalidMSISDNFormatException, AccountManagInvalidAddressFormattingException {
        QuotaInfo quotaInfo = null;
            AdminAccountManagementBeanRemote adminAccountManagementBeanRemote = null;
            try {
                adminAccountManagementBeanRemote = accountManegementRemotePools.getAdminAccountManagementBeanRemote();
            } catch (Exception ex) {
                appLogger.error("Failed to get AdminAccountManagementBeanRemote from AccountManegementBeanRemotePool", ex);
            }
            try {
                quotaInfo = adminAccountManagementBeanRemote.getQuotaInfoByMSISDN(trxInfo, billingMSISDN);
            } finally {
                try {
                    accountManegementRemotePools.returnAdminAccountManagementBeanRemote(adminAccountManagementBeanRemote);
                } catch (Exception ex) {
                    appLogger.error("Failed to return AdminAccountManagementBeanRemote to AccountManegementBeanRemotePool", ex);
                }
            }

        return quotaInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int countAccounts(AccountTrxInfo trxInfo) throws DBException {
        int count = 0;
             AdminAccountManagementBeanRemote adminAccountManagementBeanRemote = null;
            try {
                adminAccountManagementBeanRemote = accountManegementRemotePools.getAdminAccountManagementBeanRemote();
            } catch (Exception ex) {
                appLogger.error("Failed to get AdminAccountManagementBeanRemote from AccountManegementBeanRemotePool", ex);
            }
            try {
                count = adminAccountManagementBeanRemote.countAccounts(trxInfo);
            } finally {
                try {
                    accountManegementRemotePools.returnAdminAccountManagementBeanRemote(adminAccountManagementBeanRemote);
                } catch (Exception ex) {
                    appLogger.error("Failed to return AdminAccountManagementBeanRemote to AccountManegementBeanRemotePool", ex);
                }
            }

        return count;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Account> searchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName, String billingMSISDN, int first, int max) throws DBException {
        List<Account> accounts = null;
            AdminAccountManagementBeanRemote adminAccountManagementBeanRemote = null;
            try {
                adminAccountManagementBeanRemote = accountManegementRemotePools.getAdminAccountManagementBeanRemote();
            } catch (Exception ex) {
                appLogger.error("Failed to get AdminAccountManagementBeanRemote from AccountManegementBeanRemotePool", ex);
            }
            try {
                accounts = adminAccountManagementBeanRemote.searchAccount(trxInfo, accountID, companyName, billingMSISDN, first, max);
            } finally {
                try {
                    accountManegementRemotePools.returnAdminAccountManagementBeanRemote(adminAccountManagementBeanRemote);
                } catch (Exception ex) {
                    appLogger.error("Failed to return AdminAccountManagementBeanRemote to AccountManegementBeanRemotePool", ex);
                }
            }

        return accounts;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long countSearchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName, String billingMSISDN) throws DBException {
        long count = 0;
             AdminAccountManagementBeanRemote adminAccountManagementBeanRemote = null;
            try {
                adminAccountManagementBeanRemote = accountManegementRemotePools.getAdminAccountManagementBeanRemote();
                count = adminAccountManagementBeanRemote.countSearchAccount(trxInfo, accountID, companyName, billingMSISDN);
            } catch (Exception ex) {
                appLogger.error("Failed to get AdminAccountManagementBeanRemote from AccountManegementBeanRemotePool", ex);
            }
            try {
            } finally {
                try {
                    accountManegementRemotePools.returnAdminAccountManagementBeanRemote(adminAccountManagementBeanRemote);
                } catch (Exception ex) {
                    appLogger.error("Failed to return AdminAccountManagementBeanRemote to AccountManegementBeanRemotePool", ex);
                }
            }

        return count;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
