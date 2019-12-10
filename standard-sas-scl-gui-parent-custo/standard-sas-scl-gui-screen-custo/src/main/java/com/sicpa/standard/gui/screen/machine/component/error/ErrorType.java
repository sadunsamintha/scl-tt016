package com.sicpa.standard.gui.screen.machine.component.error;

import java.awt.Color;

import com.sicpa.standard.gui.plaf.SicpaColor;
@Deprecated
public enum ErrorType {
	MINOR(SicpaColor.YELLOW.darker(), SicpaColor.BLUE_ULTRA_LIGHT, SicpaColor.YELLOW, SicpaColor.BLUE_DARK),

	FATAL(SicpaColor.RED.darker(), SicpaColor.BLUE_ULTRA_LIGHT, SicpaColor.RED, SicpaColor.BLUE_DARK);

	private Color selectedBackground;
	private Color background;
	private Color foreground;
	private Color selectedForeground;

	private ErrorType(final Color background, final Color foreground, final Color selectedBackground,
			final Color selectedForeground) {
		this.background = background;
		this.foreground = foreground;
		this.selectedBackground = selectedBackground;
		this.selectedForeground = selectedForeground;
	}

	public Color getBackground() {
		return this.background;
	}

	public Color getSelectedBackground() {
		return this.selectedBackground;
	}

	public void setSelectedBackground(final Color selectedBackground) {
		this.selectedBackground = selectedBackground;
	}

	public Color getForeground() {
		return this.foreground;
	}

	public void setForeground(final Color foreground) {
		this.foreground = foreground;
	}

	public Color getSelectedForeground() {
		return this.selectedForeground;
	}

	public void setSelectedForeground(final Color selectedForeground) {
		this.selectedForeground = selectedForeground;
	}

	public void setBackground(final Color background) {
		this.background = background;
	}

}
