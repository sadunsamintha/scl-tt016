package com.sicpa.standard.sasscl.business.alert.task;


public interface IAlertTask {

	/**
	 * start the task
	 */
	void start();

	/**
	 * stop the task
	 */
	void stop();

	/**
	 * reset the task after an alert message has been sent
	 */
	void reset();
}
