package com.sicpa.standard.sasscl.messages;

import com.sicpa.standard.client.common.messages.MessageType;

public class ActionMessageType extends MessageType {

	public static final ActionMessageType IGNORE = new ActionMessageType("IGNORE", null);
	public static final ActionMessageType ERROR = new ActionMessageType("ERROR", ActionEventError.class);
	public static final ActionMessageType ERROR_DEVICE = new ActionMessageType("ERROR_DEVICE",
			ActionEventDeviceError.class);
	public static final ActionMessageType ERROR_DISPLAY = new ActionMessageType("ERROR_DISPLAY",
			ActionEventErrorDisplay.class);
	public static final ActionMessageType WARNING = new ActionMessageType("WARNING", ActionEventWarning.class);
	public static final ActionMessageType LOG = new ActionMessageType("LOG", null);
	public static final ActionMessageType MONITORING = new ActionMessageType("MONITORING", null);

	public Class<? extends ActionEvent> actionEventClass;

	public ActionMessageType(String description, Class<? extends ActionEvent> actionEventClass) {
		super(description);
		this.actionEventClass = actionEventClass;
	}

	public Class<? extends ActionEvent> getActionEventClass() {
		return actionEventClass;
	}
}