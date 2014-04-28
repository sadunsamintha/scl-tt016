package com.sicpa.standard.sasscl.business.alert.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;

/**
 * 
 * base class for alert task, it defines the listeners mechanism
 * 
 */
public abstract class AbstractAlertTask implements IAlertTask {

	private static final Logger logger = LoggerFactory.getLogger(AbstractAlertTask.class);

	public AbstractAlertTask() {
	}

	protected MessageEvent queryAlert() {
		if (isAlertPresent()) {
			reset();
			return getAlertMessage();
		}
		return null;
	}

	protected abstract MessageEvent getAlertMessage();

	protected abstract boolean isAlertPresent();

	protected abstract boolean isEnabled();

	public void checkForMessage() {
		if (isEnabled()) {
			MessageEvent message = queryAlert();
			if (message != null) {
				fireAlertMessage(message);
				reset();
			}
		}
	}

	protected void fireAlertMessage(final MessageEvent message) {
		message.setSource(getAlertName());
		logger.warn("Sending alert: {}", message);
		EventBusService.post(message);
	}

	public abstract String getAlertName();

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}
}
