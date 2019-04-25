package com.edafa.web2sms.acc_manag.service.account.user;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.alarm.AccManagAppErrorManagerAdapter;
import com.edafa.web2sms.acc_manag.alarm.AppErrors;
import com.edafa.web2sms.acc_manag.alarm.ErrorsSource;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidMSISDNException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidRequestException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotEditableException;
import com.edafa.web2sms.acc_manag.service.account.group.user.interfaces.GroupUserManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.IneligibleUserException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.SendTempSmsFailException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserLoginServiceBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserManagementService;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserManagementServiceBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.model.AccountUsersResultSet;
import com.edafa.web2sms.acc_manag.service.account.user.model.UserResult;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.CountResult;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.dalayer.exception.DBException;

/**
 *
 * @author mahmoud
 */
@Stateless
//@LocalBean
@WebService(name = "UserManegementService", serviceName = "UserManegementService", targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/account/user", endpointInterface = "com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserManagementService")
//@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")

public class UserManagementServiceImpl implements UserManagementService {

    Logger appLogger;
    Logger acctLogger;

    @EJB
    UserManagementServiceBeanLocal userManagementServiceBean;

    @EJB
    GroupUserManagementBeanLocal groupUserManagementBean;

    @EJB
    AccManagAppErrorManagerAdapter appErrorManagerAdapter;

    @EJB
    UserLoginServiceBeanLocal userLoginServiceBean;

    @PostConstruct
    public void initLoggers() {
        appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
        acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
    }

    @Override
    public UserResult findUserByCoName(AccountUserTrxInfo userTrxInfo, String companyName) {
        acctLogger.info(userTrxInfo.logInfo() + "Inquire user by company name: " + companyName);
        UserResult userResult = new UserResult(ResponseStatus.SUCCESS);

        try {
            userResult = userManagementServiceBean.findUserByCoName(userTrxInfo, companyName);
        } catch (DBException e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId() + e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            userResult.setStatus(ResponseStatus.FAIL);
        } catch (AccountNotFoundException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
            userResult.setStatus(ResponseStatus.ACCT_NOT_EXIST);
            userResult.setErrorMessage(e.getMessage());
        } catch (UserNotFoundException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_USER, "AccountUser NotFound");
            userResult.setStatus(ResponseStatus.USER_NOT_EXIST);
            userResult.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            userResult.setStatus(ResponseStatus.FAIL);
        }
        return userResult;
    }

    @Override
    public ResultStatus setUserAsGroupAdmin(AccountUserTrxInfo userTrxInfo, String userId, String groupId) {
        acctLogger.info(userTrxInfo.logInfo() + "Set user with id(" + userId + ") as group admin of group with id(" + groupId + ")");
        ResultStatus resultStatus = new ResultStatus(ResponseStatus.SUCCESS);
        try {
            groupUserManagementBean.setUserAsGroupAdmin(userTrxInfo, userId, groupId);
        } catch (DBException e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId() + e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            resultStatus.setStatus(ResponseStatus.FAIL);
        } catch (IneligibleAccountException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
            resultStatus.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
            resultStatus.setErrorMessage(e.getMessage());
        } catch (UserNotFoundException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_USER, "AccountUser NotFound");
            resultStatus.setStatus(ResponseStatus.USER_NOT_EXIST);
            resultStatus.setErrorMessage(e.getMessage());
        } catch (GroupNotFoundException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INVALID_OPERATION, "Group NotFound");
            resultStatus.setStatus(ResponseStatus.GROUP_NOT_EXIST);
            resultStatus.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            resultStatus.setStatus(ResponseStatus.FAIL);
        }
        return resultStatus;
    }

    private void reportAppError(AppErrors error, String msg) {
        appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.ACCOUNT_MANAGEMENT);
    }

    @Override
    public CountResult countAccountUsers(AccountUserTrxInfo userTrxInfo, String userName) {
        acctLogger.info(userTrxInfo.logInfo() + "Count users with search user name(" + (userName == null ? "NULL" : userName) + ")");

        CountResult countResult = new CountResult(ResponseStatus.SUCCESS);
        try {
            int usersCount = userManagementServiceBean.countAccountUsers(userTrxInfo, userName);
            countResult.setCount(usersCount);
        } catch (DBException e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId() + e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            countResult.setStatus(ResponseStatus.FAIL);
        } catch (IneligibleAccountException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
            countResult.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
            countResult.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            countResult.setStatus(ResponseStatus.FAIL);
        }
        return countResult;
    }

    @Override
    public AccountUsersResultSet getAccountUsers(AccountUserTrxInfo userTrxInfo, String search, int first, int max) {
        acctLogger.info(userTrxInfo.logInfo() + "Get users with search user name(" + (search == null ? "NULL" : search) + ") and first(" + first + ") and max(" + max + ")");
        AccountUsersResultSet result = new AccountUsersResultSet(ResponseStatus.SUCCESS);
        try {
            List<UserDetailsModel> users = userManagementServiceBean.getAccountUsers(userTrxInfo, search, first, max);
            result.setUserModels(users);
        } catch (DBException e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId() + e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            result.setStatus(ResponseStatus.FAIL);
        } catch (IneligibleAccountException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
            result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
            result.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            result.setStatus(ResponseStatus.FAIL);
        }

        return result;
    }

    @Override
    public ResultStatus updateUserData(AccountUserTrxInfo userTrxInfo, UserDetailsModel user) {
        acctLogger.info(userTrxInfo.logInfo() + "Update info for user(" + user + ") ");
        ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);

        try {
            userManagementServiceBean.updateUserData(userTrxInfo, user);

        } catch (DBException e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId() + e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            result.setStatus(ResponseStatus.FAIL);
        } catch (InvalidRequestException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INVALID_REQUEST, "Invalid request");
            result.setStatus(ResponseStatus.INVALID_REQUEST);
            result.setErrorMessage(e.getMessage());
        } catch (InvalidMSISDNException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INVALID_REQUEST, "Invalid MSISDN");
            result.setStatus(ResponseStatus.INVALID_MSISDN);
            result.setErrorMessage(e.getMessage());
        } catch (IneligibleAccountException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
            result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
            result.setErrorMessage(e.getMessage());
        } catch (IneligibleUserException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_USER, "Ineligible user");
            result.setStatus(ResponseStatus.INELIGIBLE_USER);
            result.setErrorMessage(e.getMessage());
        } catch (NotEditableException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
            result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
            result.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            result.setStatus(ResponseStatus.FAIL);
        }

        return result;
    }

    @Override
    public CountResult countDefaultGroupUsers(AccountUserTrxInfo userTrxInfo, String userName) {

        CountResult countResult = new CountResult(ResponseStatus.SUCCESS);
        try {
            int usersCount = userManagementServiceBean.countDefaultGroupUsers(userTrxInfo, userName);
            countResult.setCount(usersCount);
        } catch (DBException e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId() + e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            countResult.setStatus(ResponseStatus.FAIL);
        } catch (IneligibleAccountException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
            countResult.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
            countResult.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            countResult.setStatus(ResponseStatus.FAIL);
        }

        return countResult;
    }

    @Override
    public AccountUsersResultSet getDefaultGroupUsers(AccountUserTrxInfo userTrxInfo, String search, int first,
            int max) {
        acctLogger.info(userTrxInfo.logInfo() + "Get default group users with search user name(" + (search == null ? "NULL" : search) + ") and first(" + first + ") and max(" + max + ")");
        AccountUsersResultSet result = new AccountUsersResultSet(ResponseStatus.SUCCESS);

        try {
            List<UserDetailsModel> users = userManagementServiceBean.getDefaultGroupUsers(userTrxInfo, search, first, max);
            result.setUserModels(users);
        } catch (DBException e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId() + e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            result.setStatus(ResponseStatus.FAIL);
        } catch (IneligibleAccountException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
            result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
            result.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            result.setStatus(ResponseStatus.FAIL);
        }

        return result;
    }

    @Override
    public ResultStatus changeUserPassword(AccountUserTrxInfo userTrxInfo, String oldPassword, String newPassword) {
        acctLogger.info(userTrxInfo.logInfo() + "Change user password with userName(" + userTrxInfo.getUser().getUsername() + ")");
        ResultStatus result = new UserResult(ResponseStatus.SUCCESS);
        String secureToken = null;
        try {
            result = userLoginServiceBean.changeUserPassword(userTrxInfo, oldPassword, newPassword, secureToken);
        } catch (SendTempSmsFailException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.GENERAL_ERROR, "Send temp sms failed");
            result.setStatus(ResponseStatus.FAIL);
            result.setErrorMessage(e.getMessage());
        } catch (AccountNotFoundException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
            result.setStatus(ResponseStatus.FAILED_LOGIN);
            result.setErrorMessage(e.getMessage());
        } catch (UserNotFoundException e) {
            acctLogger.warn(userTrxInfo.logId() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_USER, "AccountUser NotFound");
            result.setStatus(ResponseStatus.FAILED_LOGIN);
            result.setErrorMessage(e.getMessage());
        } catch (DBException e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId() + e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            result.setStatus(ResponseStatus.FAIL);
        } catch (Exception e) {
            acctLogger.error(userTrxInfo.logId() + e.getMessage());
            appLogger.error(userTrxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            result.setStatus(ResponseStatus.FAIL);
        }
        return result;
    }
}
