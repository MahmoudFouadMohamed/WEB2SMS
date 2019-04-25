package com.edafa.web2sms.campaign.execution.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.model.Campaign;

@Local
public interface CampaignsNotificationSMSLocal {

	void sendNotificationSMS(Campaign campaign);

	void sendNotificationSMS(List<Campaign> campaigns);

}
