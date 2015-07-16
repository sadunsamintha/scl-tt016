package com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping;

import com.sicpa.standard.sasscl.model.ProductionMode;

public class DefaultProductionModeMapping extends ProductionModeMapping {

	public DefaultProductionModeMapping() {
		// currently there is no way to get the id of market type in a static way, hence those magic number
		add(ProductionMode.STANDARD, 1);
		add(ProductionMode.EXPORT, 5);
		add(ProductionMode.COUNTING, 6);
		add(ProductionMode.COUNTING, 7);
		add(ProductionMode.EXPORT_CODING, 8);
	}
}
