/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidMSISDNException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidRequestException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.AdminAlreadyGrantedException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.IneligibleUserException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.SendTempSmsFailException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.Remote.UserManagementBeanRemote;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.local.UserLoginBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.local.UserManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountProvTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountGroup;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.EJB;

/**
 *
 * @author mahmoud
 */
@Stateless
public class UserManagementBeanWrapper implements UserManagementBeanRemote {

    @EJB
    UserManagementBeanLocal userManagementBeanLocal;
    
    @EJB
    UserLoginBeanLocal  userLoginBeanLocal;

    @Override
    public List<AccountUser> getAccountUsers(AccountProvTrxInfo trxInfo, List<AccountStatus> statuses) throws UserNotFoundException {
        return userManagementBeanLocal.getAccountUsers(trxInfo, statuses);
    }

    @Override
    public AccountUser getAccountAdminUser(AccountProvTrxInfo trxInfo) {
        return userManagementBeanLocal.getAccountAdminUser(trxInfo);
    }

    @Override
    public AccountUser addAccountUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException {
        return userManagementBeanLocal.addAccountUser(trxInfo, username, userGroups);
    }

    @Override
    public AccountUser addAccountAdminUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException, AdminAlreadyGrantedException {
        return userManagementBeanLocal.addAccountAdminUser(trxInfo, username, userGroups);
    }

    @Override
    public AccountUser getAccountUser(AccountProvTrxInfo trxInfo, String username) throws UserNotFoundException {
        return userManagementBeanLocal.getAccountUser(trxInfo, username);
    }

    @Override
    public AccountUser getAccountUser(AccountUserTrxInfo trxInfo) throws UserNotFoundException {
        return userManagementBeanLocal.getAccountUser(trxInfo);
    }

    @Override
    public List<AccountUser> getAccountUsers(AccountAdminTrxInfo trxInfo, int first, int max) throws UserNotFoundException {
        return userManagementBeanLocal.getAccountUsers(trxInfo, first, max);
    }

    @Override
    public AccountUser activateAdminUser(AccountProvTrxInfo trxInfo, String username, List<AccountGroup> userGroups) throws DBException {
        return userManagementBeanLocal.activateAdminUser(trxInfo, username, userGroups);
    }

    @Override
    public void deactivateAccountUser(AccountProvTrxInfo trxInfo, String username) throws DBException, AdminAlreadyGrantedException, UserNotFoundException {
        userManagementBeanLocal.deactivateAccountUser(trxInfo, username);
    }

    @Override
    public List<AccountUser> searchAccountUsers(AccountAdminTrxInfo trxInfo, String userName, String accountID, String companyName, String billingMSISDN, String userMSISDN, int first, int max) throws UserNotFoundException {
        return userManagementBeanLocal.searchAccountUsers(trxInfo, userName, accountID, companyName, billingMSISDN, userMSISDN, first, max);
    }

    @Override
    public long countSearchAccountUsers(AccountAdminTrxInfo trxInfo, String userName, String accountID, String companyName, String billingMSISDN, String userMSISDN) throws UserNotFoundException {
        return userManagementBeanLocal.countSearchAccountUsers(trxInfo, userName, accountID, companyName, billingMSISDN, userMSISDN);
    }

    @Override
    public void unblockUser(AccountAdminTrxInfo accountAdminTrxInfo, String userId) throws AccountNotFoundException, UserNotFoundException, DBException, Exception {
        userLoginBeanLocal.unblockUser(accountAdminTrxInfo, userId);
    }

    @Override
    public void generateTempPassword(AccountAdminTrxInfo accountAdminTrxInfo, String userId) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception {
        userLoginBeanLocal.generateTempPassword(accountAdminTrxInfo, userId);
    }
    
    @Override
    public void updateUserData(AccountAdminTrxInfo userTrxInfo, UserDetailsModel userModel) throws IneligibleUserException, DBException, InvalidRequestException, InvalidMSISDNException {
        userManagementBeanLocal.updateUserData(userTrxInfo, userModel);
    }

}
