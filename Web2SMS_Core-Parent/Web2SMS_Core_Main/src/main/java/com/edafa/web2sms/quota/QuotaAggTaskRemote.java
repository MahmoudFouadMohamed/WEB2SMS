package com.edafa.web2sms.quota;

import javax.ejb.Remote;

import com.edafa.jee.scheduler.api.ScheduledTaskExecutor;

@Remote
public interface QuotaAggTaskRemote extends ScheduledTaskExecutor {

}
