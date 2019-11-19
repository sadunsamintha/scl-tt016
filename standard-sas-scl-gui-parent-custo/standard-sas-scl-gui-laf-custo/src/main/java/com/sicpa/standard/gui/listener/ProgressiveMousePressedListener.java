package com.sicpa.standard.gui.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.sicpa.standard.gui.utils.ThreadUtils;

public class ProgressiveMousePressedListener extends MouseAdapter {

	private Runnable task;

	public ProgressiveMousePressedListener(final Runnable task) {
		this.task = task;
	}

	public void setTask(final Runnable task) {
		this.task = task;
	}

	private int sleepTime = 1000;
	private long clickedTime;

	// used to be sure if it s actually the same click operation going on,
	// and not a fast clicks on it
	// if instead we use a boolean while having fast click on a button =>
	// multiple threads running
	private long i = 0;

	@Override
	public void mousePressed(final MouseEvent mouseEvent) {

		this.clickedTime = System.currentTimeMillis();
		final long num = this.i;

		Runnable changeValue = new Runnable() {
			@Override
			public void run() {
				while (num == ProgressiveMousePressedListener.this.i) {

					long delta = System.currentTimeMillis() - ProgressiveMousePressedListener.this.clickedTime;
					if (delta < 500) {
						ProgressiveMousePressedListener.this.sleepTime = 1000;
					} else if (delta < 750) {
						ProgressiveMousePressedListener.this.sleepTime = 60;
					} else if (delta < 1000) {
						ProgressiveMousePressedListener.this.sleepTime = 50;
					} else if (delta < 1500) {
						ProgressiveMousePressedListener.this.sleepTime = 40;
					} else if (delta < 2000) {
						ProgressiveMousePressedListener.this.sleepTime = 50;
					} else if (delta < 3000) {
						ProgressiveMousePressedListener.this.sleepTime = 30;
					} else if (delta < 4000) {
						ProgressiveMousePressedListener.this.sleepTime = 10;
					} else if (delta < 5000) {
						ProgressiveMousePressedListener.this.sleepTime = 5;
					} else if (delta < 7000) {
						ProgressiveMousePressedListener.this.sleepTime = 1;
					}
					ThreadUtils.sleepQuietly(ProgressiveMousePressedListener.this.sleepTime);

					if (num == ProgressiveMousePressedListener.this.i) {
						ProgressiveMousePressedListener.this.task.run();
					}
				}
			}
		};
		new Thread(changeValue).start();
	}

	@Override
	public void mouseReleased(final MouseEvent mouseEvent) {
		this.i++;
	}

}
