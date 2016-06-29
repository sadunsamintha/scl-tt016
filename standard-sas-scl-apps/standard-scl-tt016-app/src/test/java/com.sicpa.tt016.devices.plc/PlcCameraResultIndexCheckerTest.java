package com.sicpa.tt016.devices.plc;


import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlcCameraResultIndexCheckerTest {

	private PlcCameraResultIndexManager plcCameraResultIndexManager;

	@Before
	public void setUp() throws Exception {
		plcCameraResultIndexManager = new PlcCameraResultIndexManager();
	}

	@Test(expected = IllegalArgumentException.class)
	public void indexWithinRange() {
		plcCameraResultIndexManager.getIndexDifference(400);
	}

	@Test
	public void getIndexDifference() {
		Assert.assertEquals(1, plcCameraResultIndexManager.getIndexDifference(1));
	}

	@Test
	public void getIndexDifferenceIndexBigger() {
		plcCameraResultIndexManager.setPreviousIndex(10);

		Assert.assertEquals(5, plcCameraResultIndexManager.getIndexDifference(15));
	}

	@Test
	public void getIndexDifferenceIndexSmaller() {
		plcCameraResultIndexManager.setPreviousIndex(250);

		Assert.assertEquals(11, plcCameraResultIndexManager.getIndexDifference(5));
	}

	@Test
	public void getIndexDifferenceIndexBiggerMaxIndex() {
		plcCameraResultIndexManager.setPreviousIndex(255);

		Assert.assertEquals(1, plcCameraResultIndexManager.getIndexDifference(0));
	}

	@Test
	public void getIndexDifferenceIndexBiggerMaxIndex2() {
		plcCameraResultIndexManager.setPreviousIndex(255);

		Assert.assertEquals(3, plcCameraResultIndexManager.getIndexDifference(2));
	}

	@Test
	public void getIndexDifferenceIndexEqualsPreviousIndex() {
		plcCameraResultIndexManager.setPreviousIndex(255);

		Assert.assertEquals(0, plcCameraResultIndexManager.getIndexDifference(255));
	}
}
