package com.edafa.web2sms.service.campaign.interfaces;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.service.model.TrxInfo;
import javax.ejb.Remote;

@Remote
public interface CampaignManagementBeanRemote {
    int holdRunningCampaigns(TrxInfo trxInfo, String accountId) throws DBException;

}
