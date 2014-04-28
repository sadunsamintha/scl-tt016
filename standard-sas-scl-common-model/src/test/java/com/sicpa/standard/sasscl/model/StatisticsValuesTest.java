package com.sicpa.standard.sasscl.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;

public class StatisticsValuesTest {

	StatisticsValues statisticsValues;

	@Before
	public void setUp() throws Exception {
		statisticsValues = new StatisticsValues();
		statisticsValues.set(new StatisticsKey("test"), 2);
	}

	@Test
	public final void testEqualsObject() {
		StatisticsValues tmp = new StatisticsValues();
		tmp.set(new StatisticsKey("test"), 2);
		assertTrue(statisticsValues.equals(tmp));
		assertFalse(statisticsValues.equals(new Object()));
		tmp.set(new StatisticsKey("test2"), 4);
		assertFalse(statisticsValues.equals(tmp));
	}

	@Test
	public final void testIncreaseStatisticsKeyInt() {

		StatisticsKey key = new StatisticsKey("test");
		int tmp = statisticsValues.get(key);
		statisticsValues.increase(key, 1);
		assertTrue(tmp + 1 == statisticsValues.get(key));
	}

	@Test
	public final void testSetGet() {
		StatisticsKey key = new StatisticsKey("test2");
		statisticsValues.set(key, 2);
		assertEquals(2, statisticsValues.get(key));

	}

	@Test
	public final void testIncreaseStatisticsKey() {
		StatisticsKey key = new StatisticsKey("test");
		int tmp = statisticsValues.get(key);
		statisticsValues.increase(key);
		assertEquals(tmp + 1, statisticsValues.get(key));
	}

	@Test
	public final void testGetMapValues() {
		StatisticsKey key = new StatisticsKey("test");
		assertTrue(statisticsValues.getMapValues().get(key) == 2);
	}
}
