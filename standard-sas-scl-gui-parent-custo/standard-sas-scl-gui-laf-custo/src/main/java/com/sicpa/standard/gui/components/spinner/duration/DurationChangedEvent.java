package com.sicpa.standard.gui.components.spinner.duration;

public class DurationChangedEvent {

	protected long duration;

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDuration() {
		return duration;
	}

	public DurationChangedEvent(long duration) {
		this.duration = duration;
	}
}
