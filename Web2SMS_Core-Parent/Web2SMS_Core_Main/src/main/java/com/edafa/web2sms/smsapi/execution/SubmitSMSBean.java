package com.edafa.web2sms.smsapi.execution;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.smsgw.dalayer.enums.LanguageEnum;
import com.edafa.smsgw.dalayer.enums.SMSStatusName;
import com.edafa.smsgw.smshandler.exceptions.FailedSMSException;
import com.edafa.smsgw.smshandler.exceptions.InvalidSMSReceiver;
import com.edafa.smsgw.smshandler.exceptions.InvalidSMSSender;
import com.edafa.smsgw.smshandler.smpp.SMPPModuleAdapter;
import com.edafa.smsgw.smshandler.sms.SMS;
import com.edafa.smsgw.smshandler.sms.SMSId;
import com.edafa.smsgw.smshandler.sms.interfaces.SMSHandlingManagerLocal;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.core.execution.SmsHolderStatus;
import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.enums.SMSResponseStatus;
import com.edafa.web2sms.sms.interfaces.SubmitSMSBeanRemote;
import com.edafa.web2sms.sms.model.SMSDetails;
import com.edafa.web2sms.sms.model.SMSSubmitState;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.GenericLimiters;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class SubmitSMSBean
 */
@Stateless(name = "SubmitSMSBean")
@Remote(SubmitSMSBeanRemote.class)
public class SubmitSMSBean implements SubmitSMSBeanRemote {

    @EJB
    SMSHandlingManagerLocal smsHandlingManager;

    @EJB
    AppErrorManagerAdapter appErrorManagerAdapter;

    SMPPModuleAdapter smppAdapter;

    private Logger smsLogger = LogManager.getLogger(LoggersEnum.SMS_API.name());
    private Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());

    @PostConstruct
    public void init() {
        smppAdapter = smsHandlingManager.getSmppAdapter();
    }

    @Override
    public SMSResponseStatus submitSMS(String logId, SMSDetails smsDetails) {
        SMSResponseStatus response;
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(logId + "Received " + smsDetails);
        }

        if (smppAdapter.isReadyForSubmitting()) {
            SMS sms;
            try {
                long submitSMSWaitingTime = (int) Configs.SUBMIT_SMS_WAITING_TIME.getValue();
                int submittedSMSSegments = 0;
                sms = new SMS();
                sms.setSubmitDate(new Date());
                sms.setSMSId(new SMSId(smsDetails.getSMSId()));
                sms.setOwnerId(smsDetails.getAccountId());
                sms.setSender(smsDetails.getSenderName());
                sms.setReceiver(smsDetails.getReceiverMSISDN());
                sms.setRegisteredDelivery((boolean) Configs.SMSAPI_REGESTERED_DELIVERY.getValue());
                sms.setLanguage(LanguageEnum.valueOf(smsDetails.getLanguage()));
                sms.setSmsText(smsDetails.getSMSText());

                if ((boolean) Configs.SMSAPI_ASYNCH_SENDING.getValue()) {
                    if (smsLogger.isDebugEnabled()) {
                        smsLogger.debug(logId + sms.logId() + "Adding to SMS-API queue " + sms.dumpDebugData());
                    }
                    ApiSmsHolder smsHolder = new ApiSmsHolder(sms, smsDetails.getRateLimitersIds(), smsLogger, logId, smsDetails.isCachedSMS(), SmsApiHandler.smsApiHandler.getSmsApiAccountInfo(smsDetails.getAccountId()));

                    SmsApiHandler.smsApiHandler.submitSmsApiSMS(smsHolder, (int) Configs.SMSAPI_QUEUE_OFFER_TIMEOUT.getValue(), TimeUnit.SECONDS);
                    if (smsHolder.getSmsHolderStatus() == SmsHolderStatus.SUBMITTED) {
                        response = SMSResponseStatus.SUBMITTED;
                        if (smsLogger.isTraceEnabled()) {
                            smsLogger.trace(logId + sms.logId() + "added to SMS-API queue");
                        }
                    } else if (smsHolder.getSmsHolderStatus() == SmsHolderStatus.SENDING_RATE_EXCEEDED) {
                        response = SMSResponseStatus.FAILED_TO_SUBMITTED;
                        smsLogger.warn(logId + sms.logId() + "Sending rate exceeded.");
                    } else if (smsHolder.getSmsHolderStatus() == SmsHolderStatus.CACHE_LIMIT_EXCEEDED) {
                        response = SMSResponseStatus.CACHE_LIMIT_EXCEEDED;
                        smsLogger.warn(logId + sms.logId() + "pending list exceeded limit size.");
                    } else if (smsHolder.getSmsHolderStatus() == SmsHolderStatus.FAILED_TO_SUBMITTED) {
                        response = SMSResponseStatus.FAILED_TO_SUBMITTED;
                        smsLogger.warn(logId + sms.logId() + "can NOT be offer to SMS-API queue.");
                        reportAppError(AppErrors.FAILED_OPERATION, "SMS-API queue offering timeout");
                    } else {
                        response = SMSResponseStatus.SYSTEM_FAILURE;
                        smsLogger.warn(logId + sms.logId() + "SMSAPI queue offering timeout, status: " + sms.getStatus());
                        reportAppError(AppErrors.FAILED_OPERATION, "SMSAPI queue offering timeout");
                    }
                } else {
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace(logId + sms.logId() + "| Submitting " + sms.toString());
                    }
                    sms.setProcessingDate(new Date());
                    submittedSMSSegments = smppAdapter.submitSMS(sms, submitSMSWaitingTime, TimeUnit.SECONDS);
                    if (sms.isSubmitted()) {
                        response = SMSResponseStatus.SUBMITTED;
                        if (smsLogger.isDebugEnabled()) {
                            smsLogger.debug(logId + sms.logId() + "is successfully submit " + submittedSMSSegments + " SMSSegments.");
                        }
                    } else {
                        response = SMSResponseStatus.SYSTEM_FAILURE;
                        smsLogger.warn(logId + sms.logId() + "Timmed out, status: " + sms.getStatus());
                        reportAppError(AppErrors.FAILED_OPERATION, "SMSAPI sending timeout");
                    }
                }
            } catch (InvalidSMSSender | FailedSMSException | InvalidSMSReceiver e) {
                            smsLogger.error(logId + e.getMessage());
                            if (appLogger.isDebugEnabled()) {
                                appLogger.debug(logId + e.getMessage(), e);
                            }
                            reportAppError(AppErrors.FAILED_OPERATION, "SMSAPI submition failure");
                response = SMSResponseStatus.FAILED_TO_SUBMITTED;
            }
            return response;
        } else {
            if (smsDetails.isCachedSMS()) {
                response = SMSResponseStatus.TIMMED_OUT;
            } else {
                response = SMSResponseStatus.SYSTEM_FAILURE;
            }
            smsLogger.info(logId + "The smpp module is not ready for submitting, response=" + response);
            reportAppError(AppErrors.FAILED_OPERATION, "The smpp module is not ready for submitting");
            return response;
        }
    }

    @Override
    public List<SMSSubmitState> submitSMS(String logId, List<SMSDetails> SMSDeatailsList) {
        List<SMSSubmitState> smsSubmitStateList = new ArrayList<SMSSubmitState>();
        if (SMSDeatailsList != null && !SMSDeatailsList.isEmpty()) {
            // Add the default RateLimiter if there is no limiter for that account
            if (SMSDeatailsList.get(0).getRateLimitersIds().isEmpty()) {
                SMSDeatailsList.get(0).getRateLimitersIds().add(GenericLimiters.SmsapiDefaultLimiter.name());
            }

            SMSDeatailsList.get(0).getRateLimitersIds().add(GenericLimiters.SmsapiSystemLimiter.name());
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(logId + "Limiter Id's : " + SMSDeatailsList.get(0).getRateLimitersIds());
            }
            for (SMSDetails smsDetails : SMSDeatailsList) {
                SMSResponseStatus smsResponseStatus = null;

                try {
                    if (smsLogger.isDebugEnabled()) {
                        smsLogger.debug(logId + "Start submitting SMS.");
                    }
                    smsResponseStatus = submitSMS(logId, smsDetails);

                    smsSubmitStateList.add(new SMSSubmitState(smsDetails.getSMSId(), smsResponseStatus));
                    smsLogger.info(logId + "SMS(" + smsDetails.getSMSId() + "): SMS status: " + smsResponseStatus);
                }// end try
                catch (Exception e) {
                    smsLogger.error(logId + "The SMS failed to be submitted");
                    if (appLogger.isDebugEnabled()) {
                        appLogger.debug(logId + "The SMS failed to be submitted", e);
                    }
                    reportAppError(AppErrors.GENERAL_ERROR, "The SMS failed to be submitted");
		    smsResponseStatus = SMSResponseStatus.SYSTEM_FAILURE;
                }// end catch
            }// end for
        }

        return smsSubmitStateList;
    }// end of method submitSMS

    private void reportAppError(AppErrors error, String msg) {
        appErrorManagerAdapter.reportAppError(error, ErrorsSource.CORE_SMSAPI.name(), msg);
    }

    @Override
    public ResultStatus logExpiredSMS(String logId, List<SMSDetails> smsDeatailsList) {
        try {
            appLogger.info(logId + "Will log SMSs into database with status " + SMSStatusName.EXPIRED + " , count = " + smsDeatailsList.size());
            List<SMS> smsList = new ArrayList<SMS>(smsDeatailsList.size());
            
            for (SMSDetails smsDetails : smsDeatailsList) {
                SMS sms = new SMS();
                sms.setStatus(SMSStatusName.EXPIRED);
                sms.setProcessingDate(new Date());
                sms.setComment("Expired due to exceed caching validity period.");
                sms.setSubmitDate(new Date());
                sms.setSMSId(new SMSId(smsDetails.getSMSId()));
                sms.setOwnerId(smsDetails.getAccountId());
                sms.setSender(smsDetails.getSenderName());
                sms.setReceiver(smsDetails.getReceiverMSISDN());
                sms.setLanguage(LanguageEnum.valueOf(smsDetails.getLanguage()));
                sms.setSmsText(smsDetails.getSMSText());
                smsList.add(sms);
                if (smsLogger.isDebugEnabled()) {
                    smsLogger.debug(logId + sms.logId() + "Will be logged to DB with status " + sms.getStatus());
                }
            }
            
            smppAdapter.logSMS(smsList);
            if (appLogger.isDebugEnabled()) {
                appLogger.debug(logId + "SMSs logged into database with status " + SMSStatusName.EXPIRED + " , count = " + smsDeatailsList.size());
            }
            return ResultStatus.SUCCESS;
        } catch (Exception e) {
            appLogger.error(logId + "Failed to log SMSs to database, count = " + smsDeatailsList.size(), e);
            return ResultStatus.INTERNAL_SERVER_ERROR;
        }
    }

}// end of class SubmitSMSBean
