package com.edafa.web2sms.sms;

import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NameNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountQuotaDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountSMSAPIDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountSenderDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSAPILogDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSLogDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.enums.CampaignTypeName;
import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountQuota;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.SMSAPIView;
import com.edafa.web2sms.dalayer.model.SMSLog;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.campaign.exception.CampaignTypeNotDefinedException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampListException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignException;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanLocal;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.list.exception.InvalidRequestException;
import com.edafa.web2sms.service.model.CampaignAggregationReport;
import com.edafa.web2sms.service.model.CampaignAggregationReportResult;
import com.edafa.web2sms.service.model.ContactModel;
import com.edafa.web2sms.service.model.SubmittedCampaignModel;
import com.edafa.web2sms.service.model.UserModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.sms.SMSAPI_Utility.DateValidationResult;
import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.enums.SMSResponseStatus;
import com.edafa.web2sms.sms.exceptions.AccountNotRegisteredOnAPIException;
import com.edafa.web2sms.sms.exceptions.AuthorizationFailedException;
import com.edafa.web2sms.sms.exceptions.NotTrustedIPException;
import com.edafa.web2sms.sms.exceptions.SecretKeyDecryptionFailedException;
import com.edafa.web2sms.sms.interfaces.SubmitSMSBeanRemote;
import com.edafa.web2sms.sms.model.CampaignRecieverDetails;
import com.edafa.web2sms.sms.model.EnquireCampaignByIdDetailedRequest;
import com.edafa.web2sms.sms.model.EnquireCampaignByIdResponse;
import com.edafa.web2sms.sms.model.EnquireSMSByIdListDetailedRequest;
import com.edafa.web2sms.sms.model.EnquireSMSByIdResponse;
import com.edafa.web2sms.sms.model.EnquireSMSByIdResponseList;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesAndMSISDNResponse;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesAndMSISDNResponseList;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesDetailedRequest;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesResponse;
import com.edafa.web2sms.sms.model.SMSDetails;
import com.edafa.web2sms.sms.model.SMSResponseBySmsId;
import com.edafa.web2sms.sms.model.SMSSubmitState;
import com.edafa.web2sms.sms.model.SmsDates;
import com.edafa.web2sms.sms.model.SmsDatesWithMSISDN;
import com.edafa.web2sms.sms.model.SubmitCampaignResponse;
import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;
import com.edafa.web2sms.sms.model.SubmitSMSResponse;
import com.edafa.web2sms.sms.model.SubmitSMSResponseBySmsId;
import com.edafa.web2sms.sms.utils.SubmitSMSBeanRemotePoolLocal;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.exception.ConfigValueNotSetException;
import com.edafa.web2sms.utils.security.AESLocal;
import com.edafa.web2sms.utils.security.interfaces.HashUtilsLocal;
import com.edafa.web2sms.utils.sms.MsisdnFormat;
import com.edafa.web2sms.utils.sms.SMSUtils;

import weblogic.servlet.utils.WSClients;


@Stateless
@LocalBean
@Path("/submit")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SMSAPI {

    private Logger appLogger;
    private Logger smsLogger;    

	@EJB
	AccountManegementFacingLocal accountManagement;
	
	@EJB
	AccountSMSAPIDaoLocal accountSMSAPIDao;

        @EJB
	AccountSenderDaoLocal accountSenderDao;
	
	@EJB
	CampaignManagementBeanLocal campManagementBean;

	@EJB
	WSClients portObj;
        
	@EJB
	AESLocal aes;

	@EJB
	HashUtilsLocal hu;
	
	@EJB
	private AccountQuotaDaoLocal acctQuotaDao;
        
        @EJB
        SubmitSMSBeanRemotePoolLocal submitSMSBeanRemotePool;

        @EJB
        AppErrorManagerAdapter appErrorManagerAdapter;
        
        @EJB
	private SMSAPI_Utility smsApiUtility;

	@EJB
	AccountStatusDaoLocal accountStatusDao;
        
	@EJB
	private SMSAPILogDaoLocal smsViewDao;
        
	@EJB
	private SMSLogDaoLocal smsLogDao;

    @PostConstruct
    public void init() {
        appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
        smsLogger = LogManager.getLogger(LoggersEnum.SMS_API_MNGT.name());
    }
        
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SubmitSMSResponse submitDetailedSMSRquest(@Context HttpServletRequest contextRequest, SubmitDetailedSMSRequest request) {
		String trxId =request.getTrxId() + " | ";
            String logId = request.logAccountId() + trxId;
            smsLogger.info(logId + "Received new DetailedSMSRequest.");
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + request);
            }
		SubmitSMSResponse response = new SubmitSMSResponse();
		Map<String,SMSResponseStatus> smsResponseMap;
		Map<String,SMSDetails> validSmsQuotaMap = new HashMap<String, SMSDetails>();
            Account account = null;
		try {
                    account = validateDetailedSMSRquest(logId, trxId, request);
		} catch (DBException e) {
                    smsLogger.error(logId + "DBException: " + e.getMessage());
                    appLogger.error(logId + "DBException", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//                    response.setDescription("Unable to Submit SMSRequest");
			return response;
		} catch (AccountNotFoundException e) {
                    smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (InvalidRequestException e) {
                    smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AccountNotRegisteredOnAPIException e) {
                    smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Not Registered On API");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (NotTrustedIPException e) {
                    smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Not Trusted IP");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AuthorizationFailedException e) {
                    smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Authorization Failed");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (SecretKeyDecryptionFailedException e) {
                    smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Secret Key Decryption Failed");
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//                    response.setDescription("Unable to Submit SMSRequest");
			return response;
		} catch (IneligibleAccountException e) {
                    smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
            } catch (Exception e) {
                    smsLogger.error(logId + "UnhandledException: " + e.getMessage());
                    appLogger.error(logId + "UnhandledException", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//                    response.setDescription("Unable to Submit SMSRequest");
                return response;
            }

		List<SMSDetails> validSMSList = new ArrayList<>();

		for (SMSDetails smsDetails : request.getSMSs()) {
                    if (!request.isCachedRequest() || request.isCachedRequest() && smsDetails.isCachedSMS()) {
			smsDetails.setAccountId(request.getAccountId());
                    }
		}
            // gomaa note: make this validation at the request handle beginning after getting the list of sender names.
            validSMSList = smsApiUtility.validateSMSList(logId, request.getSMSs(), account, request.getSmsIdPrefix(), request.isCachedRequest());

		if (!validSMSList.isEmpty()) {
			try {
                            List<SMSSubmitState> smsSubmitStateList = submitDetailedSMSRquest(logId, validSMSList, account, validSmsQuotaMap);
                            
                            smsResponseMap = new HashMap<String, SMSResponseStatus>(smsSubmitStateList.size());
				for(SMSSubmitState smsSubmitState : smsSubmitStateList)
				{
					smsResponseMap.put(smsSubmitState.getSmsId(), smsSubmitState.getSmsResponseStatus());
				}// end for
                                response.setResultStatus(checkSMSResponses(smsSubmitStateList));
				
                            commitSmsQuotaReservation(logId, validSmsQuotaMap, smsResponseMap, account);
			} catch (Exception e) {
                            if (request.isCachedRequest() && e.getCause() != null && (e.getCause() instanceof NoSuchObjectException || e.getCause() instanceof NameNotFoundException)) {
                                response.setResultStatus(ResultStatus.RESEND_CACHE_REQUEST_FAILED);
                            } else {
                                response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
                            }
				smsLogger.error(logId + "Unhandled Exception: " + e.getMessage() + ", return with status " + response.getResultStatus());
                                appLogger.error(logId + "Unhandled Exception: " + e.getMessage(), e);
                            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
//                            response.setDescription("Unable to Submit SMSRequest");
				
                            revertSmsQuotaReservation(logId, validSmsQuotaMap, account);
				return response;
                        }
                    if (response.getResultStatus() == null) {
                        response.setResultStatus(ResultStatus.SUCCESS);
                    }
                    ArrayList<SMSResponseStatus> smsStatusResp = new ArrayList<SMSResponseStatus>(smsResponseMap.values());
                    response.setSmsStatus(smsStatusResp);
                    smsLogger.info(logId + "Status of SMS submission: " + smsStatusResp);
			return response;
		} else {
                    //TODO .. raise alarm with high threshold like 20
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");                    
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("There is no valid SMS Details");
			smsLogger.warn(logId + "Invalid request: there is no valid SMS ");
			return response;
		}
	}// end of method submitDetailedSMSRquest

    private ResultStatus checkSMSResponses(List<SMSSubmitState> smsSubmitStateList) {
        for (SMSSubmitState smsSubmitState : smsSubmitStateList) {
            if (smsSubmitState.getSmsResponseStatus() == SMSResponseStatus.SYSTEM_FAILURE) {
                return ResultStatus.INTERNAL_SERVER_ERROR;
            }
            if (smsSubmitState.getSmsResponseStatus() == SMSResponseStatus.TIMMED_OUT || smsSubmitState.getSmsResponseStatus() == SMSResponseStatus.CACHE_LIMIT_EXCEEDED) {
                return ResultStatus.RESEND_CACHE_REQUEST_FAILED;
            }
        }// end for
        return ResultStatus.SUCCESS;
    }

    public Account validateDetailedSMSRquest(String logId, String trxId, SubmitDetailedSMSRequest request) throws AuthorizationFailedException, SecretKeyDecryptionFailedException, IneligibleAccountException, AccountNotFoundException, NotTrustedIPException, AccountNotRegisteredOnAPIException, DBException, InvalidRequestException {
        Account account;
        if (smsLogger.isTraceEnabled()) {
            smsLogger.trace(logId + "Checking account eligibility to: " + ActionName.SEND_SMS);
        }
        account = accountManagement.checkAccountEligibilitySMSAPI(trxId, request.getAccountId(), ActionName.SEND_SMS, (int) (Configs.SMSAPI_JPA_TIMEOUT.getValue()));

        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(logId + "Account is eligible");
        }
        // validateRequest(logId, request, account);
        smsApiUtility.validateRequest(logId, account, request.getPassword(), request.getIp());
        StringBuilder requestString = new StringBuilder("");
        requestString.append("AccountId=").append(request.getAccountId());
        requestString.append("&Password=").append(request.getPassword());
        for (int i = 0; i < request.getSMSs().size(); i++) {
            requestString.append("&SenderName=").append(request.getSMSs().get(i).getSenderName());
            requestString.append("&ReceiverMSISDN=").append(request.getSMSs().get(i).getReceiverMSISDN());
            requestString.append("&SMSText=").append(request.getSMSs().get(i).getSMSText());
        }
        smsApiUtility.checkHashing(logId, requestString, request.getSecureHash(), account, request.getPassword());
        return account;
    }

    private List<SMSSubmitState> submitDetailedSMSRquest(String logId, List<SMSDetails> validSMSList, Account account, Map<String, SMSDetails> validSmsQuotaMap) throws Exception {
        SubmitSMSBeanRemote submitSMSBean = null;
        List<SMSSubmitState> smsSubmitStateList;
        try{
        validateAndReserveSmsQuota(logId, validSMSList, account, validSmsQuotaMap);
        
        Set<String> rateLimitersIds = new HashSet<>();
        if (account.getSendingRateLimiters() != null && !account.getSendingRateLimiters().isEmpty()) {
            if (smsLogger.isTraceEnabled()) {
                smsLogger.trace(logId + "Sending rate limiters : " + account.getSendingRateLimiters());
            }

            for (SendingRateLimiter sendingRateLimiter : account.getSendingRateLimiters()) {
                if (sendingRateLimiter.isSmsapiEnabled()) {
                    rateLimitersIds.add(sendingRateLimiter.getLimiterId());
                }
            }
        }
        if (!rateLimitersIds.isEmpty()) {
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + "Sending rate limiters Ids" + rateLimitersIds);
            }
        } else {
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + "there are no active Sending rate limiters");
            }
        }

        for (SMSDetails validSmsDetails : validSmsQuotaMap.values()) {
            validSmsDetails.setRateLimitersIds(rateLimitersIds);
        }
        
        if (smsLogger.isTraceEnabled()) {
            smsLogger.trace(logId + "get SubmitSMSBeanRemote from pool");
        }
        submitSMSBean = submitSMSBeanRemotePool.getSubmitSMSBeanRemote();

        if (smsLogger.isTraceEnabled()) {
            smsLogger.trace(logId + "Calling SubmitSMSBeanRemote");
        }
            smsSubmitStateList = submitSMSBean.submitSMS(logId, new ArrayList<SMSDetails>(validSmsQuotaMap.values()));
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(logId + "Called SubmitSMSBeanRemote successfully, smsSubmitStateList=" + smsSubmitStateList);
        }
        } catch (Exception e) {
            throw e;
        } finally {
            if (submitSMSBean != null) {
                try {
                    submitSMSBeanRemotePool.returnSubmitSMSBeanRemote(submitSMSBean);
                } catch (Exception e) {
                    smsLogger.error(logId + "Failed to return SubmitSMSBeanRemote to pool " + e.getMessage());
                    appLogger.error(logId + "Failed to return SubmitSMSBeanRemote to pool", e);
                }
            }
        }
        return smsSubmitStateList;
    }
    
        @POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@Path("/Campaign")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public SubmitCampaignResponse submitDetailedCampaignRquest(@Context HttpServletRequest contextRequest,
			SubmitDetailedCampaignRequest request) {
		String trxId = request.getTrxId() + " | ";
        String logId = request.logAccountId() + trxId;

		SubmitCampaignResponse response = new SubmitCampaignResponse();
		AccountUser accountUser = null;
//        Account account;
            String campaignId;
		response.setResultStatus(ResultStatus.SUCCESS);
		smsLogger.info(logId + "Received new submitDetailedCampaignRquest.");
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + request);
            }
		try {
                    accountUser = submitDetailedCampaignRquestValidation(logId, trxId, request, appLogger, smsLogger);
		} catch (DBException e) {
			smsLogger.error(logId + "DBException: " + e.getMessage());
			appLogger.error(logId + "DBException: " + e.getMessage(), e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//			response.setDescription("Error");
			return response;
		} catch (AccountNotFoundException e) {
			smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (InvalidRequestException e) {
			smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AccountNotRegisteredOnAPIException e) {
			smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Not Registered On API");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (NotTrustedIPException e) {
			smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Not Trusted IP");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AuthorizationFailedException e) {
			smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Authorization Failed");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (SecretKeyDecryptionFailedException e) {
			smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Secret Key Decryption Failed");
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//			response.setDescription(e.getMessage());
			return response;
		} catch (UserNotFoundException e) {
			smsLogger.warn("UserNotFoundException while creating campaign with smsAPI");
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "User not found");
			response.setDescription(e.getMessage());
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			return response;
		} catch (IneligibleAccountException e) {
			smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
                    response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
		} catch (SenderNameNotAttached e) {
			smsLogger.warn(logId + "SenderNameNotAttached: " + e.getMessage());
                    reportAppError(AppErrors.INVALID_REQUEST, "Sender name not attached");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
            } catch (Exception e) {
                smsLogger.error(logId + "UnhandledException: " + e.getMessage());
                    appLogger.error(logId + "UnhandledException", e);
                reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//                response.setDescription("Error");
                return response;
            }

		CampaignRecieverDetails validSMSList = new CampaignRecieverDetails();
		validSMSList = smsApiUtility.validateMSISDNs(trxId, request.getMsisdns());

		if (!validSMSList.getReceiverMSISDN().isEmpty()) {
			try {
                            campaignId = submitDetailedCampaignRquestSubmition(logId, request, validSMSList, accountUser, null, appLogger, smsLogger);
			} catch (IneligibleAccountException e) {
				smsLogger.warn(logId + "Exception while creating campaign with smsAPI");
//				appLogger.error(" Exception while creating campaign with smsAPI ", e);
                            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
				response.setDescription(e.getMessage());
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
                            return response;
			} catch (DBException e) {
				smsLogger.error(logId +" DBException while creating campaign with smsAPI: " + e.getMessage());
				appLogger.error(logId +" Exception while creating campaign with smsAPI ", e);
                            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                            } else {
                                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                            }
                            response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//				response.setDescription("Error");
                            return response;
			} catch (InvalidCampaignException e) {
				smsLogger.warn(logId+"InvalidCampaignException while creating campaign with smsAPI");
                            reportAppError(AppErrors.INVALID_REQUEST, "Invalid campaign");
				response.setDescription(e.getMessage());
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
                            return response;
			} catch (InvalidCampListException e) {
				smsLogger.warn(logId+"InvalidCampListException while creating campaign with smsAPI");
                            reportAppError(AppErrors.INVALID_REQUEST, "Invalid campaign list");
				response.setDescription(e.getMessage());
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
                            return response;
			} catch (InsufficientQuotaException e) {
				smsLogger.warn(logId+"InsufficientQuotaException while creating campaign with smsAPI");
                            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Insufficient quota");
				response.setDescription(e.getMessage());
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
                            return response;
			} catch (NotPrePaidAccountException e) {
				smsLogger.warn(logId+"NotPrePaidAccountException while creating campaign with smsAPI");
                            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Not prepaid account");
//				response.setDescription(e.getMessage());
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
                            return response;
			} catch (AccountQuotaNotFoundException e) {
				smsLogger.warn(logId +"AccountQuotaNotFoundException while creating campaign with smsAPI");
                            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account quota not found");
//				response.setDescription(e.getMessage());
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
                            return response;
			} catch (CampaignTypeNotDefinedException e) {
				smsLogger.warn(logId+"CampaignTypeNotDefinedException while creating campaign with smsAPI");
                            reportAppError(AppErrors.INVALID_OPERATION, "Campaign type not defined");
				response.setDescription(e.getMessage());
				response.setResultStatus(ResultStatus.GENERIC_ERROR);
                            return response;
			} catch (Exception e) {
				smsLogger.error(logId +"Exception while creating campaign with smsAPI, " +e.getMessage());
				appLogger.error(logId+" Exception while creating campaign with smsAPI ", e);
                            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
//				response.setDescription("Error");
				response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
                            return response;
                    }
		} else {
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("There is no valid SMS Details");
			smsLogger.info(logId + "Invalid request: there is no valid SMS ");
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			return response;
		}
		response.setCampaignId(campaignId);
			smsLogger.info(logId + "response: " + response); 
		return response;
	}// end of method submitDetailedSMSRquest

    public AccountUser submitDetailedCampaignRquestValidation(String logId, String trxId, SubmitDetailedCampaignRequest request, Logger appLogger, Logger smsLogger) 
            throws AccountNotFoundException, UserNotFoundException, IneligibleAccountException, SecretKeyDecryptionFailedException, SenderNameNotAttached, InvalidRequestException, AuthorizationFailedException, AccountNotRegisteredOnAPIException, DBException, NotTrustedIPException {
        Account account;
        AccountUser accountUser = null;
        if (smsLogger.isTraceEnabled()) {
            smsLogger.trace(logId + "checking account eligibility to: [" + ActionName.SEND_SMS + "]");
        }

        account = accountManagement.checkAccountEligibilitySMSAPICamp(trxId, request.getAccountId(), ActionName.SEND_SMS, (int) (Configs.SMSAPI_JPA_TIMEOUT.getValue()));
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(logId + "account is eligible");
        }
        smsApiUtility.validateRequest(logId, account, request.getPassword(), request.getIp());
        
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(logId + "find admin user from (" + account.getAccountUsers().size() + ")users");
        }
        for (AccountUser accUser : account.getAccountUsers()) {
            if (accUser.getAdminRoleFlag() == true 
                && accUser.getStatus()
                .getAccountStatusName()                        
                .equals(AccountStatusName.ACTIVE)) {
                accountUser = accUser;
            }
        }
        if (accountUser == null) {
            throw new UserNotFoundException();
        }

        StringBuilder requestString = new StringBuilder("");
        requestString.append("AccountId=").append(request.getAccountId());
        requestString.append("&Password=").append(request.getPassword());
        requestString.append("&SenderName=").append(request.getSenderName());
        for (int i = 0; i < request.getMsisdns().getReceiverMSISDN().size(); i++) {
            requestString.append("&ReceiverMSISDN=").append(request.getMsisdns().getReceiverMSISDN().get(i));
        }
        requestString.append("&SMSText=").append(request.getSMSText());
        requestString.append("&CampaignName=").append(request.getCampaignName());
        smsApiUtility.checkHashing(trxId, requestString, request.getSecureHash(), account, request.getPassword());
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(logId + "check sender name ");
        }
        smsApiUtility.validateSenderName(trxId, request.getSenderName(), account);
        if (smsLogger.isTraceEnabled()) {
            smsLogger.trace(logId + "sender name is valid");
        }
        return accountUser;
    }

    public String submitDetailedCampaignRquestSubmition(String logId, SubmitDetailedCampaignRequest request, CampaignRecieverDetails validSMSList, AccountUser accountUser, CampaignStatusName campaignStatusName, Logger appLogger, Logger smsLogger)
            throws InvalidCampListException, InvalidCampaignException, InsufficientQuotaException, IneligibleAccountException, CampaignTypeNotDefinedException, UserNotFoundException, DBException, AccountQuotaNotFoundException, ConfigValueNotSetException, NotPrePaidAccountException {
        //TODO .. handle (InsufficientQuotaException, AccountQuotaNotFoundException) after caching.
        String campaignId = request.getCampaignId();
        SubmittedCampaignModel campModel = new SubmittedCampaignModel();
        List<ContactModel> individualContacts = new ArrayList<ContactModel>();
        for (int i = 0; i < validSMSList.getReceiverMSISDN().size(); i++) {
            ContactModel temp = new ContactModel(validSMSList.getReceiverMSISDN().get(i));
            individualContacts.add(temp);
        }
        campModel.setIndividualContacts(individualContacts);
        campModel.setCampaignName(request.getCampaignName());
        if (request.setLanguage() == "ENGLISH") {
            campModel.setLanguage(LanguageNameEnum.ENGLISH);
        } else {
            campModel.setLanguage(LanguageNameEnum.ARABIC);
        }
        if (campaignId != null) {
            campModel.setCampaignId(campaignId);
        }
        campModel.setSmsText(request.getSMSText());
        campModel.setSenderName(request.getSenderName());
        campModel.setCampaignType(CampaignTypeName.API_CAMPAIGN);
        campModel.setRegisteredDelivery((boolean) Configs.REGESTERED_DELIVERY.getValue());
        campModel.setScheduleFrequency(ScheduleFrequencyName.ONCE);
        campModel.setScheduleStartTimestamp(new Date());
        campModel.setScheduleStopTime(null);
        campModel.setScheduleEndTimestamp(null);
        campModel.setScheduledFlag(false);
        UserTrxInfo userInfo = new UserTrxInfo();
        userInfo.setTrxId(request.getTrxId());
        userInfo.addUserAction(ActionName.CREATE_CAMPAIGN);
        UserModel usrmodel = new UserModel();
        usrmodel.setAccountId(accountUser.getAccountId());
        usrmodel.setUsername(accountUser.getUsername());
        userInfo.setUser(usrmodel);
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(logId + "call management bean to create campaign with: [" + campModel + "].");
        }
        campManagementBean.createCampaign(userInfo, campModel, campaignStatusName);
        return campModel.getCampaignId();
    }
    
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@Path("/CampaignInquiry")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public EnquireCampaignByIdResponse submitDetailedCampaignInquiryRquest(@Context HttpServletRequest contextRequest,
			EnquireCampaignByIdDetailedRequest request) {
		String trxId = request.getTrxId() + " | ";
        String logId = request.logAccountId() + trxId;
        EnquireCampaignByIdResponse response = new EnquireCampaignByIdResponse();
		CampaignAggregationReport result = new CampaignAggregationReport();
		AccountUser accountUser = null;
                Account account;
		response.setResultStatus(ResultStatus.SUCCESS);
		smsLogger.info(logId + "Received new submitDetailedCampaignInquiryRquest.");
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + request);
            }
		try {
			if(smsLogger.isDebugEnabled()){
			smsLogger.debug(logId + "checking account eligibility to: [" + ActionName.VIEW_REPORTS + "]");
			}

			account = accountManagement.checkAccountEligibilitySMSAPICamp(logId, request.getAccountId(), ActionName.VIEW_REPORTS, (int) (Configs.SMSAPI_JPA_TIMEOUT.getValue()));
			if(smsLogger.isDebugEnabled()){
			smsLogger.debug(logId + "account is eligible to VIEW REPORT");
			}
			smsApiUtility.validateRequest(logId, account, request.getPassword(), request.getIp());
                    if (((boolean) Configs.SMSAPI_ENABLE_REPORT_TIME_INTERVAL.getValue()) == true && !smsApiUtility.checkPassingReportTimeInterval(logId, request.getAccountId())) {
                        smsLogger.warn(logId + "Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        // reportAppError(AppErrors.INELIGIBLE_ACCOUNT, " Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " seconds");
                        response.setResultStatus(ResultStatus.INVALID_REQUEST);
                        response.setDescription("Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        return response;
                    }
                        if(smsLogger.isTraceEnabled())
			smsLogger.trace(logId + "Getting account's admin user");
                        for (AccountUser accUser : account.getAccountUsers()) {
                        if (accUser.getAdminRoleFlag() == true && accUser.getStatus().getAccountStatusName().equals(AccountStatusName.ACTIVE)) {
                            accountUser = accUser;
                        }
                            }
			if (accountUser == null)
				throw new UserNotFoundException();

                    StringBuilder requestString = new StringBuilder("");
                    requestString.append("AccountId=").append(request.getAccountId());
                    requestString.append("&CampaignId=").append(request.getCampaignId());
                    requestString.append("&Password=").append(request.getPassword());
                        smsApiUtility.checkHashing(logId, requestString, request.getSecureHash(), account, request.getPassword());
                               
                } catch (DBException e) {
			smsLogger.error(logId + "DBException: " + e.getMessage());
			appLogger.error(logId + "DBException: " + e.getMessage(), e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the campaigns");
			return response;

		} catch (AccountNotFoundException e) {
			smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
//			appLogger.error(trxId + "AccountNotFoundException: " + e.getMessage(), e);
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (InvalidRequestException e) {
			smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
//			appLogger.error(trxId + "InvalidRequestException: " + e.getMessage(), e);
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (AccountNotRegisteredOnAPIException e) {
			smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
//			appLogger.error(trxId + "AccountNotRegisteredOnAPIException: " + e.getMessage(), e);
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Not Registered On API");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (NotTrustedIPException e) {
			smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
//			appLogger.error(trxId + "NotTrustedIPException: " + e.getMessage(), e);
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Not Trusted IP");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AuthorizationFailedException e) {
			smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
//			appLogger.error(trxId + "AuthorizationFailedException: " + e.getMessage(), e);
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Authorization Failed");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (SecretKeyDecryptionFailedException e) {
			smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
//			appLogger.error(trxId + "SecretKeyDecryptionFailedException: " + e.getMessage(), e);
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Secret Key Decryption Failed");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the campaigns");
			return response;

		} catch (IneligibleAccountException e) {
			smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
//			appLogger.error(trxId + "IneligibleAccountException: " + e.getMessage(), e);
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
		} catch (UserNotFoundException e) {
			smsLogger.warn(logId + "UserNotFoundException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "User Not Found");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
            } catch (Exception e) {
                smsLogger.error(logId + "Exception: " + e.getMessage());
                    appLogger.error(logId + "UnhandledException", e);
                reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
                response.setResultStatus(ResultStatus.GENERIC_ERROR);
                response.setDescription("Unable to inquire about the campaigns");
                return response;
            }
		UserTrxInfo userInfo = new UserTrxInfo();
		userInfo.setTrxId(trxId);
		userInfo.addUserAction(ActionName.VIEW_REPORTS);
		UserModel usrmodel = new UserModel();
		usrmodel.setAccountId(accountUser.getAccountId());
		usrmodel.setUsername(accountUser.getUsername());
		userInfo.setUser(usrmodel);

		try {
			if(smsLogger.isTraceEnabled())
				smsLogger.trace(logId + "calling report management bean" );
			
			CampaignAggregationReportResult campaignAggregationReportResult = portObj.getReportServicePort()
					.getReportByCampId(userInfo, request.getCampaignId());
			if (campaignAggregationReportResult.getStatus() == ResponseStatus.SUCCESS) {
				result = campaignAggregationReportResult.getCampaignAggregationReport();

				if (smsLogger.isDebugEnabled())
					smsLogger.debug(logId + "result returned from report management service: " + result);
				if (result != null) {
					response.setNumOfDeliveredSeg(result.getDeliverdSMSSegCount());
					response.setNumOfUnDeliveredSeg(result.getUnDeliverdSMSCount());
					response.setCampaignStatus(result.getCampaignStatus().getStatusName());
					if (result.getSmsCount() != 0)
						response.setSubmittedRatio(
								(result.getSubmittedSMSCount() / (double) result.getSmsCount()) * 100);
					else
						smsLogger.warn(logId + "sms count equals zero");
				}
			} else {
				smsLogger.warn(logId + "Campaign report error [" + request.getCampaignId() + "], ResponseStatus="
						+ campaignAggregationReportResult.getStatus());
				//				appLogger.error("No Report found for this campaign [" + request.getCampaignId() + "] ",e);
				reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
				response.setDescription(campaignAggregationReportResult.getErrorMessage());
				response.setResultStatus(ResultStatus.SUCCESS);
			}
		} catch (Exception e) {
			smsLogger.error(logId +"Exception while inquiring campaign [" + request.getCampaignId() + "] with smsAPI");
			appLogger.error(logId +"Exception while inquiring campaign [" + request.getCampaignId() + "] with smsAPI",e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
                        response.setDescription(e.getMessage());
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
		}
		smsLogger.info(logId + "response: " + response); 

		return response;
	}// end of method 

	/**
	 * Web Method with new Response include(SMSID) :
	 * 
	 * This Method Send SMS and return response of SMS Details info. including
	 * each SMS ID
	 */

	@Path("/submitAndgetSmsId")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SubmitSMSResponseBySmsId submitSmsRequestAndGetSmsId(@Context HttpServletRequest contextRequest, SubmitDetailedSMSRequest request) {
		String trxId = request.getTrxId() + " | ";
            String logId = request.logAccountId() + trxId;
            smsLogger.info(logId + "Received new SmsRequestAndGetSmsId.");
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + request);
            }
            SubmitSMSResponseBySmsId response = new SubmitSMSResponseBySmsId();

            Map<String, SMSResponseStatus> smsResponseMap;
		Map<String, SMSDetails> validSmsQuotaMap = new HashMap<String, SMSDetails>();
            List<SMSResponseBySmsId> smsStatusList;
            Account account;
		try {
                    account = validateDetailedSMSRquest(logId, trxId, request);
		} catch (DBException e) {
                    smsLogger.error(logId + "DBException: " + e.getMessage());
                    appLogger.error(logId + "DBException", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//                    response.setDescription("Error");
			return response;
		} catch (AccountNotFoundException e) {
                    smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (InvalidRequestException e) {
                    smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AccountNotRegisteredOnAPIException e) {
                    smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Not Registered On API");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (NotTrustedIPException e) {
                    smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Not Trusted IP");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AuthorizationFailedException e) {
                    smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Authorization Failed");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (SecretKeyDecryptionFailedException e) {
                    smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Secret Key Decryption Failed");
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//			response.setDescription(e.getMessage());
			return response;
		} catch (IneligibleAccountException e) {
                    smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
            } catch (Exception e) {
                    smsLogger.error(logId + "Exception: " + e.getMessage());
                    appLogger.error(logId + "UnhandledException", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");  
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//                    response.setDescription("Error");
                return response;
            }

		List<SMSDetails> validSMSList = new ArrayList<>();

		for (SMSDetails smsDetails : request.getSMSs()) {
                    if (!request.isCachedRequest() || request.isCachedRequest() && smsDetails.isCachedSMS()) {
			smsDetails.setAccountId(request.getAccountId());
                    }
		}
            validSMSList = smsApiUtility.validateSMSList(logId, request.getSMSs(), account, request.getSmsIdPrefix(), request.isCachedRequest());

		if (!validSMSList.isEmpty()) {
			try {
                            List<SMSSubmitState> smsSubmitStateList = submitDetailedSMSRquest(logId, validSMSList, account, validSmsQuotaMap);
                            if(smsLogger.isTraceEnabled()){
                                smsLogger.trace(logId + "Will generate response, smsSubmitStateList=" + smsSubmitStateList
                                        + ", validSmsQuotaMap.key=" + validSmsQuotaMap.keySet() + ", validSmsQuotaMap.values=" + validSmsQuotaMap.values());
                            }
                            
                            smsResponseMap = new HashMap<String, SMSResponseStatus>(smsSubmitStateList.size());
                            smsStatusList = new ArrayList<SMSResponseBySmsId>(smsSubmitStateList.size());
                            for (SMSSubmitState smsSubmitState : smsSubmitStateList) {
                                smsStatusList.add(new SMSResponseBySmsId(validSmsQuotaMap.get(smsSubmitState.getSmsId()).getReceiverMSISDN(),
                                        smsSubmitState.getSmsId(), smsSubmitState.getSmsResponseStatus()));
                                smsResponseMap.put(smsSubmitState.getSmsId(), smsSubmitState.getSmsResponseStatus());
                            }
                            response.setResultStatus(checkSMSResponses(smsSubmitStateList));
                            
                            commitSmsQuotaReservation(logId, validSmsQuotaMap, smsResponseMap, account);
			} catch (Exception e) {
				smsLogger.error(logId + "Unhandled Exception: " + e.getMessage());
                            appLogger.error(logId + "Unhandled Exception: " + e.getMessage(), e);
                            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
                            response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
//                            response.setDescription("Error");
				
                            revertSmsQuotaReservation(logId, validSmsQuotaMap, account);
				return response;
                        }
                    if (response.getResultStatus() == null) {
                        response.setResultStatus(ResultStatus.SUCCESS);
                    }
			response.setsmsResponseList(smsStatusList);
                    smsLogger.info(logId + "Status of SMS submission: " + smsStatusList);
			return response;
		} else {
                    //TODO .. raise alarm with high threshold like 20
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");                    
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription("There is no valid SMS Details");
			smsLogger.warn(logId + "Invalid request: there is no valid SMS ");
			return response;
		}
	}// end of method submitSmsRequestAndGetSmsId

	/**S
	 * Web Method to inquire with SMSID
	 * 
	 * This Method inquire in the DataBase for SMS's with this SMSid
	 */
    /**
	@Path("/inquireBySmsId")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public EnquireSMSByIdResponse inquireWithSmsId(@Context HttpServletRequest contextRequest,
			EnquireSMSByIdDetailedRequest request) {
            String trxId = request.getTrxId() + " | ";
            String logId = request.logAccountId() + trxId;
            smsLogger.info(logId + "Received new inquireWithSmsId request.");
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + request);
            }
            
                Account account;
                EnquireSMSByIdResponse response = new EnquireSMSByIdResponse();
		List<EnquireSMSByIdResponseList> smsStatusnewList = new ArrayList<EnquireSMSByIdResponseList>();
		// check for account eligibility
		try {
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace(logId + "Checking account eligibility to: " + ActionName.INQUIRY_SMS_DATES);
                    }
                    account = accountManagementBean.checkAccountEligibilitySMSAPI(trxId, request.getAccountId(), ActionName.INQUIRY_SMS_DATES, (int) (Configs.SMSAPI_JPA_TIMEOUT.getValue()));

                    if (((boolean) Configs.SMSAPI_ENABLE_REPORT_TIME_INTERVAL.getValue()) == true && !smsApiUtility.checkPassingReportTimeInterval(logId, request.getAccountId())) {
                        smsLogger.warn(logId + "Can't request more than one rseport within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        // reportAppError(AppErrors.INELIGIBLE_ACCOUNT, " Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " seconds");
                        response.setResultStatus(ResultStatus.INVALID_REQUEST);
                        response.setDescription("Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        return response;
                    }
                    if(smsLogger.isDebugEnabled()){
                        smsLogger.debug(logId + "Account is eligible");
                    }
                    
			smsApiUtility.validateRequest(logId, account, request.getPassword(), request.getIp());

                    StringBuilder requestString = new StringBuilder("");
                    requestString.append("AccountId=").append(request.getAccountId());
                    requestString.append("&Password=").append(request.getPassword());
                    requestString.append("&smsId=").append(request.getSmsId());
			smsApiUtility.checkHashing(logId, requestString, request.getSecureHash(), account, request.getPassword());
                    // validate on smsId
                    smsApiUtility.validateSmsId(request.getSmsId());
		} catch (DBException e) {
                    smsLogger.error(logId + "DBException: " + e.getMessage());
                    appLogger.error(logId + "DBException", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
                    response.setDescription("Unable to inquire about the SMSs");
			return response;
		} catch (AccountNotFoundException e) {
                    smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (InvalidRequestException e) {
                    smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AccountNotRegisteredOnAPIException e) {
                    smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Not Registered On API");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (NotTrustedIPException e) {
                    smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Not Trusted IP");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AuthorizationFailedException e) {
                    smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Authorization Failed");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (SecretKeyDecryptionFailedException e) {
                    smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Secret Key Decryption Failed");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs");
			return response;
		} catch (IneligibleAccountException e) {
                    smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
            } catch (Exception e) {
                    smsLogger.error(logId + "Exception: " + e.getMessage());
                    appLogger.error(logId + "UnhandledException", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");                    
                    response.setResultStatus(ResultStatus.GENERIC_ERROR);
                    response.setDescription("Unable to inquire about the SMSs");
                return response;
            }

		try {
                    if (smsLogger.isDebugEnabled()) {
                        smsLogger.debug(logId + "Inquery with SMS Id : " + request.getSmsId());
                    }
			SMSAPIView smsLog = (SMSAPIView) smsViewDao.find(request.getSmsId());
                    if (smsLog == null) {
                        smsLogger.info(logId + "There is no such SMS with Id: " + request.getSmsId());
				response.setResultStatus(ResultStatus.SUCCESS);
				response.setDescription("There is no such SMS with this Id");
				return response;
			} else {
                        if (smsLogger.isDebugEnabled()) {
                            smsLogger.debug(logId + "Found SMS : " + smsLog.toString());
                        }
                        EnquireSMSByIdResponseList smsResponse = new EnquireSMSByIdResponseList();
				smsResponse.setSmsId(smsLog.getSmsId());
                        smsResponse.setSmsStatus(smsLog.getSMSStatus().getName() == null ? " " : smsLog.getSMSStatus().getName().name());
                        smsResponse.setReciever(smsLog.getReceiver() == null ? " " : smsLog.getReceiver());
                        smsResponse.setDeliveryDate(smsLog.getDeliveryDate() == null ? " " : smsLog.getDeliveryDate().toString());
                        smsResponse.setSendDate(smsLog.getSendReceiveDate() == null ? " " : smsLog.getSendReceiveDate().toString());
                        smsResponse.setRecievedDate(smsLog.getSubmitDate() == null ? " " : smsLog.getSubmitDate().toString());
				smsStatusnewList.add(smsResponse);
			} // end of else
		} catch (DBException e) {
                    smsLogger.error(logId + "DBException: " + e.getMessage());
                    appLogger.error(logId + "DBException", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs");
			return response;
		} catch (Exception e) {
                    smsLogger.error(logId + "Exception: " + e.getMessage());
                    appLogger.error(logId + "UnhandledException", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");                    
                    response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs");
                return response;
            }// end of catch
            smsLogger.info(logId + "Response = " + smsStatusnewList);
		response.setResultStatus(ResultStatus.SUCCESS);
		response.setSmsStatusWithSmsId(smsStatusnewList);
		return response;
	}// end of method inquireWithSmsId
*/
        @Path("/inquireBySmsId")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
        public EnquireSMSByIdResponse inquireWithSmsIdList(@Context HttpServletRequest contextRequest,
			EnquireSMSByIdListDetailedRequest request) {
            String trxId = request.getTrxId() + " | ";
            String logId = request.logAccountId() + trxId;
            smsLogger.info(logId + "Received new inquireWithSmsIdList request.");
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + request);
            }
            
                Account account;
                EnquireSMSByIdResponse response = new EnquireSMSByIdResponse();
		List<EnquireSMSByIdResponseList> smsStatusnewList = new ArrayList<EnquireSMSByIdResponseList>();
		// check for account eligibility
		try {
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace(logId + "Checking account eligibility to: " + ActionName.INQUIRY_SMS_DATES);
                    }

                    account = accountManagement.checkAccountEligibilitySMSAPI(trxId, request.getAccountId(), ActionName.INQUIRY_SMS_DATES, (int) (Configs.SMSAPI_JPA_TIMEOUT.getValue()));

                    if (((boolean) Configs.SMSAPI_ENABLE_REPORT_TIME_INTERVAL.getValue()) == true && !smsApiUtility.checkPassingReportTimeInterval(logId, request.getAccountId())) {
                        smsLogger.warn(logId + "Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        // reportAppError(AppErrors.INELIGIBLE_ACCOUNT, " Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " seconds");
                        response.setResultStatus(ResultStatus.INVALID_REQUEST);
                        response.setDescription("Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        return response;
                    }
                    if(smsLogger.isDebugEnabled()){
                        smsLogger.debug(logId + "Account is eligible");
                    }
                    
			smsApiUtility.validateRequest(logId, account, request.getPassword(), request.getIp());

                    StringBuilder requestString = new StringBuilder("");
                    requestString.append("AccountId=").append(request.getAccountId());
                    requestString.append("&Password=").append(request.getPassword());                   
                    for (int i = 0; i < request.getSmsIdList().size(); i++) {
                        requestString.append("&smsId=").append(request.getSmsIdList().get(i));
                    }                    
		    smsApiUtility.checkHashing(logId, requestString, request.getSecureHash(), account, request.getPassword());
		} catch (DBException e) {
                    smsLogger.error(logId + "DBException: " + e.getMessage());
                    appLogger.error(logId + "DBException", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
                    response.setDescription("Error");
			return response;
		} catch (AccountNotFoundException e) {
                    smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (InvalidRequestException e) {
                    smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AccountNotRegisteredOnAPIException e) {
                    smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Not Registered On API");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (NotTrustedIPException e) {
                    smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Not Trusted IP");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AuthorizationFailedException e) {
                    smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Authorization Failed");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (SecretKeyDecryptionFailedException e) {
                    smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Secret Key Decryption Failed");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
		} catch (IneligibleAccountException e) {
                    smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
            } catch (Exception e) {
                    smsLogger.error(logId + "Exception: " + e.getMessage());
                    appLogger.error(logId + "UnhandledException", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");                    
                    response.setResultStatus(ResultStatus.GENERIC_ERROR);
                    response.setDescription("Error");
                return response;
            }

		try {
                    if (smsLogger.isDebugEnabled()) {
                        smsLogger.debug(logId + "Inquery with SMS Id List : " + request.getSmsIdList());
                    }
			List<SMSLog> smsLog = smsLogDao.findInIdList(request.getSmsIdList());

                    if (smsLog == null || smsLog.isEmpty()) {
                        smsLogger.info(logId + "There is no such SMS with Id: " + request.getSmsIdList());
				response.setResultStatus(ResultStatus.SUCCESS);
				response.setDescription("There is no such SMS in this Id list");
				return response;
			} else {
                        if (smsLogger.isDebugEnabled()) {
                            smsLogger.debug(logId + "Found SMS : " + smsLog.toString());
                        }
                        for (int i = 0; i < smsLog.size(); i++) {
                            EnquireSMSByIdResponseList smsResponse = new EnquireSMSByIdResponseList();
                            smsResponse.setSmsId(smsLog.get(i).getSmsId());
                            smsResponse.setSmsStatus(smsLog.get(i).getSMSStatus().getName() == null ? " " : smsLog.get(i).getSMSStatus().getName().name());
                            smsResponse.setReciever(smsLog.get(i).getReceiver() == null ? " " : smsLog.get(i).getReceiver());
                            smsResponse.setDeliveryDate(smsLog.get(i).getDeliveryDate() == null ? " " : smsLog.get(i).getDeliveryDate().toString());
                            smsResponse.setSendDate(smsLog.get(i).getSendReceiveDate() == null ? " " : smsLog.get(i).getSendReceiveDate().toString());
                            smsResponse.setRecievedDate(smsLog.get(i).getSubmitDate() == null ? " " : smsLog.get(i).getSubmitDate().toString());
                            smsStatusnewList.add(smsResponse);
                        }
			} // end of else
		} catch (DBException e) {
                    smsLogger.error(logId + "DBException: " + e.getMessage());
                    appLogger.error(logId + "DBException", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Error");
			return response;
		} catch (Exception e) {
                    smsLogger.error(logId + "Exception: " + e.getMessage());
                    appLogger.error(logId + "UnhandledException", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");                    
                    response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Error");
                return response;
            }// end of catch
            smsLogger.info(logId + "Response = " + smsStatusnewList);
		response.setResultStatus(ResultStatus.SUCCESS);
		response.setSmsStatusWithSmsId(smsStatusnewList);
		return response;
	}// end of method inquireWithSmsId

	/**
	 * Web Method to inquire with start, end date, receiver msisdn [up to 100
	 * sms with this smsID]
	 * 
	 * this Method get from DB all the SMS's for this MSISDN in this Period from
	 * [Start Date , End Date]
	 */

	@Path("/inquireByRecieverAndDate")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public EnquireSMSsByDatesAndMSISDNResponse inquireByDateAndReciever(@Context HttpServletRequest contextRequest,
			EnquireSMSsByDatesDetailedRequest request) {
                Account account;
		String trxId = request.getTrxId() + " | ";
        String logId = request.logAccountId() + trxId;
        
        DateValidationResult dateValidationResult= new DateValidationResult();

		EnquireSMSsByDatesAndMSISDNResponse response = new EnquireSMSsByDatesAndMSISDNResponse();
		EnquireSMSsByDatesAndMSISDNResponseList smsResponse = new EnquireSMSsByDatesAndMSISDNResponseList();
		List<SmsDates> smsList = new ArrayList<SmsDates>();
		smsLogger.info(logId + "Received new inquireByDateAndReciever request ");
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + request);
            }
		// check for account eligibility
		try {                   
			if(smsLogger.isDebugEnabled())

			smsLogger.debug(logId + "checking account eligibility to: " + ActionName.INQUIRY_SMS_DATES);
			account = accountManagement.checkAccountEligibilitySMSAPI(logId, request.getAccountId(), ActionName.INQUIRY_SMS_DATES, (int) (Configs.SMSAPI_JPA_TIMEOUT.getValue()));
                    if (((boolean)Configs.SMSAPI_ENABLE_REPORT_TIME_INTERVAL.getValue()) == true && !smsApiUtility.checkPassingReportTimeInterval(logId, request.getAccountId())) {
                        smsLogger.warn(logId + "Can't request more than one rseport within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        // reportAppError(AppErrors.INELIGIBLE_ACCOUNT, " Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " seconds");
                        response.setResultStatus(ResultStatus.INVALID_REQUEST);
                        response.setDescription("Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        return response;
                    }

                        if(smsLogger.isTraceEnabled())
                            smsLogger.trace(logId + "account is eligible"); 
                        
			smsApiUtility.validateRequest(logId, account, request.getPassword(), request.getIp());

//			if(smsLogger.isTraceEnabled())
//				smsLogger.trace(logId + "Check hashing");
				
                    StringBuilder requestString = new StringBuilder("");
                    requestString.append("AccountId=").append(request.getAccountId());
                    requestString.append("&Password=").append(request.getPassword());
                    requestString.append("&ReceiverMSISDN=").append(request.getReceiverMSISDN());
                    requestString.append("&StartDate=").append(request.getStartDate());
                    requestString.append("&EndDate=").append(request.getEndDate());
			smsApiUtility.checkHashing(logId, requestString, request.getSecureHash(), account, request.getPassword());
			
			dateValidationResult =smsApiUtility.validateDates(logId, request.getStartDate(), request.getEndDate());
		
			
		} catch (DBException e) {
			smsLogger.error(logId + "DBException: " + e.getMessage());
			appLogger.error(logId + "DBException: " + e.getMessage(), e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs by date and reciever");
			return response;

		} catch (AccountNotFoundException e) {
			smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
//			appLogger.error(logId + "AccountNotFoundException: " + e.getMessage(), e);
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (InvalidRequestException e) {
			smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
//			appLogger.error(logId + "InvalidRequestException: " + e.getMessage(), e);
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (AccountNotRegisteredOnAPIException e) {
			smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
//			appLogger.error(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage(), e);
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Not Registered On API");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (NotTrustedIPException e) {
			smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
//			appLogger.error(logId + "NotTrustedIPException: " + e.getMessage(), e);
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Not Trusted IP");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AuthorizationFailedException e) {
			smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
//			appLogger.error(logId + "AuthorizationFailedException: " + e.getMessage(), e);
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Authorization Failed");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (SecretKeyDecryptionFailedException e) {
			smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
//			appLogger.error(logId + "SecretKeyDecryptionFailedException: " + e.getMessage(), e);
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Secret Key Decryption Failed");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs by date and reciever");
			return response;

		} catch (IneligibleAccountException e) {
			smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
//			appLogger.error(logId + "IneligibleAccountException: " + e.getMessage(), e);
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
		} catch (Exception e) {
			smsLogger.error(logId + "Exception: " + e.getMessage());
			appLogger.error(logId + "Exception: " + e.getMessage(), e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs by date and reciever");
			return response;
		}
                
		try {
			boolean validMSISDN = false;
			int min = 0;
			int max = (int)Configs.MAX_RETURNED_SMSs.getValue();
			if(smsLogger.isTraceEnabled())
				smsLogger.trace(logId + "MAX_RETURNED_SMSs value from configuration: " + max);
			if (request.getReceiverMSISDN() != null) {
				validMSISDN = smsApiUtility.validateMSISDN(logId, request.getReceiverMSISDN());
			if (validMSISDN) { 
				String msisdn = SMSUtils.formatAddress(request.getReceiverMSISDN(), MsisdnFormat.INTER_CC_LOCAL);
                                if(smsLogger.isTraceEnabled()){
                                    smsLogger.trace(logId + "Will call smsViewDao with parameters: accountId=" + request.getAccountId()
                                            + ", msisdn=" + msisdn + ", startDate=" + dateValidationResult.getStartDate()+ ", endDate=" + dateValidationResult.getEndDate() + ", min=" + min + ", max=" + max);
                                }
				List<SMSAPIView> smsLogList = smsViewDao.findSMSwithinMSISDNandDates(
						request.getAccountId(), msisdn, dateValidationResult.getStartDate(), dateValidationResult.getEndDate(), min, max);
				if (smsLogList == null || smsLogList.isEmpty()) {
					smsLogger.warn(logId + "No SMSs Sent From [ " + dateValidationResult.getStartDate() + " ] to [ " + dateValidationResult.getEndDate() + "], formated reciever ["+ msisdn +"]"); 
					response.setResultStatus(ResultStatus.SUCCESS);
					response.setDescription("No SMSs Sent From [" + dateValidationResult.getStartDate() + "] to [" + dateValidationResult.getEndDate() + "],["+msisdn+"]"); 
					return response;
				} else {
					if(smsLogger.isDebugEnabled())
					smsLogger.debug(logId + "SMSs list size:" + smsLogList.size());
					for (int i = 0; i < smsLogList.size(); i++) {
						smsList.add(i, new SmsDatesWithMSISDN(smsLogList.get(i).getSmsId(), smsLogList.get(i).getReceiver() ,
								smsLogList.get(i).getSMSStatus().getName().name(),
								smsLogList.get(i).getSendReceiveDate() == null ? " " : smsLogList.get(i)
										.getSendReceiveDate().toString(),
								smsLogList.get(i).getSubmitDate() == null ? " " : smsLogList.get(i).getSubmitDate()
										.toString(), smsLogList.get(i).getDeliveryDate() == null ? " " : smsLogList
										.get(i).getDeliveryDate().toString()));
					
					}
					if(smsLogger.isDebugEnabled())
						smsLogger.debug(logId + "result list: " + smsLogList.toString());
				}
				smsResponse.setSmsDatesList(smsList); 
				response.setResponseList(smsResponse); 

				if(smsLogger.isDebugEnabled())
				smsLogger.debug(logId + "number of Returned SMS's = " + smsList.size()); 

			}else {
				smsLogger.warn(logId + "Invalid MSISDN");
            	reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
            	response.setResultStatus(ResultStatus.INVALID_REQUEST);
            	response.setDescription("Invalid MSISDN");

            	return response;
            	}
			}else {
				smsLogger.warn(logId + "empty reciever MSISDN");
                            reportAppError(AppErrors.INVALID_REQUEST, "empty reciever MSISDN");
				response.setResultStatus(ResultStatus.INVALID_REQUEST);
				response.setDescription("empty reciever MSISDN");
				return response;

			}
		
		} catch (DBException e) {
			smsLogger.error(logId + "DBException: " + e.getMessage());
			appLogger.error(logId + "DBException: " + e.getMessage(), e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs by date and reciever");
			return response;
            } catch (Exception e) {
                smsLogger.error(logId + "Exception: " + e.getMessage());
                 appLogger.error(logId + "UnhandledException", e);
                reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
                response.setResultStatus(ResultStatus.GENERIC_ERROR);
                response.setDescription("Unable to inquire about the SMSs by date and reciever");
                return response;
            }
		response.setResultStatus(ResultStatus.SUCCESS);
		return response;
	}// end of web method inquireByDateAndReciever

	/**
	 * Web Method to inquire with start, end date
	 * 
	 * this Method get from DB all the SMS's in this Period from [Start Date ,
	 * End Date]
	 */

	@Path("/inquireByDate")
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public EnquireSMSsByDatesResponse inquireByDate(@Context HttpServletRequest contextRequest,
			EnquireSMSsByDatesDetailedRequest request) {
                Account account;
		String trxId = request.getTrxId() + " | ";
        String logId = request.logAccountId() + trxId;
		EnquireSMSsByDatesResponse response = new EnquireSMSsByDatesResponse();
		DateValidationResult dateValidationResult = new DateValidationResult();
		smsLogger.info(logId + "Received new inquireByDate request ");
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + request);
            }

		try {

			smsLogger.debug(logId + "checking account eligibility to: " + ActionName.INQUIRY_SMS_DATES);
			account = accountManagement.checkAccountEligibilitySMSAPI(trxId, request.getAccountId(), ActionName.INQUIRY_SMS_DATES, (int) (Configs.SMSAPI_JPA_TIMEOUT.getValue()));
                    if (((boolean)Configs.SMSAPI_ENABLE_REPORT_TIME_INTERVAL.getValue()) == true && !smsApiUtility.checkPassingReportTimeInterval(logId, request.getAccountId())) {
                        smsLogger.warn(logId + "Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        response.setResultStatus(ResultStatus.INVALID_REQUEST);
                        response.setDescription("Can't request more than one report within " + ((int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue()) / (float) 1000 + " second");
                        return response;
                    }
			if(smsLogger.isTraceEnabled())
			smsLogger.trace(logId + "account is eligible ");
			smsApiUtility.validateRequest(logId, account, request.getPassword(), request.getIp());
				
                    StringBuilder requestString = new StringBuilder("");
                    requestString.append("AccountId=").append(request.getAccountId());
                    requestString.append("&Password=").append(request.getPassword());
                    requestString.append("&StartDate=").append(request.getStartDate());
                    requestString.append("&EndDate=").append(request.getEndDate());
                    smsApiUtility.checkHashing(logId, requestString, request.getSecureHash(), account, request.getPassword());
                    
                    dateValidationResult = smsApiUtility.validateDates(logId, request.getStartDate(), request.getEndDate());
		} catch (DBException e) {
			smsLogger.error(logId + "DBException: " + e.getMessage());
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs by date");
			return response;

		} catch (AccountNotFoundException e) {
			smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (InvalidRequestException e) {
			smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
                    reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (AccountNotRegisteredOnAPIException e) {
			smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;

		} catch (NotTrustedIPException e) {
			smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Not Registered On API");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (AuthorizationFailedException e) {
			smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Authorization Failed");
			response.setResultStatus(ResultStatus.INVALID_REQUEST);
			response.setDescription(e.getMessage());
			return response;
		} catch (SecretKeyDecryptionFailedException e) {
			smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
                    reportAppError(AppErrors.AUTHORIZATION_FAILED, "Secret Key Decryption Failed");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs by date");
			return response;

		} catch (IneligibleAccountException e) {
			smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
                    reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription(e.getMessage());
			return response;
            } catch (Exception e) {
                smsLogger.error(logId + "Exception: " + e.getMessage());
                    appLogger.error(trxId + "UnhandledException", e);
                reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
                response.setResultStatus(ResultStatus.GENERIC_ERROR);
                response.setDescription("Unable to inquire about the SMSs by date");
                return response;
            }

		// calling Data Access layer
		if(smsLogger.isDebugEnabled())
		smsLogger.debug(logId + "enquire from [ " + dateValidationResult.getStartDate() + " ] to [ "+  dateValidationResult.getEndDate() + " ]");
		try {
			// formating dates comes from request

			// returned list from DB
			List<SMSLog> smsLogList = smsLogDao.findSMSwithinDates(request.getAccountId(),
					dateValidationResult.getStartDate(), dateValidationResult.getEndDate());


			if (smsLogList == null|| smsLogList.size()==0 ||smsLogList.isEmpty()) {
				if(smsLogger.isDebugEnabled())
				smsLogger.debug(logId + "No SMSs in DB from [ " + dateValidationResult.getStartDate() + " ] to [ "+  dateValidationResult.getEndDate() + " ]");
				response.setResultStatus(ResultStatus.SUCCESS);
				response.setDescription("No SMSs in DB from [ " + dateValidationResult.getStartDate() + " ] to [ "+ dateValidationResult.getEndDate()+ " ]");
				return response;
			} else {
				if(smsLogger.isDebugEnabled())
				smsLogger.debug(logId + "result list size : " + smsLogList.size());
//				if(smsLogger.isTraceEnabled())
//				smsLogger.trace(logId + "result list: " + smsLogList.toString());
			}

			// calculate statistics
			int apiDeliveredCount = 0;
			int campDeliveredCount = 0;
			int apiUNDeliveredCount = 0;
			int campUNDeliveredCount = 0;
			for (int i = 0; i < smsLogList.size(); i++) {
				if (smsLogList.get(i).getCampaign() == null) { // sms-api only
					if (smsLogList.get(i).getSMSStatus().getId().intValue() == SMSStatusName.DELIVERED.getDbId()) { //5 delivered
						apiDeliveredCount++;
					} else { // un-delivered
						apiUNDeliveredCount++;
					}
				} else { // sms-api-Campaigns
					if (smsLogList.get(i).getSMSStatus().getId().intValue() == SMSStatusName.NOT_DELIVERED.getDbId()) { //6 not delivered
						campUNDeliveredCount++;
					} else { // un-delivered
						campDeliveredCount++;
					}
				}
			}
			response.setSmsApiDelivered(apiDeliveredCount);
			response.setSmsApiUnDelivered(apiUNDeliveredCount);
			response.setApiCampDelivered(campDeliveredCount);
			response.setApiCampUnDelivered(campUNDeliveredCount);
			response.setResultStatus(ResultStatus.SUCCESS);
			
			smsLogger.info(logId + "response: " + response); 

		} catch (DBException e) {
			smsLogger.error(logId + "DBException: " + e.getMessage());
			appLogger.error(logId + "DBException: " + e.getMessage(), e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs by date");
			return response;
		} catch (Exception e) {
			smsLogger.error(logId + "Exception: " + e.getMessage());
			appLogger.error(logId + "Exception: " + e.getMessage(), e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			response.setResultStatus(ResultStatus.GENERIC_ERROR);
			response.setDescription("Unable to inquire about the SMSs by date");
			return response;
		}
		return response;
	}// end of web method inquireByDate

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void validateAndReserveSmsQuota(String trxId, List<SMSDetails> validSMSList, Account account, Map<String, SMSDetails> validSmsQuotaMap) {
            String accountId = account.getAccountId();
		int acctTotalQuota;
		int remainQuota;
		int reservedSegments = 0;
                if(smsLogger.isTraceEnabled()){
                    smsLogger.trace(trxId + "Will validate SMS quota for SMSList= " + validSMSList);
                }
		
		try
		{
			AccountTier acctTier = account.getAccountTier();
			
			if(acctTier != null && acctTier.isPrepaidTier())
			{
				AccountQuota acctQuota = acctTier.getAccountQuota();
				if(acctQuota == null)
				{
                                    //TODO .. raise alarm with low threshold like 5
                                    smsLogger.error(trxId + "Prepaid account is not configured properly, as there is no quota defined for accountId: " + accountId);
					throw new AccountQuotaNotFoundException();
				}// end if
				
				acctTotalQuota = acctTier.getTier().getQuota();
				remainQuota = acctTotalQuota - (acctQuota.getConsumedSmss()+ acctQuota.getReservedSmss());
				
				smsLogger.info(trxId + "AccountId=[" + accountId + "], "
						+ " acctTotalQuota=[" + acctTotalQuota + "], "
						+ " remainQuota=[" + remainQuota + "]");
				
				for(SMSDetails smsDetails : validSMSList)
				{
					int smsSegmentCount = SMSUtils.calcSegCount(LanguageNameEnum.valueOf(smsDetails.getLanguage())
							, smsDetails.getSMSText());
					if(smsSegmentCount <= remainQuota)
					{
						reservedSegments += smsSegmentCount;
						remainQuota -= smsSegmentCount;
						validSmsQuotaMap.put(smsDetails.getSMSId(), smsDetails);
                                            if (smsLogger.isDebugEnabled()) {
						smsLogger.debug(trxId + "AccountId=[" + accountId + "], "
								+ " acctTotalQuota=[" + acctTotalQuota + "], "
								+ " remainQuota=[" + remainQuota + "], "
								+ " SMSId=[" + smsDetails.getSMSId() + "], "
								+ "Sms added to valid quota list");
                                            }
					}// end if
					else
					{
                                            if (smsLogger.isDebugEnabled()) {
						smsLogger.debug(trxId + "AccountId=[" + accountId + "], "
								+ " acctTotalQuota=[" + acctTotalQuota + "], "
								+ " remainQuota=[" + remainQuota + "], "
								+ " SMSId=[" + smsDetails.getSMSId() + "], "
								+ "account quota exceeded");
                                            }
					}// end else
				}// end for	
			
				if(reservedSegments > 0)
				{
					smsLogger.info(trxId + "AccountId=[" + accountId + "], "
							+ " reservedSegments=[" + reservedSegments + "], "
							+ " start reserving");
					
					acctQuotaDao.incrementReservedSmss(acctQuota.getAccountTiersId(), reservedSegments);
				}// end if
				else
				{
					smsLogger.info(trxId + "AccountId=[" + accountId + "], "
							+ " reservedSegments=[" + reservedSegments + "], "
							+ " discard reserving");
				}// end else
			}// end if
			else
			{
				// Postpaid account, so add all smsDetails to validQuotaMap
                            if (smsLogger.isDebugEnabled()) {
                                smsLogger.debug(trxId + "AccountId=[" + accountId + "]" + ", postpaid account, so discard quota validation");
                            }
				
				for(SMSDetails smsDetails : validSMSList)
				{
					validSmsQuotaMap.put(smsDetails.getSMSId(), smsDetails);
				}// end for
			}// end else
		}// end try// end try
		catch(Exception e)
		{
                    //TODO .. raise alarm with low threshold like 5
                    trxId = "account id=" + accountId + "| " + trxId;
                    smsLogger.error(trxId + "Error while reserving quota");
                    appLogger.error(trxId + "Error while reserving quota ", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
		}// end catch// end catch
		
                if (smsLogger.isTraceEnabled()) {
                    smsLogger.trace(trxId + "ValidSmsQuota: validSmsQuotaMap.key=" + validSmsQuotaMap.keySet() + ", validSmsQuotaMap.values=" + validSmsQuotaMap.values());
                }
	}// end of method validateAndReserveSmsQuota
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void commitSmsQuotaReservation(String trxId, Map<String,SMSDetails> validSmsQuotaMap, Map<String,SMSResponseStatus> smsResponseMap, Account account)
	{
            String accountId = account.getAccountId();
		try
		{
			int submittedSegmentCount = 0;
			int totalSegmentCount = 0;
			
                    AccountTier acctTier = account.getAccountTier();
			
			if(acctTier != null && acctTier.isPrepaidTier())
			{
				AccountQuota acctQuota = acctTier.getAccountQuota();
				if(acctQuota == null)
				{
                                    smsLogger.error(trxId + "Prepaid account is not configured properly, as there is no quota defined for accountId: " + accountId);
					throw new AccountQuotaNotFoundException();
				}// end if
				
				Set<String> smsIds = validSmsQuotaMap.keySet();
				for(String smsId : smsIds)
				{
					SMSDetails smsDetails = validSmsQuotaMap.get(smsId);
					int smsSegmentCount = SMSUtils.calcSegCount(LanguageNameEnum.valueOf(smsDetails.getLanguage())
							, smsDetails.getSMSText());
					
					totalSegmentCount += smsSegmentCount;
					
					SMSResponseStatus smsResponseStatus = smsResponseMap.get(smsId);
					if(smsResponseStatus != null)
					{
						switch(smsResponseStatus)
						{
							case SUBMITTED:
							{
								submittedSegmentCount += smsSegmentCount;
								break;
							}// end case
							default:
							{
								break;
							}// end default
						}// end switch
					}// end if
				}// end for
				
                            smsLogger.info(trxId + "AccountId=[" + accountId + "], "
						+ " totalSegmentCount=[" + totalSegmentCount + "], "
						+ " submittedSegmentCount=[" + submittedSegmentCount + "], "
						+ " start reverting total reserved segments, and increment consumed value with total submitted sgements");
				
				// Reverting total reserved segments, and Increment consumed value with total submitted segments
				if(totalSegmentCount > 0)
				{
                                    //TODO .. raise alarm with low threshold like 5
                                    acctQuotaDao.updateAccountQuota(accountId, totalSegmentCount * -1, submittedSegmentCount);
				}// end if
			}// end if
			else
			{
                            if (smsLogger.isDebugEnabled()) {
                                smsLogger.debug(trxId + "Postpaid account, discard committing quota.");
                            }
			}// end else
		}// end try
		catch(Exception e)
		{
                    //TODO .. raise alarm with low threshold like 5
                    trxId = "account id=" + accountId + " | " + trxId;
                    smsLogger.error(trxId + " | " + "Error while committing reserved quota");
                    appLogger.error(trxId + "Error while committing reserved quota ", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
		}// end catch
	}// end of method commitSmsQuotaReservation
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void revertSmsQuotaReservation(String trxId, Map<String, SMSDetails> validSmsQuotaMap, Account account)
	{
            String accountId = account.getAccountId();
		try
		{
			int totalSegmentCount = 0;
			
                    AccountTier acctTier = account.getAccountTier();
			
			if(acctTier != null && acctTier.isPrepaidTier())
			{
				AccountQuota acctQuota = acctTier.getAccountQuota();
				if(acctQuota == null)
				{
                                    //TODO .. raise alarm with low threshold like 5
                                    smsLogger.error(trxId + "Prepaid account is not configured properly, as there is no quota defined for accountId: " + accountId);
					throw new AccountQuotaNotFoundException();
				}// end if
				
				Set<String> smsIds = validSmsQuotaMap.keySet();
				for(String smsId : smsIds)
				{
					SMSDetails smsDetails = validSmsQuotaMap.get(smsId);
					int smsSegmentCount = SMSUtils.calcSegCount(LanguageNameEnum.valueOf(smsDetails.getLanguage())
							, smsDetails.getSMSText());
					
					totalSegmentCount += smsSegmentCount;
				}// end for
				
                            smsLogger.info(trxId + "AccountId=[" + accountId + "], "
						+ " totalSegmentCount=[" + totalSegmentCount + "], "
						+ " start reverting total reserved segments");
				
				// Reverting total reserved segments
				if(totalSegmentCount > 0)
				{
					acctQuotaDao.incrementReservedSmss(acctQuota.getAccountTiersId(), totalSegmentCount * -1);
				}// end if
			}// end if
			else
			{
                            if (smsLogger.isDebugEnabled()) {
                                smsLogger.debug(trxId + "Postpaid account, discard reverting quota.");
                            }
			}// end else
		}// end try
		catch(Exception e)
		{
                    //TODO .. raise alarm with low threshold like 5
                    trxId = "account id=" + accountId + " | " + trxId;
                    smsLogger.error(trxId + " | " + "Error while reverting reserved quota ");
                    appLogger.error(trxId + "Error while committing reserved quota ", e);
                    reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
		}// end catch
	}// end of method revertSmsQuotaReservation
        
    private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.APP_SMSAPI); 
	}
    
}// end of class SMSAPI
