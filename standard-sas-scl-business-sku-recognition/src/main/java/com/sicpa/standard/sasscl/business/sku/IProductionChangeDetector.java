package com.sicpa.standard.sasscl.business.sku;

import java.time.Instant;

public interface IProductionChangeDetector {

	boolean isPossibleProductionChange(Instant t1, Instant t2);

}
