package com.sicpa.standard.gui.screen.DMS.mvc.utils;

import javax.swing.JOptionPane;

public class JoptionPaneMessageAutoCloseWrapper extends JOptionPaneMessageWrapper {

	private int seconds;

	public JoptionPaneMessageAutoCloseWrapper(final String message, final String title, final int seconds) {
		super(message, title, JOptionPane.INFORMATION_MESSAGE);
		this.seconds = seconds;
	}

	public JoptionPaneMessageAutoCloseWrapper(final String message, final String title, final int messageType,
			final int seconds) {
		super(message, title, messageType);
		this.seconds = seconds;
	}

	public int getSeconds() {
		return this.seconds;
	}

	public void setSeconds(final int seconds) {
		this.seconds = seconds;
	}
}
