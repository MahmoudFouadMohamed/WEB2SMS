package com.edafa.web2sms.campaign.execution;

import com.edafa.web2sms.core.execution.SmsHolder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;

import com.edafa.jee.apperr.monitor.AppErrMonitor;
import com.edafa.commons.concurrent.ManagedThread;
import com.edafa.commons.concurrent.ManagedThreadState;
import com.edafa.smsgw.smshandler.smpp.SMPPModuleAdapter;
import com.edafa.smsgw.smshandler.sms.interfaces.SMSHandlingManagerLocal;
import com.edafa.utils.rate.controller.interfaces.RateController;
import com.edafa.utils.rate.controller.interfaces.TimerListener;
import com.edafa.web2sms.account.AccountHandler;
import com.edafa.web2sms.account.exceptions.NoAccountQuotaHandleFoundException;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.campaign.execution.exception.CampaignExecutionException;
import com.edafa.web2sms.campaign.execution.exception.RetrieveContactsException;
import com.edafa.web2sms.campaign.execution.interfaces.CoreExecutionManagerBeanLocal;
import com.edafa.web2sms.campaign.execution.interfaces.CampaignsNotificationSMSLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignExecutionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignAction;
import com.edafa.web2sms.dalayer.model.CampaignExecution;
import com.edafa.web2sms.dalayer.model.CampaignLists;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.quota.interfaces.AccountQuotaHandle;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.GenericLimiters;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;

public class CampaignsHandler extends ManagedThread implements TimerListener {

    protected Logger campEngLogger = LogManager.getLogger(LoggersEnum.CAMP_EXE_ENGINE.name());
    protected Logger campaignsLogger = LogManager.getLogger(LoggersEnum.CAMP_EXE.name());
    protected Logger acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_HANDLER.name());

    protected Map<String, CampaignHandle> activeCampaignHandles;
    protected List<Campaign> campUpdateList;
    protected List<List<CampaignLists>> listUpdateLists;
    protected int handlerId;
    protected AccountHandler accountHandler;
    protected CoreExecutionManagerBeanLocal coreManagerBean;
    protected SMSHandlingManagerLocal smsHandlingManager;
    protected SMPPModuleAdapter smppAdapter;
    protected CampaignDaoLocal campaignDao;
    protected ContactDaoLocal contactDao;
    protected CampaignListsDaoLocal campaignListsDao;
    protected CampaignExecutionDaoLocal campaignExecutionDao;
    protected CampaignsNotificationSMSLocal campaignsNotificationSMS;
    protected AppErrorManagerAdapter appErrorManagerAdapter;
    protected RateController rateController;
    protected List<SmsHolder> delayedSmsList = new LinkedList<SmsHolder>();
    // For CampaignHandle
    CampaignStatusDaoLocal campaignStatusDao;
    SMSStatusDaoLocal smsStatusDao;
    // AccountTierDaoLocal acctTierDao;
    int rateIneligibleCampaigns = 0;

    protected String threadName;
    protected String logName;

    // This flag is set by campaigns dispatcher task
    public boolean campaignsUpdateStatusFlag = false;
    Timer timer = new Timer();
    AtomicBoolean newSecondHead = new AtomicBoolean(false);

    public CampaignsHandler(int handlerId, CoreExecutionManagerBeanLocal coreManagerBean, RateController rateController) {

        this.coreManagerBean = coreManagerBean;
        this.campaignDao = coreManagerBean.getCampaignDao();
        this.handlerId = handlerId;
        this.smsHandlingManager = coreManagerBean.getSMSHandlingManager();
        this.smppAdapter = smsHandlingManager.getSmppAdapter();
        this.contactDao = coreManagerBean.getContactDao();
        this.campaignListsDao = coreManagerBean.getCampaignListsDao();
        this.campaignExecutionDao = coreManagerBean.getCampaignExecutionDao();
        this.accountHandler = coreManagerBean.getAccountHandler();
        this.appErrorManagerAdapter = coreManagerBean.getAppErrorManager();
        this.rateController = rateController;

        threadName = "CampaignsHandler(" + handlerId + ")";
        logName = threadName + ": ";
        setName(threadName);
        activeCampaignHandles = new ConcurrentHashMap<String, CampaignHandle>();
        campUpdateList = new ArrayList<Campaign>();
        listUpdateLists = new ArrayList<List<CampaignLists>>();
        campaignStatusDao = coreManagerBean.getCampaignStatusDao();
        smsStatusDao = coreManagerBean.getSmsStatusDao();
        campaignsNotificationSMS = coreManagerBean.getCampaignsNotificationSMS();

        rateController.registerTimerListener(this);

    }

    public int getHandlingCampaignsCount() {
        return activeCampaignHandles.size();
    }

    @Override
    public void process() {
        campEngLogger.info("Campaigns handler is starting .........");
        // Processing cycle
        int sentSMSSegments = 0;
mainLoop:
        while (true) {
            int bulkSMSSentCount = (int) Configs.BULK_SMS_SEND_COUNT.getValue();
            try {
                if (!checkProcessingState()) {

                    if (getProcessingState().equals(ManagedThreadState.STOPPING)) {
                        campEngLogger.info("Campaigns handler  will be stopped");
                        stopCampaignsHandling("Campaigns Handler is stoped");
                        try {
                            updateCampaigns();
                        } catch (DBException e) {
                            handleDBException(e);
                            break mainLoop;
                        }
                    }
                    // setProcessingState(ManagedThreadState.STOPPED);
                    break mainLoop;
                }
                handleRateIneligibleCampaigns();

                if (!smppAdapter.isReadyForSubmitting()) {
                    // TODO: set counter for some times then hold the
                    // expired campaigns
                    holdExpiredCampaigns("Campaign has been expired while running and smpp module not ready");
                    try {
                        updateCampaigns();
                    } catch (DBException e) {
                        handleDBException(e);
                        // break mainLoop;
                        continue;
                    }
                    campEngLogger.info("Will wait until smpp module is ready");
                    int smppWaitingTime = (int) Configs.SMPP_MODULE_READINESS_WAITING_TIME.getValue();
                    try {
                        smppAdapter.waitUntilReadyForSending(smppWaitingTime, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        campEngLogger.info("Thread is interrupted while waiting for smpp module");
                    }
                    continue mainLoop;
                }

                if (checkForCampaignsUpdates()) {
                    campEngLogger.info("Found new executable campaigns");
                    // Sending notification sms before update the status
                    // sendNotificationSMS();

                    handleCampaignsState();

                    try {
                        // TODO: Check this
                        updateCampaigns();
                        // updateCampaignsStatus();
                    } catch (DBException e) {
                        handleDBException(e);
                        // break mainLoop;
                    }

                    // Sending notification sms after update the status
                    sendNotificationSMS();

                    setUpdateCampaignsFlag(false);
                }

                // Sending SMSs
                sentSMSSegments += processCampaigns();

                if ( // smppAdapter.reachedSendingRate() ||
                        sentSMSSegments >= bulkSMSSentCount) {
                    sentSMSSegments = 0;
                    try {
                        updateCampaignsExecutionInfo();
                    } catch (DBException e) {
                        campEngLogger.error("Cannot updates campaigns processing info into datebase, will stop now", e);
                        logCampaignsHandlingState("Cannot updates campaign processing into database.");
                        // setProcessingState(ManagedThreadState.STOPPED);
                        // break mainLoop;
                    }
                }
            } catch (Exception e) {
                try {
                    handleException(e);
                } catch (Exception e2) {
                    campEngLogger.error("Error while handling Exception");
                }
            } catch (Throwable t) {
                campEngLogger.fatal("Unhandled Throwable cought in " + getName() + " thread", t);
            }
        }

        setProcessingState(ManagedThreadState.STOPPED);
        campEngLogger.info(logName + "has been terminated ..........");
    }

    private void handleDBException(DBException e) {
        String msg = "Cannot persist campaigns processing info";
        campEngLogger.error(msg + ", For campaigns detailed processing info check campaigns log file", e);
        // stopCampaignsHandling("Cannot persist campaignHandle processing info database exception occured");
        logCampaignsHandlingState("Cannot persist campaign processing info database exception occured");
        for (Campaign updatedCampaign : campUpdateList) {
            campaignsLogger.info(updatedCampaign.logId() + "Cannot be updated with execution state to database");
            CampaignHandle campaignHandle = activeCampaignHandles.get(updatedCampaign.getCampaignId());

            if (campaignHandle != null && !campaignHandle.isRunning()) {
                activeCampaignHandles.remove(updatedCampaign.getCampaignId());
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(updatedCampaign.logId() + "Removed campaign handle");
                }
            }
        }

        if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
            reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
        } else {
            reportAppError(AppErrors.DATABASE_ERROR, "DB error");
        }

    }

    //note: called when there is DataBase failure while retrieving contacts for a campaignHandle either at campaignHandle start or while processing.
    private void handleRetrieveContactsFailure(String campId, RetrieveContactsException e) {
        String msg = campId + "Cannot Retrieve Contacts due to DataBase Failure";
        campEngLogger.error(msg, e);

        if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
            reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
        } else {
            reportAppError(AppErrors.DATABASE_ERROR, "DB error");
        }

    }

    private void handleException(Exception e) {
        String msg = "Unhandled exception cought in " + getName() + " thread";
        campEngLogger.error(msg, e);

        if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
            reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
        } else {
            reportAppError(AppErrors.GENERAL_ERROR, msg);
        }
    }

    protected void reportAppError(AppErrors error, String msg) {
        int waitingTimeout = (Integer) Configs.THREAD_EXCEPTION_SLEEP_TIME.getValue();
        AppErrMonitor almMonitor = appErrorManagerAdapter.reportAppError(error, threadName, msg);

        if (almMonitor == null || (almMonitor != null && almMonitor.reachedThreshold() != null)) {

            campEngLogger.fatal("App Error " + error + " reached threshold, wait " + waitingTimeout);
            //campEngLogger.fatal("CampaignsHandler " + handlerId + " will be stoped ");
            try {
                waitSomeTime(waitingTimeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e1) {
            }
//            forceStopPrcessing();
//            coreManagerBean.campaignsHandlerThreadTerminated(handlerId);
        }
    }

    private void waitSomeTime(long timeout, TimeUnit timeUnit) throws InterruptedException {
        sleep(timeUnit.toMillis(timeout));
    }

    private void sendNotificationSMS() {
        // campaignsNotificationSMS.sendNotificationSMS(campUpdateList);
    }

    private void holdExpiredCampaigns(String holdReason) {
        clearUpdateLists();
        for (Iterator<CampaignHandle> it = activeCampaignHandles.values().iterator(); it.hasNext();) {
            CampaignHandle campaignHandle = it.next();
            if (!campaignHandle.isValidForExecution()) {
                campaignHandle.holdCampaign(holdReason);
                addToUpdateList(campaignHandle);
                it.remove();
            }
        }
    }

    private void stopCampaignsHandling(String holdReason) {
        clearUpdateLists();
        for (Iterator<CampaignHandle> it = activeCampaignHandles.values().iterator(); it.hasNext();) {
            CampaignHandle campaignHandle = it.next();
            campaignHandle.holdCampaign(holdReason);
            campaignHandle.unSetCampaignHandler();
            addToUpdateList(campaignHandle);
            it.remove();
        }
    }

    //NOT used!
    public void holdCampaigns(List<Campaign> campaigns, String holdReason) {
        clearUpdateLists();
        for (Iterator<Campaign> it = campaigns.iterator(); it.hasNext();) {
            Campaign campaign = it.next();
            CampaignHandle campaignHandle = activeCampaignHandles
                    .remove(campaign.getCampaignId());
            if (campaignHandle == null) {
                AccountQuotaHandle accountQuotaHandle = accountHandler
                        .getAccountQuotaHandle(campaign);

                campaignHandle = new CampaignHandle(campaign, smppAdapter,
                        campaignStatusDao, smsStatusDao, campaignListsDao,
                        contactDao, accountQuotaHandle, this, new HashSet<String>()); // note: just dummy list
            }
            campaignHandle.holdCampaign(holdReason);
        }
    }

    private boolean checkProcessingState() {
        while (isRunning() && activeCampaignHandles.isEmpty()) {
            int watingPeriod = (int) Configs.CAMPAIGNS_HANDLER_WAITING_TIME.getValue();
            campEngLogger.info("Waiting for new campaigns to handle");
            waitToProcessCampaigns(watingPeriod);
        }

        if (getProcessingState() == ManagedThreadState.RUNNING) {
            return true;
        } else {
            return false;
        }
    }

    private void handleRateIneligibleCampaigns() {
        if ((activeCampaignHandles.size() <= rateIneligibleCampaigns) || (newSecondHead.get())) {
            if (campEngLogger.isDebugEnabled()) {
                campEngLogger.debug("RateIneligibleCampaigns: " + rateIneligibleCampaigns + ", activeCampaignHandles: " + activeCampaignHandles.size());
            }
            if (newSecondHead.get()) {
                newSecondHead.set(false);
            } else {
                campEngLogger.info("All Active campaigns exceeded sending rate, will wait until new sending frame.");
                waitToProcessCampaigns(2000);
            }
            rateIneligibleCampaigns = 0;
            for (CampaignHandle campaignHandle : activeCampaignHandles.values()) {
                if (campaignHandle.isDelayedSmsExist()) {
                    if (campEngLogger.isDebugEnabled()) {
                        //TODO log remain permits here or before :)
                        campEngLogger.debug(campaignHandle.getCampaign().logId() + "has delayed sms: " + campaignHandle.delayedSmsHolder.getSms().logId());
                    }

                    try {
                        if (campaignHandle.sendDelayedSms()) {
                            campaignHandle.setRateEligible(true);
                        }
                    } catch (CampaignExecutionException e) {
                        campaignHandle.setStateTranstion();
                        setUpdateCampaignsFlag(true);
                    } catch (Exception e) {
                        campEngLogger.error(campaignHandle.getCampaign().logId() + "Campaign execution failed: ", e);
                        campaignHandle.handleExecutionException(e);
                        campaignHandle.setStateTranstion();
                        setUpdateCampaignsFlag(true);
                    }
                } else {
                    campaignHandle.setRateEligible(true);
                }
            }
        }
    }

    private void waitToProcessCampaigns(long watingPeriod) {
        synchronized (activeCampaignHandles) {
            try {
                activeCampaignHandles.wait(watingPeriod);
            } catch (InterruptedException e) {
                campEngLogger.debug("The thread is interrupted");
            }
        }
    }

    private void updateCampaigns() throws DBException {
        int campUpdateListSize = campUpdateList.size();
        if (campUpdateListSize <= 0) {
            campEngLogger.warn("Campaigns update list is empty");
            return;
        }

        campEngLogger.info("Will update campaigns execution state to database, camp list size=" + campUpdateListSize);
        campaignDao.updateCampaignsExecutionState(campUpdateList, listUpdateLists);

        for (Campaign updatedCampaign : campUpdateList) {
            campaignsLogger.info(updatedCampaign.logId() + "updated with execution state to database, Execution state: " + updatedCampaign.logExecutionState());
            CampaignHandle campaignHandle = activeCampaignHandles.get(updatedCampaign.getCampaignId());
            if (campaignHandle != null) {
                campaignHandle.unsetStateTranstion();
            }
        }

        if (campaignsLogger.isDebugEnabled()) {
            campEngLogger.debug("Updated campaigns execution state to database, camp list size=" + campUpdateList.size());
        }
        clearUpdateLists();
        setUpdateCampaignsFlag(false);
    }

    private void updateCampaignsExecutionInfo() throws DBException, NoAccountQuotaHandleFoundException {
        clearUpdateLists();
        for (Iterator<CampaignHandle> it = activeCampaignHandles.values().iterator(); it.hasNext();) {
            CampaignHandle campaignHandle = it.next();
            addToUpdateList(campaignHandle);
        }

        campEngLogger.info("Will update campaigns execution info into database, camp list size=" + campUpdateList.size());
        // TODO: Check this
        campaignDao.updateCampaignsExecutionInfo(campUpdateList, listUpdateLists);
        if (campEngLogger.isDebugEnabled()) {
            campEngLogger.debug("Updated campaigns execution info into database");
        }

        for (Campaign updatedCampaign : campUpdateList) {
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(updatedCampaign.logId() + "Execution info: " + updatedCampaign.getCampaignExecution().logExecutionInfo());
            }
        }
        accountHandler.updateAllAccountQuota();
    }

    private void logCampaignsHandlingState(String logStr) {
        campEngLogger.info("Will log campaigns info into log files");

        // First, log the campaigns that are not found in activeCampaignHandles
        // (deleted previously)
        for (Iterator<Campaign> it = campUpdateList.iterator(); it.hasNext();) {
            Campaign campaign = it.next();
            CampaignExecution campaignExecution = campaign.getCampaignExecution();
            if (activeCampaignHandles.get(campaign.getCampaignId()) == null) {
                campaignsLogger.error(campaign.logId() + logStr + " " + campaignExecution.logExecutionFullInfo());
            }
        }
        for (Iterator<CampaignHandle> it = activeCampaignHandles.values().iterator(); it.hasNext();) {
            Campaign campaign = it.next().getCampaign();
            CampaignExecution campaignExecution = campaign.getCampaignExecution();
            campaignsLogger.error(campaign.logId() + logStr + " " + campaignExecution.logExecutionFullInfo());
        }
        campEngLogger.info("Campaigns logged into log files");
    }

    private boolean checkForCampaignsUpdates() {
        return campaignsUpdateStatusFlag;
    }

    private int processCampaigns() {
        int submittedSegments = 0;
        for (Iterator<CampaignHandle> it = activeCampaignHandles.values().iterator(); it.hasNext();) {
            CampaignHandle campaignHandle = it.next();
            try {
                if (campaignHandle.isRunning() && !campaignHandle.isFinished()) {
                    if (campaignHandle.isRateEligible()) {
                        submittedSegments += campaignHandle.submitNewSMS();
                    }
                } else {
                    campaignHandle.setStateTranstion();
                    setUpdateCampaignsFlag(true);
                }
            } catch (CampaignExecutionException e) {
                campaignHandle.setStateTranstion();
                setUpdateCampaignsFlag(true);
            } catch (RetrieveContactsException e) {
                handleRetrieveContactsFailure(campaignHandle.getCampaign().logId(), e);
            } catch (Exception e) {
                campEngLogger.error(campaignHandle.getCampaign().logId() + "Campaign execution failed: ", e);
                campaignHandle.handleExecutionException(e);
                campaignHandle.setStateTranstion();
                setUpdateCampaignsFlag(true);
            }
        }
        return submittedSegments;
    }

    public void addCampaign(Campaign newCampaign) {
        // campaignsQueue.add(newCampaign);
        if (campEngLogger.isDebugEnabled()) {
            campEngLogger.debug(newCampaign.logId() + "Add campaign to campaigns handles (" + handlerId + ")");
        }
        CampaignHandle campaignHandle = activeCampaignHandles.get(newCampaign.getCampaignId());
        try {
            accountHandler.refreshAfterUpdateAccountQuota(newCampaign.getAccountUser().getAccountId());
        } catch (DBException e) {
            acctLogger.error(e.getMessage(), e);
        }
        // Check if the campaignHandle is new or have an existing campaignHandle handle
        if (campaignHandle == null) {
            if (campEngLogger.isDebugEnabled()) {
                campEngLogger.debug(newCampaign.logId() + "Added to campaign handling list");
            }
            campaignsLogger.info(newCampaign.logId() + "Added to campaign handling list");
            AccountQuotaHandle accountQuotaHandle = accountHandler.getAccountQuotaHandle(newCampaign);
            Set<String> rateLimitersIds = new HashSet<>();
            
            List<SendingRateLimiter> sendingRateLimiters = newCampaign.getAccountUser().getAccount().getSendingRateLimiters();
            if (sendingRateLimiters != null && !sendingRateLimiters.isEmpty()) {
                for (SendingRateLimiter sendingRateLimiter : sendingRateLimiters) {
                    if (sendingRateLimiter.isCampEnabled()) {
                        rateLimitersIds.add(sendingRateLimiter.getLimiterId());
                    }
                }
            }
            
            if (rateLimitersIds.isEmpty()) {
                rateLimitersIds.add(GenericLimiters.CampDefaultLimiter.name());
            }
            rateLimitersIds.add(GenericLimiters.CampSystemLimiter.name());

            campaignHandle = new CampaignHandle(newCampaign, smppAdapter, campaignStatusDao, smsStatusDao, campaignListsDao, contactDao, accountQuotaHandle, this, rateLimitersIds);
            activeCampaignHandles.put(newCampaign.getCampaignId(), campaignHandle);
            if (campaignsLogger.isTraceEnabled()) {
                campaignsLogger.trace(campaignHandle.getCampaign().logId() + rateLimitersIds);
            }
        } else {
            CampaignAction action = newCampaign.getCampaignExecution().getAction();
            if (action != null) {
                String msg = newCampaign.logId() + "Already in the handler, will update the campaign action with " + action.getCampaignActionName();
                if (campEngLogger.isDebugEnabled()) {
                    campEngLogger.debug(msg);
                }
                campaignsLogger.info(msg);
                campaignHandle.setAccountQuotaHandle(accountHandler.getAccountQuotaHandle(newCampaign));
                campaignHandle.setCampaignAction(action);
                campaignHandle.setStateTranstion();
            }
        }
        setUpdateCampaignsFlag(true);

        // To start handling if was waiting for campaigns
        synchronized (activeCampaignHandles) {
            activeCampaignHandles.notify();
        }
    }

    private void handleCampaignsState() {
        campEngLogger.info("Handling active campaigns status, active campaings=" + activeCampaignHandles.size());
        clearUpdateLists();
        CampaignAction campaignAction;
        for (Iterator<CampaignHandle> it = activeCampaignHandles.values().iterator(); it.hasNext();) {
            CampaignHandle campaignHandle = it.next();

            // TODO: test stateTranstion of campaing
            // if (!campaignHandle.isStateTranstion()) {
            // campaignsLogger.debug(campaignHandle.getCampaign().logId() +
            // "is not in state transtion");
            // continue;
            // }
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaignHandle.getCampaign().logId() + "handle campaign state, current status: "
                        + campaignHandle.getCampaignStatus().getCampaignStatusName().name());
            }

            try {
                CampaignStatusName campStatus = campaignHandle.getCampaign().getStatus().getCampaignStatusName();
                campaignAction = campaignHandle.getCampaign().getCampaignExecution().getAction();
                switch (campStatus) {
                    case NEW:
                    case PARTIAL_RUN:
                        if (!campaignHandle.isValidForExecution()) {
                            campaignHandle.obsoleteCampaign();
                        } else if (campaignAction != null) {
                            switch (campaignAction.getCampaignActionName()) {
                                case APPROVE:
                                case SEND:
                                    if (campaignHandle.isTimeToExecute()) {
                                        campaignHandle.startCampaign();
                                    }
                                    break;
                                case CANCEL:
                                    campaignHandle.cancelCampaign();
                                    break;
                                default:
                                    break;
                            }

                        } else if (campaignHandle.isTimeToExecute()) {
                            campaignHandle.startCampaign();
                        }

                        addToUpdateList(campaignHandle);
                        break;

                    case RUNNING:
                        if (campaignHandle.isFinished()) {
                            campaignHandle.finalizeCampaign();

                        } else if (campaignAction != null) {
                            switch (campaignAction.getCampaignActionName()) {
                                case CANCEL:
                                    campaignHandle.cancelCampaign();
                                    break;
                                case PAUSE:
                                    campaignHandle.pauseCampaign();
                                    break;
                                case HOLD:
                                    campaignHandle.holdCampaign("Hold action is reqested on the campaign");
                                    break;
                                default:
                                    break;
                            }

                            // TODO: Add the condition to include campaigns in
                            // invalid RUNNING state
                        }

                        addToUpdateList(campaignHandle);
                        break;
                    case WAITING_APPROVAL:
                        if (!campaignHandle.isValidForExecution()) {
                            campaignHandle.obsoleteCampaign();
                        } else if (campaignAction != null) {
                            switch (campaignAction.getCampaignActionName()) {
                                case CANCEL:
                                    campaignHandle.cancelCampaign();
                                    break;
                                case APPROVE:
                                    if (((boolean) Configs.CAMP_SEND_AT_APPROVAL.getValue())) {
                                        if (campaignHandle.isTimeToExecute()) {
                                            campaignHandle.startCampaign();
                                        } else {
                                            campaignHandle.setCampaignStatus(CampaignStatusName.NEW);
                                            // campaignHandle.setCampaignAction(null);
                                        }
                                    } else {
                                        campaignHandle.approveCampaign();
                                    }
                                    break;
                                case REJECT:
                                    campaignHandle.rejectCampaign();
                                    break;
                                default:
                                    break;
                            }
                        }
                        addToUpdateList(campaignHandle);
                        break;
                    case APPROVED:
                        if (!campaignHandle.isValidForExecution()) {
                            campaignHandle.obsoleteCampaign();
                        } else if (campaignAction != null) {
                            switch (campaignAction.getCampaignActionName()) {
                                case CANCEL:
                                    campaignHandle.cancelCampaign();
                                    break;
                                case SEND:
                                    if (campaignHandle.isTimeToExecute()) {
                                        campaignHandle.startCampaign();
                                    } else {
                                        campaignHandle.setCampaignStatus(CampaignStatusName.NEW);
                                        // campaignHandle.setCampaignAction(null);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        addToUpdateList(campaignHandle);
                        break;  
                    case PAUSED:
                        if (!campaignHandle.isValidForExecution()) {
                            campaignHandle.obsoleteCampaign();
                        } else if (campaignAction != null) {
                            switch (campaignAction.getCampaignActionName()) {
                                case CANCEL:
                                    campaignHandle.cancelCampaign();
                                    break;
                                case RESUME:
                                    campaignHandle.resumeCampaign();
                                    break;
                                default:
                                    break;
                            }
                        }

                        addToUpdateList(campaignHandle);
                        break;

                    case ON_HOLD:
                        if (!campaignHandle.isValidForExecution()) {
                            campaignHandle.obsoleteCampaign();
                        } else if (campaignAction != null) {
                            switch (campaignAction.getCampaignActionName()) {
                                case CANCEL:
                                    campaignHandle.cancelCampaign();
                                    break;
                                case UN_HOLD:
                                    campaignHandle.unHoldCampaign();
                                    break;
                                default:
                                    break;
                            }
                        }

                        addToUpdateList(campaignHandle);
                        break;

                    case FAILED:
                        campUpdateList.add(campaignHandle.getCampaign());
                        break;
                    default:
                        campEngLogger.warn(campaignHandle.getCampaign().logId() + " invalid campaign status for campaign processing");
                }
                campaignsLogger.info("Will update all AccountQuota");
                accountHandler.updateAllAccountQuota();

            } catch (CampaignExecutionException e) {
                // campaignHandle.unSetCampaignHandler();
                campUpdateList.add(campaignHandle.getCampaign());
                // activeCampaignHandles.remove(campaignHandle.getCampaign().getCampaignId());
            } catch (RetrieveContactsException e) {
                handleRetrieveContactsFailure(campaignHandle.getCampaign().logId(), e);
            } catch (Exception e) {
                String logMsg = "Unhandled exception thrown while handling campaign status and actions";
                campEngLogger.error(campaignHandle.getCampaign().logId() + logMsg, e);
                campaignsLogger.error(campaignHandle.getCampaign().logId() + logMsg, e);
                campaignHandle.setCampaignStatus(CampaignStatusName.FAILED);
                campaignHandle.setCampaignStatusComment(logMsg);
                campUpdateList.add(campaignHandle.getCampaign());
            }

            if (campaignHandle.isRunning()) {
                campaignHandle.setCampaignHandlerId(handlerId);
            } else {
                campaignHandle.unSetCampaignHandler();
                activeCampaignHandles.remove(campaignHandle.getCampaign().getCampaignId());
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(campaignHandle.getCampaign().logId() + "Removed campaign handle");
                }
                AccountQuotaHandle accountHandle = accountHandler.getAccountQuotaHandle(campaignHandle.getCampaign());
                if (accountHandle != null) {
                    boolean lastRemovedCamp = accountHandle.disassociateCamp(campaignHandle.getCampaign().getCampaignId());
                    try {
                        if (lastRemovedCamp) {
                            accountHandler.finalizeAccountQuotaHandle(campaignHandle.getCampaign().getAccountUser().getAccountId());
                        } else {
                            accountHandler.refreshAfterUpdateAccountQuota(campaignHandle.getCampaign().getAccountUser().getAccountId());
                        }
                    } catch (DBException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NoAccountQuotaHandleFoundException e) {
                        // Shouldn't happend
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void clearUpdateLists() {
        campUpdateList.clear();
        listUpdateLists.clear();
    }

    private void addToUpdateList(CampaignHandle campaignHandle) {
        campUpdateList.add(campaignHandle.getCampaign());
        if (campaignHandle.getCampaignLists() != null) {
            listUpdateLists.add(campaignHandle.getCampaignLists());
        }
    }

    public void setUpdateCampaignsFlag(boolean campaignsUpdateStatusFlag) {
        this.campaignsUpdateStatusFlag = campaignsUpdateStatusFlag;
    }

    public RateController getRateController() {
        return rateController;
    }

    public List<SmsHolder> getDelayedSmsList() {
        return delayedSmsList;
    }

    public void incrementRateIneligibleCampaigns() {
        this.rateIneligibleCampaigns++;
    }

    @Override
    public void windowStarted() {
        synchronized (activeCampaignHandles) {
            newSecondHead.set(true);
            activeCampaignHandles.notify();
        }
    }

    @Override
    public String dumpId() {
        return threadName;
    }
}
