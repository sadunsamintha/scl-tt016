package com.sicpa.standard.gui.screen.DMS.mvc.utils;

public class JOptionPaneMessageWrapper {
	private String message;
	private int type;
	private String title;

	public JOptionPaneMessageWrapper(final String message, final String title, final int type) {
		this.message = message;
		this.title = title;
		this.type = type;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public int getType() {
		return this.type;
	}

	public void setType(final int type) {
		this.type = type;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

}
