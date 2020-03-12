package com.sicpa.tt079.remote.mapping;

import static com.sicpa.standard.sasscl.model.ProductionMode.COUNTING;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT_CODING;
import static com.sicpa.standard.sasscl.model.ProductionMode.STANDARD;
import static com.sicpa.tt079.model.TT079ProductionMode.IMPORT;

import com.sicpa.standard.sasscl.devices.remote.mapping.ProductionModeMapping;

public class TT079ProductionModeMapping extends ProductionModeMapping {

    public TT079ProductionModeMapping() {
        // Default Modes
        add(STANDARD, 1);
        add(EXPORT, 5);
        add(COUNTING, 6);
        add(COUNTING, 7);
		add(EXPORT_CODING, 8);
		
		// Custo mapping
	    add(IMPORT, 2); // IMPORT
    }

}
