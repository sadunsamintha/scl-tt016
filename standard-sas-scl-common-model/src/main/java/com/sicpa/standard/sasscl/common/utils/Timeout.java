package com.sicpa.standard.sasscl.common.utils;

import java.util.Timer;
import java.util.TimerTask;

public class Timeout {
	protected final Timer timer;
	protected TimerTask timeoutTask;
	protected long time;

	public Timeout(String name, TimerTask timeoutTask, long time) {
		timer = new Timer(name);
		this.timeoutTask = timeoutTask;
		this.time = time;
	}

	public void start() {
		timer.schedule(timeoutTask, time);
	}

	public void stop() {
		if (timeoutTask != null) {
			timeoutTask.cancel();
		}
	}
}