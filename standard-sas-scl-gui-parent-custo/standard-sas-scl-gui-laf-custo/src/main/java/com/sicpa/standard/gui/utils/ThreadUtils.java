package com.sicpa.standard.gui.utils;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

public class ThreadUtils {

	/**
	 * if the current thread is not the EDT call SwingUtilities.invokLater </br>if the current thread is the EDT call
	 * doRun.run()
	 * 
	 * @param task
	 *            the code to call
	 */
	public static void invokeLater(final Runnable task) {
		if (task == null) {
			throw new IllegalArgumentException("The runnable cannot be null");
		}
		if (EventQueue.isDispatchThread()) {
			task.run();
		} else {
			EventQueue.invokeLater(task);
		}
	}

	public static void invokeAndWait(final Runnable doRun) {
		if (doRun == null) {
			throw new IllegalArgumentException("The runnable cannot be null");
		}
		if (EventQueue.isDispatchThread()) {
			doRun.run();
		} else {
			try {
				EventQueue.invokeAndWait(doRun);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * a sleep that does not throw an exception
	 * 
	 * @param duration
	 */
	public static void sleepQuietly(final long duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("The duration cannot be < 0");
		}
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * sleep until the timestamp change => sleep for the OS resolution timer usually 6ms on windows
	 */
	public static void waitForNextTimeStamp() {
		long now = System.currentTimeMillis();
		do {
			ThreadUtils.sleepQuietly(1);
			// while, because the sleep could get interrupted and we could possibly wake up at the same
			// time(possible considering the timer resolution is about 6ms)
		} while (now == System.currentTimeMillis());
	}

}
