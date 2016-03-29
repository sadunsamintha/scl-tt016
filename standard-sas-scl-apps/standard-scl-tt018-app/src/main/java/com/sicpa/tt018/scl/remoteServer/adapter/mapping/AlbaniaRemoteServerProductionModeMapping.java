package com.sicpa.tt018.scl.remoteServer.adapter.mapping;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductionModes;
import com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode;
import com.sicpa.tt018.scl.utils.Mapping;

public class AlbaniaRemoteServerProductionModeMapping extends Mapping<ProductionMode, ProductionModes> {
	@Override
	protected void populateMap() {

		addEntry(ProductionMode.STANDARD, ProductionModes.STANDARD);
		addEntry(ProductionMode.EXPORT, ProductionModes.EXPORT);
		addEntry(ProductionMode.MAINTENANCE, ProductionModes.MAINTENANCE);
		addEntry(AlbaniaProductionMode.SOFT_DRINK, ProductionModes.SOFTDRINK);

	}

}
