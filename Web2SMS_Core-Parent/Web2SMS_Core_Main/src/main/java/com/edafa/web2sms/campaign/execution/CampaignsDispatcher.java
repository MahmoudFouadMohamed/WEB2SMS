package com.edafa.web2sms.campaign.execution;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.jee.apperr.monitor.AppErrMonitor;
import com.edafa.utils.concurrent.PeriodicTask;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.campaign.execution.interfaces.CoreExecutionManagerBeanLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

public class CampaignsDispatcher extends PeriodicTask {
	Logger campEngLogger = LogManager.getLogger(LoggersEnum.CAMP_EXE_ENGINE.name());

	protected CampaignDaoLocal campaignDao;
	protected CoreExecutionManagerBeanLocal coreManagerBean;
	protected AppErrorManagerAdapter appErrorManagerAdapter;
	protected AccountStatusDaoLocal acctStatusDao;

	protected String threadName = "CampaignsDispatcher";
	protected int campaignsHandlerCount = 0;
	protected List<CampaignsHandler> campaignsHandlers;
	protected int smppModuleReadinessWaitingTime = 5000;
	protected boolean firstTime = true;

	public CampaignsDispatcher(long campaignsDipacherCheckingPeriod, CoreExecutionManagerBeanLocal coreExecutionManagerBean) {
		super(campaignsDipacherCheckingPeriod);
		this.coreManagerBean = coreExecutionManagerBean;
		setName(threadName);
		this.campaignDao = coreExecutionManagerBean.getCampaignDao();
		this.acctStatusDao = coreExecutionManagerBean.getAccountStatusDao();
		this.appErrorManagerAdapter = coreExecutionManagerBean.getAppErrorManager();

		int campaignsHandlersCount = (int) Configs.CAMPAIGNS_HANDLER_THREAD_COUNT.getValue();
		for (int i = 0; i < campaignsHandlersCount; i++) {
			coreExecutionManagerBean.addNewCampaignsHandler();
		}

		this.campaignsHandlers = coreExecutionManagerBean.getCampaignsHandlers();
		for (int i = 0; i < campaignsHandlers.size(); i++) {
			CampaignsHandler campaignsHandler = campaignsHandlers.get(i);
			campEngLogger.info("Starting campaigns handler number:(" + (i + 1) + ")");
			campaignsHandler.startProcessing();
		}
	}

	@Override
	public void execute() {
		try {
			if (coreManagerBean.checkSMPPModuleReadiness()) {
				List<Campaign> campaignsList = null;
				int campaignValidityPeriod = (int) Configs.CAMP_EXE_VALIDITY_PERIOD.getValue();
                                int campaignApprovalValidityPeriod = (int) Configs.CAMP_APPROVAL_VALIDITY_PERIOD.getValue();
				try {

					if (firstTime) {
						campEngLogger.info("Checking database for all active campaigns");
						List<CampaignStatus> activeCampStatusList = coreManagerBean.getActiveCampaignStatusList();
						AccountStatus acctStatus = acctStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE);
						campaignsList = campaignDao.findCampaignsByStatus(activeCampStatusList, acctStatus);
						firstTime = false;
					} else {
                                            campEngLogger.info("Checking database for executable campaigns");
						campaignsList = campaignDao.findExecutableCampaigns(campaignValidityPeriod, campaignApprovalValidityPeriod);
					}
					if (campaignsList != null && !campaignsList.isEmpty()) {
						campEngLogger.info("Found Executable campaign(s)=" + campaignsList.size());
						dispatchCampaigns(campaignsList);
					} else {
						campEngLogger.info("No Executable campaigns");
					}
				} catch (DBException e) {
					handleDBException(e);
				}

			} else {
				smppModuleReadinessWaitingTime = (int) Configs.SMPP_MODULE_READINESS_WAITING_TIME.getValue();

				try {
					campEngLogger.info("Wait until smpp module is ready");
					coreManagerBean.waitUntilSMPPMoudleReady(smppModuleReadinessWaitingTime);
				} catch (InterruptedException e) {
					campEngLogger.info("CampaignsDispatcher is interrupted");
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

	private void dispatchCampaigns(List<Campaign> activeCampaignsList) {
		campEngLogger.info("Dispatching campaigns");
		for (int i = 0; i < activeCampaignsList.size(); i++) {
			CampaignsHandler campaignsHandler;
			Campaign campaign = activeCampaignsList.get(i);
			// If the campaigns is already running, assign it again to its
			// campaigns handler
			int handlerId = Integer.MAX_VALUE;
			String handlerIdStr = campaign.getCampaignExecution().getHandlerId();
			if(handlerIdStr !=null && !handlerIdStr.isEmpty())
				handlerId = Integer.parseInt(handlerIdStr);
			if (campaign.getStatus().getCampaignStatusName() == CampaignStatusName.RUNNING && handlerIdStr != null
					&& campaignsHandlers.size() > handlerId) {
				campaignsHandler = campaignsHandlers.get(handlerId);
			} else {
				campaignsHandler = selectCampaignsHandler();
			}
			campaignsHandler.addCampaign(campaign);
		}
	}

	private CampaignsHandler selectCampaignsHandler() {
		int minHandlingCampaignsCount = Integer.MAX_VALUE;
		CampaignsHandler selectedCampaignsHandler = null;
		for (int i = (campaignsHandlers.size() - 1); i >= 0; i--) {
			CampaignsHandler campaignsHandler = campaignsHandlers.get(i);
			if (campaignsHandler.getHandlingCampaignsCount() < minHandlingCampaignsCount) {
				minHandlingCampaignsCount = campaignsHandler.getHandlingCampaignsCount();
				selectedCampaignsHandler = campaignsHandler;
			}
		}
		return selectedCampaignsHandler;
	}

	protected void handleDBException(DBException e) {
		String msg = "Cannot retrieve active campaigns from database";
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
			campEngLogger.warn("App Error " + error + " reached threshold, wait " + waitingTimeout);
			try {
				waitSomeTime(waitingTimeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void waitSomeTime(long timeout, TimeUnit timeUnit) throws InterruptedException {
		sleep(timeUnit.toMillis(timeout));
	}

	@Override
	public void postCancel() {
		campEngLogger.info(getName() + " task has been cancelled");
	}

}
