package com.sicpa.standard.gui.screen.machine.component.devicesStatus;

import java.awt.Color;

import com.sicpa.standard.gui.plaf.SicpaColor;

public enum DeviceStatus {
	WARNING(SicpaColor.ORANGE), ERROR(SicpaColor.RED), OK(SicpaColor.GREEN_DARK), UNKNOWN(SicpaColor.GRAY);

	DeviceStatus(final Color color) {
		this.color = color;
	}

	private Color color;

	public Color getColor() {
		return this.color;
	}
}
