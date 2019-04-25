package com.edafa.web2sms.acc_manag.service.account;

import com.edafa.web2sms.acc_manag.alarm.AccManagAppErrorManagerAdapter;
import com.edafa.web2sms.acc_manag.alarm.AppErrors;
import com.edafa.web2sms.acc_manag.alarm.ErrorsSource;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.interfaces.AccountManegementServiceBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.interfaces.AccountManegementService;
import com.edafa.web2sms.acc_manag.service.account.model.AccountResult;
import com.edafa.web2sms.acc_manag.service.account.model.AccountResultFullInfo;
import com.edafa.web2sms.acc_manag.service.account.model.PrivilegesResult;
import com.edafa.web2sms.acc_manag.service.account.model.QuotaHistoryResult;
import com.edafa.web2sms.acc_manag.service.account.model.QuotaInquiryResult;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.QuotaHistoryModel;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.PrivilegeModel;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidAddressFormattingException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidMSISDNFormatException;
import java.util.List;
import javax.annotation.PostConstruct;

/**
 * Session Bean implementation class AccountManagementBean
 */
@Stateless
@LocalBean
@WebService(name = "AccountManegementService", serviceName = "AccountManegementService", targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/account", endpointInterface = "com.edafa.web2sms.acc_manag.service.account.interfaces.AccountManegementService")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
public class AccountManegementServiceImpl implements AccountManegementService {

	Logger appLogger;
	Logger acctLogger;

	@EJB
	AccountManegementServiceBeanLocal accountBean;
        
        @EJB
	AccManagAppErrorManagerAdapter appErrorManagerAdapter;

	public AccountManegementServiceImpl() {
	}

	// @Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	// public AccountResult findAccount(String acctAdmin) {
	// AccountResult result = new AccountResult(ResponseStatus.SUCCESS);
	// result.setStatus(ResponseStatus.SUCCESS);
	//
	// AccountModel acct;
	// try {
	// acct = accountBean.findAccount(acctAdmin);
	// result.setAccount(acct);
	// } catch (DBException e) {
	// result.setStatus(ResponseStatus.FAIL);
	// } catch (AccountNotFoundException e) {
	// result.setStatus(ResponseStatus.ACCT_NOT_EXIST);
	// } catch (Exception e) {
	// result.setStatus(ResponseStatus.FAIL);
	// }
	// return result;
	// }

    @PostConstruct
    public void initLoggers() {
        appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
        acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
    }
        
//	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	public AccountResult findAccountByCoName(AccountUserTrxInfo userTrxInfo, String companyName) {
//		AccountResult result = new AccountResult(ResponseStatus.SUCCESS);
//		acctLogger.info(userTrxInfo.logInfo() + "Inquire account by company name: " + companyName);
//		result.setStatus(ResponseStatus.SUCCESS);
//
//		AccountModel acct;
//		try {
//			acct = accountBean.findAccountByCoName(userTrxInfo, companyName);
//			result.setAccount(acct);
//		} catch (DBException e) {
//			acctLogger.error(userTrxInfo.logId() + e.getMessage());
//                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
//                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
//                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
//                    } else {
//                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
//                    }
//			result.setStatus(ResponseStatus.FAIL);
//		} catch (AccountNotFoundException e) {
//			acctLogger.error(userTrxInfo.logId() + e.getMessage());
//                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
//			result.setStatus(ResponseStatus.ACCT_NOT_EXIST);
//			result.setErrorMessage(e.getMessage());
//		} catch (Exception e) {
//                        acctLogger.error(userTrxInfo.logId() + e.getMessage());
//                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
//			result.setStatus(ResponseStatus.FAIL);
//		}
//		return result;
//	}
//
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AccountResultFullInfo findAccountByCoNameFullInfo(AccountUserTrxInfo userTrxInfo, String companyName) {
		acctLogger.info(userTrxInfo.logInfo() + "Inquire account full info by company name: " + companyName);
		AccountResultFullInfo result = new AccountResultFullInfo(ResponseStatus.SUCCESS);
		result.setStatus(ResponseStatus.SUCCESS);

		AccountModelFullInfo acct;
		try {
			acct = accountBean.findAccountByCoNameFullInfo(userTrxInfo, companyName);
			result.setAccount(acct);
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
		} catch (AccountNotFoundException e) {
			acctLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			result.setStatus(ResponseStatus.ACCT_NOT_EXIST);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			acctLogger.error(userTrxInfo.logId() + e.getMessage());
                        appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}
		return result;
	}

	public QuotaInquiryResult getQuotaInfoByMSISDN(AccountUserTrxInfo trxInfo, String billingMSISDN) {
		QuotaInquiryResult quotaResult = new QuotaInquiryResult(ResponseStatus.SUCCESS);
		QuotaInfo qi;
		try {
			qi = accountBean.getQuotaInfoByMSISDN(trxInfo, billingMSISDN);
			quotaResult.setQuotaInfo(qi);
		} catch (DBException e) {
			acctLogger.error(trxInfo.logId() + "DBException " + e.getMessage());
			appLogger.debug(trxInfo.logId(), e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			quotaResult.setStatus(ResponseStatus.FAIL);
		} catch (AccountNotFoundException e) {
			acctLogger.error(trxInfo.logId() + "AccountNotFoundException " + e.getMessage());
			appLogger.error(trxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			quotaResult.setStatus(ResponseStatus.ACCT_NOT_EXIST);
		} catch (InvalidCustomerForQuotaInquiry e) {
			acctLogger.error(trxInfo.logId() + "InvalidCustomerForQuotaInquiry " + e.getMessage());
			appLogger.error(trxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Invalid Customer For Quota Inquiry");
			quotaResult.setStatus(ResponseStatus.FAIL);
		} catch (QuotaInquiryFailed e) {
			acctLogger.error(trxInfo.logId() + "QuotaInquiryFailed " + e.getMessage());
			appLogger.error(trxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Quota Inquiry Failed");
			quotaResult.setStatus(ResponseStatus.FAIL);
		} catch (AccountManagInvalidMSISDNFormatException e) {
			acctLogger.error(trxInfo.logId() + "InvalidMSISDNFormatException " + e.getMessage());
			appLogger.error(trxInfo.logId(), e);
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid MSISDN Format");
			quotaResult.setStatus(ResponseStatus.INVALID_REQUEST);
		} catch (AccountManagInvalidAddressFormattingException e) {
			acctLogger.error(trxInfo.logId() + "InvalidAddressFormattingException " + e.getMessage());
			appLogger.error(trxInfo.logId(), e);
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Address Formatting");
			quotaResult.setStatus(ResponseStatus.INVALID_REQUEST);
		} catch (IneligibleAccountException e) {
                        acctLogger.warn(trxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
                        quotaResult.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
                        quotaResult.setErrorMessage(e.getMessage());
                } catch (Exception e) {
			acctLogger.error(trxInfo.logId() + "Exception " + e.getMessage());
			appLogger.error(trxInfo.logId(), e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			quotaResult.setStatus(ResponseStatus.FAIL);
		}
		return quotaResult;
	}

	@Override
	public QuotaHistoryResult getQuotaHistory(AccountUserTrxInfo trxInfo, String accountId) {
		QuotaHistoryResult quotaResult = new QuotaHistoryResult(ResponseStatus.SUCCESS);
		try {
			QuotaHistoryModel quotaHistory = accountBean.getQuotaHistory(trxInfo, accountId);
			quotaResult.setQuotaHistoryModel(quotaHistory);
		} catch (DBException e) {
			acctLogger.error(trxInfo.logId() + "DBException " + e.getMessage());
			appLogger.error(trxInfo.logId(), e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			quotaResult.setStatus(ResponseStatus.FAIL);
		} catch (AccountNotFoundException e) {
			acctLogger.error(trxInfo.logId() + "AccountNotFoundException " + e.getMessage());
			appLogger.error(trxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			quotaResult.setStatus(ResponseStatus.ACCT_NOT_EXIST);
		} catch (IneligibleAccountException e) {
                    acctLogger.warn(trxInfo.logId() + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible account");
                    quotaResult.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
                    quotaResult.setErrorMessage(e.getMessage());
                }
		return quotaResult;
	}
        
        	private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.ACCOUNT_MANAGEMENT);
	}

    public PrivilegesResult getAllSystemPrivileges(AccountTrxInfo trxInfo) {
        PrivilegesResult privilegesResult = new PrivilegesResult(ResponseStatus.SUCCESS);
        try {
            List<PrivilegeModel> privilegesList = accountBean.getAllSystemPrivileges(trxInfo);
            privilegesResult.setPrivilegesList(privilegesList);
        } catch (DBException e) {
            acctLogger.error(trxInfo.logId() + "DBException " + e.getMessage());
            appLogger.error(trxInfo.logId(), e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            privilegesResult.setStatus(ResponseStatus.FAIL);
        } catch (Exception e) {
            acctLogger.error(trxInfo.logId() + "Exception " + e.getMessage());
            appLogger.error(trxInfo.logId(), e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            privilegesResult.setStatus(ResponseStatus.FAIL);
        }

        return privilegesResult;
    }

	
}
