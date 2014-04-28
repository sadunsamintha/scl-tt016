package com.sicpa.standard.sasscl.business.alert.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.business.alert.IAlert;
import com.sicpa.standard.sasscl.business.alert.task.IAlertTask;

/**
 * 
 * alert module, can start and stop all the alert tasks
 * 
 * @author DIelsch
 */
public class Alert implements IAlert {

	public final static Logger logger = LoggerFactory.getLogger(Alert.class);

	protected final List<IAlertTask> tasks = new ArrayList<IAlertTask>();

	/**
	 * 
	 * @param tasks
	 *            tasks to manage
	 */
	public Alert(final IAlertTask[] tasks) {
		for (IAlertTask t : tasks) {
			addTask(t);
		}
	}

	public Alert() {
	}

	public void start() {
		synchronized (tasks) {
			for (IAlertTask aTask : tasks) {
				logger.debug("Starting alert task {}", aTask);
				aTask.reset();
				aTask.start();
			}
		}
	}

	public void stop() {
		synchronized (tasks) {
			for (IAlertTask aTask : tasks) {
				logger.debug("Stopping alert task {}", aTask);
				aTask.stop();
			}
		}
	}

	public void addTask(final IAlertTask task) {
		synchronized (tasks) {
			tasks.add(task);
		}
	}
}
