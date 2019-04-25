package com.edafa.web2sms.core.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.smsgw.smshandler.sms.interfaces.SMSHandlingManagerLocal;
import com.edafa.utils.rate.controller.RateControllerImp;
import com.edafa.utils.rate.controller.exceptions.DuplicateLimiterException;
import com.edafa.utils.rate.controller.exceptions.InvalidParameterException;
import com.edafa.utils.rate.controller.exceptions.NoSuchLimiterException;
import com.edafa.utils.rate.controller.interfaces.RateController;
import com.edafa.web2sms.account.AccountHandler;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.campaign.execution.CampaignsDispatcher;
import com.edafa.web2sms.campaign.execution.CampaignsHandler;
import com.edafa.web2sms.campaign.execution.interfaces.CampaignsNotificationSMSLocal;
import com.edafa.web2sms.campaign.execution.interfaces.CoreExecutionManagerBeanLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountQuotaDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountTierDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignExecutionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SendingRateLimiterDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.smsapi.execution.SmsApiHandler;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.GenericLimiters;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;
import java.util.logging.Level;

/**
 * Session Bean implementation class CoreExecutionManagerBean
 */
@Singleton
@LocalBean
@DependsOn({"SMSHandlingManager", "Web2SMS-Core_Utils/LoggingManagerBean", "Web2SMS-Core_Utils/ConfigsManagerBean"})
@Startup
public class CoreExecutionManagerBean implements CoreExecutionManagerBeanLocal {

    @EJB
    CampaignDaoLocal campaignDao;

    @EJB
    AccountQuotaDaoLocal accountQuotaDao;

    @EJB
    AccountTierDaoLocal accountTierDao;

    @EJB
    SMSHandlingManagerLocal smsHandlingManager;

    @EJB
    CampaignStatusDaoLocal campaignStatusDao;

    @EJB
    CampaignListsDaoLocal campaignListsDao;

    @EJB
    AccountStatusDaoLocal acctStatusDao;

    @EJB
    ContactDaoLocal contactDao;

    @EJB
    SMSStatusDaoLocal smsStatusDao;

    @EJB
    CampaignExecutionDaoLocal campaignExecutionDao;

    @EJB
    SendingRateLimiterDaoLocal sendingRateLimiterDao;

    @EJB
    AppErrorManagerAdapter appErrorManagerAdapter;

    @EJB
    ConfigsManagerBeanLocal configsManagerBean;
    private Logger appLogger = LogManager.getLogger(LoggersEnum.CAMP_EXE_ENGINE.name());
    private CampaignsDispatcher campaignsDispatcher;
    private AccountHandler accountHandler;
    private List<CampaignsHandler> campaignsHandlers;
    private AtomicInteger handlerIdCount = new AtomicInteger();
    RateController rateController;
    boolean smppModuleReadiness = false;

    public CoreExecutionManagerBean() {
    }

    @PostConstruct
    public void start() {
        appLogger.info("Campaigns manager will be started");
        if (smsHandlingManager.isStarted()) {
            int campaignsDipacherCheckingPeriod = (int) Configs.CAMP_DISPACHER_CHEKING_PERIOD.getValue();
            campaignsHandlers = new ArrayList<CampaignsHandler>();
            rateController = new RateControllerImp();
            try {
                configsManagerBean.initSendingRateController(rateController);
            } catch (DuplicateLimiterException | InvalidParameterException | NoSuchLimiterException e) {
                throw new Error(e);
            }
         //   configsManagerBean.registeredLimiterController(rateController);
            smsHandlingManager.registerInTimer(rateController);
            campaignsDispatcher = new CampaignsDispatcher(campaignsDipacherCheckingPeriod, this);
            accountHandler = new AccountHandler(accountTierDao, accountQuotaDao);
            appLogger.info("CoreExecutionManagerBean will start CampaignsDispatcher");
            campaignsDispatcher.start();

            if ((boolean) Configs.SMSAPI_ASYNCH_SENDING.getValue()) {
                SmsApiHandler.smsApiHandler = new SmsApiHandler(1, this, rateController);
                appLogger.info("CoreExecutionManagerBean will start SmsApiHandler");
                SmsApiHandler.smsApiHandler.start();
            }
            appLogger.info("Campaigns manager is started ............");
        } else {
            appLogger.info("Campaigns manager will not start because the SMSHandlingManager is not initialized");
        }
    }

//    public void initSendingRateController() {
//        if (appLogger.isDebugEnabled()) {
//            appLogger.debug("Configure sending rate controller");
//        }
//
//        rateController = new RateControllerImp();
//
//        try {
//            rateController.addLimiter(GenericLimiters.SystemLimiter.name(), smsHandlingManager.getSystemSendingRate());
//            rateController.addLimiter(GenericLimiters.CampSystemLimiter.name(), (int) Configs.SMS_CAMPAIGN_SENDING_RATE.getValue());
//            rateController.addLimiter(GenericLimiters.SmsapiSystemLimiter.name(), (int) Configs.SMS_SMSAPI_SENDING_RATE.getValue());
//            rateController.setSystemLimiter(GenericLimiters.SystemLimiter.name());
//        } catch (DuplicateLimiterException e) {
//            appLogger.warn("Limiter already exist " + e.getMessage());
//        } catch (InvalidParameterException e) {
//            appLogger.warn("Failed to create Limiter , invalied parameter " + e.getMessage());
//        } catch (NoSuchLimiterException e) {
//           appLogger.warn("Failed to set system Limiter " + e.getMessage());
//        }
//
//        List<SendingRateLimiter> sendingRateLimiters;
//        try {
//            sendingRateLimiters = sendingRateLimiterDao.findAll();
//        } catch (DBException ex) {
//            throw new Error("Failed to load Sending Rate Limiters configs", ex);
//        }
//        if (sendingRateLimiters != null && !sendingRateLimiters.isEmpty()) {
//            if (appLogger.isDebugEnabled()) {
//                appLogger.debug("limiters list : " + sendingRateLimiters);
//            }
//
//            for (SendingRateLimiter sendingRateLimiter : sendingRateLimiters) {
//                if (sendingRateLimiter.isCampEnabled() || sendingRateLimiter.isSmsapiEnabled()) {
//                    if (appLogger.isTraceEnabled()) {
//                        appLogger.trace("Create limiter : " + sendingRateLimiter);
//                    }
//                    
//                    try {
//                        rateController.addLimiter(sendingRateLimiter.getLimiterId(), sendingRateLimiter.getMaxPermits());
//                    } catch (DuplicateLimiterException e) {
//                        appLogger.warn("Limiter already exist " + e.getMessage());
//                    } catch (InvalidParameterException e) {
//                        appLogger.warn("Failed to create Limiter , invalied parameter " + e.getMessage());
//                    }
//                }
//            }
//        }
//    }

    @PreDestroy
    public void terminate() {
        appLogger.info("CoreExecutionManagerBean will be termianted");
        appLogger.info("Stopping campaigns dispacher");
        campaignsDispatcher.cancel();
        try {
            if (campaignsDispatcher.isAlive()) {
                campaignsDispatcher.join();
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        for (CampaignsHandler campaignsHandler : campaignsHandlers) {
            campaignsHandler.stopPrcessing();
        }
        try {
            appLogger.debug("Joining campaigns dispatcher thread");
            campaignsDispatcher.join();
            appLogger.debug("Joining campaigns handlers threads");
            for (CampaignsHandler campaignsHandler : campaignsHandlers) {
                if (campaignsHandler.isAlive()) {
                    campaignsHandler.join();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ((boolean) Configs.SMSAPI_ASYNCH_SENDING.getValue()) {
            SmsApiHandler.smsApiHandler.stopPrcessing();
            try {
                appLogger.debug("Joining SmsApiHandler thread");
                SmsApiHandler.smsApiHandler.join();
                appLogger.debug("Joining SmsApiHandler threads");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        appLogger.info("CoreExecutionManagerBean has been terminated ............");
    }

    @Override
    public void campaignsHandlerThreadTerminated(int handlerId) {
        appLogger.debug("Remove CampaignsHandler from campaignsHandler list, handlerId=" + handlerId);

        CampaignsHandler campaignsHandler = campaignsHandlers.remove(handlerId);

        if (campaignsHandlers.isEmpty()) {

            appLogger.fatal("All CampaignsHandler threads are terminated, no more requests can be handled,"
                    + " will raise error");
            appErrorManagerAdapter.reportAppError(AppErrors.FATAL_ERROR, ErrorsSource.CAMPAIGN_EXECUTION_MANAGER.name(), "All CampaignsHandler threads are terminated, No campaign can be handled");
            appLogger.fatal("CampaignsDispatcher Task will be canceled");
            campaignsDispatcher.cancel();

        } else {
            appLogger.error(campaignsHandler.getName() + " stopped, "
                    + "remaining RequestsHandler threads count=" + campaignsHandlers.size());
        }
    }

    @Override
    public List<CampaignStatus> getAllCampaignStatusList() {
        List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
        listOfStatus.add(getCampaignStatus(CampaignStatusName.NEW));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.PAUSED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.RUNNING));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.ON_HOLD));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.PARTIAL_RUN));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.WAITING_APPROVAL));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.CANCELLED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.FAILED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.FINISHED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVAL_OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.SEND_OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.REJECTED));
        return listOfStatus;
    }

    @Override
    public List<CampaignStatus> getActiveCampaignStatusList() {
        List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
        listOfStatus.add(getCampaignStatus(CampaignStatusName.NEW));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.PAUSED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.RUNNING));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.ON_HOLD));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.PARTIAL_RUN));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.WAITING_APPROVAL));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVED));
        return listOfStatus;
    }

    @Override
    public List<CampaignStatus> getArchiveCampaignStatusList() {
        List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
        listOfStatus.add(getCampaignStatus(CampaignStatusName.CANCELLED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.FAILED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.FINISHED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVAL_OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.SEND_OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.REJECTED));
        return listOfStatus;
    }

    @Override
    public CampaignStatus getCampaignStatus(CampaignStatusName statusName) {
        return campaignStatusDao.getCachedObjectByName(statusName);
    }

    @Override
    public boolean checkSMPPModuleReadiness() {
        SMSHandlingManagerLocal smsHandlingManagerLocal = smsHandlingManager;
        return smsHandlingManagerLocal.getSmppAdapter().isReadyForSubmitting();
    }

    @Override
    public void waitUntilSMPPMoudleReady(long timeout) throws InterruptedException {
        smsHandlingManager.getSmppAdapter().waitUntilReadyForSending(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public List<CampaignsHandler> getCampaignsHandlers() {
        return campaignsHandlers;
    }

    @Override
    public int addNewCampaignsHandler() {
        int handlerId = incrementHandlerId();
        campaignsHandlers.add(new CampaignsHandler(handlerId, this, rateController));
        appLogger.info("Added new campaigns handler, handlerId=" + handlerId);
        return handlerId;
    }

    @Override
    public SMSHandlingManagerLocal getSMSHandlingManager() {
        return smsHandlingManager;
    }

    @Override
    public CampaignStatusDaoLocal getCampaignStatusDao() {
        return campaignStatusDao;
    }

    @Override
    public AccountStatusDaoLocal getAccountStatusDao() {
        return acctStatusDao;
    }

//	@Override
//	public AccountTierDaoLocal getAccountTierDao() {
//		return accountTierDao;
//	}
//
//	@Override
//	public void setAccountTierDao(AccountTierDaoLocal accountTierDao) {
//		this.accountTierDao = accountTierDao;
//	}
    @Override
    public SMSStatusDaoLocal getSmsStatusDao() {
        return smsStatusDao;
    }

    @Override
    public CampaignsNotificationSMSLocal getCampaignsNotificationSMS() {
        // return campaignsNotificationSMS;
        return null;
    }

    private int incrementHandlerId() {
        return handlerIdCount.incrementAndGet();
    }

    @Override
    public CampaignDaoLocal getCampaignDao() {
        return campaignDao;
    }

    @Override
    public CampaignListsDaoLocal getCampaignListsDao() {
        return campaignListsDao;
    }

    @Override
    public ContactDaoLocal getContactDao() {
        return contactDao;
    }

    @Override
    public CampaignExecutionDaoLocal getCampaignExecutionDao() {
        return campaignExecutionDao;
    }

    @Override
    public AppErrorManagerAdapter getAppErrorManager() {
        return appErrorManagerAdapter;
    }

    public void setAppErrorManager(AppErrorManagerAdapter appErrorManagerAdapter) {
        this.appErrorManagerAdapter = appErrorManagerAdapter;
    }

    @Override
    public AccountHandler getAccountHandler() {
        if (accountHandler != null) {
            return accountHandler;
        }
        this.accountHandler = new AccountHandler(accountTierDao, accountQuotaDao);
        return this.accountHandler;

    }

    @Override
    public void setAccountHandler(AccountHandler accountHandler) {
        this.accountHandler = accountHandler;
    }

    public RateController getRateController() {
        return rateController;
    }
}
