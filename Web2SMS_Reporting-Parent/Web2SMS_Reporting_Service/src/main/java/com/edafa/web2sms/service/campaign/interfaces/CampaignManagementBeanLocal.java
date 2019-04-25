package com.edafa.web2sms.service.campaign.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.model.CampaignStatus;

@Local
public interface CampaignManagementBeanLocal {

	List<CampaignStatus> getActiveCampaignStatusList();

	List<CampaignStatus> getArchiveCampaignStatusList();

	List<CampaignStatus> getPendingCampaignStatusList();

	List<CampaignStatus> getAllCampaignStatusList();

}
