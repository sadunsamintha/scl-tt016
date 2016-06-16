package com.sicpa.standard.sasscl.controller.flow.validator;

import java.util.Set;

import com.sicpa.standard.client.common.controller.predicate.start.IStartProductionValidator;
import com.sicpa.standard.client.common.controller.predicate.start.NoStartReason;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class ParametersAvailableStartValidator implements IStartProductionValidator {

	private ProductionParameters productionParameters;
	private SkuListProvider skuListProvider;
	private ISkuSelectionBehavior skuSelectionBehavior;

	private boolean isProductionParameterStillAvailable() {
		if (!skuSelectionBehavior.isLoadPreviousSelection()) {
			//no need to check as the previous is not loaded
			return true;
		}

		SKU skuSelected = productionParameters.getSku();
		if (skuSelected == null) {
			// no sku for maintenance mode
			return true;
		}
		return currentListContainsSku(skuSelected);

	}

	private boolean currentListContainsSku(SKU skuSelected) {
		Set<SKU> skus = skuListProvider.getAvailableSKUs();
		if (skus == null || skus.isEmpty()) {
			return false;
		}
		return skus.contains(skuSelected);
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	@Override
	public NoStartReason isPossibleToStartProduction() {
		NoStartReason res = new NoStartReason();

		if (!isProductionParameterStillAvailable()) {
			res.addReason(MessageEventKey.ProductionParameters.NO_LONGER_AVAILABLE);
		}

		return res;
	}

	public void setSkuSelectionBehavior(ISkuSelectionBehavior skuSelectionBehavior) {
		this.skuSelectionBehavior = skuSelectionBehavior;
	}
}
