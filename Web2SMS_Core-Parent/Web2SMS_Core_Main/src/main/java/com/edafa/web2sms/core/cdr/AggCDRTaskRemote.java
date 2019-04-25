package com.edafa.web2sms.core.cdr;

import javax.ejb.Remote;

import com.edafa.jee.scheduler.api.ScheduledTaskExecutor;

@Remote
public interface AggCDRTaskRemote extends ScheduledTaskExecutor {

}
