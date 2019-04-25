package com.edafa.web2sms.smsapi.execution;

import com.edafa.commons.concurrent.ManagedThread;
import com.edafa.commons.concurrent.ManagedThreadState;
import com.edafa.smsgw.dalayer.enums.SMSStatusName;
import com.edafa.smsgw.smshandler.exceptions.FailedSMSException;
import com.edafa.smsgw.smshandler.smpp.SMPPModuleAdapter;
import com.edafa.smsgw.smshandler.sms.SMS;
import com.edafa.smsgw.smshandler.sms.interfaces.SMSHandlingManagerLocal;
import com.edafa.utils.rate.controller.exceptions.InvalidParameterException;
import com.edafa.utils.rate.controller.exceptions.NoSuchLimiterException;
import com.edafa.utils.rate.controller.interfaces.RateController;
import com.edafa.utils.rate.controller.interfaces.TimerListener;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.campaign.execution.interfaces.CoreExecutionManagerBeanLocal;
import com.edafa.web2sms.core.execution.SmsHolderStatus;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mahmoud
 */
public class SmsApiHandler extends ManagedThread implements TimerListener {

    private final RateController rateController;
    private List<ApiSmsHolder> delayedSmsList = new LinkedList<ApiSmsHolder>();
    private Map<String, SmsApiAccountInfo> smsApiAccountsInfo = new HashMap<>();
    private static final BlockingQueue<ApiSmsHolder> smsApiQueue = new ArrayBlockingQueue<>((int) Configs.SMSAPI_QUEUE_SIZE.getValue());
    AtomicBoolean newSecondHead = new AtomicBoolean(false);
    private Logger smsLogger = LogManager.getLogger(LoggersEnum.SMS_API.name());
    private Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());
    protected CoreExecutionManagerBeanLocal campaignsManagerBean;
    protected SMPPModuleAdapter smppAdapter;
    protected SMSHandlingManagerLocal smsHandlingManager;
    protected AppErrorManagerAdapter appErrorManagerAdapter;

    protected int handlerId;
    protected String threadName;
    protected String logName;
    protected AtomicBoolean pollState = new AtomicBoolean(false);

    public static SmsApiHandler smsApiHandler;

    public SmsApiHandler(int handlerId, CoreExecutionManagerBeanLocal campaignsManagerBean, RateController rateController) {
        this.rateController = rateController;
        this.campaignsManagerBean = campaignsManagerBean;
        this.handlerId = handlerId;
        this.smsHandlingManager = campaignsManagerBean.getSMSHandlingManager();
        this.smppAdapter = smsHandlingManager.getSmppAdapter();
        this.appErrorManagerAdapter = campaignsManagerBean.getAppErrorManager();
        rateController.registerTimerListener(this);

        threadName = "SmsApiHandler(" + handlerId + ")";
        logName = threadName + ": ";
        setName(threadName);
    }

    @Override
    public void process() {
        smsLogger.info("SmsApiHandler handler is starting .........");

        String logId = "";
        while (true) {
            try {
                logId = "";
                if (!checkProcessingState()) {
                    if (getProcessingState().equals(ManagedThreadState.STOPPING)) {
                        smsLogger.info("SmsApiHandler will be stopped, remaining SMSs in queue= " + smsApiQueue.size() + ", in pending list=" + delayedSmsList.size());
                        logNotSentSMSs();
                        break;
                    }
                }

                if (!smppAdapter.isReadyForSubmitting()) {
                    smsLogger.info("Will wait until smpp module is ready, remaining SMSs in queue= " + smsApiQueue.size() + ", in pending list=" + delayedSmsList.size());
                    int smppWaitingTime = (int) Configs.SMPP_MODULE_READINESS_WAITING_TIME
                            .getValue();
                    try {
                        smppAdapter.waitUntilReadyForSending(smppWaitingTime,
                                TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        smsLogger.info("Thread is interrupted while waiting for smpp module, remaining SMSs in queue= " + smsApiQueue.size() + ", in pending list=" + delayedSmsList.size());
                    }
                    continue;
                }
                ApiSmsHolder smsHolder = null;
                try {
                    if (smsLogger.isDebugEnabled()) {
                        smsLogger.debug("Polling SMS from the SmsApiQueue, SmsApiQueue: " + smsApiQueue.size());
                    }
                    if (smsApiQueue.isEmpty()) { // gomaa: check if this faster or using (1000 - Calendar.getInstance().get(Calendar.MILLISECOND))
                        if (smsLogger.isDebugEnabled()) {
                            smsLogger.debug("Will wait on smsApiQueue until new window.");
                        }
                        pollState.set(true);
                        smsHolder = smsApiQueue.poll(2000, TimeUnit.MILLISECONDS);
                        pollState.set(false);
                    } else {
                        if (smsLogger.isDebugEnabled()) {
                            smsLogger.debug("Will poll sms from smsApiQueue.");
                        }
                        smsHolder = smsApiQueue.poll();
                    }
                } catch (InterruptedException ex) {
                    smsLogger.info("Thread is interrupted while waiting to take from the smsApiQueue");
                }

                if (smsHolder == null) {
                    smsLogger.info("No SMSApi sms to pull");
                } else {
                    logId = smsHolder.getLogId();
                    if (smsHolder.isSMSAllowedToSend()) {
                        if (smsLogger.isTraceEnabled()) {
                            smsLogger.trace(smsHolder.getLogId() + " allowed to send");
                        }
                        sendSms(smsHolder.getSms(), smsHolder.getLogId());
                    } else {
                        if (smsLogger.isDebugEnabled()) {
                            smsLogger.debug(smsHolder.getLogId() + " NOT allowed to send, will send it later");
                        }
                        if (smsHolder.isCachedSms()) {
                            delayedSmsList.add(smsHolder);
                        } else {
                            delayedSmsList.add(0, smsHolder);
                        }
                    }
                }
                logId = "";

                if (newSecondHead.get()) {
                    newSecondHead.set(false);
                    for (SmsApiAccountInfo smsApiAccountInfo : smsApiAccountsInfo.values()) {
                        smsApiAccountInfo.setAccountAllowedToSend(true);
                    }
                    if (!delayedSmsList.isEmpty()) {
                        if (smsLogger.isDebugEnabled()) {
                            smsLogger.debug("Try to send delayed SMSs " + ", count=" + delayedSmsList.size());
                        }
                        long time = System.currentTimeMillis();
                        int count = 0;
                        ApiSmsHolder delayedSmsHolder;
                        for (ListIterator<ApiSmsHolder> delayedSmsIt = delayedSmsList.listIterator(); delayedSmsIt.hasNext();) {
                            delayedSmsHolder = delayedSmsIt.next();
                            logId = delayedSmsHolder.getLogId();
                            if (smsLogger.isTraceEnabled()) {
                                smsLogger.trace(delayedSmsHolder.getLogId() + "Try to resend with " + delayedSmsHolder.getRequiredPermits() + " permits");
                            }

                            if (delayedSmsHolder.isSMSAllowedToSend()) {
                                count++;
                                if (smsLogger.isTraceEnabled()) {
                                    smsLogger.trace(delayedSmsHolder.getLogId() + "Allowed to send");
                                }
                                sendSms(delayedSmsHolder.getSms(), delayedSmsHolder.getLogId());
                                delayedSmsIt.remove();
                            } else {
                                if (smsLogger.isTraceEnabled()) {
                                    smsLogger.trace(delayedSmsHolder.getLogId() + "Not allowed to send, remaining " + delayedSmsHolder.getRequiredPermits() + " permits");
                                }
                            }
                            logId = "";
                        }
                        time = System.currentTimeMillis() - time;
                        smsLogger.info("sent delayed SMSs, countSent=" + count + ", timeMillies=" + time + ", remaining=" + delayedSmsList.size());
                    }
                }
            } catch (NoSuchLimiterException e) {
                String msg = "NoSuchLimiterException cought in " + getName() + " thread";
                smsLogger.error(logId + msg, e);
                reportAppError(AppErrors.GENERAL_ERROR, "The SMS failed to be submitted");
            } catch (InvalidParameterException e) {
                String msg = "InvalidParameterException cought in " + getName() + " thread";
                smsLogger.error(logId + msg, e);
                reportAppError(AppErrors.GENERAL_ERROR, "The SMS failed to be submitted");
            } catch (Exception e) {
                String msg = "Unhandled exception cought in " + getName() + " thread";
                smsLogger.error(logId + msg, e);
                reportAppError(AppErrors.GENERAL_ERROR, "The SMS failed to be submitted");
            } catch (Throwable t) {
                appLogger.fatal(logId + "Unhandled Throwable cought in " + getName() + " thread", t);
            }
        }

    }

    private void sendSms(SMS sms, String logId) {
        int submittedSMSSegments = 0;
        try {
            if (smsLogger.isTraceEnabled()) {
                smsLogger.trace(logId + "Submitting " + sms.toString());
            }
            sms.setProcessingDate(new Date());
            submittedSMSSegments = smppAdapter.submitSMS(sms, (int) Configs.SUBMIT_SMS_WAITING_TIME.getValue(), TimeUnit.SECONDS);

            if (sms.isSubmitted()) {
                if (smsLogger.isDebugEnabled()) {
                    smsLogger.debug(logId + "is successfully submit " + submittedSMSSegments + " SMSSegments.");
                }
            } else {
                smsLogger.warn(logId + "did NOT send, status: " + sms.getStatus() + ", segments count=" + sms.getSegmentsCount() + ", submitted segments count=" + sms.getSubmittedSegCount());
            }

        } catch (FailedSMSException e) {
            reportAppError(AppErrors.FAILED_OPERATION, "SMSAPI submition failure");
            smsLogger.warn(logId + e.getMessage());
            if (appLogger.isDebugEnabled()) {
                appLogger.debug(sms.logId() + e.getMessage(), e);
            }
        } catch (Exception e) {
            String msg = "Unhandled exception cought in " + getName() + " thread";
            smsLogger.error(msg, e);
            reportAppError(AppErrors.GENERAL_ERROR, "The SMS failed to be submitted");
        }
    }

    private void reportAppError(AppErrors error, String msg) {
        appErrorManagerAdapter.reportAppError(error, threadName, msg);
    }

    private void waitSomeTime(long timeout, TimeUnit timeUnit)
            throws InterruptedException {
        sleep(timeUnit.toMillis(timeout));
    }

    private boolean checkProcessingState() {
        if (getProcessingState() == ManagedThreadState.RUNNING) {
            return true;
        } else {
            return false;
        }
    }

    public SmsApiAccountInfo getSmsApiAccountInfo(String accountId) {
        SmsApiAccountInfo smsApiAccountInfo = smsApiAccountsInfo.get(accountId);
        if (smsApiAccountInfo == null) {
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug("Account " + accountId + " will be initalized");
            }
            synchronized (this) {
                if (smsApiAccountInfo == null) {
                    smsApiAccountInfo = new SmsApiAccountInfo((int) Configs.SMSAPI_MAX_CONCURRENT_PROCESSING_SMS_PER_ACCOUNT.getValue());
                    smsApiAccountsInfo.put(accountId, smsApiAccountInfo);
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace("Account " + accountId + " initalized successfully");
                    }
                }
            }
        }
        return smsApiAccountInfo;
    }

    public void submitSmsApiSMS(ApiSmsHolder smsHolder, long submitSmswaitingTime, TimeUnit timeUnit) {
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(smsHolder.getTransLogId() + "Submit SMS " + smsHolder.toString());
        }

        boolean offerresult = false;
        if ((!smsHolder.isCachedSms() && smsHolder.getSmsApiAccountInfo().incrementAccountConcurrentSmsCounter())
                || (smsHolder.isCachedSms() && delayedSmsList.size() <= ((int) Configs.SMSAPI_PENDING_LIST_SIZE.getValue()))) {
            try {
                smsHolder.setSmsSendingRateController(rateController);
                if (smsLogger.isDebugEnabled()) {
                    smsLogger.debug(smsHolder.getLogId() + "Offering SMS to the SmsApiQueue");
                }
                offerresult = smsApiQueue.offer(smsHolder, submitSmswaitingTime, timeUnit);
            } catch (InterruptedException ex) {
                //TODO .. MUST BE .. Will lose sms try to offer!!
                smsLogger.warn(smsHolder.getSms().logId() + "Thread is interrupted while waiting to put at the SmsApiQueue");
            }

            if (offerresult) {
                smsHolder.setSmsHolderStatus(SmsHolderStatus.SUBMITTED);
            } else {
                smsHolder.setSmsStatus(SMSStatusName.FAILED_TO_SEND);
                smsHolder.setSmsHolderStatus(SmsHolderStatus.FAILED_TO_SUBMITTED);
                smsHolder.getSmsApiAccountInfo().decrementAccountConcurrentSmsCounter();
                if (smsLogger.isDebugEnabled()) {
                    smsLogger.debug(smsHolder.getLogId() + "Can NOT be offered to smsapi queue.");
                }
            }
        } else if (!smsHolder.isCachedSms()) {
            smsHolder.setSmsStatus(SMSStatusName.FAILED_TO_SEND);
            smsHolder.setSmsHolderStatus(SmsHolderStatus.SENDING_RATE_EXCEEDED);
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(smsHolder.getLogId() + "exceeded max count " + smsHolder.getSmsApiAccountInfo().toString());
            }
        } else {
            smsHolder.setSmsStatus(SMSStatusName.FAILED_TO_SEND);
            smsHolder.setSmsHolderStatus(SmsHolderStatus.CACHE_LIMIT_EXCEEDED);
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(smsHolder.getLogId() + "pending list exceeded limit size, size=" + delayedSmsList.size());
            }
        }
    }

    @Override
    public void windowStarted() {
        newSecondHead.set(true);
        if (pollState.get()) {
            this.interrupt();
            pollState.set(false);
        }
    }

    @Override
    public String dumpId() {
        return threadName;
    }

    private void logNotSentSMSs() {
        for (ApiSmsHolder apiSmsHolder : smsApiQueue) {
            smsLogger.warn(apiSmsHolder.getLogId() + "Will shutdown CORE, SMS NOT sent, " + apiSmsHolder.getSms().dumpInfoData());
        }
        for (ApiSmsHolder apiSmsHolder : delayedSmsList) {
            smsLogger.warn(apiSmsHolder.getLogId() + "Will shutdown CORE, SMS NOT sent, " + apiSmsHolder.getSms().dumpInfoData());
        }
    }
}
