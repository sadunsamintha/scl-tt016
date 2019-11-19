package com.sicpa.standard.gui.components.spinner.duration;

import java.util.ArrayList;
import java.util.List;

public class DurationSelectorModel {

	protected List<IDurationChangedListener> durationChangedListener = new ArrayList<IDurationChangedListener>();

	protected long duration;

	public void setDuration(long duration) {
		this.duration = duration;
		fireDurationChanged();
	}

	public void addDurationChangedListener(IDurationChangedListener listener) {
		synchronized (durationChangedListener) {
			durationChangedListener.add(listener);
		}
	}

	public void removeDurationChangedListener(IDurationChangedListener listener) {
		synchronized (durationChangedListener) {
			durationChangedListener.remove(listener);
		}
	}

	protected void fireDurationChanged() {
		synchronized (durationChangedListener) {
			for (IDurationChangedListener listener : durationChangedListener) {
				listener.durationChanged(new DurationChangedEvent(duration));
			}
		}
	}

	public void add(DurationUnit unit, int amount) {
		duration += (unit.getMultiplier() * amount);
		fireDurationChanged();
	}

	public long getDuration() {
		return duration;
	}

	public long getDuration(DurationUnit unit) {
		return (duration / unit.getMultiplier())%(unit.getMax()+1);
	}
}
