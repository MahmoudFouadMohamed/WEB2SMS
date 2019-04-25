package com.edafa.web2sms.utils.scheduler.task;

import javax.ejb.Stateless;

import com.edafa.jee.scheduler.model.ScheduledTaskStatus;

/**
 * Session Bean implementation class PartitioningTask
 */
@Stateless
public class PartitioningTask implements PartitioningTaskRemote {

	private static final long serialVersionUID = -3644797633202672521L;

	/**
	 * Default constructor.
	 */
	public PartitioningTask() {}

	@Override
	public com.edafa.jee.scheduler.model.ScheduledTaskStatus execute(com.edafa.jee.scheduler.api.ScheduledTask task) {
		System.out.println("Task excuted name= " + task.getName());
		return ScheduledTaskStatus.SUCCESS;
	}

}
