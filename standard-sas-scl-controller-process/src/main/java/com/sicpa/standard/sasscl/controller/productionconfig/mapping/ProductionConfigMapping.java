package com.sicpa.standard.sasscl.controller.productionconfig.mapping;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.model.ProductionMode;

public class ProductionConfigMapping implements IProductionConfigMapping {

	protected final Map<ProductionMode, String> mapping = new HashMap<ProductionMode, String>();

	public ProductionConfigMapping() {
		put(ProductionMode.COUNTING, "counting");
		put(ProductionMode.EXPORT, "export");
		put(ProductionMode.EXPORT_CODING, "exportCoding");
		put(ProductionMode.MAINTENANCE, "maintenance");
		put(ProductionMode.REFEED_CORRECTION, "refeedCorrection");
		put(ProductionMode.REFEED_NORMAL, "refeedNormal");
		put(ProductionMode.STANDARD, "standard");

	}

	@Override
	public String getProductionConfigId(ProductionMode mode) {
		return mapping.get(mode);
	}

	@Override
	public void put(ProductionMode mode, String productionConfigId) {
		mapping.put(mode, productionConfigId);
	}

}
