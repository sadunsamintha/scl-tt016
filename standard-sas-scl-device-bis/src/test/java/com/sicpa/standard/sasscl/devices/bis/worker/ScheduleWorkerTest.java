package com.sicpa.standard.sasscl.devices.bis.worker;

import junit.framework.Assert;

import org.junit.Test;

public class ScheduleWorkerTest {

	public static class TestScheduleWorker extends AbstractScheduleWorker {

		private int count = 0;

		public TestScheduleWorker(long scheduleInterval) {
			super("Testing worker", scheduleInterval);
		}

		@Override
		protected void doWork() {
			count++;
		}

		public int getCurrentCount() {
			return count;
		}

		public void resetCount() {
			count = 0;
		}

	}

	@Test
	public void workerTest() throws Exception {

		TestScheduleWorker worker = new TestScheduleWorker(500);

		Thread.sleep(500);

		worker.create();
		Assert.assertEquals(0, worker.getCurrentCount());

		Thread.sleep(500);

		Assert.assertEquals(0, worker.getCurrentCount());

		worker.start();

		Thread.sleep(2000);

		Assert.assertTrue(worker.getCurrentCount() >= 4);

		worker.stop();
		worker.resetCount();
		Assert.assertEquals(0, worker.getCurrentCount());

		Thread.sleep(500);
		Assert.assertEquals(0, worker.getCurrentCount());

		// start again
		worker.start();

		Thread.sleep(2000);
		Assert.assertTrue(worker.getCurrentCount() >= 4);

		worker.dispose();

	}

}
