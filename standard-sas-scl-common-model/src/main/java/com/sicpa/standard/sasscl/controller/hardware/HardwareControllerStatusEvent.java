package com.sicpa.standard.sasscl.controller.hardware;

import java.util.Collection;
import java.util.Collections;

public class HardwareControllerStatusEvent {

	protected final HardwareControllerStatus status;

	protected final Collection<String> errors;

	public HardwareControllerStatusEvent(HardwareControllerStatus hardwareControllerStatus, Collection<String> errors) {
		this.status = hardwareControllerStatus;
		this.errors = errors;
	}

	public HardwareControllerStatusEvent(HardwareControllerStatus hardwareControllerStatus) {
		this.status = hardwareControllerStatus;
		errors = Collections.emptyList();
	}

	public HardwareControllerStatus getStatus() {
		return status;
	}

	public Collection<String> getErrors() {
		return errors;
	}
}
