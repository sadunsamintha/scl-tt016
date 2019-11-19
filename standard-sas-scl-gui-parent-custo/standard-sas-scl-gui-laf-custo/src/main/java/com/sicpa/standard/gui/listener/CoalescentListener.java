package com.sicpa.standard.gui.listener;

import javax.swing.Timer;

public class CoalescentListener {

	private long previousEventTime = -1l;

	private Timer timer;
	private int delta;
	private Runnable task;
	private final Object lock = new Object();

	public CoalescentListener(int delta, Runnable task) {
		this.delta = delta;
		this.task = task;
	}

	public CoalescentListener(int delta) {
		this.delta = delta;
		this.timer = new Timer(delta, e -> {
			synchronized (lock) {
				timer.stop();
			}
			doAction();
		});
	}

	public CoalescentListener() {
		this(500);
	}

	public void eventReceived() {
		synchronized (lock) {
			if (isTimeToGenerateEventReached()) {
				timer.stop();
			}
			previousEventTime = System.currentTimeMillis();
			timer.start();
		}
	}

	private boolean isTimeToGenerateEventReached() {
		long now = System.currentTimeMillis();
		return now - previousEventTime < delta;
	}

	public void doAction() {
		task.run();
	}

	public void setTask(Runnable task) {
		this.task = task;
	}

}
