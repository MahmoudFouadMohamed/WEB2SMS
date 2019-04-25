/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing;

import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountConversionBeanRemote;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountConversionFacingLocal;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserModel;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.Tier;
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
public class AccountConversionFacing implements AccountConversionFacingLocal {

    org.apache.logging.log4j.Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

    @EJB
    AccountManegementRemotePoolsLocal accountManegementRemotePools;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account getAccount(AccManagUserModel userModel) {
        Account account = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            account = accountConversionBeanRemote.getAccount(userModel);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return account;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account getAccount(AccountModel accountModel) {
        Account account = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            account = accountConversionBeanRemote.getAccount(accountModel);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return account;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser getAccountUser(AccountModel account) {
        AccountUser accountUser = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUser = accountConversionBeanRemote.getAccountUser(account);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountModel getAccountModel(Account account) {
        AccountModel accountModel = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountModel = accountConversionBeanRemote.getAccountModel(account);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountModel;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountModel getAccountModel(AccountUser accountUser) {
        AccountModel accountModel = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountModel = accountConversionBeanRemote.getAccountModel(accountUser);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountModel;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUserModel getAccountUserModel(AccountUser accountUser) {
        AccountUserModel accountUserModel = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUserModel = accountConversionBeanRemote.getAccountUserModel(accountUser);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUserModel;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<AccountUserModel> getAccountUserModel(List<AccountUser> accountUsers) {
        List<AccountUserModel> accountlist = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountlist = accountConversionBeanRemote.getAccountUserModel(accountUsers);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountlist;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser getAccountUser(AccountUserModel accountUserModel) {
        AccountUser accountUser = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUser = accountConversionBeanRemote.getAccountUser(accountUserModel);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountModelFullInfo getAccountModelFullInfo(Account account) {
        AccountModelFullInfo accountModelFullInfo = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountModelFullInfo = accountConversionBeanRemote.getAccountModelFullInfo(account);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountModelFullInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountModelFullInfo getAccountModelFullInfo(AccountUser accountUsr) {
        AccountModelFullInfo accountModelFullInfo = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountModelFullInfo = accountConversionBeanRemote.getAccountModelFullInfo(accountUsr);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountModelFullInfo;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Tier getTier(TierModel tierModel) {
        Tier tier = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            tier = accountConversionBeanRemote.getTier(tierModel);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return tier;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TierModel getTierModel(Tier tier) {
        TierModel tierModel = null;
        AccountConversionBeanRemote accountConversionBeanRemote = null;
        try {
            accountConversionBeanRemote = accountManegementRemotePools.getAccountConversionBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get AccountConversionBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            tierModel = accountConversionBeanRemote.getTierModel(tier);
        } finally {
            try {
                accountManegementRemotePools.returnAccountConversionBeanRemote(accountConversionBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return AccountConversionBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return tierModel;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
