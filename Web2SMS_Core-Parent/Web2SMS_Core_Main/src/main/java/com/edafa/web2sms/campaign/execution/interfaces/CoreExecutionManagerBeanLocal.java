package com.edafa.web2sms.campaign.execution.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.smsgw.smshandler.sms.interfaces.SMSHandlingManagerLocal;
import com.edafa.web2sms.account.AccountHandler;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.campaign.execution.CampaignsHandler;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignExecutionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.CampaignStatus;

@Local
public interface CoreExecutionManagerBeanLocal {

    List<CampaignsHandler> getCampaignsHandlers();

    int addNewCampaignsHandler();

    CampaignDaoLocal getCampaignDao();

    SMSHandlingManagerLocal getSMSHandlingManager();

    boolean checkSMPPModuleReadiness();

    void waitUntilSMPPMoudleReady(long timeout) throws InterruptedException;

    CampaignStatusDaoLocal getCampaignStatusDao();

    SMSStatusDaoLocal getSmsStatusDao();

    CampaignsNotificationSMSLocal getCampaignsNotificationSMS();

    CampaignListsDaoLocal getCampaignListsDao();

    ContactDaoLocal getContactDao();

    CampaignExecutionDaoLocal getCampaignExecutionDao();

    CampaignStatus getCampaignStatus(CampaignStatusName statusName);

    List<CampaignStatus> getArchiveCampaignStatusList();

    List<CampaignStatus> getActiveCampaignStatusList();

    List<CampaignStatus> getAllCampaignStatusList();

    AppErrorManagerAdapter getAppErrorManager();

    AccountStatusDaoLocal getAccountStatusDao();

    AccountHandler getAccountHandler();

    void setAccountHandler(AccountHandler accountHandler);

    void campaignsHandlerThreadTerminated(int handlerId);
}
