package com.sicpa.standard.sasscl.business.sku;

import java.time.Duration;
import java.time.Instant;

public class ProductionChangeDetector implements IProductionChangeDetector {

	private int delayMinute;

	@Override
	public boolean isPossibleProductionChange(Instant t1, Instant t2) {
		return Duration.between(t1, t2).toMinutes() >= delayMinute;
	}

	public void setDelayMinute(int delayMinute) {
		this.delayMinute = delayMinute;
	}
}
