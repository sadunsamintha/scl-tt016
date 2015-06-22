package com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping;

import com.sicpa.standard.sasscl.model.ProductionMode;

import java.util.ArrayList;
import java.util.List;

public class DefaultProductionModeMapping extends ProductionModeMapping {

	public DefaultProductionModeMapping() {
		// currently there is no way to get the id of market type in a static way, hence those magic number
		add(ProductionMode.ALL, 0);
		add(ProductionMode.STANDARD, 1);
		add(ProductionMode.EXPORT, 5);
		add(ProductionMode.COUNTING, 6);
		add(ProductionMode.COUNTING, 7);
		add(ProductionMode.EXPORT_CODING, 8);
	}

	@Override
	public List<ProductionMode> getAllAvailableProductionModes() {
		List<ProductionMode> productionModes = new ArrayList<>();
		productionModes.add(ProductionMode.STANDARD);
		productionModes.add(ProductionMode.EXPORT);
		productionModes.add(ProductionMode.EXPORT_CODING);
		productionModes.add(ProductionMode.COUNTING);
		productionModes.add(ProductionMode.MAINTENANCE);
		return productionModes;
	}
}
