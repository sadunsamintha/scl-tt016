package com.sicpa.standard.gui.components.spinner.duration;

public class DurationUnit {

	public static DurationUnit DAY = new DurationUnit(24 * 60 * 60 * 1000, 999);
	public static DurationUnit HOUR = new DurationUnit(60 * 60 * 1000, 23);
	public static DurationUnit MINUTE = new DurationUnit(60 * 1000, 59);
	public static DurationUnit SECOND = new DurationUnit(1000, 59);
	public static DurationUnit MILLISECOND = new DurationUnit(1, 999);

	protected int multiplier;
	protected int max;

	public DurationUnit(int multiplier, int max) {
		this.multiplier = multiplier;
		this.max = max;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public int getMax() {
		return max;
	}
}