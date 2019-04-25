package com.edafa.web2sms.sms.caching;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.service.campaign.exception.CampaignTypeNotDefinedException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampListException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignException;
import com.edafa.web2sms.service.list.exception.InvalidRequestException;
import com.edafa.web2sms.sms.SMSAPI;
import com.edafa.web2sms.sms.SMSAPI_Utility;
import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.exceptions.AccountNotRegisteredOnAPIException;
import com.edafa.web2sms.sms.exceptions.AuthorizationFailedException;
import com.edafa.web2sms.sms.exceptions.NotTrustedIPException;
import com.edafa.web2sms.sms.exceptions.SecretKeyDecryptionFailedException;
import com.edafa.web2sms.sms.interfaces.SubmitSMSBeanRemote;
import com.edafa.web2sms.sms.model.CampaignRecieverDetails;
import com.edafa.web2sms.sms.model.ExpiredResponse;
import com.edafa.web2sms.sms.model.SMSDetails;
import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;
import com.edafa.web2sms.sms.utils.SubmitSMSBeanRemotePoolLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author loay
 */
@Stateless
@LocalBean
@Path("/log")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SMSAPICaching {

    private Logger appLogger;
    private Logger smsLogger;

    @EJB
    private SubmitSMSBeanRemotePoolLocal submitSMSBeanRemotePool;

    @EJB
    SMSAPI smsapi;

    @EJB
    private SMSAPI_Utility smsApiUtility;

    @PostConstruct
    public void init() {
        appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
        smsLogger = LogManager.getLogger(LoggersEnum.SMS_API_CACHING.name());
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    @Path("/SMS")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ExpiredResponse logExpiredSMSRquest(@Context HttpServletRequest contextRequest, SubmitDetailedSMSRequest request) {
        String trxId = request.getTrxId() + " | ";
        String logId = request.logAccountId() + trxId;
        smsLogger.info(logId + "Will log all request SMSes to DB  with status EXPIRED");
        Account account;
        ExpiredResponse response = new ExpiredResponse();
        response.setResultStatus(ResultStatus.GENERIC_ERROR);
        try {
            account = smsapi.validateDetailedSMSRquest(logId, trxId, request);
        } catch (DBException e) {
            smsLogger.error(logId + "DBException: " + e.getMessage());
            appLogger.error(logId + "DBException", e);
            response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
            return response;
        } catch (AccountNotFoundException e) {
            smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (InvalidRequestException e) {
            smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (AccountNotRegisteredOnAPIException e) {
            smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (NotTrustedIPException e) {
            smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (AuthorizationFailedException e) {
            smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (SecretKeyDecryptionFailedException e) {
            smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
            return response;
        } catch (IneligibleAccountException e) {
            smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
            return response;
        } catch (Exception e) {
            smsLogger.error(logId + "Exception: " + e.getMessage());
            appLogger.error(logId + "UnhandledException", e);
            response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
            return response;
        }

        List<SMSDetails> validSMSList = new ArrayList<>();

        for (SMSDetails smsDetails : request.getSMSs()) {
            if (smsDetails.isCachedSMS()) {
                smsDetails.setAccountId(request.getAccountId());
            }
        }
        // gomaa note: make this validation at the request handle beginning after getting the list of sender names.
        validSMSList = smsApiUtility.validateSMSList(logId, request.getSMSs(), account, request.getSmsIdPrefix(), request.isCachedRequest());

        if (!validSMSList.isEmpty()) {
            SubmitSMSBeanRemote submitSMSBean = null;
            try {

//                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//                    @Override
//                    public void uncaughtException(Thread t, Throwable e) {
//                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                    }
//                });
                if (smsLogger.isTraceEnabled()) {
                    smsLogger.trace(logId + "Calling SubmitSMSBeanRemote");
                }
                submitSMSBean = submitSMSBeanRemotePool.getSubmitSMSBeanRemote();
                ResultStatus result = submitSMSBean.logExpiredSMS(logId, request.getSMSs());
                response.setResultStatus(result);

                if (smsLogger.isDebugEnabled()) {
                    if (result == ResultStatus.SUCCESS) {
                        smsLogger.debug(logId + "Request SMSes logged to DB  with status EXPIRED successfully");
                    } else {
                        smsLogger.debug(logId + "Can NOT log SMSes to DB  with status EXPIRED, result=" + result);
                    }
                }
                return response;
            } catch (Exception e) {
                if (e.getCause() != null && (e.getCause() instanceof NoSuchObjectException || e.getCause() instanceof NameNotFoundException)) {
                    response.setResultStatus(ResultStatus.RESEND_CACHE_REQUEST_FAILED);
                } else {
                    response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
                }
                smsLogger.error(logId + "Unhandled Exception: " + e.getMessage() + ", return with status " + response.getResultStatus());
                appLogger.error(logId + "Unhandled Exception" + e.getMessage(), e);
                return response;
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
        } else {
            smsLogger.warn(logId + "Invalid request: there is no valid SMS ");
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        }
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    @Path("/Campaign")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ExpiredResponse logExpiredCampaignRquest(@Context HttpServletRequest contextRequest, SubmitDetailedCampaignRequest request) {
        String trxId = request.getTrxId() + " | ";
        String logId = request.logAccountId() + trxId;
        CampaignStatusName campaignStatus = CampaignStatusName.OBSOLETE;
        smsLogger.info(logId + "Will log Campaign to DB  with status " + campaignStatus);
        AccountUser accountUser;
        ExpiredResponse response = new ExpiredResponse();
        response.setResultStatus(ResultStatus.GENERIC_ERROR);

        try {
            accountUser = smsapi.submitDetailedCampaignRquestValidation(logId, trxId, request, appLogger, smsLogger);
        } catch (DBException e) {
            smsLogger.error(logId + "DBException: " + e.getMessage());
            appLogger.error(logId + "DBException: " + e.getMessage(), e);
            response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
            return response;
        } catch (AccountNotFoundException e) {
            smsLogger.warn(logId + "AccountNotFoundException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (InvalidRequestException e) {
            smsLogger.warn(logId + "InvalidRequestException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (AccountNotRegisteredOnAPIException e) {
            smsLogger.warn(logId + "AccountNotRegisteredOnAPIException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (NotTrustedIPException e) {
            smsLogger.warn(logId + "NotTrustedIPException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (AuthorizationFailedException e) {
            smsLogger.warn(logId + "AuthorizationFailedException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (SecretKeyDecryptionFailedException e) {
            smsLogger.warn(logId + "SecretKeyDecryptionFailedException: " + e.getMessage());
            response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
            return response;
        } catch (UserNotFoundException e) {
            smsLogger.warn("UserNotFoundException while creating campaign with smsAPI");
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (IneligibleAccountException e) {
            smsLogger.warn(logId + "IneligibleAccountException: " + e.getMessage());
            return response;
        } catch (SenderNameNotAttached e) {
            smsLogger.warn(logId + "SenderNameNotAttached: " + e.getMessage());
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        } catch (Exception e) {
            smsLogger.error(logId + "UnhandledException: " + e.getMessage());
            appLogger.error(logId + "UnhandledException", e);
            response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
            return response;
        }

        CampaignRecieverDetails validSMSList = new CampaignRecieverDetails();
        validSMSList = smsApiUtility.validateMSISDNs(trxId, request.getMsisdns());

        if (!validSMSList.getReceiverMSISDN().isEmpty()) {
            try {
                smsapi.submitDetailedCampaignRquestSubmition(logId, request, validSMSList, accountUser, campaignStatus, appLogger, smsLogger);

                if (smsLogger.isDebugEnabled()) {
                    smsLogger.debug(logId + "Campaign to DB  with status " + campaignStatus + " successfully");
                }
            } catch (IneligibleAccountException e) {
                smsLogger.warn(logId + "Exception while creating campaign with smsAPI");
                return response;
            } catch (DBException e) {
                smsLogger.error(logId + "DBException while creating campaign with smsAPI: " + e.getMessage());
                appLogger.error(logId + "Exception while creating campaign with smsAPI ", e);
                response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
                return response;
            } catch (InvalidCampaignException e) {
                smsLogger.warn(logId + "InvalidCampaignException while creating campaign with smsAPI");
                return response;
            } catch (InvalidCampListException e) {
                smsLogger.warn(logId + "InvalidCampListException while creating campaign with smsAPI");
                return response;
            } catch (InsufficientQuotaException e) {
                smsLogger.warn(logId + "InsufficientQuotaException while creating campaign with smsAPI");
                return response;
            } catch (NotPrePaidAccountException e) {
                smsLogger.warn(logId + "NotPrePaidAccountException while creating campaign with smsAPI");
                return response;
            } catch (AccountQuotaNotFoundException e) {
                smsLogger.warn(logId + "AccountQuotaNotFoundException while creating campaign with smsAPI");
                return response;
            } catch (CampaignTypeNotDefinedException e) {
                smsLogger.warn(logId + "CampaignTypeNotDefinedException while creating campaign with smsAPI");
                return response;
            } catch (Exception e) {
                smsLogger.error(logId + "Exception while creating campaign with smsAPI, " + e.getMessage());
                appLogger.error(logId + "Exception while creating campaign with smsAPI ", e);
                response.setResultStatus(ResultStatus.INTERNAL_SERVER_ERROR);
                return response;
            }
            response.setResultStatus(ResultStatus.SUCCESS);
        } else {
            smsLogger.info(logId + "Invalid request: there is no valid SMS ");
            response.setResultStatus(ResultStatus.INVALID_REQUEST);
            return response;
        }
        return response;
    }
}
