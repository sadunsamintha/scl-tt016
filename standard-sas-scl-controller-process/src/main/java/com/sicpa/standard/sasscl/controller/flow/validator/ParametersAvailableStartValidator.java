package com.sicpa.standard.sasscl.controller.flow.validator;

import java.util.Set;

import com.sicpa.standard.sasscl.controller.process.IProductionStartValidator;
import com.sicpa.standard.sasscl.controller.process.ProductionStartValidatorResult;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class ParametersAvailableStartValidator implements IProductionStartValidator {

	protected ProductionParameters productionParameters;
	protected SkuListProvider skuListProvider;

	@Override
	public ProductionStartValidatorResult validateStart() {
		if (isProductionParameterStillAvailable()) {
			return ProductionStartValidatorResult.createValidResult();
		} else {
			return ProductionStartValidatorResult
					.createInvalidResult(MessageEventKey.ProductionParameters.NO_LONGER_AVAILABLE);
		}
	}

	protected boolean isProductionParameterStillAvailable() {

		SKU skuSelected = productionParameters.getSku();
		if (skuSelected != null) {
			Set<SKU> skus = skuListProvider.getAvailableSKUs();
			if (skus != null && !skus.isEmpty()) {
				// isSelectedSku still in the list of available skus
				return skus.contains(skuSelected);
			} else {
				return false;
			}
		} else {
			// no sku for maintenance mode
			return true;
		}
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}
}
