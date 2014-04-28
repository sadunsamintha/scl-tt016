package com.sicpa.standard.sasscl.messages;


public enum ActionMessageType {
	                                                                 
	IGNORE(null),
	ERROR(ActionEventError.class),
	ERROR_DEVICE(ActionEventDeviceError.class),
	ERROR_DISPLAY(ActionEventErrorDisplay.class),
	WARNING(ActionEventWarning.class),
	LOG(null),
	MONITORING(null);
	
	protected Class<? extends ActionEvent> actionEventClass;

	private ActionMessageType(Class<? extends ActionEvent> actionEventClass) {
		this.actionEventClass = actionEventClass;
	}

	public Class<? extends ActionEvent> getActionEventClass() {
		return actionEventClass;
	}

}
