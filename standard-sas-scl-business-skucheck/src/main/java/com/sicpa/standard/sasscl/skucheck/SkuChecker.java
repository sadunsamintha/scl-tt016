package com.sicpa.standard.sasscl.skucheck;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.skucheck.checking.AbstractSkuChecker;

public class SkuChecker extends AbstractSkuChecker<SKU> {

	protected ProductionParameters productionParameters;
	protected SkuListProvider skuListProvider;

	public SKU getSelectedSku() {
		return productionParameters.getSku();
	}

	public List<SKU> getKnownSkus() {
		return new ArrayList<SKU>(skuListProvider.getAvailableSKUs());
	}

	protected String getID(SKU sku) {
		return String.valueOf(sku.getBarCodes() == null ? "" : sku.getBarCodes().get(0));
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}
}
