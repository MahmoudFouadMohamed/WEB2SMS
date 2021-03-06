/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidMSISDNException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidRequestException;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.UserManagementFacingLocal;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.AdminAlreadyGrantedException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.IneligibleUserException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.SendTempSmsFailException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.Remote.UserManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountProvTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.service.accountmanagement.remote.AccountManegementRemotePoolsLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author mahmoud
 */
@Stateless
public class UserManagementFacing implements UserManagementFacingLocal {

    org.apache.logging.log4j.Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

    @EJB
    AccountManegementRemotePoolsLocal accountManegementRemotePools;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<AccountUser> getAccountUsers(AccountProvTrxInfo trxInfo, List<AccountStatus> statuses) throws UserNotFoundException {
        List<AccountUser> users = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            users = userManagementBeanRemote.getAccountUsers(trxInfo, statuses);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return users;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser getAccountAdminUser(AccountProvTrxInfo trxInfo) {
        AccountUser accountUser = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUser = userManagementBeanRemote.getAccountAdminUser(trxInfo);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser addAccountUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException {
        AccountUser accountUser = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUser = userManagementBeanRemote.addAccountUser(trxInfo, username, userGroups);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser addAccountAdminUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException, AdminAlreadyGrantedException {
        AccountUser accountUser = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUser = userManagementBeanRemote.addAccountAdminUser(trxInfo, username, userGroups);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser getAccountUser(AccountProvTrxInfo trxInfo, String username) throws UserNotFoundException {
        AccountUser accountUser = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUser = userManagementBeanRemote.getAccountUser(trxInfo, username);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser getAccountUser(AccountUserTrxInfo trxInfo) throws UserNotFoundException {
        AccountUser accountUser = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUser = userManagementBeanRemote.getAccountUser(trxInfo);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<AccountUser> getAccountUsers(AccountAdminTrxInfo trxInfo, int first, int max) throws UserNotFoundException {
        List<AccountUser> usersList = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            usersList = userManagementBeanRemote.getAccountUsers(trxInfo, first, max);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return usersList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccountUser activateAdminUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException {
        AccountUser accountUser = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            accountUser = userManagementBeanRemote.activateAdminUser(trxInfo, username, userGroups);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return accountUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deactivateAccountUser(AccountProvTrxInfo trxInfo, String username) throws DBException, AdminAlreadyGrantedException, UserNotFoundException {
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            userManagementBeanRemote.deactivateAccountUser(trxInfo, username);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<AccountUser> searchAccountUsers(AccountAdminTrxInfo trxInfo, String userName, String accountID, String companyName, String billingMSISDN, String userMSISDN, int first, int max) throws UserNotFoundException {
        List<AccountUser> usersList = null;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            usersList = userManagementBeanRemote.searchAccountUsers(trxInfo, userName, accountID, companyName, billingMSISDN, userMSISDN, first, max);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }

        return usersList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long countSearchAccountUsers(AccountAdminTrxInfo trxInfo, String userName, String accountID, String companyName, String billingMSISDN, String userMSISDN) throws UserNotFoundException {
        long count = 0;
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            count = userManagementBeanRemote.countSearchAccountUsers(trxInfo, userName, accountID, companyName, billingMSISDN, userMSISDN);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }
        return count;
    }

    @Override
    public void unblockUser(AccountAdminTrxInfo accountAdminTrxInfo, String userId) throws AccountNotFoundException, UserNotFoundException, DBException, Exception {
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            userManagementBeanRemote.unblockUser(accountAdminTrxInfo, userId);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }
    }

    @Override
    public void generateTempPassword(AccountAdminTrxInfo accountAdminTrxInfo, String userId) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception {
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            userManagementBeanRemote.generateTempPassword(accountAdminTrxInfo, userId);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }
    }

    @Override
    public void updateUserData(AccountAdminTrxInfo userTrxInfo, UserDetailsModel userModel) throws IneligibleUserException, DBException, InvalidRequestException, InvalidMSISDNException {
        UserManagementBeanRemote userManagementBeanRemote = null;
        try {
            userManagementBeanRemote = accountManegementRemotePools.getUserManagementBeanRemote();
        } catch (Exception ex) {
            appLogger.error("Failed to get UserManagementBeanRemote from AccountManegementBeanRemotePool", ex);
        }
        try {
            userManagementBeanRemote.updateUserData(userTrxInfo, userModel);
        } finally {
            try {
                accountManegementRemotePools.returnUserManagementBeanRemote(userManagementBeanRemote);
            } catch (Exception ex) {
                appLogger.error("Failed to return UserManagementBeanRemote to AccountManegementBeanRemotePool", ex);
            }
        }
    }

}
