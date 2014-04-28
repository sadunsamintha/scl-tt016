package com.sicpa.standard.sasscl.messages;

public class ActionEvent {
	protected String key;
	protected Object[] params;
	protected Object source;

	public ActionEvent(String key, Object source, Object... params) {
		this.key = key;
		this.source = source;
		this.params = params;
	}

	public ActionEvent() {
	}

	public String getKey() {
		return key;
	}

	public Object[] getParams() {
		return params;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public Object getSource() {
		return source;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
