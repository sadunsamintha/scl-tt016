package com.sicpa.tt085.devices.remote.impl;

import com.sicpa.standard.sasscl.devices.remote.impl.DefaultProductionModeMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class TT085DefaultProductionModeMapping extends DefaultProductionModeMapping {

	public TT085DefaultProductionModeMapping() {
		super();
		add(ProductionMode.STANDARD, 2);
		add(ProductionMode.ALL, 0);
	}
}
