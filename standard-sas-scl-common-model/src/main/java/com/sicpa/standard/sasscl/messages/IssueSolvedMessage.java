package com.sicpa.standard.sasscl.messages;

/**
 * messages sent on the event bus to notify that a device error has been solved
 * 
 * @author DIelsch
 * 
 */
public class IssueSolvedMessage {

	protected String key;
	protected Object source;

	public IssueSolvedMessage() {
	}

	public IssueSolvedMessage(String key, Object source) {
		this.key = key;
		this.source = source;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
