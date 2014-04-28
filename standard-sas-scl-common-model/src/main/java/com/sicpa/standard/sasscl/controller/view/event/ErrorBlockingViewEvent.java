package com.sicpa.standard.sasscl.controller.view.event;

/**
 * event used when asking the gui to show an erro
 * 
 * @author DIelsch
 * 
 */
public class ErrorBlockingViewEvent {
	protected String key;
	protected String message;

	public ErrorBlockingViewEvent(final String key, final String message) {
		this.key = key;
		this.message = message;
	}

	public String getKey() {
		return this.key;
	}

	public String getMessage() {
		return this.message;
	}
}
