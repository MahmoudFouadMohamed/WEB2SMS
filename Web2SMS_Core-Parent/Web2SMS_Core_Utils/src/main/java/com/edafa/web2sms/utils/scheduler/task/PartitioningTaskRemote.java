package com.edafa.web2sms.utils.scheduler.task;

import javax.ejb.Remote;

import com.edafa.jee.scheduler.api.ScheduledTaskExecutor;

@Remote
public interface PartitioningTaskRemote extends ScheduledTaskExecutor {

}
