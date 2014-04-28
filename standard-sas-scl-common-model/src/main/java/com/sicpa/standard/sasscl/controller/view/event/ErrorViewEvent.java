package com.sicpa.standard.sasscl.controller.view.event;

/**
 * event used to notify the gui to show this warning
 * 
 * @author DIelsch
 * 
 */
public class ErrorViewEvent {

	protected String messageLangKey;
	// if a warning with the same key exist, update it
	protected boolean update;
	protected Object[] params;
	protected String codeExt;

	public ErrorViewEvent(final String messageLangKey, final boolean update, Object... params) {
		this.messageLangKey = messageLangKey;
		this.update = update;
		this.params = params;
	}

	public ErrorViewEvent(final String messageLangKey, String codeExt, final boolean update, Object... params) {
		this.messageLangKey = messageLangKey;
		this.update = update;
		this.params = params;
		this.codeExt = codeExt;
	}

	public String getMessageLangKey() {
		return messageLangKey;
	}

	public boolean isUpdate() {
		return this.update;
	}

	public Object[] getParams() {
		return params;
	}

	public String getCodeExt() {
		return codeExt;
	}

	public void setCodeExt(String codeExt) {
		this.codeExt = codeExt;
	}
}
