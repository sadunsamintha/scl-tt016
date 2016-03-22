package com.sicpa.tt018.scl.model.plc;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.plc.controller.model.PlcModel;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class AlbaniaPlcModel extends PlcModel {

	private ProductionParameters productionParameters;

	protected static int allowedProductPackage = 0;

	@Override
	public Map<String, IPlcVariable<?>> getParameters() {
		// map that will be returned to caller
		final Map<String, IPlcVariable<?>> filteredMap = new HashMap<>();
		// temporary map for
		final Map<String, IPlcVariable<?>> chosenValuesMap = new HashMap<>();

		for (final Map.Entry<String, IPlcVariable<?>> entry : super.getParameters().entrySet()) {
			filteredMap.put(entry.getKey(), entry.getValue());
		}

		// Here override the already existing plc variable with the chosen one
		for (final Map.Entry<String, IPlcVariable<?>> entry : chosenValuesMap.entrySet()) {
			filteredMap.put(entry.getKey(), new PlcVariable(entry.getKey(), entry.getValue().getVariableType(), entry.getValue().getValue()));
		}

		return filteredMap;
	}

	public int getAllowedProductType() {
		return allowedProductPackage;
	}

	public void setAllowedProductType(final String allowedProductType) {
		AlbaniaPlcModel.allowedProductPackage = Integer.parseInt(allowedProductType);
	}

	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

}
