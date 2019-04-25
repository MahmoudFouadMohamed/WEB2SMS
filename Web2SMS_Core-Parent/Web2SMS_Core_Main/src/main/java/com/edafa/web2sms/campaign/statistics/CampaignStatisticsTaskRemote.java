package com.edafa.web2sms.campaign.statistics;

import javax.ejb.Remote;

import com.edafa.jee.scheduler.api.ScheduledTaskExecutor;

@Remote
public interface CampaignStatisticsTaskRemote extends ScheduledTaskExecutor {

}
