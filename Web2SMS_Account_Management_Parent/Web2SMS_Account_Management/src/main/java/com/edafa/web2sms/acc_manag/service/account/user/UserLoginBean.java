/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountConversionBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.local.AccountManegementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.model.AccountResultFullInfo;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.SendTempSmsFailException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserLoginServiceBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.local.UserLoginBeanLocal;
import com.edafa.web2sms.acc_manag.service.conversoin.UserConversionBean;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.utils.configs.enums.Configs;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.dalayer.dao.ActionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountGroupDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserLoginDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.IntraSenderDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.UserLoginStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.enums.UserLoginStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.AccountUserLogin;
import com.edafa.web2sms.dalayer.model.IntraSender;
import com.edafa.web2sms.dalayer.model.LoginOldPassword;
import com.edafa.web2sms.sms.enums.SMSResponseStatus;
import com.edafa.web2sms.sms.interfaces.SubmitSMSBeanRemote;
import com.edafa.web2sms.sms.model.SMSDetails;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mahmoud saad
 */
@Stateless
public class UserLoginBean implements UserLoginServiceBeanLocal, UserLoginBeanLocal {

    Logger appLogger;
    Logger acctLogger;

    @EJB
    AccountUserDaoLocal accountUserDao;

    @EJB
    AccountStatusDaoLocal accountStatusDao;

    @EJB
    AccountDaoLocal accountDao;

    @EJB
    AccountGroupDaoLocal accountGroupDao;

    @EJB
    ActionDaoLocal actionDao;

    @EJB
    AccountManegementBeanLocal accountManegementBean;

    @EJB
    AccountManegementBeanLocal accountManagement;

    @EJB
    UserConversionBean userConversionBean;

    @EJB
    private AccountGroupDaoLocal acctGroupDao;

    @EJB
    private UserLoginStatusDaoLocal userLoginStatusDao;

    @EJB
    private UserLoginUtils userLoginUtils;

    @EJB
    private AccountConversionBeanLocal acctConversionBean;

    @EJB
    private IntraSenderDaoLocal intraSenderDao;

    @EJB
    private AccountUserLoginDaoLocal accountUserLogin;

    /**
     * Default constructor.
     */
    public UserLoginBean() {
        // zero argument constructor
    }

    @PostConstruct
    public void initLoggers() {
        appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
        acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
    }

    @Override
    public ResultStatus checkUserForLogin(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception {
        acctLogger.debug(userTrxInfo.logInfo() + "Retriving account with company name");
        Account acct = accountDao.findByCompanyName(companyName);
        if (acct == null) {
            throw new AccountNotFoundException();
        }
        AccountUser acctUser = getUser(userTrxInfo.logId(), acct.getAccountId(), userTrxInfo.getUser().getUsername(), true);

        ResponseStatus loginstatus = handleUserLoginEvent(userTrxInfo.logId(), acctUser, new LoginEvent(UserLoginEventsEnum.USER_CHECK, userLang));

        acctLogger.info(userTrxInfo.logInfo() + "Request handled with ResponseStatus(" + loginstatus + ")");
        return new ResultStatus(loginstatus);
    }

    @Override
    public AccountResultFullInfo userLogin(AccountUserTrxInfo userTrxInfo, String companyName, String password, String userLang) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception {
        acctLogger.debug(userTrxInfo.logInfo() + "Retriving account with company name");
        Account acct = accountDao.findByCompanyName(companyName);
        if (acct == null) {
            throw new AccountNotFoundException();
        }
        AccountUser acctUser = getUser(userTrxInfo.logId(), acct.getAccountId(), userTrxInfo.getUser().getUsername(), true);

        ResponseStatus loginResult = handleUserLoginEvent(userTrxInfo.logId(), acctUser, new UserLoginEvent(UserLoginEventsEnum.USER_LOGIN, password, userLang));

        AccountResultFullInfo accountResultFullInfo = new AccountResultFullInfo(loginResult);

        if (loginResult == ResponseStatus.SUCCESS || loginResult == ResponseStatus.CHANGE_PASSWORD || loginResult == ResponseStatus.CHANGE_PASSWORD_OLD_REQUIRED) {
            AccountModelFullInfo account = getAccountFullInfo(userTrxInfo, acctUser);
            accountResultFullInfo.setAccount(account);
        }
        acctLogger.info(userTrxInfo.logInfo() + "Request handled with ResponseStatus(" + loginResult + ")");
        return accountResultFullInfo;
    }

    @Override
    public AccountResultFullInfo directUserLogin(AccountUserTrxInfo userTrxInfo, String companyName) throws AccountNotFoundException, UserNotFoundException, DBException, Exception {
        AccountResultFullInfo accountResultFullInfo;
        if ((boolean) Configs.ENABLE_DIRECT_LOGIN.getValue()) {
            acctLogger.debug(userTrxInfo.logInfo() + "Retriving account with company name");
            Account acct = accountDao.findByCompanyName(companyName);
            accountResultFullInfo = new AccountResultFullInfo(ResponseStatus.SUCCESS);
            if (acct == null) {
                throw new AccountNotFoundException();
            }
            AccountUser acctUser = getUser(userTrxInfo.logId(), acct.getAccountId(), userTrxInfo.getUser().getUsername(), false);
            AccountModelFullInfo account = getAccountFullInfo(userTrxInfo, acctUser);
            accountResultFullInfo.setAccount(account);
        } else {
            acctLogger.warn(userTrxInfo.logInfo() + "Direct login not enabled");
            accountResultFullInfo = new AccountResultFullInfo(ResponseStatus.FAIL);
        }

        acctLogger.info(userTrxInfo.logInfo() + "Request handled with ResponseStatus(" + accountResultFullInfo.getStatus() + ")");
        return accountResultFullInfo;
    }

    @Override
    public ResultStatus changeUserPassword(AccountUserTrxInfo userTrxInfo, String oldPassword, String newPassword, String secureToken) throws AccountNotFoundException, UserNotFoundException, DBException, Exception {
        acctLogger.debug(userTrxInfo.logInfo() + "Retriving account with account id");
        Account acct = accountDao.findByAccountId(userTrxInfo.getUser().getAccountId());
        if (acct == null) {
            throw new AccountNotFoundException();
        }
        AccountUser acctUser = getUser(userTrxInfo.logId(), acct.getAccountId(), userTrxInfo.getUser().getUsername(), true);

//        String tokenKey = (String) Configs.TOKEN_KEY.getValue();
//        int tokenValidPeriod = (int) Configs.TOKEN_EXPIRE_PERIOD.getValue();
//        userLoginUtils.validateSecureToken(secureToken, tokenKey, acctUser.getUsername(), acctUser.getAccountId(), tokenValidPeriod);
        ResponseStatus loginstatus = handleUserLoginEvent(userTrxInfo.logId(), acctUser, new ChangePasswordEvent(UserLoginEventsEnum.CHANGE_PASSWORD, oldPassword, newPassword, null));

        acctLogger.info(userTrxInfo.logInfo() + "Request handled with ResponseStatus(" + loginstatus + ")");
        return new ResultStatus(loginstatus);
    }

    @Override
    public ResultStatus userForgetPassword(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception {
        acctLogger.debug(userTrxInfo.logInfo() + "Retriving account with company name");
        Account acct = accountDao.findByCompanyName(companyName);
        if (acct == null) {
            throw new AccountNotFoundException();
        }
        AccountUser acctUser = getUser(userTrxInfo.logId(), acct.getAccountId(), userTrxInfo.getUser().getUsername(), true);
        ResponseStatus loginstatus = handleUserLoginEvent(userTrxInfo.logId(), acctUser, new LoginEvent(UserLoginEventsEnum.FORGET_PASSWORD, userLang));

        acctLogger.info(userTrxInfo.logInfo() + "Request handled with ResponseStatus(" + loginstatus + ")");
        return new ResultStatus(loginstatus);
    }

    @Override
    public ResultStatus resendTempPassword(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) throws AccountNotFoundException, UserNotFoundException, DBException, SendTempSmsFailException, Exception {
        acctLogger.debug(userTrxInfo.logInfo() + "Retriving account with company name");
        Account acct = accountDao.findByCompanyName(companyName);
        if (acct == null) {
            throw new AccountNotFoundException();
        }
        AccountUser acctUser = getUser(userTrxInfo.logId(), acct.getAccountId(), userTrxInfo.getUser().getUsername(), true);
        ResponseStatus loginstatus = handleUserLoginEvent(userTrxInfo.logId(), acctUser, new LoginEvent(UserLoginEventsEnum.FORGET_PASSWORD, userLang));

        acctLogger.info(userTrxInfo.logInfo() + "Request handled with ResponseStatus(" + loginstatus + ")");
        return new ResultStatus(loginstatus);
    }

    @Override
    public ResultStatus unblockUser(AccountAdminTrxInfo accountAdminTrxInfo, String userId) throws UserNotFoundException, SendTempSmsFailException, Exception {
        acctLogger.debug(accountAdminTrxInfo.logInfo() + "Retriving account with company name");

        AccountUser acctUser = checkUserExistance(accountAdminTrxInfo.logId(), userId);
        ResponseStatus loginstatus = handleUserLoginEvent(accountAdminTrxInfo.logId(), acctUser, new LoginEvent(UserLoginEventsEnum.USER_UNLOCK, null));

        acctLogger.info(accountAdminTrxInfo.logInfo() + "Request handled with ResponseStatus(" + loginstatus + ")");
        return new ResultStatus(loginstatus);
    }

    @Override
    public ResultStatus generateTempPassword(AccountAdminTrxInfo accountAdminTrxInfo, String userId) throws UserNotFoundException, SendTempSmsFailException, Exception {
        acctLogger.debug(accountAdminTrxInfo.logInfo() + "Retriving account with company name");

        AccountUser acctUser = checkUserExistance(accountAdminTrxInfo.logId(), userId);
        ResponseStatus loginstatus = handleUserLoginEvent(accountAdminTrxInfo.logId(), acctUser, new LoginEvent(UserLoginEventsEnum.GENERATE_PASSWORD, null));

        acctLogger.info(accountAdminTrxInfo.logInfo() + "Request handled with ResponseStatus(" + loginstatus + ")");
        return new ResultStatus(loginstatus);
    }

    private AccountModelFullInfo getAccountFullInfo(AccountUserTrxInfo userTrxInfo, AccountUser acctUser) throws DBException {

        List<IntraSender> intraSenderSys = intraSenderDao.findSystemIntraSendersList();
        AccountModelFullInfo account = acctConversionBean.getAccountModelFullInfo(acctUser);
        if (account != null && intraSenderSys != null) {
            List<String> intraSenders = new ArrayList<String>();
            for (int i = 0; i < intraSenderSys.size(); i++) {
                intraSenders.add(intraSenderSys.get(i).getSenderName());
            }
            account.getIntraSenders().addAll(intraSenders);
        }
        acctLogger.debug(userTrxInfo.logId() + "account user: " + acctUser + " converted to account model full info: "
                + account);

        acctLogger.debug(userTrxInfo.logInfo() + "Get User Allowed Actions");
        List<ActionName> allowedActionNames = actionDao.getUserAllowedActions(acctUser.getAccountId(), userTrxInfo.getUser().getUsername());

        if (allowedActionNames != null && !allowedActionNames.isEmpty()) {
            List<ActionName> addedActions = new ArrayList<>();
            Iterator<ActionName> allowedAction = allowedActionNames.iterator();
            while (allowedAction.hasNext()) {
                ActionName actionName = allowedAction.next();
                if (actionName.equals(ActionName.VIEW_OWN_GROUP)) {
                    allowedAction.remove();
                    addedActions.add(ActionName.VIEW_GROUPS);
                } else if (actionName.equals(ActionName.VIEW_OWN_GROUP_USERS)) {
                    allowedAction.remove();
                    addedActions.add(ActionName.VIEW_USERS);
                }
            }
            for (ActionName actionName : addedActions) {
                if (!allowedActionNames.contains(actionName)) {
                    allowedActionNames.add(actionName);
                }
            }
        }
        AccManagUserModel userModel = new AccManagUserModel();
        userModel.setUsername(userTrxInfo.getUser().getUsername());
        userModel.setAccountId(acctUser.getAccountId());
        userModel.setUserActions(allowedActionNames);

        account.setLoginUser(userModel);
        return account;
    }

    private AccountUser getUser(String trxInfo, String accountId, String userName, boolean addLoginData) throws UserNotFoundException {
        List<AccountStatus> userStatus = new ArrayList<>();
        userStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
        //userStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.SUSPENDED));

        acctLogger.debug(trxInfo + "Retriving user(" + userName + ")");
        AccountUser acctUser = accountUserDao.findAccountUser(accountId, userName, userStatus);

        if (acctUser == null) {
            acctLogger.error(trxInfo + "User(" + userName + ") not found or not actve user");
            throw new UserNotFoundException(trxInfo, userName);
        }

        if (acctUser.getAccountUserLogin() == null && addLoginData == true) {
            AccountUserLogin userLogin = new AccountUserLogin();
            userLogin.setAccountUserId(acctUser);
            userLogin.setUserLoginStatus(userLoginStatusDao.getCachedObjectByName(UserLoginStatusName.INITIAL));
            acctUser.setAccountUserLogin(userLogin);
        }

        return acctUser;
    }

    private AccountUser checkUserExistance(String trxInfo, String userId) throws UserNotFoundException, DBException {
        List<AccountStatus> userStatus = new ArrayList<>();
        userStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
        // userStatus.add(accountStatusDao.getCachedObjectByName(AccountStatusName.SUSPENDED));

        acctLogger.debug(trxInfo + "Retriving user with id(" + userId + ")");
        AccountUser acctUser = accountUserDao.findAccountUserById(userId);

        if (acctUser == null) {
            acctLogger.error(trxInfo + "User with id (" + userId + ") not found or not actve user");
            throw new UserNotFoundException(trxInfo, userId);
        }

        if (acctUser.getAccountUserLogin() == null) {
            AccountUserLogin userLogin = new AccountUserLogin();
            userLogin.setAccountUserId(acctUser);
            userLogin.setUserLoginStatus(userLoginStatusDao.getCachedObjectByName(UserLoginStatusName.INITIAL));
            acctUser.setAccountUserLogin(userLogin);
        }

        return acctUser;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private ResponseStatus handleUserLoginEvent(String trxInfo, AccountUser acctUser, LoginEvent userEvent) throws DBException, SendTempSmsFailException, Exception {
        UserLoginStatusName userStatus = acctUser.getAccountUserLogin().getUserLoginStatus().getName();
        ResponseStatus returnStatus;
        acctLogger.debug(trxInfo + "Handle login event(" + userEvent.getLoginEventName().getEventName()
                + ") from user(" + acctUser.getUsername() + ") with login status(" + userStatus + ")");

        switch (userStatus) {
            case INITIAL: {
                returnStatus = handleEventAtInitialState(trxInfo, acctUser, userEvent);
                break;
            }
            case TEMP: {
                returnStatus = handleEventAtTempState(trxInfo, acctUser, userEvent);
                break;
            }
            case ACTIVE: {
                returnStatus = handleEventAtActiveState(trxInfo, acctUser, userEvent);
                break;
            }
            case BLOCKED: {
                returnStatus = handleEventAtBlockState(trxInfo, acctUser, userEvent);
                break;
            }
            default: {
                returnStatus = ResponseStatus.FAIL;
            }
        }

        return returnStatus;
    }

    private ResponseStatus handleEventAtInitialState(String trxInfo, AccountUser acctUser, LoginEvent userEvent) throws DBException, Exception {

        String userTempPassword = null;
        ResponseStatus returnStatus = ResponseStatus.SUCCESS;

        switch (userEvent.getLoginEventName()) {
            case USER_CHECK:
            case GENERATE_PASSWORD: {
                if (acctUser.getPhoneNumber() != null && !acctUser.getPhoneNumber().isEmpty()) {
                    userTempPassword = getNewTempPassword();
                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                    acctUser.getAccountUserLogin().setFailedTempPassword(0);
                    acctUser.getAccountUserLogin().setFailedLogins(0);
                    acctUser.getAccountUserLogin().setTempTrial(true);
                    chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.TEMP);
                    returnStatus = ResponseStatus.USER_CONTACTED;
                } else {
                    returnStatus = ResponseStatus.CUSTOMER_CARE;
                }
                break;
            }
            default: {
                returnStatus = ResponseStatus.CUSTOMER_CARE;
                break;
            }
        }
        if (userTempPassword != null && !userTempPassword.isEmpty()) {
            sendTempPassword(trxInfo, acctUser, userTempPassword, userEvent.getUserLang());
        }
        return returnStatus;
    }

    private ResponseStatus handleEventAtTempState(String trxInfo, AccountUser acctUser, LoginEvent userEvent) throws DBException, Exception {
        String userTempPassword = null;
        ResponseStatus returnStatus = ResponseStatus.SUCCESS;
        switch (userEvent.getLoginEventName()) {
            case USER_CHECK: {
                if (acctUser.getAccountUserLogin().getPassword() != null && !acctUser.getAccountUserLogin().getPassword().isEmpty()) {
                    returnStatus = ResponseStatus.TEMP_PASSWORD_REQUIRED;
                    //returnStatus = ResponseStatus.PASSWORD_REQUIRED;
                } else {
                    userTempPassword = getNewTempPassword();
                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                    acctUser.getAccountUserLogin().setTempTrial(true);
                    returnStatus = ResponseStatus.USER_CONTACTED;
                }
                break;
            }
            case USER_LOGIN: {
                UserLoginEvent userLoginEvent = (UserLoginEvent) userEvent;
                if (userLoginEvent.getPassword() != null && !userLoginEvent.getPassword().isEmpty()) {
                    if (userLoginUtils.validatePassword(acctUser.getAccountUserLogin().getPassword(), userLoginEvent.getPassword(), acctUser.getUsername(), acctUser.getAccountId())) {
                        if (acctUser.getAccountUserLogin().getTempTrial()) {
                            int tempExpirePeriodInMinutes = (int) Configs.TEMP_PASSWORD_EXPIRE_PERIOD.getValue();
                            Calendar now = Calendar.getInstance();
                            Calendar lastPasswordValidTime = Calendar.getInstance();
                            lastPasswordValidTime.setTime(acctUser.getAccountUserLogin().getPasswordCreateDate());
                            lastPasswordValidTime.add(Calendar.MINUTE, tempExpirePeriodInMinutes);

                            if (now.before(lastPasswordValidTime)) {
                                returnStatus = ResponseStatus.CHANGE_PASSWORD;
                                acctUser.getAccountUserLogin().setTempTrial(false);
                                acctUser.getAccountUserLogin().setFailedLogins(0);
                            } else {
                                int maxFialedTempPass = (int) Configs.MAX_FAILED_TEMP_PASSWORD.getValue();
                                acctUser.getAccountUserLogin().setFailedTempPassword(acctUser.getAccountUserLogin().getFailedTempPassword() + 1);
                                if (acctUser.getAccountUserLogin().getFailedTempPassword() < maxFialedTempPass) {
                                    userTempPassword = getNewTempPassword();
                                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                                    acctUser.getAccountUserLogin().setTempTrial(true);
                                    returnStatus = ResponseStatus.USER_CONTACTED;
                                } else {
                                    acctLogger.info(trxInfo + "User blocked due to exceed max failed temp password");
                                    chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.BLOCKED);
                                    returnStatus = ResponseStatus.CUSTOMER_CARE;
                                }
                            }
                        } else {
                            int maxFialedTempPass = (int) Configs.MAX_FAILED_TEMP_PASSWORD.getValue();
                            acctUser.getAccountUserLogin().setFailedTempPassword(acctUser.getAccountUserLogin().getFailedTempPassword() + 1);
                            if (acctUser.getAccountUserLogin().getFailedTempPassword() < maxFialedTempPass) {
                                userTempPassword = getNewTempPassword();
                                acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                                acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                                acctUser.getAccountUserLogin().setTempTrial(true);
                                returnStatus = ResponseStatus.USER_CONTACTED;
                            } else {
                                acctLogger.info(trxInfo + "User blocked due to exceed max failed temp password");
                                chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.BLOCKED);
                                returnStatus = ResponseStatus.CUSTOMER_CARE;
                            }
                        }
                    } else {
                        int maxFialedLogins = (int) Configs.MAX_FAILED_LOGINS.getValue();
                        acctUser.getAccountUserLogin().setFailedLogins(acctUser.getAccountUserLogin().getFailedLogins() + 1);
                        if (acctUser.getAccountUserLogin().getFailedLogins() >= maxFialedLogins) {
                            acctLogger.info(trxInfo + "User blocked due to exceed max failed logins");
                            chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.BLOCKED);
                            returnStatus = ResponseStatus.CUSTOMER_CARE;
                        } else {
                            returnStatus = ResponseStatus.FAILED_LOGIN;
                        }
                    }
                } else {
                    userTempPassword = getNewTempPassword();
                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                    acctUser.getAccountUserLogin().setFailedTempPassword(acctUser.getAccountUserLogin().getFailedTempPassword() + 1);
                    acctUser.getAccountUserLogin().setTempTrial(true);
                    returnStatus = ResponseStatus.USER_CONTACTED;
                }

                break;
            }

            case CHANGE_PASSWORD: {
                ChangePasswordEvent changePasswordEvent = (ChangePasswordEvent) userEvent;
                if (changePasswordEvent.getOldPassword() != null && !changePasswordEvent.getOldPassword().isEmpty()) {
                    if (userLoginUtils.validatePassword(acctUser.getAccountUserLogin().getPassword(), changePasswordEvent.getOldPassword(), acctUser.getUsername(), acctUser.getAccountId())) {
                        if (changePasswordEvent.getNewPassword() != null && !changePasswordEvent.getNewPassword().isEmpty()) {
//                                    if (!changePasswordEvent.getNewPassword().equals(changePasswordEvent.getOldPassword())) {
                            if (checkPasswordAvailability(acctUser, changePasswordEvent.getNewPassword())) {
                                Set<String> avoidedWords = new HashSet<>();
                                avoidedWords.add(acctUser.getUsername());
                                avoidedWords.add(acctUser.getAccount().getCompanyName());
                                if (userLoginUtils.validatePasswordRules(changePasswordEvent.getNewPassword(), avoidedWords)) {
                                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(changePasswordEvent.getNewPassword(), acctUser.getUsername(), acctUser.getAccountId()));
                                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                                    acctUser.getAccountUserLogin().setFailedTempPassword(0);
                                    //----
                                    chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.ACTIVE);
                                    returnStatus = ResponseStatus.SUCCESS;
                                } else {
                                    returnStatus = ResponseStatus.BAD_PASSWORD;
                                }
                            } else {
                                returnStatus = ResponseStatus.SAME_OLD_PASSWORD;
                            }
                        } else {
                            returnStatus = ResponseStatus.BAD_PASSWORD;
                        }
                    } else {
                        int maxFialedLogins = (int) Configs.MAX_FAILED_LOGINS.getValue();
                        acctUser.getAccountUserLogin().setFailedLogins(acctUser.getAccountUserLogin().getFailedLogins() + 1);
                        if (acctUser.getAccountUserLogin().getFailedLogins() >= maxFialedLogins) {
                            acctLogger.info(trxInfo + "User blocked due to exceed max failed logins");
                            chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.BLOCKED);
                            returnStatus = ResponseStatus.CUSTOMER_CARE;
                        } else {
                            returnStatus = ResponseStatus.FAILED_LOGIN;
                        }
                    }
                } else {
                    acctUser.getAccountUserLogin().setFailedLogins(acctUser.getAccountUserLogin().getFailedLogins() + 1);
                    returnStatus = ResponseStatus.FAILED_LOGIN;
                }
                break;
            }

            case FORGET_PASSWORD: {
                int maxFialedTempPass = (int) Configs.MAX_FAILED_TEMP_PASSWORD.getValue();
                acctUser.getAccountUserLogin().setFailedTempPassword(acctUser.getAccountUserLogin().getFailedTempPassword() + 1);
                if (acctUser.getAccountUserLogin().getFailedTempPassword() < maxFialedTempPass) {
                    userTempPassword = getNewTempPassword();
                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                    acctUser.getAccountUserLogin().setTempTrial(true);
                    returnStatus = ResponseStatus.USER_CONTACTED;
                } else {
                    acctLogger.info(trxInfo + "User blocked due to exceed max failed temp password");
                    chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.BLOCKED);
                    returnStatus = ResponseStatus.CUSTOMER_CARE;
                }

                break;
            }

            case GENERATE_PASSWORD: {
                if (acctUser.getPhoneNumber() != null && !acctUser.getPhoneNumber().isEmpty()) {
                    userTempPassword = getNewTempPassword();
                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                    acctUser.getAccountUserLogin().setFailedTempPassword(0);
                    acctUser.getAccountUserLogin().setFailedLogins(0);
                    acctUser.getAccountUserLogin().setTempTrial(true);
                    chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.TEMP);
                    returnStatus = ResponseStatus.USER_CONTACTED;
                } else {
                    returnStatus = ResponseStatus.CUSTOMER_CARE;
                }
                break;
            }
        }
        if (userTempPassword != null && !userTempPassword.isEmpty()) {
            sendTempPassword(trxInfo, acctUser, userTempPassword, userEvent.getUserLang());
        }
        return returnStatus;
    }

    private ResponseStatus handleEventAtActiveState(String trxInfo, AccountUser acctUser, LoginEvent userEvent) throws DBException, Exception {

        String userTempPassword = null;
        ResponseStatus returnStatus = ResponseStatus.SUCCESS;
        switch (userEvent.getLoginEventName()) {
            case USER_CHECK: {
                if (acctUser.getAccountUserLogin().getPassword() != null && !acctUser.getAccountUserLogin().getPassword().isEmpty()) {
                    returnStatus = ResponseStatus.PASSWORD_REQUIRED;
                } else {
                    userTempPassword = getNewTempPassword();
                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                    chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.TEMP);
                    returnStatus = ResponseStatus.USER_CONTACTED;
                }
                break;
            }
            case USER_LOGIN: {
                UserLoginEvent userLoginEvent = (UserLoginEvent) userEvent;

                if (userLoginEvent.getPassword() != null && !userLoginEvent.getPassword().isEmpty()) {
                    if (userLoginUtils.validatePassword(acctUser.getAccountUserLogin().getPassword(), userLoginEvent.getPassword(), acctUser.getUsername(), acctUser.getAccountId())) {
                        int tempExpirePeriodInMinutes = (int) Configs.PASSWORD_EXPIRE_PERIOD.getValue();
                        Calendar now = Calendar.getInstance();
                        Calendar lastPasswordValidTime = Calendar.getInstance();
                        lastPasswordValidTime.setTime(acctUser.getAccountUserLogin().getPasswordCreateDate());
                        lastPasswordValidTime.add(Calendar.DAY_OF_MONTH, tempExpirePeriodInMinutes);

                        if (now.before(lastPasswordValidTime)) {
                            acctUser.getAccountUserLogin().setFailedLogins(0);
                            returnStatus = ResponseStatus.SUCCESS;
                        } else {
                            returnStatus = ResponseStatus.CHANGE_PASSWORD_OLD_REQUIRED;
                        }
                    } else {
                        int maxFialedLogins = (int) Configs.MAX_FAILED_LOGINS.getValue();
                        acctUser.getAccountUserLogin().setFailedLogins(acctUser.getAccountUserLogin().getFailedLogins() + 1);
                        if (acctUser.getAccountUserLogin().getFailedLogins() >= maxFialedLogins) {
                            acctLogger.info(trxInfo + "User blocked due to exceed max failed logins");
                            chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.BLOCKED);
                            returnStatus = ResponseStatus.CUSTOMER_CARE;
                        } else {
                            returnStatus = ResponseStatus.FAILED_LOGIN;
                        }
                    }
                } else {
                    acctUser.getAccountUserLogin().setFailedLogins(acctUser.getAccountUserLogin().getFailedLogins() + 1);
                    returnStatus = ResponseStatus.FAILED_LOGIN;
                }
                break;
            }
            case CHANGE_PASSWORD: { // no check old password required
                ChangePasswordEvent changePasswordEvent = (ChangePasswordEvent) userEvent;

                if (changePasswordEvent.getOldPassword() != null && !changePasswordEvent.getOldPassword().isEmpty()) {
                    if (userLoginUtils.validatePassword(acctUser.getAccountUserLogin().getPassword(), changePasswordEvent.getOldPassword(), acctUser.getUsername(), acctUser.getAccountId())) {
                        if (changePasswordEvent.getNewPassword() != null && !changePasswordEvent.getNewPassword().isEmpty()) {
                            //if (!changePasswordEvent.getNewPassword().equals(changePasswordEvent.getOldPassword())) {
                            if (checkPasswordAvailability(acctUser, changePasswordEvent.getNewPassword())) {
                                Set<String> avoidedWords = new HashSet<>();
                                avoidedWords.add(acctUser.getUsername());
                                avoidedWords.add(acctUser.getAccount().getCompanyName());
                                if (userLoginUtils.validatePasswordRules(changePasswordEvent.getNewPassword(), avoidedWords)) {
                                    saveOldPassword(acctUser.getAccountUserLogin());
                                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(changePasswordEvent.getNewPassword(), acctUser.getUsername(), acctUser.getAccountId()));
                                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                                    acctUser.getAccountUserLogin().setFailedLogins(0);
                                    returnStatus = ResponseStatus.SUCCESS;
                                } else {
                                    returnStatus = ResponseStatus.BAD_PASSWORD;
                                }
                            } else {
                                returnStatus = ResponseStatus.SAME_OLD_PASSWORD;
                            }
                        } else {
                            returnStatus = ResponseStatus.BAD_PASSWORD;
                        }
                    } else {
                        int maxFialedLogins = (int) Configs.MAX_FAILED_LOGINS.getValue();
                        acctUser.getAccountUserLogin().setFailedLogins(acctUser.getAccountUserLogin().getFailedLogins() + 1);
                        if (acctUser.getAccountUserLogin().getFailedLogins() >= maxFialedLogins) {
                            acctLogger.info(trxInfo + "User blocked due to exceed max failed logins");
                            chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.BLOCKED);
                            returnStatus = ResponseStatus.CUSTOMER_CARE;
                        } else {
                            returnStatus = ResponseStatus.FAILED_LOGIN;
                        }
                    }

                } else {
                    acctUser.getAccountUserLogin().setFailedLogins(acctUser.getAccountUserLogin().getFailedLogins() + 1);
                    returnStatus = ResponseStatus.FAILED_LOGIN;
                }
                break;
            }
            case FORGET_PASSWORD: {
                userTempPassword = getNewTempPassword();
                saveOldPassword(acctUser.getAccountUserLogin());
                acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                acctUser.getAccountUserLogin().setFailedTempPassword(acctUser.getAccountUserLogin().getFailedTempPassword() + 1);
                acctUser.getAccountUserLogin().setTempTrial(true);
                chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.TEMP);
                returnStatus = ResponseStatus.USER_CONTACTED;
                break;
            }
            case GENERATE_PASSWORD: {
                if (acctUser.getPhoneNumber() != null && !acctUser.getPhoneNumber().isEmpty()) {
                    userTempPassword = getNewTempPassword();
                    saveOldPassword(acctUser.getAccountUserLogin());
                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                    acctUser.getAccountUserLogin().setFailedTempPassword(0);
                    acctUser.getAccountUserLogin().setFailedLogins(0);
                    acctUser.getAccountUserLogin().setTempTrial(true);
                    chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.TEMP);
                    returnStatus = ResponseStatus.USER_CONTACTED;
                } else {
                    returnStatus = ResponseStatus.CUSTOMER_CARE;
                }
                break;
            }
        }
        if (userTempPassword != null && !userTempPassword.isEmpty()) {
            sendTempPassword(trxInfo, acctUser, userTempPassword, userEvent.getUserLang());
        }
        return returnStatus;
    }

    private ResponseStatus handleEventAtBlockState(String trxInfo, AccountUser acctUser, LoginEvent userEvent) throws DBException, Exception {

        String userTempPassword = null;
        ResponseStatus returnStatus = ResponseStatus.SUCCESS;
        switch (userEvent.getLoginEventName()) {

            case USER_UNLOCK: {
                saveOldPassword(acctUser.getAccountUserLogin());
                acctUser.getAccountUserLogin().setPassword(null);
                acctUser.getAccountUserLogin().setPasswordCreateDate(null);
                acctUser.getAccountUserLogin().setFailedTempPassword(0);
                acctUser.getAccountUserLogin().setFailedLogins(0);
                chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.INITIAL);
                returnStatus = ResponseStatus.SUCCESS;
                break;
            }

            case GENERATE_PASSWORD: {
                if (acctUser.getPhoneNumber() != null && !acctUser.getPhoneNumber().isEmpty()) {
                    userTempPassword = getNewTempPassword();
                    saveOldPassword(acctUser.getAccountUserLogin());
                    acctUser.getAccountUserLogin().setPassword(userLoginUtils.hashPassword(userTempPassword, acctUser.getUsername(), acctUser.getAccountId()));
                    acctUser.getAccountUserLogin().setPasswordCreateDate(Calendar.getInstance().getTime());
                    acctUser.getAccountUserLogin().setFailedTempPassword(0);
                    acctUser.getAccountUserLogin().setFailedLogins(0);
                    acctUser.getAccountUserLogin().setTempTrial(true);
                    chenageUserLoginStatus(trxInfo, acctUser, UserLoginStatusName.TEMP);
                    returnStatus = ResponseStatus.USER_CONTACTED;
                } else {
                    returnStatus = ResponseStatus.CUSTOMER_CARE;
                }
                break;
            }

            default: {
                returnStatus = ResponseStatus.CUSTOMER_CARE;
                break;
            }
        }
        if (userTempPassword != null && !userTempPassword.isEmpty()) {
            sendTempPassword(trxInfo, acctUser, userTempPassword, userEvent.getUserLang());
        }
        return returnStatus;
    }

    private void saveOldPassword(AccountUserLogin userLogin) {

        if (userLogin.getPassword() != null) {
            LoginOldPassword oldPassword = new LoginOldPassword(
                    userLogin.getPassword(),
                    userLogin.getPasswordCreateDate(),
                    Calendar.getInstance().getTime(),
                    userLogin);

            if (userLogin.getLoginOldPasswordList() != null) {
                int keepOldPassCount = (int) Configs.KEEP_OLD_PASSWORD_COUNT.getValue();
                if (userLogin.getLoginOldPasswordList().size() >= keepOldPassCount) {

                    Collections.sort(userLogin.getLoginOldPasswordList(), new Comparator<LoginOldPassword>() {
                        @Override
                        public int compare(LoginOldPassword o1, LoginOldPassword o2) {
                            if (o1.getCreationDate() == o2.getCreationDate()) {
                                return 0;
                            }
                            return o1.getCreationDate().before(o2.getCreationDate()) ? -1 : 1;
                        }
                    });

                    int numOfRemovableOldPass = userLogin.getLoginOldPasswordList().size() - keepOldPassCount + 1;
                    for (int x = 0; x < numOfRemovableOldPass; x++) {
                        userLogin.getLoginOldPasswordList().remove(0);
                    }

//                    LoginOldPassword oldestPass = userLogin.getLoginOldPasswordList().get(0);
//                    for (LoginOldPassword oldPass : userLogin.getLoginOldPasswordList()) {
//                        if (oldPass.getCreationDate().before(oldestPass.getCreationDate())) {
//                            oldestPass = oldPass;
//                        }
//                    }
//                    for (ListIterator<LoginOldPassword> it = userLogin.getLoginOldPasswordList().listIterator(); it.hasNext();) {
//                        LoginOldPassword storedOldPassword = it.next();
//                        if (storedOldPassword.getId().equals(oldestPass.getId())) {
//                            it.remove();
//                            acctLogger.info("Old password removed with id (" + storedOldPassword.getId() + ")");
//                            break;
//                        }
//                    }
//                    userLogin.getLoginOldPasswordList().remove(oldestPass);
                }
                userLogin.getLoginOldPasswordList().add(oldPassword);
            } else {
                List<LoginOldPassword> oldPasswords = new ArrayList<>();
                oldPasswords.add(oldPassword);
                userLogin.setLoginOldPasswordList(oldPasswords);
            }
        }
    }

    private boolean checkPasswordAvailability(AccountUser acctUser, String newPassword) {
        boolean returnStatus = true;
        String newPasswordHash = userLoginUtils.hashPassword(newPassword, acctUser.getUsername(), acctUser.getAccountId());
        if (!acctUser.getAccountUserLogin().getPassword().equals(newPasswordHash)) {
            if (acctUser.getAccountUserLogin().getLoginOldPasswordList() != null && !acctUser.getAccountUserLogin().getLoginOldPasswordList().isEmpty()) {
                for (LoginOldPassword oldPassword : acctUser.getAccountUserLogin().getLoginOldPasswordList()) {
                    if (oldPassword.getPassword().equals(newPasswordHash)) {
                        returnStatus = false;
                        break;
                    }
                }
            }
        } else {
            returnStatus = false;
        }
        return returnStatus;
    }

    public void sendTempPassword(String trxlogId, AccountUser acctUser, String tempPassword, String userLang) throws SendTempSmsFailException, DBException, Exception {
        String providerUrl = (String) Configs.CORE_PROVIDER_URL.getValue();
        String jndi = (String) Configs.SUBMIT_SMS_BEAN_JNDI.getValue();

        SubmitSMSBeanRemote submitSMSBeanRemote = null;
        try {
            acctLogger.debug(trxlogId + "Lookup SMS provider at providerUrl(" + providerUrl + ") with JNDI(" + jndi + ")");

            InitialContext ic = new InitialContext();
            ic.addToEnvironment(javax.naming.Context.PROVIDER_URL, providerUrl);
            submitSMSBeanRemote = (SubmitSMSBeanRemote) ic.lookup(jndi);
        } catch (NamingException ex) {
            throw new SendTempSmsFailException("Failed to lookup the SMS provider jndi", ex);
        }

        String smsTextBody;
        if (userLang != null) {
            if (userLang.toLowerCase().equals("en")) {
                smsTextBody = (String) Configs.TEMP_PASS_SMS_BODY_EN.getValue();
            } else {
                smsTextBody = (String) Configs.TEMP_PASS_SMS_BODY_AR.getValue();
            }
        } else {
            if (((String) Configs.TEMP_PASS_SMS_BODY_DEFAULT_LANG.getValue()).equals("en")) {
                smsTextBody = (String) Configs.TEMP_PASS_SMS_BODY_EN.getValue();
            } else {
                smsTextBody = (String) Configs.TEMP_PASS_SMS_BODY_AR.getValue();
            }
        }

        String senderName = (String) Configs.TEMP_PASS_SMS_SENDER.getValue();
        String tempSmsIdPrefix = (String) Configs.TEMP_PASS_SMS_ID_PRIFIX.getValue();
        String tmpSmsAccountId = (String) Configs.TEMP_PASS_SMS_ACC_ID.getValue();

        if (senderName.length() > 11) {
            senderName = senderName.substring(0, 11);
            acctLogger.warn(trxlogId + "Temp password sender name (" + ((String) Configs.TEMP_PASS_SMS_SENDER.getValue())
                    + ") with invalid length of (" + ((String) Configs.TEMP_PASS_SMS_SENDER.getValue()).length() + ") chars, "
                    + "is converted to sender name(" + senderName + ")");
        }

        String smsText = smsTextBody
                .replace("XuserName", acctUser.getUsername())
                .replace("Xcompany", acctUser.getAccount().getCompanyName())
                .replace("Xpassword", tempPassword);

        SMSDetails SMSDeatail = new SMSDetails();
        SMSDeatail.setSMSText(smsText);
        SMSDeatail.setLanguage();
        SMSDeatail.setCachedSMS(false);
        SMSDeatail.setDeliveryReport(true);
        SMSDeatail.setSenderName(senderName);
        SMSDeatail.setReceiverMSISDN(acctUser.getPhoneNumber());
        SMSDeatail.setSMSIdPrefix(tempSmsIdPrefix);
        SMSDeatail.setAccountId(tmpSmsAccountId);

//        acctLogger.info(trxlogId + "Retriving admin account with ID(" + tmpSmsAccountId + ")");
//        Account account = null;
        try {
//            account = accountDao.findByAccountId(tmpSmsAccountId);
//
//            Set<String> rateLimitersIds = new HashSet<>();
//            if (account.getSendingRateLimiters() != null && !account.getSendingRateLimiters().isEmpty()) {
//                if (acctLogger.isTraceEnabled()) {
//                    acctLogger.trace(trxlogId + "Sending rate limiters : " + account.getSendingRateLimiters());
//                }
//                for (SendingRateLimiter sendingRateLimiter : account.getSendingRateLimiters()) {
//                    if (sendingRateLimiter.isSmsapiEnabled()) {
//                        rateLimitersIds.add(sendingRateLimiter.getLimiterId());
//                    }
//                }
//            }
            Set<String> rateLimitersIds = new HashSet<>();
            SMSDeatail.setRateLimitersIds(rateLimitersIds);
            String logId = acctUser.getAccountId() + trxlogId;
            acctLogger.debug(trxlogId + "Submit temp password sms to user (" + acctUser.getUsername() + ") with MSISDN(" + acctUser.getPhoneNumber() + ")");
            SMSResponseStatus smsStatus = submitSMSBeanRemote.submitSMS(logId, SMSDeatail);
            if (smsStatus == SMSResponseStatus.SUBMITTED) {
                acctLogger.info(trxlogId + "Submit temp password sms to user (" + acctUser.getUsername() + ") with MSISDN(" + acctUser.getPhoneNumber() + ") success");
            } else {
                acctLogger.error("Failed to send the SMS with status: " + smsStatus);
                throw new SendTempSmsFailException("Failed to send the SMS with status: " + smsStatus);
            }
        } catch (DBException ex) {
            throw new SendTempSmsFailException("Failed to retrieve the admin account", ex);
        } catch (Exception ex) {
            throw new SendTempSmsFailException("Failed to send the SMS", ex);
        }
    }

    public void chenageUserLoginStatus(String trxlogId, AccountUser acctUser, UserLoginStatusName newStatus) {
        acctLogger.info(trxlogId + "Switch user(" + acctUser.getUsername() + ") login status from(" + acctUser.getAccountUserLogin().getUserLoginStatus().getName().getStatusName() + ") to(" + newStatus.getStatusName() + ")");
        acctUser.getAccountUserLogin().setUserLoginStatus(userLoginStatusDao.getCachedObjectByName(newStatus));
    }

    private String getNewTempPassword() {
        Set<String> tempPassPatterns = new HashSet<>((List<String>) Configs.TEMP_PASS_GENERATION_PATTERNS.getValue());
        int tempPassLength = (int) Configs.TEMP_PASS_LENGTH.getValue();
        return userLoginUtils.generateTempPassword(tempPassLength, tempPassPatterns);
    }

}
