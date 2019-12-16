package com.sicpa.standard.gui.screen.machine.impl.SPL.stats;

public class StatisticsChangeEvent {

	private long numberOld;
	private long numberNew;

	public StatisticsChangeEvent(final long numberOld, final long numberNew) {
		this.numberOld = numberOld;
		this.numberNew = numberNew;
	}

	public long getNumberOld() {
		return this.numberOld;
	}

	public long getNumberNew() {
		return this.numberNew;
	}
}
