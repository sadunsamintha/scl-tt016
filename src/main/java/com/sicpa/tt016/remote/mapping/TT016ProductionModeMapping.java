package com.sicpa.tt016.remote.mapping;

import static com.sicpa.standard.sasscl.model.ProductionMode.COUNTING;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT;
import static com.sicpa.standard.sasscl.model.ProductionMode.EXPORT_CODING;
import static com.sicpa.standard.sasscl.model.ProductionMode.STANDARD;
import static com.sicpa.tt016.model.TT016ProductionMode.AGING;

import com.sicpa.standard.sasscl.devices.remote.mapping.ProductionModeMapping;

public class TT016ProductionModeMapping extends ProductionModeMapping {

    public TT016ProductionModeMapping() {
        // Default Modes
        add(STANDARD, 1);
        add(EXPORT, 5);
        add(COUNTING, 6);
        add(COUNTING, 7);
		add(EXPORT_CODING, 8);
		
		// Custo mapping
	    add(AGING, 3); // AGING (VIEILLISSEMENT)
    }

}
