package com.sicpa.standard.sasscl.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.DeadEvent;
import com.sicpa.standard.client.common.eventbus.service.IDeadEventListener;

/**
 * responsible to handle event that have no listener register in the event bus
 * 
 * @author DIelsch
 * 
 */
public class DeadEventListener implements IDeadEventListener {

	private static final Logger logger = LoggerFactory.getLogger(DeadEventListener.class);

	public void handleDeadEvent(DeadEvent deadEvent) {
		logger.warn("no handler for " + deadEvent.getEvent().getClass() + " event=" + deadEvent.getEvent() + " source="
				+ deadEvent.getSource());
	}
}
