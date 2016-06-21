package com.sicpa.standard.sasscl.business.alert.task.scheduled;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * task to check if an event happens to much, and if so , send an alert message
 * 
 * @author DIelsch
 * 
 */
public abstract class AbstractScheduledOverTimeAlertTask extends AbstractScheduledAlertTask {

	protected volatile Grid grid;
	protected volatile int eventCount;
	protected volatile int currentTickIndex;

	/**
	 * 
	 * @param length
	 *            the number of interval we want to check
	 * @param interval
	 *            the period of time to group the event, usually 1 sec
	 * @param threshold
	 *            the number of events that will trigger a alert message
	 */
	public AbstractScheduledOverTimeAlertTask() {
		super();
		currentTickIndex = 0;
	}

	@Override
	public void reset() {
		grid = new Grid(getSampleSize());
		eventCount = 0;
		currentTickIndex = 0;
	}

	@Override
	public void start() {
		reset();
		super.start();
	}

	@Override
	protected boolean isAlertPresent() {
		addCounterToGrid();
		currentTickIndex++;
		if (currentTickIndex >= getSampleSize()) {
			currentTickIndex = 0;
		}
		return isThresholdReached();

	}

	protected boolean isThresholdReached() {
		return grid.get() > getThreshold();
	}

	protected abstract int getThreshold();

	protected abstract int getSampleSize();

	/**
	 * add the counter for the last interval and reset the counter
	 */
	private void addCounterToGrid() {
		grid.add(currentTickIndex, eventCount);
		eventCount = 0;
	}

	/**
	 * increase the counter of event by one
	 */
	public void increment() {
		eventCount++;
	}

	/**
	 * class used to keep track of the counter for each interval
	 * 
	 * @author DIelsch
	 * 
	 */
	protected static class Grid {
		Queue<Integer>[] array;

		@SuppressWarnings("unchecked")
		public Grid(final int length) {
			this.array = new Queue[length];
			for (int i = 0; i < array.length; i++) {
				array[i] = new LinkedList<Integer>();
			}
		}

		/**
		 * 
		 * @return the number of events for the last interval and remove values for this interval
		 */
		public int get() {
			int res = 0;
			Integer value;
			for (Queue<Integer> line : array) {
				value = line.poll();
				res += (value == null ? 0 : value);
			}
			return res;
		}

		/**
		 * 
		 * @param line
		 *            the interval number
		 * @param value
		 *            the number of events that occurred during the interval
		 */
		protected void add(final int line, final int value) {
			Integer[] elements = new Integer[array.length];
			Arrays.fill(elements, 0, array.length, value);
			Collections.addAll(array[line], elements);
		}
	}
}
