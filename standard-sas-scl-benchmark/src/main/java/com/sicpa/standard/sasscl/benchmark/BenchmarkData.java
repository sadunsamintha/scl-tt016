package com.sicpa.standard.sasscl.benchmark;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkData {
	private List<LogEvent> events;

	public BenchmarkData() {
		this.events = new ArrayList<LogEvent>();
	}

	public void addLogBefore(final String signature, final long time) {
		addLog(signature, time, -1, LogEventType.before);
	}

	public void addLogAfter(final String signature, final long time, final long refTime) {
		addLog(signature, time, refTime, LogEventType.after);
	}

	public void addLog(final String signature, final long time, final long refTime, final LogEventType type) {
		this.events.add(new LogEvent(signature, time, refTime, type));
	}

	public List<LogEvent> getEvents() {
		return this.events;
	}
}
