package com.sicpa.tt084.sasscl.devices.remote.impl;

import com.sicpa.standard.sasscl.devices.remote.mapping.ProductionModeMapping;
import com.sicpa.tt084.sasscl.model.TT084ProductionMode;

import static com.sicpa.standard.sasscl.model.ProductionMode.*;


public class TT084ProductionModeMapping extends ProductionModeMapping {

    public TT084ProductionModeMapping() {
        // Default Modes
        add(STANDARD, 1);
        add(EXPORT, 5);
        add(COUNTING, 6);
        add(COUNTING, 7);
		add(EXPORT_CODING, 8);

		// Custo mapping
	    add(TT084ProductionMode.IMPORT, 2);
    }

}
