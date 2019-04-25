package com.edafa.web2sms.service.prov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBElement;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.adapters.cloud.callback.CloudCallBack;
import com.edafa.web2sms.adapters.cloud.callback.model.ProvisioningStatusUpdateType;
import com.edafa.web2sms.adapters.cloud.callback.model.StatusType;
import com.edafa.web2sms.adapters.cloud.exception.FailedToCallBackCloud;
import com.edafa.web2sms.adapters.tibco.SRCreationClient;
import com.edafa.web2sms.adapters.tibco.exception.SRCreationFailed;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountSenderDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestTypeDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestsArchDaoLocal;
import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.ProvRequest;
import com.edafa.web2sms.dalayer.model.ProvRequestActive;
import com.edafa.web2sms.dalayer.model.ProvRequestArch;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;
import com.edafa.web2sms.dalayer.model.ProvRequestType;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountAlreadyActiveException;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.DuplicateProvioniongRequest;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountStateException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidSenderType;
import com.edafa.web2sms.acc_manag.service.account.exception.ProvRequestNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameAlreadyAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.AdminAlreadyGrantedException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.service.conversoin.ProvConversionBean;
//import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.ProvTrxInfo;
import com.edafa.web2sms.service.model.ProvisioningRequest;
import com.edafa.web2sms.acc_manag.service.model.ProvisioningRequestInfo;
//import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.service.prov.exception.InvalidProvRequestException;
import com.edafa.web2sms.service.prov.interfaces.ProvisioningEventListener;
import com.edafa.web2sms.service.prov.interfaces.ServiceProvisioningLocal;
import com.edafa.web2sms.service.prov.model.TIBCOresponse;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.MsisdnFormat;
import com.edafa.web2sms.utils.sms.SMSUtils;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidSMSSender;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountConversionFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.UserManagementFacingLocal;
import com.edafa.web2sms.utils.sms.exception.InvalidAddressFormattingException;
import com.edafa.web2sms.utils.sms.exception.InvalidMSISDNFormatException;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;

import eg.com.vfe.xmlns.eai.celfocus.setservice.reply.SetServiceReply;
import eg.com.vfe.xmlns.internet.celfocus.client.autocreatesr.process.HTTPSR;
import eg.com.vfe.xmlns.internet.celfocus.client.autocreatesr.process.ObjectFactory;
import eg.com.vfe.xmlns.internet.celfocus.client.autocreatesr.process.SmartScript;
import eg.com.vfe.xmlns.internet.celfocus.client.autocreatesr.process.TroubleTicket;

/**
 * Session Bean implementation class ServiceProvisioning
 */
@Stateless
public class ServiceProvisioning implements ServiceProvisioningLocal {

	private Logger appLogger = LogManager.getLogger(LoggersEnum.PROV_UTILS.name());
	private Logger provLogger = LogManager.getLogger(LoggersEnum.PROV.name());

	@Resource
	private EJBContext context;

	/**
	 * this is a work around instead of moving some business method in another bean 
	 * more explanation will be provided in the usage case.
	 */
	@EJB
	ServiceProvisioningLocal serviceProv;

	@EJB
	private ProvRequestDaoLocal provRequestDao;

	@EJB
	AccountSenderDaoLocal accountSenderDao;

	@EJB
	private ProvRequestStatusDaoLocal provRequestStatusDao;

	@EJB
	private ProvRequestsArchDaoLocal provRequestsArchDao;

	@EJB
	ProvRequestTypeDaoLocal provRequestTypeDao;

	@EJB
	private ProvConversionBean provConversionBean;

        @EJB
        private ProvisioningEventListener provisioningEventListener;
        
	@EJB
	AccountManegementFacingLocal accountManagement;

	@EJB
	UserManagementFacingLocal userManagementBean;

	@EJB
	AccountConversionFacingLocal accountConversion;

	@EJB
	CloudCallBack cloudCallBackClient;

	@EJB
	SRCreationClient srCreationClient;
        
        @EJB
	AppErrorManagerAdapter appErrorManagerAdapter;

	public ServiceProvisioning() {
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void handleCloudProvRequest(ProvTrxInfo trxInfo, ProvisioningRequest provRequest, boolean createSR)
			throws TierNotFoundException, DuplicateProvioniongRequest, DBException, SRCreationFailed, AccountNotFoundException
			, UserNotFoundException {

		provRequest.setCompanyName(provRequest.getCompanyName().toLowerCase());
		provRequest.setAccountAdmin(provRequest.getAccountAdmin().toLowerCase());

		if (provRequest.getUserId() != null) {
			provRequest.setUserId(provRequest.getUserId().toLowerCase());
		}
		
		if(provRequest.getRequestType() == ProvRequestTypeName.USER_CREATE){
			checkAccountRegistered(trxInfo, provRequest);
		}else if(provRequest.getRequestType() == ProvRequestTypeName.USER_DELETE){
			checkAccountRegistered(trxInfo, provRequest);
			checkAccountUserRegistered(trxInfo, provRequest);
		}
		createProvisioningRequest(trxInfo, provRequest, false);

		if (createSR) {
			try {
				createProvSR(trxInfo, provRequest);
			} catch (SRCreationFailed e) {
				provLogger.error(trxInfo.logId() + e.getMessage() + ", set rollback", e);
				context.setRollbackOnly();
				provLogger.trace(trxInfo.logId() + "RollbackOnly is set");
				throw e;
			}
		}
	}

	@Override
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void handleAsyncCloudProvRequest(ProvTrxInfo trxInfo, ProvisioningRequest provRequest) {
		// ProvResultStatus status = new ProvResultStatus();
		StatusType callBackStatus = null;
		String callBackErrMsg = null;
		String username = null;
		boolean rollBackFlag = false;
		Account acct = null;
		int callBackWaitingTime = (Integer) Configs.CLOUD_CALL_BACK_WATING_TIME.getValue();
		// Handling the request
                ProvisioningRequestInfo provisioningRequestInfo;
		try {
			switch (provRequest.getRequestType()) {
			case DEACTIVATE_ACCOUNT:
				provLogger.info(trxInfo.logId() + "Deactivating the account");
				provisioningRequestInfo = accountManagement.deactivateAccount(trxInfo.getAccountProvTrxInfo());
                                provisioningEventListener.handleProvisioningEvent(new ProvisioningEvent(provisioningRequestInfo));
				provLogger.info(trxInfo.logId() + "The account is deactivated successfully");
				break;

			case SUSPEND_ACCOUNT:
				provLogger.info(trxInfo.logId() + "Suspending the account");
				provisioningRequestInfo = accountManagement.suspendAccount(trxInfo.getAccountProvTrxInfo());
                                provisioningEventListener.handleProvisioningEvent(new ProvisioningEvent(provisioningRequestInfo));
                                provLogger.info(trxInfo.logId() + "The account is suspended successfully");
				break;

			case REACTIVATE_ACCT_AFTER_SUSPENSION:
				provLogger.info(trxInfo.logId() + "Reactivating the account after suspension");
				provisioningRequestInfo = accountManagement.reactivateAccountAfterSuspension(trxInfo.getAccountProvTrxInfo());
                                provisioningEventListener.handleProvisioningEvent(new ProvisioningEvent(provisioningRequestInfo));
                                provLogger.info(trxInfo.logId() + "The account is reactivated successfully");
				break;
			case USER_CREATE:
				username = provRequest.getUserId().substring(0, provRequest.getUserId().indexOf("@"));
				provLogger.info(trxInfo.logId() + "creating account user (" + username + ").");
				userManagementBean.addAccountUser(trxInfo.getAccountProvTrxInfo(), username, null);
				provLogger.info(trxInfo.logId() + "User created successfully");
				break;
			case USER_DELETE:
				username = provRequest.getUserId().substring(0, provRequest.getUserId().indexOf("@"));
				provLogger.info(trxInfo.logId() + "Deleting account user (" + username + ").");
				userManagementBean.deactivateAccountUser(trxInfo.getAccountProvTrxInfo(), username);
				provLogger.info(trxInfo.logId() + "User deleted successfully");
				break;
			default:
				return;
			}
			provLogger.debug(trxInfo.logId() + "Set provisioning request status to " + ProvReqStatusName.SUCCESS);
			// Update request status to SUCCESS
			provRequest.setStatus(ProvReqStatusName.SUCCESS);
			// status.setStatus(ProvResponseStatus.SUCCESS);
			callBackStatus = StatusType.SUCCEDED;

		} catch (DBException e) {
			provLogger.error(trxInfo.logId() + "Database error " + e);
			callBackStatus = StatusType.FAILED_GENERIC_ERROR;
			provRequest.setStatus(ProvReqStatusName.FAIL);
		} catch (AccountNotFoundException e) {
			provLogger.error(trxInfo.logId() + e.getMessage());
			callBackStatus = StatusType.FAILED_GENERIC_ERROR;
			callBackErrMsg = e.getMessage();
			provRequest.setStatus(ProvReqStatusName.FAIL);
		} catch (InvalidAccountStateException e) {
			provLogger.error(trxInfo.logId() + e.getMessage());
			callBackStatus = StatusType.FAILED_GENERIC_ERROR;
			callBackErrMsg = e.getMessage();
			provRequest.setStatus(ProvReqStatusName.FAIL);
		} catch (AdminAlreadyGrantedException e) {
			provLogger.error(trxInfo.logId() + e.getMessage());
			callBackStatus = StatusType.FAILED_GENERIC_ERROR;
			callBackErrMsg = e.getMessage();
		} catch (UserNotFoundException e) {
			provLogger.error(trxInfo.logId() + e.getMessage());
			callBackStatus = StatusType.FAILED_GENERIC_ERROR;
			callBackErrMsg = e.getMessage();
		}
		// call TIBCO -Get- for suspension or de-activation
		if (provRequest.getRequestType() == ProvRequestTypeName.DEACTIVATE_ACCOUNT
				|| provRequest.getRequestType() == ProvRequestTypeName.SUSPEND_ACCOUNT) {

			provLogger.info(trxInfo.logId() + "calling TIBCO for " + provRequest.getRequestType()
					+ ", Getting account[" + trxInfo.getAccountId() + "].");

			// calling CallTibcoForProvRequest
			// set callBackStatus with response status
			try {

				acct = accountManagement.findAccountById(trxInfo.getAccountProvTrxInfo(), trxInfo.getAccountId());
				provRequest.setMSISDN(acct.getBillingMsisdn());
				provLogger.debug(trxInfo.logId() + "Found " + acct);

				TIBCOresponse response = CallTibcoForProvRequest(trxInfo, acct.getBillingMsisdn(),
						provRequest.getRequestType());
				provLogger.info(trxInfo.logId() + "Calling Tibco for " + provRequest.getRequestType().name()
						+ "deactivate account with id [" + trxInfo.getAccountId() + "] is finished with "
						+ response.getErrorCode());
				String errCode = response.getErrorCode();
                                if (errCode != "0" && errCode != "-2000") {
                                    reportAppError(AppErrors.FAILED_TO_FORWARD_REQUEST, "Failed to forward prov request");
                                }
				switch (errCode) {
				case "-2000":
				case "0":
					callBackStatus = StatusType.SUCCEDED;
					callBackErrMsg = response.getErrorMsg();
					provLogger.debug(trxInfo.logId() + " Tibco response for " + provRequest.getRequestType().name()
							+ " account with id [" + trxInfo.getAccountId() + "] is " + errCode + " with response msg "
							+ response.getErrorMsg());
					break;
				case "-100":
					callBackStatus = StatusType.FAILED_GENERIC_ERROR;
					callBackErrMsg = response.getErrorMsg();
					provLogger.debug(trxInfo.logId() + " Tibco response for " + provRequest.getRequestType().name()
							+ " account with id [" + trxInfo.getAccountId() + "] is " + errCode + " with response msg "
							+ response.getErrorMsg());
					rollBackFlag = true;

					provLogger.debug(trxInfo.logId() + " Roll back is Set");
					break;
				case "-500":
					callBackStatus = StatusType.FAILED_GENERIC_ERROR;
					callBackErrMsg = response.getErrorMsg();
					provLogger.debug(trxInfo.logId() + " Tibco response for " + provRequest.getRequestType().name()
							+ " account with id [" + trxInfo.getAccountId() + " ] is " + errCode
							+ " with response msg " + response.getErrorMsg());
					rollBackFlag = true;
					provLogger.debug(trxInfo.logId() + " Roll back is Set");

					break;
				case "-502":
					callBackStatus = StatusType.FAILED_GENERIC_ERROR;
					callBackErrMsg = response.getErrorMsg();
					provLogger.debug(trxInfo.logId() + " Tibco response for " + provRequest.getRequestType().name()
							+ " account with id [" + trxInfo.getAccountId() + "] is " + errCode + " with response msg "
							+ response.getErrorMsg());
					rollBackFlag = true;
					provLogger.debug(trxInfo.logId() + " Roll back is Set");

					break;
				case "-999":
					callBackStatus = StatusType.FAILED_GENERIC_ERROR;
					callBackErrMsg = response.getErrorMsg();
					provLogger.debug(trxInfo.logId() + " Tibco response for " + provRequest.getRequestType().name()
							+ " account with id [" + trxInfo.getAccountId() + "] is " + errCode + " with response msg "
							+ response.getErrorMsg());
					rollBackFlag = true;
					provLogger.debug(trxInfo.logId() + " Roll back is Set");

					break;
				case "-998":
					callBackStatus = StatusType.FAILED_GENERIC_ERROR;
					callBackErrMsg = response.getErrorMsg();
					provLogger.debug(trxInfo.logId() + " Tibco response for " + provRequest.getRequestType().name()
							+ " account with id [ " + trxInfo.getAccountId() + "] is " + errCode
							+ " with response msg " + response.getErrorMsg());
					rollBackFlag = true;
					provLogger.debug(trxInfo.logId() + " Roll back is Set");

					break;

				default:
					callBackStatus = StatusType.FAILED_GENERIC_ERROR;
					callBackErrMsg = response.getErrorMsg();
					provLogger.debug(trxInfo.logId() + " Tibco response for " + provRequest.getRequestType().name()
							+ " account with id [" + trxInfo.getAccountId() + "] is " + errCode + " with response msg "
							+ response.getErrorMsg());
					rollBackFlag = true;
					provLogger.debug(trxInfo.logId() + " Roll back is Set");

				}

			} catch (DBException e) {
				provLogger.error(trxInfo.logId() + e.getMessage(), e);
			} catch (AccountNotFoundException e) {
				provLogger.error(trxInfo.logId() + e.getMessage(), e);
			}

		}

		// Waiting before sending call back
		provLogger.info(trxInfo.logId() + "Waiting before sending call back (" + callBackWaitingTime + ") ms");
		try {
			Thread.sleep(callBackWaitingTime);
			// Thread.sleep(100);

		} catch (InterruptedException e) {
			provLogger.warn(trxInfo.logId() + "Interrupted while waiting before sending cloud call back ");
		}

		// Cloud call back
		provLogger.debug(trxInfo.logId() + "Will replace call back URL ip and port according to configuration URL=\""
				+ provRequest.getCallbackUrl() + "\"");
		String cloudCallBackURL = getCallBackURL(provRequest.getCallbackUrl());
		provLogger.info(trxInfo.logId() + "Will call the cloud back with status " + callBackStatus + " on URL: \""
				+ cloudCallBackURL + "\"");
		try {
			sendCloudCallBack(provRequest.getRequestId(), cloudCallBackURL, callBackStatus, callBackErrMsg);

			/**/
			// provLogger.debug("removing one off account with id : [ "
			// +acct.getAccountId() + "].");
			if (provRequest.getRequestType() == ProvRequestTypeName.DEACTIVATE_ACCOUNT) {
				accountManagement.removePrePaidAccount(trxInfo.getAccountProvTrxInfo(), acct);
				provLogger.debug("account is removed succefully .");
			}
			if (rollBackFlag) {
				provRequest.setStatus(ProvReqStatusName.FAIL);
			}
			provLogger.debug(trxInfo.logId() + "Cloud called back successfully");
		} catch (FailedToCallBackCloud e) {
			provLogger.error(trxInfo.logId() + e.getMessage());
			provLogger.info(trxInfo.logId() + "Faild to call Back cloud, Setting Roll back");
			provRequest.setStatus(ProvReqStatusName.FAIL);
			rollBackFlag = true;
			provLogger.debug(trxInfo.logId() + " Roll back is Set");

			// Send SR for RollingBack the provRequest in TIBCO
			if (provRequest.getRequestType() == ProvRequestTypeName.DEACTIVATE_ACCOUNT
					|| provRequest.getRequestType() == ProvRequestTypeName.SUSPEND_ACCOUNT) {
				provLogger.info("Sending ROLL_BACK SR to TIBCO ");
				try {
					provLogger.debug(trxInfo.logId() + "Call createProvSR ");
					createProvSR(trxInfo, provRequest);
				} catch (SRCreationFailed e1) {
					provLogger.error(trxInfo.logId() + e1.getMessage() + ", set rollback", e1);
				}

			}
		} catch (DBException e) {
			provLogger.error(trxInfo.logId() + " database error while deleting one off account " + e);
			provRequest.setStatus(ProvReqStatusName.FAIL);
			rollBackFlag = true;
			provLogger.debug(trxInfo.logId() + "Rollback Flag changed to be true");

		}

		// Archiving the request
		try {
			/**
			 * using archiveProvisioningRequest directly can't start new Trx
			 * So to start new Trx, you need to inject the session Bean in herself
			 * or move this method to another bean.
			 */
			serviceProv.archiveProvisioningRequest(trxInfo, provRequest.getRequestId(), provRequest.getStatus());
		} catch (DBException e) {
			provLogger.error(trxInfo.logId() + "database error while archiving the request " + e);
		} catch (ProvRequestNotFoundException e) {
			// Should never happen
			provLogger.error(trxInfo.logId() + e.getMessage());
		}

		/**
		 * this Flag used instead of putting context.setRollbackOnly() to ensure call
		 * setRollBackOnly after archiving the Prov Req, that was because
		 * archiveProvisioningRequest cause EJB exception when mark transaction
		 * rolled back as its didn't start new Trx because its in a same session
		 * bean
		 * 
		 */
		if (rollBackFlag) {
			context.setRollbackOnly();
			provLogger.debug(trxInfo.logId() + "Rollback called.");

		}
	}

	// TIBCO Prov

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void activateService(ProvTrxInfo provTrxInfo, ProvisioningRequest provRequest) throws Exception {
		Account account;
		try {
			provLogger.debug(provTrxInfo.logId() + "Creating new account");
			provTrxInfo.setAccountId(provRequest.getCompanyId());

			provLogger.debug(provTrxInfo.logId() + "Validate request MSISDN and format it: \""
					+ provRequest.getMSISDN() + "\"");
			String formattedBillingMsisdn = null;
			try {
				formattedBillingMsisdn = SMSUtils.formatAddress(provRequest.getMSISDN(), MsisdnFormat.INTER_CC_LOCAL);
			} catch (InvalidMSISDNFormatException e1) {
				throw new InvalidProvRequestException("Invalid billing MSISDN: \"" + provRequest.getMSISDN() + "\"");
			} catch (InvalidAddressFormattingException e) {
				throw new InvalidProvRequestException("Invalid billing MSISDN: \"" + provRequest.getMSISDN() + "\" "
						+ e.getMessage());
			}

			provLogger.debug(provTrxInfo.logId() + "Validate the sender name: \"" + provRequest.getSenderName() + "\"");
			try {
				accountManagement.validateSMSSender(provTrxInfo.getAccountProvTrxInfo(), provRequest.getSenderName());
			} catch (AccountManagInvalidSMSSender e) {
				throw new InvalidProvRequestException(e);
			}

			account = new Account();
			account.setAccountId(provRequest.getCompanyId());
			account.setCompanyName(provRequest.getCompanyName());
			// account.setAccountAdmin(provRequest.getAccountAdmin());
			account.setBillingMsisdn(formattedBillingMsisdn);

			provLogger.debug(provTrxInfo.logId() + "Get rate plan tier mapping, rate plan: "
					+ provRequest.getRatePlan());
			Tier mappedTier = getRateplanTierMapping(provTrxInfo, provRequest.getRatePlan());

			if (!mappedTier.getTierId().equals(provRequest.getTier().getTierId())) {
				String log = "the rateplan mapping tier is: " + mappedTier.getTierId() + " and the requested tier is "
						+ provRequest.getTier().getTierId();
				provLogger.error(provTrxInfo.logId() + log);
				throw new InvalidProvRequestException(log);
			}
			account.setTier(mappedTier);

			provLogger.info(provTrxInfo.logId() + "Activate the new account");
			ProvisioningRequestInfo provisioningRequestInfo = accountManagement.activateNewAccount(provTrxInfo.getAccountProvTrxInfo(), account, provRequest.getAccountAdmin());
                        provisioningEventListener.handleProvisioningEvent(new ProvisioningEvent(provisioningRequestInfo));

		} catch (DBException e) {
			String logMsg = "Database error, set rollback only";
			appLogger.error(provTrxInfo.logId() + logMsg, e);
			provLogger.error(provTrxInfo.logId() + logMsg + e.getCause().getMessage());
			context.setRollbackOnly();
			throw e;
		} catch (InvalidAccountException e) {
			String logMsg = "Invlaid account ";
			provLogger.error(provTrxInfo.logId() + logMsg + e.getMessage());
			throw e;
		} catch (AccountAlreadyActiveException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			throw e;
		} catch (TierNotFoundException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			throw e;
		}

		// Adding the sender to the new account
		try {

			provLogger.info(provTrxInfo.logId() + "Adding the sender to the new account");
			accountManagement.addAccountSenderName(provTrxInfo.getAccountProvTrxInfo(), provRequest.getSenderName());
		} catch (SenderNameAlreadyAttached | InvalidSMSSender e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage() + ", set rollback only");
			context.setRollbackOnly();
			throw e;
		} catch (DBException e) {
			provLogger
					.error(provTrxInfo.logId() + "Database problem while adding sender to account, set rollback only");
			context.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			provLogger.error(provTrxInfo.logId() + "Unhandled error");
			context.setRollbackOnly();
			throw e;
		}

		// Cloud call back
		try {
			provLogger.debug(provTrxInfo.logId()
					+ "Will replace call back URL ip and port according to configuration URL=\""
					+ provRequest.getCallbackUrl() + "\"");
			String callBackURL = getCallBackURL(provRequest.getCallbackUrl());
			provLogger.info(provTrxInfo.logId() + "Will call the cloud back service with status " + StatusType.SUCCEDED
					+ " on URL: \"" + callBackURL + "\"");
			sendCloudCallBack(provRequest.getRequestId(), callBackURL, StatusType.SUCCEDED);
			provLogger.info(provTrxInfo.logId() + "Cloud called back successfully");
		} catch (FailedToCallBackCloud e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage() + ", set rollback only");
			context.setRollbackOnly();
			throw e;
		}

		try {
			provLogger.info(provTrxInfo.logId() + "Archiving the provisioning request");
			serviceProv.archiveProvisioningRequest(provTrxInfo, provRequest.getRequestId(), ProvReqStatusName.SUCCESS);
		} catch (DBException e) {
			provLogger.error(provTrxInfo.logId() + "Error while archiving the request, set rollback only ", e);
			context.setRollbackOnly();
			throw e;
		} catch (ProvRequestNotFoundException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void migrateService(ProvTrxInfo provTrxInfo, ProvisioningRequest provRequest) throws DBException,
			FailedToCallBackCloud, InvalidProvRequestException, InvalidAccountStateException, AccountNotFoundException,
			TierNotFoundException {
		// Account account;
		try {
			provTrxInfo.setAccountId(provRequest.getCompanyId());

			provLogger.debug(provTrxInfo.logId() + "Get rate plan tier mapping, rate plan: "
					+ provRequest.getRatePlan());
			Tier mappedTier = getRateplanTierMapping(provTrxInfo, provRequest.getRatePlan());

			if (!mappedTier.getTierId().equals(provRequest.getTier().getTierId())) {
				String log = "the rateplan mapping tier is: " + mappedTier.getTierId() + " and the requested tier is "
						+ provRequest.getTier().getTierId();
				provLogger.error(provTrxInfo.logId() + log);
				throw new InvalidProvRequestException(log);
			}

			provLogger.debug(provTrxInfo.logId() + "Migrating the account to the new tier " + mappedTier.logInfo());
			ProvisioningRequestInfo provisioningRequestInfo = accountManagement.migrateAccount(provTrxInfo.getAccountProvTrxInfo(), mappedTier);
                        provisioningEventListener.handleProvisioningEvent(new ProvisioningEvent(provisioningRequestInfo));
                        provLogger.info(provTrxInfo.logId() + "The account is migrated to the new tier");
		} catch (DBException e) {
			String logMsg = "Database error ";
			appLogger.error(provTrxInfo.logId() + logMsg, e);
			provLogger.error(provTrxInfo.logId() + logMsg, e);
			throw e;

		} catch (InvalidAccountStateException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			throw e;
		} catch (AccountNotFoundException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			throw e;
		} catch (TierNotFoundException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			throw e;
		}

		try {
			// Cloud call back
			provLogger.debug(provTrxInfo.logId()
					+ "Will replace call back URL ip and port according to configuration URL=\""
					+ provRequest.getCallbackUrl() + "\"");
			String callBackURL = getCallBackURL(provRequest.getCallbackUrl());
			provLogger.info(provTrxInfo.logId() + "Calling the cloud back with status (" + StatusType.SUCCEDED
					+ ") on URL: \"" + callBackURL + "\"");
			sendCloudCallBack(provRequest.getRequestId(), callBackURL, StatusType.SUCCEDED);
			provLogger.info(provTrxInfo.logId() + "Cloud called back successfully");
		} catch (FailedToCallBackCloud e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			context.setRollbackOnly();
			throw e;
		}

		// Archiving the provisioning request
		try {
			provLogger.info(provTrxInfo.logId() + "Archiving the provisioning request");
			serviceProv.archiveProvisioningRequest(provTrxInfo, provRequest.getRequestId(), ProvReqStatusName.SUCCESS);
		} catch (DBException e) {
			String logMsg = "Database error, set rollback only";
			appLogger.error(provTrxInfo.logId() + logMsg, e);
			provLogger.error(provTrxInfo.logId() + logMsg, e);
			context.setRollbackOnly();
			throw e;
		} catch (ProvRequestNotFoundException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			// Should never happen
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ProvisioningRequest findProvisioningRequest(ProvTrxInfo provTrxInfo, String requestId)
			throws ProvRequestNotFoundException, DBException {
		provLogger.info(provTrxInfo.logId() + "Retrieve provisioning request with id=" + requestId);
		ProvRequestActive provRequestActive = provRequestDao.find(requestId);

		if (provRequestActive == null) {
			ProvRequestNotFoundException e = new ProvRequestNotFoundException(requestId);
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			throw e;
		}

		provLogger.debug(provTrxInfo.logId() + "Provisioning request with id=" + requestId
				+ " is retrieved from database, will be converted to model type");
		ProvisioningRequest provisioningRequest = provConversionBean.getProvRequest(provRequestActive);
		provLogger.info(provTrxInfo.logId() + "The provisioning request is converted to model type: "
				+ provisioningRequest.logRequest());
		return provisioningRequest;
	}

//	@Override
//	public void updateProvisioningRequestStatus(ProvTrxInfo trxInfo, String requestId, ProvReqStatusName newStatus) {
//		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);
//		ProvRequestStatus status = provRequestStatusDao.getCachedObjectByName(newStatus);
//		try {
//			provRequestDao.updateStatus(requestId, status);
//		} catch (DBException e) {
//			e.printStackTrace();
//			result.setStatus(ResponseStatus.FAIL);
//			result.setErrorMessage(e.getCause().getMessage());
//		}
//
//	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createProvisioningRequest(ProvTrxInfo trxInfo, ProvisioningRequest provRequest, boolean archive)
			throws DuplicateProvioniongRequest, DBException, TierNotFoundException {
		provLogger.info(trxInfo.logId() + "creating new provisioning request " + provRequest.logRequest()
				+ (archive ? "in archice" : ""));

		// Check if the request is duplicate
		checkProvRequestExistence(trxInfo, provRequest);

		ProvRequest newReq = provConversionBean.getProvRequest(provRequest);
		if (archive) {
			provRequestsArchDao.create((ProvRequestArch) newReq);
		} else {
			provRequestDao.create((ProvRequestActive) newReq);
		}
		provLogger.info(trxInfo.logId() + "provisioning request created successfully");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void archiveProvisioningRequest(ProvTrxInfo trxInfo, String requestId, ProvReqStatusName archiveStatus)
			throws DBException, ProvRequestNotFoundException {
		provLogger.info(trxInfo.logId() + " Moving provisioning request(" + requestId + ") to archive");
		// provRequestsArchDao.insertFromProvRequest(requestId);
		ProvRequestActive req = provRequestDao.find(requestId);

		provLogger.info(trxInfo.logId() + "Found " + req);

		if (req == null) {
			ProvRequestNotFoundException e = new ProvRequestNotFoundException(requestId);
			throw e;
		}

		provLogger.info(trxInfo.logId() + "Removing the provisioning request from Provisioning Requests table");
		provRequestDao.remove(req);

		if (archiveStatus != null) {
			ProvRequestStatus newStatus = provRequestStatusDao.getCachedObjectByName(archiveStatus);
			provLogger.info(trxInfo.logId() + "Update the request status from " + req.getStatus().getStatusName()
					+ " to " + archiveStatus);
			req.setStatus(newStatus);
		}
		provRequestsArchDao.create(new ProvRequestArch(req));
		provLogger.info(trxInfo.logId() + "provisioning request moved to archive successfully");
	}

	@Override
	public AccountModelFullInfo findAccountByCoAdminFullInfo(TrxInfo trxInfo, String accountHolderId)
			throws DBException, AccountNotFoundException {
		provLogger.debug(trxInfo.logId() + "Getting account full info");
		AccountModelFullInfo accountInfoFull = accountManagement.findAccountByCoAdminFullInfo(trxInfo.getAccountTrxInfo(), accountHolderId);
		provLogger.debug(trxInfo.logId() + "Account retrieved with id " + accountInfoFull.logId());
		return accountInfoFull;
	}

	@Override
	public AccountModelFullInfo findAccountByMSISDNFullInfo(TrxInfo trxInfo, String msisdn) throws DBException,
			AccountNotFoundException {
		provLogger.debug(trxInfo.logId() + "Getting account full info");
		AccountModelFullInfo accountInfoFull = accountManagement.findAccountByMSISDNFullInfo(trxInfo.getAccountTrxInfo(), msisdn);
		provLogger.debug(trxInfo.logId() + "Account retrieved with id " + accountInfoFull.logId());
		// provLogger.debug(trxInfo.logId() +
		// "Getting account pending requests");
		return accountInfoFull;
	}

	@Override
	public void cancelProvisioningRequest(ProvTrxInfo provTrxInfo, ProvisioningRequest provRequest) throws DBException,
			ProvRequestNotFoundException, FailedToCallBackCloud {
		String reqId = provRequest.getRequestId();
		provLogger.info(provTrxInfo.logId() + "Cancelling provisioning request(" + provRequest.getRequestId() + ")");
		try {
			StatusType returnStatus = StatusType.FAILED_GENERIC_ERROR;
			String errorMessage = "Request is rejected";

			provLogger.debug(provTrxInfo.logId()
					+ "Will replace call back URL ip and port according to configuration URL=\""
					+ provRequest.getCallbackUrl() + "\"");
			String cloudCallBackUrl = getCallBackURL(provRequest.getCallbackUrl());
			provLogger.info(provTrxInfo.logId() + "Will call the cloud back with status " + returnStatus
					+ " on URL: \"" + cloudCallBackUrl + "\", errorMessage: " + errorMessage);
			sendCloudCallBack(provRequest.getRequestId(), cloudCallBackUrl, returnStatus, errorMessage);
			provLogger.info(provTrxInfo.logId() + "Cloud called back successfully");
		} catch (FailedToCallBackCloud e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage() + ", set rollback only");
			context.setRollbackOnly();
			throw e;
		}
		serviceProv.archiveProvisioningRequest(provTrxInfo, reqId, ProvReqStatusName.CANCELLED);
	}

	@Override
	public List<ProvisioningRequest> findActiveProvisioningRequests(TrxInfo trxInfo, String accountAdmin)
			throws DBException {
		List<ProvRequestStatus> statuses = new ArrayList<>();
		statuses.add(provRequestStatusDao.getCachedObjectByName(ProvReqStatusName.PENDING));
		List<ProvRequestActive> activeProvRequests = provRequestDao.findByCompanyAdminAndStatus(accountAdmin, statuses);
		provLogger.debug(trxInfo.logId() + "Found (" + activeProvRequests.size() + ") pending requests");
		return provConversionBean.getProvRequests(activeProvRequests);
	}

	public void createProvSR(ProvTrxInfo trxInfo, ProvisioningRequest provRequest) throws SRCreationFailed {
		provLogger.info(trxInfo.logId() + "Creating provisioning SR on TIBCO");
		// Getting the configs
		provLogger.debug(trxInfo.logId() + "Getting the configs");
		String srServiceURL = (String) Configs.PROV_SR_SERVICE_URI.getValue();

		String srCat = null;
		String srType = null;
		String srCustType = null;
		String srCase = null;
		String srTeam = null;
		String srDesc = null;
		int ssCompanyFieldNum = (int) Configs.PROV_SMART_SCRIPT_COMPANY_FIELD_NUM.getValue();
		int ssAcctAdminFieldNum = (int) Configs.PROV_SMART_SCRIPT_ACCTADMIN_FIELD_NUM.getValue();
		int ssMSISDNFieldNum = (int) Configs.PROV_SMART_SCRIPT_MSISDN_FIELD_NUM.getValue();

		switch (provRequest.getRequestType()) {
		case ACTIVATE_ACCOUNT:
			srCat = (String) Configs.PROV_ACTIVATE_SR_CATEGORY.getValue();
			srType = (String) Configs.PROV_ACTIVATE_SR_TYPE.getValue();
			srCustType = (String) Configs.PROV_ACTIVATE_SR_CUSTOMER_TYPE.getValue();
			srCase = (String) Configs.PROV_ACTIVATE_SR_CASE.getValue();
			srTeam = (String) Configs.PROV_ACTIVATE_SR_TEAM.getValue();
			break;

		case UPGRADE_ACCOUNT:
			srCat = (String) Configs.PROV_UPGRADE_SR_CATEGORY.getValue();
			srType = (String) Configs.PROV_UPGRADE_SR_TYPE.getValue();
			srCustType = (String) Configs.PROV_UPGRADE_SR_CUSTOMER_TYPE.getValue();
			srCase = (String) Configs.PROV_UPGRADE_SR_CASE.getValue();
			srTeam = (String) Configs.PROV_UPGRADE_SR_TEAM.getValue();
			break;

		case DOWNGRADE_ACCOUNT:
			srCat = (String) Configs.PROV_DOWNGRADE_SR_CATEGORY.getValue();
			srType = (String) Configs.PROV_DOWNGRADE_SR_TYPE.getValue();
			srCustType = (String) Configs.PROV_DOWNGRADE_SR_CUSTOMER_TYPE.getValue();
			srCase = (String) Configs.PROV_DOWNGRADE_SR_CASE.getValue();
			srTeam = (String) Configs.PROV_DOWNGRADE_SR_TEAM.getValue();
			break;

		case CHANGE_SENDER_NAME:
			srCat = (String) Configs.PROV_CHANGE_SENDER_SR_CATEGORY.getValue();
			srType = (String) Configs.PROV_CHANGE_SENDER_SR_TYPE.getValue();
			srCustType = (String) Configs.PROV_CHANGE_SENDER_SR_CUSTOMER_TYPE.getValue();
			srCase = (String) Configs.PROV_CHANGE_SENDER_SR_CASE.getValue();
			srTeam = (String) Configs.PROV_CHANGE_SENDER_SR_TEAM.getValue();
			srDesc = provRequest.getSenderName() + "," + provRequest.getNewSenderName();
			break;
		case ADD_SENDER_NAME:
			srCat = (String) Configs.PROV_ADD_SENDER_SR_CATEGORY.getValue();
			srType = (String) Configs.PROV_ADD_SENDER_SR_TYPE.getValue();
			srCustType = (String) Configs.PROV_ADD_SENDER_SR_CUSTOMER_TYPE.getValue();
			srCase = (String) Configs.PROV_ADD_SENDER_SR_CASE.getValue();
			srTeam = (String) Configs.PROV_ADD_SENDER_SR_TEAM.getValue();
			srDesc = provRequest.getSenderName();
			break;

		case SUSPEND_ACCOUNT:
		case DEACTIVATE_ACCOUNT:
			srCat = (String) Configs.PROV_ROLL_BACK_SR_CATEGORY.getValue();
			srType = (String) Configs.PROV_ROLL_BACK_SR_TYPE.getValue();
			srCustType = (String) Configs.PROV_ROLL_BACK_SR_CUSTOMER_TYPE.getValue();
			srCase = (String) Configs.PROV_ROLL_BACK_SR_CASE.getValue();
			srTeam = (String) Configs.PROV_ROLL_BACK_SR_TEAM.getValue();
			break;

		default:
			provLogger.warn(trxInfo.logId() + "Invalid request type for SR creation");
			return;
		}

		// Initializing the objects
		provLogger.debug(trxInfo.logId() + "Initializing the SR objects");
		ObjectFactory httpSRFactory = new ObjectFactory();
		HTTPSR sr = httpSRFactory.createHTTPSR();
		TroubleTicket ticket = new TroubleTicket();
		SmartScript smartScript = new SmartScript();
		String formattedBillingMsisdn;
		ticket.setSmartScript(httpSRFactory.createTroubleTicketSmartScript(smartScript));
		sr.getTroubleTicket().add(ticket);
		JAXBElement<HTTPSR> request = httpSRFactory.createHTTPSRRequest(sr);

		try {
			provLogger.debug(trxInfo.logId() + "Formatting MSISDN: " + provRequest.getMSISDN()
					+ " to 010xxxxxxxx format.");
			formattedBillingMsisdn = SMSUtils.formatAddress(provRequest.getMSISDN(), MsisdnFormat.NATIONAL_KEY);
			provLogger.debug(trxInfo.logId() + "MSISDN Formated:" + formattedBillingMsisdn);
		} catch (InvalidMSISDNFormatException | InvalidAddressFormattingException e) {
			provLogger.warn(trxInfo.logId() + "Failed to format msisdn: " + provRequest.getMSISDN()
					+ " to national format.");
			formattedBillingMsisdn = provRequest.getMSISDN();

		}

		// Setting trouble ticket values
		provLogger.debug(trxInfo.logId() + "Setting Trouble Ticket values");
		ticket.setCategory(httpSRFactory.createTroubleTicketCategory(srCat));
		ticket.setType(httpSRFactory.createTroubleTicketType(srType));
		ticket.setCase(httpSRFactory.createTroubleTicketCase(srCase));
		ticket.setCustomerType(httpSRFactory.createTroubleTicketCustomerType(srCustType));
		ticket.setOwnerTeam(httpSRFactory.createTroubleTicketOwnerTeam(srTeam));
		ticket.setMobileNumber(httpSRFactory.createTroubleTicketMobileNumber(formattedBillingMsisdn));
		if (srDesc != null)
			ticket.setDescription(httpSRFactory.createTroubleTicketDescription(srDesc));
		provLogger.debug(trxInfo.logId() + "Trouble Ticket values: (Category=" + srCat + ", Type=" + srType + ", Case="
				+ srCase + ", CustomerType=" + srCustType + ", OwnerTeam=" + srTeam + ", MobileNumber="
				+ provRequest.getMSISDN() + (srDesc != null ? ", Description=" + srDesc : "") + ")");
		// Setting smart script values
		provLogger.debug(trxInfo.logId() + "Setting Smart Script values");

		fillSmartScript(smartScript, formattedBillingMsisdn, ssMSISDNFieldNum);
		fillSmartScript(smartScript, provRequest.getCompanyName(), ssCompanyFieldNum);
		fillSmartScript(smartScript, provRequest.getAccountAdmin(), ssAcctAdminFieldNum);
		// smartScript
		// .setVFCCspcSRspcGenspc31(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc31(formattedBillingMsisdn));
		// smartScript.setVFCCspcSRspcGenspc06(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc06(provRequest
		// .getCompanyName()));
		// smartScript.setVFCCspcSRspcGenspc17(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc17(provRequest
		// .getAccountAdmin()));
		provLogger.debug(trxInfo.logId() + "Smart Script values: MSISDN(VFCCspcSRspcGenspc" + ssMSISDNFieldNum + ")="
				+ formattedBillingMsisdn + ", AccountAdmin(VFCCspcSRspcGenspc" + ssAcctAdminFieldNum + ")="
				+ provRequest.getAccountAdmin() + ", CompanyName(VFCCspcSRspcGenspc" + ssCompanyFieldNum + ")="
				+ provRequest.getCompanyName());

		provLogger.debug(trxInfo.logId() + "Sending created SR  to URL: \"" + srServiceURL + "\"");
		SetServiceReply reply = srCreationClient.createSR(provLogger, trxInfo.getTrxId(), srServiceURL, request);
		provLogger.info(trxInfo.logId() + "Provisioning SR created successfully, URL: \"" + srServiceURL
				+ "\", reply: " + getSRReplyLogMsg(reply));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void sendCloudCallBack(String provId, String callBackUrl, StatusType status) throws FailedToCallBackCloud {
		sendCloudCallBack(provId, callBackUrl, status, null);
	}

	@Override
	public void sendCloudCallBack(String provId, String callBackUrl, StatusType status, String errMessage)
			throws FailedToCallBackCloud {
		ProvisioningStatusUpdateType cloudStatusUpdate = new ProvisioningStatusUpdateType();
		cloudStatusUpdate.setProvisioningID(provId);
		cloudStatusUpdate.setStatus(status);
		cloudStatusUpdate.setErrorMessage(errMessage);
		cloudCallBackClient.sendCallBackStatusUpdate(callBackUrl, cloudStatusUpdate);
	}

	@Override
	public void requestAddSender(ProvTrxInfo trxInfo, String sender) throws DBException, AccountNotFoundException,
			SenderNameAlreadyAttached, InvalidSMSSender, SRCreationFailed, DuplicateProvioniongRequest,
			InvalidSenderType, SenderNameNotAttached, InvalidProvRequestException {

		provLogger.info(trxInfo.logInfo() + "Request add sender name (" + sender + ") to account ("
				+ trxInfo.getAccountId() + ").");

		provLogger.debug(trxInfo.logId() + "Check prov. request existance");
		checkProvRequestExistence(trxInfo, ProvRequestTypeName.ADD_SENDER_NAME, ProvReqStatusName.PENDING, sender);
		provLogger.debug(trxInfo.logId() + "Validate the new sender");
                try {
		accountManagement.validateSMSSender(trxInfo.getAccountProvTrxInfo(), sender);
                } catch (AccountManagInvalidSMSSender e) {
                    throw new InvalidSMSSender(e.getSender(), e.getMessage());
                }
		provLogger.debug(trxInfo.logId() + "Retriving account from database");
		Account acct = accountManagement.findAccountById(trxInfo.getAccountProvTrxInfo(), trxInfo.getAccountId());

		provLogger.debug(trxInfo.logId() + "Account retrived: " + acct.logInfo()
				+ ", Retriving account's admin user from database");
		AccountUser admin = userManagementBean.getAccountAdminUser(trxInfo.getAccountProvTrxInfo());
		String acctAdmin = admin.getUsername() + "@" + acct.getCompanyName();
		provLogger.debug(trxInfo.logId() + "Admin " + admin + ", AccountAdmin= " + acctAdmin
				+ ", Initializing provisioning request");
		ProvisioningRequest provRequest = new ProvisioningRequest();
		String reqId = UUID.randomUUID().toString();
		provLogger.trace(trxInfo.logId() + "Generated provisioning request id=" + reqId);
		provRequest.setRequestId(reqId);
		provRequest.setStatus(ProvReqStatusName.PENDING);
		provRequest.setAccountAdmin(acctAdmin);
		provRequest.setCompanyId(acct.getAccountId());
		provRequest.setCompanyName(acct.getCompanyName());
		provRequest.setEntryDate(new Date());
		provRequest.setRequestType(ProvRequestTypeName.ADD_SENDER_NAME);
		provRequest.setSenderName(sender);
		provRequest.setMSISDN(acct.getBillingMsisdn());
		provLogger.info(trxInfo.logId() + "Add sender provisioning request: " + provRequest.logRequest());

		try {
			// TODO MFarouk: use servProv to ensure new transaction
			createProvisioningRequest(trxInfo, provRequest, false);
		} catch (DuplicateProvioniongRequest e) {
			// Should never happen
			provLogger.error(trxInfo.logId() + "Failed to create provisioning request", e);
		} catch (TierNotFoundException e) {
			// Should never happen
			provLogger.error(trxInfo.logId() + "Failed to create provisioning request", e);
		}

		try {
			createProvSR(trxInfo, provRequest);
		} catch (SRCreationFailed e) {
			provLogger.error(trxInfo.logId() + "Failed to create SR: " + e.getMessage() + ", set rollBackOnly");
			context.setRollbackOnly();
			throw e;

		}

		provLogger.info(trxInfo.logId() + "Add new sender provisioning request created and send SR");

	}

	@Override
	public void requestChangeSender(ProvTrxInfo trxInfo, String oldSender, String newSender) throws DBException,
			AccountNotFoundException, SenderNameAlreadyAttached, InvalidSMSSender, SRCreationFailed,
			DuplicateProvioniongRequest, InvalidSenderType, SenderNameNotAttached, InvalidProvRequestException {

		provLogger.info(trxInfo.logInfo() + "Request change sender name from \"" + oldSender + "\" to \"" + newSender
				+ "\"");

		provLogger.debug(trxInfo.logId() + "Check prov. request existance");
		checkProvRequestExistence(trxInfo, ProvRequestTypeName.CHANGE_SENDER_NAME, ProvReqStatusName.PENDING, null);
		provLogger.debug(trxInfo.logId() + "Validate the new sender");
                try {
		accountManagement.validateSMSSender(trxInfo.getAccountProvTrxInfo(), newSender);
                } catch (AccountManagInvalidSMSSender e) {
                    throw new InvalidSMSSender(e.getSender(), e.getMessage());
                }
		provLogger.debug(trxInfo.logId() + "Retriving account from database");
		Account acct = accountManagement.findAccountById(trxInfo.getAccountProvTrxInfo(), trxInfo.getAccountId());
		provLogger.debug(trxInfo.logId() + "Account retrived: " + acct.logInfo()
				+ ", Retriving account's admin user from database");
		AccountUser admin = userManagementBean.getAccountAdminUser(trxInfo.getAccountProvTrxInfo());
		String acctAdmin = admin.getUsername() + "@" + acct.getCompanyName();
		provLogger.debug(trxInfo.logId() + "Admin " + admin + ", AccountAdmin= " + acctAdmin
				+ ", Initializing provisioning request");
		ProvisioningRequest provRequest = new ProvisioningRequest();
		String reqId = UUID.randomUUID().toString();
		provLogger.trace(trxInfo.logId() + "Generated provisioning request id=" + reqId);
		provRequest.setRequestId(reqId);
		provRequest.setStatus(ProvReqStatusName.PENDING);
		provRequest.setAccountAdmin(acctAdmin);
		provRequest.setCompanyId(acct.getAccountId());
		provRequest.setCompanyName(acct.getCompanyName());
		provRequest.setEntryDate(new Date());
		provRequest.setRequestType(ProvRequestTypeName.CHANGE_SENDER_NAME);
		provRequest.setSenderName(oldSender);
		provRequest.setNewSenderName(newSender);

		provRequest.setMSISDN(acct.getBillingMsisdn());
		provLogger.info(trxInfo.logId() + "Change sender provisioning request: " + provRequest.logRequest());

		try {
			createProvisioningRequest(trxInfo, provRequest, false);
		} catch (DuplicateProvioniongRequest e) {
			// Should never happen
			provLogger.error(trxInfo.logId() + "Failed to create provisioning request", e);
		} catch (TierNotFoundException e) {
			// Should never happen
			provLogger.error(trxInfo.logId() + "Failed to create provisioning request", e);
		}

		try {
			createProvSR(trxInfo, provRequest);
		} catch (SRCreationFailed e) {
			provLogger.error(trxInfo.logId() + "Failed to create SR: " + e.getMessage() + ", set rollBackOnly");
			context.setRollbackOnly();
			throw e;

		}

		provLogger.info(trxInfo.logId() + "Change sender provisioning request created and send SR");

	}

	@Override
	public void requestDeleteSender(ProvTrxInfo trxInfo, String sender) throws DBException, AccountNotFoundException,
			SenderNameAlreadyAttached, InvalidSMSSender, SRCreationFailed, DuplicateProvioniongRequest,
			InvalidSenderType, SenderNameNotAttached, InvalidProvRequestException {

		provLogger.info(trxInfo.logInfo() + "Request delete sender name (" + sender + ") from account ("
				+ trxInfo.getAccountId() + ").");

		provLogger.debug(trxInfo.logId() + "Check prov. request existance");
		checkProvRequestExistence(trxInfo, ProvRequestTypeName.DELETE_SENDER_NAME, ProvReqStatusName.PENDING, sender);
		provLogger.debug(trxInfo.logId() + "Validate the new sender");
		
		// TODO MFarouk: It is not logical to validate sender name while removal
                try {
		accountManagement.validateSMSSender(trxInfo.getAccountProvTrxInfo(), sender);
                } catch (AccountManagInvalidSMSSender e){
                    throw new InvalidSMSSender(e.getSender(), e.getMessage());
                }
		provLogger.debug(trxInfo.logId() + "Retriving account from database");
		Account acct = accountManagement.findAccountById(trxInfo.getAccountProvTrxInfo(), trxInfo.getAccountId());
		provLogger.debug(trxInfo.logId() + "Account retrived: " + acct.logInfo()
				+ ", Retriving account's admin user from database");
		AccountUser admin = userManagementBean.getAccountAdminUser(trxInfo.getAccountProvTrxInfo());
		provLogger.debug(trxInfo.logId() + "Admin " + admin + ", Initializing provisioning request");
		ProvisioningRequest provRequest = new ProvisioningRequest();
		String reqId = UUID.randomUUID().toString();
		provLogger.trace(trxInfo.logId() + "Generated provisioning request id=" + reqId);
		provRequest.setRequestId(reqId);
		provRequest.setStatus(ProvReqStatusName.PENDING);
		provRequest.setAccountAdmin(admin.getUsername());
		provRequest.setCompanyId(acct.getAccountId());
		provRequest.setCompanyName(acct.getCompanyName());
		provRequest.setEntryDate(new Date());
		provRequest.setRequestType(ProvRequestTypeName.DELETE_SENDER_NAME);
		provRequest.setSenderName(sender);
		provRequest.setMSISDN(acct.getBillingMsisdn());
		provLogger.info(trxInfo.logId() + "Delete sender provisioning request: " + provRequest.logRequest());

		try {
			// TODO MFarouk: using the injected bean to ensure the request being committed directly
			// as the business is handled in the same EJB transaction
			serviceProv.createProvisioningRequest(trxInfo, provRequest, false);
			provLogger.debug(trxInfo.logId()
					+ " Delete Sender provisioning request created, start deleting sender name:" + sender);

			accountManagement.deleteAccountSenderName(trxInfo.getAccountProvTrxInfo(), provRequest.getSenderName());
			provLogger.debug(trxInfo.logId() + " Sender name deleted successfully.");
			
		} catch (DuplicateProvioniongRequest e) {
			// Should never happen
			provLogger.error(trxInfo.logId() + "Failed to create provisioning request", e);
		} catch (TierNotFoundException e) {
			// Should never happen
			provLogger.error(trxInfo.logId() + "Failed to create provisioning request", e);
		} catch (DBException e) {
			provLogger.error(trxInfo.logId() + "Failed to remove sender", e);
		} catch (SenderNameNotAttached e) {
			provLogger.error(trxInfo.logId() + "Failed to find sender", e);
		} catch (IneligibleAccountException e) {
			provLogger.error(trxInfo.logId() + "Failed to remove sender", e);
		} 

		try {
			provLogger.info(trxInfo.logId() + "Archiving the provisioning request");
			serviceProv.archiveProvisioningRequest(trxInfo, provRequest.getRequestId(), ProvReqStatusName.SUCCESS);
		} catch (DBException e) {
			provLogger.error(trxInfo.logId() + "Error while archiving the request, set rollback only ", e);
			context.setRollbackOnly();
			throw e;
		} catch (ProvRequestNotFoundException e) {
			provLogger.error(trxInfo.logId() + e.getMessage());
		}

	}

	// =================Helper Methods==================

	
	private void checkAccountRegistered(ProvTrxInfo trxInfo, ProvisioningRequest provRequest) throws DBException, AccountNotFoundException {
			accountManagement.findAccountById(trxInfo.getAccountProvTrxInfo(),trxInfo.getAccountId());
	}
	
	private void checkAccountUserRegistered(ProvTrxInfo trxInfo, ProvisioningRequest provRequest) throws DBException, UserNotFoundException {
		String username = provRequest.getUserId().substring(0, provRequest.getUserId().indexOf("@"));
		userManagementBean.getAccountUser(trxInfo.getAccountProvTrxInfo(),username);
	}

	
	private void checkProvRequestExistence(ProvTrxInfo trxInfo, ProvisioningRequest provRequest) throws DBException,
			DuplicateProvioniongRequest {

		if (provRequestDao.count(provRequest.getRequestId()) > 0) {
			DuplicateProvioniongRequest e = new DuplicateProvioniongRequest(provRequest.getRequestId());
			provLogger.error(trxInfo.logId() + e.getMessage());
			throw e;
		}
	}

	private void checkProvRequestExistence(ProvTrxInfo trxInfo, ProvRequestTypeName provRequestType,
			ProvReqStatusName statusName, String sender) throws DBException, DuplicateProvioniongRequest,
			InvalidProvRequestException {

		ProvRequestStatus status = provRequestStatusDao.getCachedObjectByName(statusName);
		ProvRequestType type = provRequestTypeDao.getCachedObjectByName(provRequestType);

		switch (provRequestType) {

		case ADD_SENDER_NAME: {
			if (provRequestDao.countByCompanyIdRequestTypeStatusAndSender(trxInfo.getAccountId(), type, status, sender) > 0) {
				DuplicateProvioniongRequest e = new DuplicateProvioniongRequest(provRequestType);
				throw e;
			}
			break;
		}
		case CHANGE_SENDER_NAME: {
			// TODO: what will happens if more than one change request for same
			// sender

			break;
		}
		case DELETE_SENDER_NAME: {
			if (provRequestDao.countByCompanyIdRequestTypeStatusAndSender(trxInfo.getAccountId(), type, status, sender) > 0) {
				DuplicateProvioniongRequest e = new DuplicateProvioniongRequest(provRequestType);
				throw e;
			}

			// Can't delete all sender... Account should has at least one sender
			int provDelRequests = provRequestDao.countByCompanyIdAndRequestTypeAndStatus(trxInfo.getAccountId(), type,
					status);
			int acctSenders = accountSenderDao.CountByAccountId(trxInfo.getAccountId());
			if (acctSenders - provDelRequests < 2) {
				InvalidProvRequestException e = new InvalidProvRequestException(provRequestType,
						"Account Should has at least one sender");
				throw e;
			}
			break;
		}
		default:
			break;

		}

		/*
		 * if (provRequestDao.countByCompanyIdAndRequestTypeAndStatus(trxInfo.
		 * getAccountId(), type, status) > 0 ) { DuplicateProvioniongRequest e =
		 * new DuplicateProvioniongRequest(provRequestType); throw e; }
		 */
	}

	private Tier getRateplanTierMapping(ProvTrxInfo provTrxInfo, String ratePlan) throws TierNotFoundException,
			DBException {
		return accountManagement.getRateplanTierMapping(provTrxInfo.getAccountProvTrxInfo(), ratePlan);
	}

	private String getCallBackURL(String callbackUrl) {
		String callBackBaseURI = (String) Configs.CLOUD_CALL_BACK_BASE_URI.getValue();
		callbackUrl = callBackBaseURI + callbackUrl.substring(callbackUrl.indexOf('/', 8));
		return callbackUrl;
	}

	private String getSRReplyLogMsg(SetServiceReply reply) {
		return "SetServiceReply(AccountID=" + reply.getAccountID() + ", SRID=" + reply.getSRID() + ", ECode="
				+ reply.getECode() + ", EDescription="
				+ (reply.getEDescription() != null ? reply.getEDescription() : "") + ")";
	}

	private void fillSmartScript(SmartScript smartScript, String str, int smartScriptNum) {
		ObjectFactory httpSRFactory = new ObjectFactory();
		switch (smartScriptNum) {
		case 1: {
			smartScript.setVFCCspcSRspcGenspc01(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc01(str));
			break;
		}
		case 2: {
			smartScript.setVFCCspcSRspcGenspc02(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc02(str));
			break;
		}
		case 3: {
			smartScript.setVFCCspcSRspcGenspc03(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc03(str));
			break;
		}
		case 4: {
			smartScript.setVFCCspcSRspcGenspc04(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc04(str));
			break;
		}
		case 5: {
			smartScript.setVFCCspcSRspcGenspc05(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc05(str));
			break;
		}
		case 6: {
			smartScript.setVFCCspcSRspcGenspc06(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc06(str));
			break;
		}
		case 7: {
			smartScript.setVFCCspcSRspcGenspc07(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc07(str));
			break;
		}
		case 8: {
			smartScript.setVFCCspcSRspcGenspc08(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc08(str));
			break;
		}
		case 9: {
			smartScript.setVFCCspcSRspcGenspc09(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc09(str));
			break;
		}
		case 10: {
			smartScript.setVFCCspcSRspcGenspc10(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc10(str));
			break;
		}
		case 11: {
			smartScript.setVFCCspcSRspcGenspc11(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc11(str));
			break;
		}
		case 12: {
			smartScript.setVFCCspcSRspcGenspc12(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc12(str));
			break;
		}
		case 13: {
			smartScript.setVFCCspcSRspcGenspc13(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc13(str));
			break;
		}
		case 14: {
			smartScript.setVFCCspcSRspcGenspc14(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc14(str));
			break;
		}
		case 15: {
			smartScript.setVFCCspcSRspcGenspc15(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc15(str));
			break;
		}
		case 16: {
			smartScript.setVFCCspcSRspcGenspc16(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc16(str));
			break;
		}
		case 17: {
			smartScript.setVFCCspcSRspcGenspc17(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc17(str));
			break;
		}
		case 18: {
			smartScript.setVFCCspcSRspcGenspc18(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc18(str));
			break;
		}
		case 19: {
			smartScript.setVFCCspcSRspcGenspc19(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc19(str));
			break;
		}
		case 20: {
			smartScript.setVFCCspcSRspcGenspc20(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc20(str));
			break;
		}
		case 21: {
			smartScript.setVFCCspcSRspcGenspc21(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc21(str));
			break;
		}
		case 22: {
			smartScript.setVFCCspcSRspcGenspc22(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc22(str));
			break;
		}
		case 23: {
			smartScript.setVFCCspcSRspcGenspc23(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc23(str));
			break;
		}
		case 24: {
			smartScript.setVFCCspcSRspcGenspc24(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc24(str));
			break;
		}
		case 25: {
			smartScript.setVFCCspcSRspcGenspc25(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc25(str));
			break;
		}
		case 26: {
			smartScript.setVFCCspcSRspcGenspc26(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc26(str));
			break;
		}
		case 27: {
			smartScript.setVFCCspcSRspcGenspc27(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc27(str));
			break;
		}
		case 28: {
			smartScript.setVFCCspcSRspcGenspc28(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc28(str));
			break;
		}
		case 29: {
			smartScript.setVFCCspcSRspcGenspc29(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc29(str));
			break;
		}
		case 30: {
			smartScript.setVFCCspcSRspcGenspc30(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc30(str));
			break;
		}
		case 31: {
			smartScript.setVFCCspcSRspcGenspc31(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc31(str));
			break;
		}
		case 32: {
			smartScript.setVFCCspcSRspcGenspc32(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc32(str));
			break;
		}
		case 33: {
			smartScript.setVFCCspcSRspcGenspc33(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc33(str));
			break;
		}
		case 34: {
			smartScript.setVFCCspcSRspcGenspc34(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc34(str));
			break;
		}
		case 35: {
			smartScript.setVFCCspcSRspcGenspc35(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc35(str));
			break;
		}
		case 36: {
			smartScript.setVFCCspcSRspcGenspc36(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc36(str));
			break;
		}
		case 37: {
			smartScript.setVFCCspcSRspcGenspc37(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc37(str));
			break;
		}
		case 38: {
			smartScript.setVFCCspcSRspcGenspc38(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc38(str));
			break;
		}
		case 39: {
			smartScript.setVFCCspcSRspcGenspc39(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc39(str));
			break;
		}
		case 40: {
			smartScript.setVFCCspcSRspcGenspc40(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc40(str));
			break;
		}
		case 41: {
			smartScript.setVFCCspcSRspcGenspc41(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc41(str));
			break;
		}
		case 42: {
			smartScript.setVFCCspcSRspcGenspc42(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc42(str));
			break;
		}
		case 43: {
			smartScript.setVFCCspcSRspcGenspc43(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc43(str));
			break;
		}
		case 44: {
			smartScript.setVFCCspcSRspcGenspc44(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc44(str));
			break;
		}
		case 45: {
			smartScript.setVFCCspcSRspcGenspc45(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc45(str));
			break;
		}
		case 46: {
			smartScript.setVFCCspcSRspcGenspc46(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc46(str));
			break;
		}
		case 47: {
			smartScript.setVFCCspcSRspcGenspc47(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc47(str));
			break;
		}
		case 48: {
			smartScript.setVFCCspcSRspcGenspc48(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc48(str));
			break;
		}
		case 49: {
			smartScript.setVFCCspcSRspcGenspc49(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc49(str));
			break;
		}
		case 50: {
			smartScript.setVFCCspcSRspcGenspc50(httpSRFactory.createSmartScriptVFCCspcSRspcGenspc50(str));
			break;
		}
		}
	}

	// request type = s for suspension and d for deactivate
	// return TibcoResponse Object
	private TIBCOresponse CallTibcoForProvRequest(ProvTrxInfo trxInfo, String authMSISDN,
			ProvRequestTypeName provTypeName) {
		String errcode;
		TIBCOresponse resp = new TIBCOresponse();
		provLogger.info(trxInfo.logInfo() + "Calling TIBCO with parameters[MSISDN: " + authMSISDN + ", provTypeName"
				+ provTypeName + "].");
		HttpURLConnection con = null;

		String urlIP = (String) Configs.TIBCO_CALL_URL_IP.getValue();
		String urlPort = (String) Configs.TIBCO_CALL_URL_PORT.getValue();
		String urlResource = (String) Configs.TIBCO_CALL_URL_RESOURCE.getValue();
		String tibcoUser = (String) Configs.TIBCO_CALL_BILLING_USERNAME.getValue();
		String tibcoPass = (String) Configs.TIBCO_CALL_BILLING_PASSWORD.getValue();
		String suspendReasonCode = (String) Configs.TIBCO_CALL_SUSPEND_REASON_CODE.getValue();
		String deactivateReasonCode = (String) Configs.TIBCO_CALL_DEACTIVATE_REASON_CODE.getValue();
		String suspendStatus = (String) Configs.TIBCO_CALL_SUSPEND_STATUS.getValue();
		String deactivateStatus = (String) Configs.TIBCO_CALL_DEACTIVATE_STATUS.getValue();
		int connectionTimeout = (int) Configs.TIBCO_CALL_CONNECTION_TIMEOUT.getValue();
		int requestTimeout = (int) Configs.TIBCO_CALL_REQUEST_TIMEOUT.getValue();
		String TIBCO_URL = "http://" + urlIP + ":" + urlPort + urlResource + "BSCSUser=" + tibcoUser + "&BSCSPass="
				+ tibcoPass + "&coID=" + "";

		if (provTypeName.equals(ProvRequestTypeName.SUSPEND_ACCOUNT))
			TIBCO_URL += "&status=" + suspendStatus;
		else if (provTypeName.equals(ProvRequestTypeName.DEACTIVATE_ACCOUNT))
			TIBCO_URL += "&status=" + deactivateStatus;

		TIBCO_URL += "&authMSISDN=" + "&custMSISDN=" + authMSISDN + "&DaysToReactivate=" + "";

		if (provTypeName.equals(ProvRequestTypeName.SUSPEND_ACCOUNT))
			TIBCO_URL += "&reasonCode=" + suspendReasonCode;
		else if (provTypeName.equals(ProvRequestTypeName.DEACTIVATE_ACCOUNT))
			TIBCO_URL += "&reasonCode=" + deactivateReasonCode;

		provLogger.info(trxInfo.logId() + "The calling URL: " + TIBCO_URL);

		URL url;
		try {

			url = new URL(TIBCO_URL);
			provLogger.debug(trxInfo.logId() + "The URL: " + url);
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(connectionTimeout);
			con.setReadTimeout(requestTimeout);
			// provLogger.debug(trxInfo.logId() + "The HttpURLConnection: " +
			// con);
			provLogger.debug(trxInfo.logId() + " Response code from Tibco: " + con.getResponseCode());

			// This is default.
			// con.setRequestMethod("GET");
			StringBuffer response = new StringBuffer();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// provLogger.info(trxInfo.logId() + " Response from Tibco: " +
				// con.getResponseCode());
				provLogger.info(trxInfo.logId() + " Tibco response : [" + response.toString() + "].");

				String result = response.toString();
				String[] parts = result.split("&");
				if (parts.length == 2) {
					String errorCode = parts[0];
					String errorMsg = parts[1];

					String[] errorcode = errorCode.split("=");
					errcode = errorcode[1];
					String[] msgError = errorMsg.split("=");
					String msg = msgError[1];
					//
					// System.out.println("error code:" + errcode);
					resp.setErrorCode(errcode);
					resp.setErrorMsg(msg);

					//
					// provLogger.info(trxInfo.logId() + " Tibco response : " +
					// response.toString());
				}

				else {
					provLogger.debug(trxInfo.logId() + " Can't parse response body");
					resp.setErrorCode("500");
					resp.setErrorMsg("Can't parse response body");

				}
			} else {
				provLogger.debug(trxInfo.logId() + " Faild to call Tibco");
				resp.setErrorCode("404");
				resp.setErrorMsg("Faild To call Tibco");
			}
		} catch (IOException e) {
			provLogger.error(trxInfo.logId() + e.getMessage(), e);

		} catch (Exception e) {
			provLogger.error(trxInfo.logId() + e.getMessage(), e);

		}

		return resp;
	}

	@Override
	public List<ProvRequestArch> findProvArchByAccount(AdminTrxInfo trxInfo, String accountId) throws DBException {
		try {
			provLogger.debug(trxInfo.logId() + "Getting archive provisioning request for accountId:(" + accountId
					+ " ).");
			List<ProvRequestArch> result = provRequestsArchDao.findProvRequestByAccountId(accountId);
			provLogger.debug(trxInfo.logId() + "found archive provisioning request for accountId:(" + accountId + " ) "
					+ result);
			return result;

		} catch (DBException e) {
			provLogger.error(trxInfo.logId() + "DBException while getting provRequestArch for accountID:(" + accountId
					+ " )", e);

			throw new DBException("DBException while getting provRequestArch for accountID :(" + accountId + " ).", e);
		}

	}
        
        private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.APP_PROV);
	}

	// @Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	// public void HandleInternalSuspendProvRequest(ProvTrxInfo trxInfo,
	// ProvisioningRequest provRequest) {
	// boolean rollBackFlag = false;
	// provLogger.info(trxInfo.logId() +
	// "recieving internal request to susbend account with id:"
	// + provRequest.getCompanyId());
	// try {
	// accountManagement.suspendAccount(trxInfo);
	//
	// provLogger.info(trxInfo.logId() +
	// "The account is suspended successfully");
	//
	// provLogger.debug(trxInfo.logId() + "Set provisioning request status to "
	// + ProvReqStatusName.SUCCESS);
	// provRequest.setStatus(ProvReqStatusName.SUCCESS);
	//
	// } catch (DBException e) {
	// provLogger.error(trxInfo.logId() + "Database error " + e);
	// provRequest.setStatus(ProvReqStatusName.FAIL);
	// } catch (AccountNotFoundException e) {
	// provLogger.error(trxInfo.logId() + e.getMessage());
	// provRequest.setStatus(ProvReqStatusName.FAIL);
	// } catch (InvalidAccountStateException e) {
	// provLogger.error(trxInfo.logId() + e.getMessage());
	// provRequest.setStatus(ProvReqStatusName.FAIL);
	// }
	// // call TIBCO for suspension
	// provLogger.info(trxInfo.logId() + "calling TIBCO for " +
	// provRequest.getRequestType() + ", Getting account("
	// + trxInfo.getAccountId() + ").");
	//
	// // calling CallTibcoForProvRequest
	// // set callBackStatus with response status
	// try {
	//
	// Account acct = accountManagement.findAccountById(trxInfo,
	// trxInfo.getAccountId());
	// provRequest.setMSISDN(acct.getBillingMsisdn());
	// // TODO: change log levels.
	// provLogger.info(trxInfo.logId() + "Found " + acct);
	//
	// TIBCOresponse response = CallTibcoForProvRequest(trxInfo,
	// acct.getBillingMsisdn(),
	// provRequest.getRequestType());
	// // TODO: change logs messages.
	// provLogger.info(trxInfo.logId() +
	// "Calling Tibco for susbend account with id" + trxInfo.getAccountId()
	// + " is finished with " + response.getErrorCode());
	// String errCode = response.getErrorCode();
	// switch (errCode) {
	// case "-2000":
	// case "0":
	// provLogger.info(trxInfo.logId() + " Tibco response for " +
	// provRequest.getRequestType().name()
	// + " account with id" + trxInfo.getAccountId() + " is " + errCode +
	// " with response msg "
	// + response.getErrorMsg());
	// break;
	// case "-100":
	// provLogger.info(trxInfo.logId() + " Tibco response for " +
	// provRequest.getRequestType().name()
	// + " account with id" + trxInfo.getAccountId() + " is " + errCode +
	// " with response msg "
	// + response.getErrorMsg());
	// rollBackFlag = true;
	//
	// provLogger.info(trxInfo.logId() + " Roll back is Set");
	// break;
	// case "-500":
	// provLogger.info(trxInfo.logId() + " Tibco response for " +
	// provRequest.getRequestType().name()
	// + " account with id" + trxInfo.getAccountId() + " is " + errCode +
	// " with response msg "
	// + response.getErrorMsg());
	// rollBackFlag = true;
	// provLogger.info(trxInfo.logId() + " Roll back is Set");
	//
	// break;
	// case "-502":
	// provLogger.info(trxInfo.logId() + " Tibco response for " +
	// provRequest.getRequestType().name()
	// + " account with id" + trxInfo.getAccountId() + " is " + errCode +
	// " with response msg "
	// + response.getErrorMsg());
	// rollBackFlag = true;
	// provLogger.info(trxInfo.logId() + " Roll back is Set");
	//
	// break;
	// case "-999":
	// provLogger.info(trxInfo.logId() + " Tibco response for " +
	// provRequest.getRequestType().name()
	// + " account with id" + trxInfo.getAccountId() + " is " + errCode +
	// " with response msg "
	// + response.getErrorMsg());
	// rollBackFlag = true;
	// provLogger.info(trxInfo.logId() + " Roll back is Set");
	//
	// break;
	// case "-998":
	// provLogger.info(trxInfo.logId() + " Tibco response for " +
	// provRequest.getRequestType().name()
	// + " account with id" + trxInfo.getAccountId() + " is " + errCode +
	// " with response msg "
	// + response.getErrorMsg());
	// rollBackFlag = true;
	// provLogger.info(trxInfo.logId() + " Roll back is Set");
	//
	// break;
	//
	// default:
	// provLogger.info(trxInfo.logId() +
	// " Tibco response for deactivate/suspend account with id"
	// + trxInfo.getAccountId() + " is " + errCode + " with response msg " +
	// response.getErrorMsg());
	// rollBackFlag = true;
	// provLogger.info(trxInfo.logId() + " Roll back is Set");
	//
	// }
	//
	// } catch (DBException e) {
	// provLogger.error(trxInfo.logId() + e.getMessage(), e);
	// } catch (AccountNotFoundException e) {
	// provLogger.error(trxInfo.logId() + e.getMessage(), e);
	// }
	//
	// if (rollBackFlag) {
	// context.setRollbackOnly();
	// provLogger.info(trxInfo.logId() + "Set Roll Back is called.");
	//
	// }
	// }
	//
	// }
}