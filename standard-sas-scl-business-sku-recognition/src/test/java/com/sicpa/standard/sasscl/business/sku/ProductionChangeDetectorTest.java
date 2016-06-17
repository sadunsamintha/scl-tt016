package com.sicpa.standard.sasscl.business.sku;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.time.Instant;

import org.junit.Test;

public class ProductionChangeDetectorTest {

	ProductionChangeDetector detector = new ProductionChangeDetector();

	@Test
	public void testIsPossibleProductionChange() {

		int maxDelay = 5;

		detector.setDelayMinute(maxDelay);

		Instant beforeMaxDelay = now().minus(maxDelay - 1, MINUTES);
		Instant atMaxDelay = now().minus(maxDelay, MINUTES);
		Instant afterMaxDelay = now().minus(maxDelay + 1, MINUTES);

		assertFalse(detector.isPossibleProductionChange(beforeMaxDelay, now()));
		assertTrue(detector.isPossibleProductionChange(atMaxDelay, now()));
		assertTrue(detector.isPossibleProductionChange(afterMaxDelay, now()));
	}
}
