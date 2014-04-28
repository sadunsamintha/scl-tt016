package com.sicpa.standard.sasscl.devices.remote;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * The class <code>MaxDownTimeReachedEventTest</code> contains tests for the class
 * <code>{@link MaxDownTimeReachedEvent}</code>.
 * @generatedBy CodePro at 12/11/10 15:54
 * @author JAguililla
 */
public class MaxDownTimeReachedEventTest {
	/**
	 * Run the boolean isMaxDownTimeReached() method test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testIsMaxDownTimeReachedTrue() {
		MaxDownTimeReachedEvent fixture = new MaxDownTimeReachedEvent(true);

		boolean result = fixture.isMaxDownTimeReached();

		assertEquals(true, result);
	}

	/**
	 * Run the boolean isMaxDownTimeReached() method test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testIsMaxDownTimeReachedFalse() {
		MaxDownTimeReachedEvent fixture = new MaxDownTimeReachedEvent(false);

		boolean result = fixture.isMaxDownTimeReached();

		assertEquals(false, result);
	}
}