package com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping;

import com.sicpa.standard.sasscl.devices.remote.mapping.ProductionModeMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class TT021DefaultProductionModeMapping extends ProductionModeMapping {

	public TT021DefaultProductionModeMapping() {
		super();
	}
	public TT021DefaultProductionModeMapping(int all, int domestic, int importId, int exportId) {
		add(ProductionMode.STANDARD, all);
		add(ProductionMode.STANDARD, domestic);
		add(ProductionMode.STANDARD, importId);
		add(ProductionMode.EXPORT, exportId);
		add(ProductionMode.COUNTING, 6);
		add(ProductionMode.COUNTING, 7);
		add(ProductionMode.EXPORT_CODING, 8);
	}
}
