package com.sicpa.tt016.monitoring.mbean.mapping;

import com.sicpa.standard.sasscl.model.ProductionMode;

import java.util.HashMap;
import java.util.Map;

public class LegacyToProductionModeIdProvider {

    private static Map<Integer, Integer> productionModeToProductionModeLegacy = new HashMap<>();

    public static final int PRODUCTION_MODE_NOT_AVAILABLE = 3;

    static {
        productionModeToProductionModeLegacy.put(ProductionMode.STANDARD.getId(), 0);        //Domestic
        productionModeToProductionModeLegacy.put(ProductionMode.EXPORT.getId(), 1);          //Export
        productionModeToProductionModeLegacy.put(ProductionMode.MAINTENANCE.getId(), 2);     //Maintenance
        productionModeToProductionModeLegacy.put(ProductionMode.REFEED_NORMAL.getId(), 7);   //Refeed
    }

    public static int getProductionModeId(int productionModeId) {
        return productionModeToProductionModeLegacy.getOrDefault(productionModeId, PRODUCTION_MODE_NOT_AVAILABLE);
    }
}
