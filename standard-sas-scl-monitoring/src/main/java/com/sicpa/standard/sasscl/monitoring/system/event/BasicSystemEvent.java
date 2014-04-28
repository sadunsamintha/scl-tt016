package com.sicpa.standard.sasscl.monitoring.system.event;

import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;

public class BasicSystemEvent extends AbstractSystemEvent {

	private static final long serialVersionUID = 1L;

	protected String message;

	public BasicSystemEvent() {
	}

	public BasicSystemEvent(final SystemEventLevel level, final SystemEventType type, final String message) {
		super(level, type);
		this.message = message;
	}

	public BasicSystemEvent(final SystemEventType type, final String message) {
		super(SystemEventLevel.INFO, type);
		this.message = message;
	}

	public BasicSystemEvent(final SystemEventLevel level, final SystemEventType type) {
		super(level, type);
	}

	public BasicSystemEvent(final SystemEventType type) {
		super(SystemEventLevel.INFO, type);
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
