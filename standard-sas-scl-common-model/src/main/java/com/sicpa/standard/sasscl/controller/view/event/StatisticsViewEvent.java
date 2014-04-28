package com.sicpa.standard.sasscl.controller.view.event;

/**
 * event used to notify the gui to show those statistics
 * 
 * @author DIelsch
 * 
 */
public class StatisticsViewEvent {
	protected int good;
	protected int bad;

	public StatisticsViewEvent(final int good, final int bad) {
		super();
		this.good = good;
		this.bad = bad;
	}

	public int getGood() {
		return this.good;
	}

	public int getBad() {
		return this.bad;
	}
}
