package com.sicpa.standard.sasscl.view.question;

import java.awt.Color;

public class Answer {

	protected String text;
	protected Runnable callback;
	protected boolean requireConfirmation;
	protected String confirmationText;
	protected boolean enabled;
	protected Color backgroundColor;

	public Answer(String text, Runnable callback) {
		this(text, callback, false, "", true);
	}

	public Answer(String text, Runnable callback, boolean enabled) {
		this(text, callback, false, "", enabled);
	}

	public Answer(String text, Runnable callback, boolean requireConfirmation, String confirmationText) {
		this(text, callback, requireConfirmation, confirmationText, true);
	}

	public Answer(String text, Runnable callback, boolean requireConfirmation, String confirmationText, boolean enabled) {
		this.text = text;
		this.callback = callback;
		this.requireConfirmation = requireConfirmation;
		this.confirmationText = confirmationText;
		this.enabled = enabled;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Runnable getCallback() {
		return callback;
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

	public boolean isRequireConfirmation() {
		return requireConfirmation;
	}

	public String getConfirmationText() {
		return confirmationText;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}
}