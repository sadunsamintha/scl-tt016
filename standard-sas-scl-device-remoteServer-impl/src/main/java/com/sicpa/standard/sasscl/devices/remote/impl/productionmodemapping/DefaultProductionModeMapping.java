package com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping;

import static com.sicpa.standard.sasscl.model.ProductionMode.COUNTING;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT_CODING;
import static com.sicpa.standard.sasscl.model.ProductionMode.STANDARD;

public class DefaultProductionModeMapping extends ProductionModeMapping {

    public DefaultProductionModeMapping() {
        // currently there is no way to get the id of market type in a static way, hence those magic number
        add(STANDARD, 1);
        add(EXPORT, 5);
        add(COUNTING, 6);
        add(COUNTING, 7);
		add(EXPORT_CODING, 8);
    }

}
