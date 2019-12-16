package com.sicpa.standard.gui.screen.machine.component.applicationStatus;

import java.awt.Color;

import com.sicpa.standard.gui.plaf.SicpaColor;

public class ApplicationStatus {

	public final static ApplicationStatus STOP = new ApplicationErrorStatus("STOP");
	public final static ApplicationStatus RUNNING = new ApplicationOKStatus("RUNNING");

	private Color color;
	private String labelStatus;

	public ApplicationStatus() {
		this(SicpaColor.GRAY, "UNKNOWN");

	}

	public ApplicationStatus(final Color color, final String labelStatus) {
		this.color = color;
		this.labelStatus = labelStatus;
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public String getLabelStatus() {
		return this.labelStatus;
	}

	public void setLabelStatus(final String labelStatus) {
		this.labelStatus = labelStatus;
	}
}
