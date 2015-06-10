package com.sicpa.standard.sasscl.controller.flow;

import com.sicpa.standard.client.common.statemachine.Trigger;

/**
 * input for the flow control, to decide what to do next
 * 
 * @author DIelsch
 * 
 */
public final class ActivityTrigger extends Trigger {

	public ActivityTrigger(String name) {
		super(name);
	}

	public final static ActivityTrigger TRG_START_PRODUCTION = new ActivityTrigger("START");
	public final static ActivityTrigger TRG_STARTED = new ActivityTrigger("STARTED");
	public final static ActivityTrigger TRG_STOP_PRODUCTION = new ActivityTrigger("STOP");
	public final static ActivityTrigger TRG_ENTERSELECTION = new ActivityTrigger("ENTER SELECTION");
	public final static ActivityTrigger TRG_EXITSELECTION = new ActivityTrigger("EXIT_SELECTION");
	public final static ActivityTrigger TRG_SELECT = new ActivityTrigger("SELECT");
	public final static ActivityTrigger TRG_EXIT_APPLICATION = new ActivityTrigger("EXIT");
	public final static ActivityTrigger TRG_HARDWARE_CONNECTED = new ActivityTrigger("HW_CONNECTED");
	public final static ActivityTrigger TRG_RECOVERING_CONNECTION = new ActivityTrigger("RECOVERING_CONNECTION");
	public final static ActivityTrigger TRG_HARDWARE_DISCONNECTED = new ActivityTrigger("HW_DISCONNECTED");
	public final static ActivityTrigger TRG_HARDWARE_STOPPING = new ActivityTrigger("HW_STOPPING");

}
