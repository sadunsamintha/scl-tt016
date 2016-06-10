package com.sicpa.standard.sasscl.skureader;

import java.util.Optional;
import java.util.function.Predicate;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class SkuFinder implements ISkuFinder {

	private SkuListProvider skuListProvider;
	private ProductionParameters productionParameters;

	@Override
	public Optional<SKU> getSkuFromBarcode(String barcode) {
		return getSku(sku -> sku.getBarCodes().contains(barcode));
	}

	@Override
	public Optional<SKU> getSkuFromId(int id) {
		return getSku(sku -> sku.getId() == id);
	}

	private Optional<SKU> getSku(Predicate<SKU> filter) {
		ProductionMode mode = productionParameters.getProductionMode();
		return skuListProvider.getAvailableSKUsForProductionMode(mode).stream().filter(filter).findFirst();
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
