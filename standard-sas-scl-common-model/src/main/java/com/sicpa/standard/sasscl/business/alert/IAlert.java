package com.sicpa.standard.sasscl.business.alert;

import com.sicpa.standard.sasscl.business.alert.task.IAlertTask;

/**
 * start/stop all the alert task
 * 
 * @author DIelsch
 * 
 */
public interface IAlert {
	/**
	 * start all the alert tasks
	 */
	void start();

	/**
	 * stop all the alert tasks
	 */
	void stop();

	void addTask(final IAlertTask task);
}
