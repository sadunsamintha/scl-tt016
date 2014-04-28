package com.sicpa.standard.sasscl.benchmark;

public class LogEvent {
	private String key;
	private long time;
	private long refTime;
	private LogEventType type;

	public LogEvent(final String key, final long time, final long refTime, final LogEventType type) {
		super();
		this.key = key;
		this.time = time;
		this.refTime = refTime;
		this.type = type;
	}

	public String getKey() {
		return this.key;
	}

	public long getTime() {
		return this.time;
	}

	public long getRefTime() {
		return this.refTime;
	}

	public LogEventType getType() {
		return this.type;
	}

}
