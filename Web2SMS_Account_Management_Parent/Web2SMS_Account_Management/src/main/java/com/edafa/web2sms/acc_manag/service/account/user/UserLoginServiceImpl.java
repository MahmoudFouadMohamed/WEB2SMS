package com.edafa.web2sms.acc_manag.service.account.user;

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
import com.edafa.web2sms.acc_manag.service.account.group.user.interfaces.GroupUserManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.model.AccountResultFullInfo;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.SendTempSmsFailException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserLoginService;
import com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserLoginServiceBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.model.UserResult;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import javax.jws.HandlerChain;

/**
 *
 * @author mahmoud
 */
@Stateless
@WebService(serviceName = "UserLoginService", targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/account/user", endpointInterface = "com.edafa.web2sms.acc_manag.service.account.user.interfaces.UserLoginService")
public class UserLoginServiceImpl implements UserLoginService {

    Logger appLogger;
    Logger acctLogger;

    @EJB
    UserLoginServiceBeanLocal userLoginServiceBean;

    @EJB
    GroupUserManagementBeanLocal groupUserManagementBean;

    @EJB
    AccManagAppErrorManagerAdapter appErrorManagerAdapter;

    @PostConstruct
    public void initLoggers() {
        appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
        acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
    }

    private void reportAppError(AppErrors error, String msg) {
        appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.ACCOUNT_MANAGEMENT);
    }

    @Override
    public ResultStatus checkUserForLogin(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) {
        acctLogger.info(userTrxInfo.logInfo() + "Check User For Login with userName(" + userTrxInfo.getUser().getUsername() + ") and companyName(" + companyName + ")");
        ResultStatus result = new UserResult(ResponseStatus.SUCCESS);
        try {
            result = userLoginServiceBean.checkUserForLogin(userTrxInfo, companyName, userLang);

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

    @Override
    public AccountResultFullInfo userLogin(AccountUserTrxInfo userTrxInfo, String companyName, String password, String userLang) {
        acctLogger.info(userTrxInfo.logInfo() + "Login user with userName(" + userTrxInfo.getUser().getUsername() + ") and companyName(" + companyName + ")");
        AccountResultFullInfo result = new AccountResultFullInfo(ResponseStatus.SUCCESS);
        try {
            result = userLoginServiceBean.userLogin(userTrxInfo, companyName, password, userLang);
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

    @Override
    public AccountResultFullInfo directUserLogin(AccountUserTrxInfo userTrxInfo, String companyName) {
        acctLogger.info(userTrxInfo.logInfo() + "Direct Login user with userName(" + userTrxInfo.getUser().getUsername() + ") and companyName(" + companyName + ")");
        AccountResultFullInfo result = new AccountResultFullInfo(ResponseStatus.SUCCESS);
        try {
            result = userLoginServiceBean.directUserLogin(userTrxInfo, companyName);
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

    @Override
    public ResultStatus userForgetPassword(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) {
        acctLogger.info(userTrxInfo.logInfo() + "User forget password with userName(" + userTrxInfo.getUser().getUsername() + ") and companyName(" + companyName + ")");
        ResultStatus result = new UserResult(ResponseStatus.SUCCESS);
        try {
            result = userLoginServiceBean.userForgetPassword(userTrxInfo, companyName, userLang);
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

    @Override
    public ResultStatus resendTempPassword(AccountUserTrxInfo userTrxInfo, String companyName, String userLang) {
        acctLogger.info(userTrxInfo.logInfo() + "Resend user password with userName(" + userTrxInfo.getUser().getUsername() + ") and companyName(" + companyName + ")");
        ResultStatus result = new UserResult(ResponseStatus.SUCCESS);
        try {
            result = userLoginServiceBean.resendTempPassword(userTrxInfo, companyName, userLang);
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
