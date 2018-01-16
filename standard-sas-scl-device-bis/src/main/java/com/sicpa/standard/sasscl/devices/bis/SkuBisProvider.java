package com.sicpa.standard.sasscl.devices.bis;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.sicpa.standard.sasscl.devices.bis.ISkuBisProvider;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class SkuBisProvider implements ISkuBisProvider {

	private SkuListProvider skuListProvider;

	@Override
	public Collection<SKU> getSkusToSendToBIS() {
		Collection<SKU> skus = skuListProvider.getAvailableSKUs();
		return getFilteredSkus(skus);
	}

	@Override
	public Collection<SKU> getSkusToSendToBIS(ProductionMode pProductionMode) {
		Collection<SKU> skus = skuListProvider.getAvailableSKUsForProductionMode(pProductionMode);
		return getFilteredSkus(skus);
	}
	
	
	private Collection<SKU> getFilteredSkus(Collection<SKU> skus) {
		Map<String, SKU> map = new TreeMap<>();
		skus.forEach(sku -> map.put(sku.getAppearanceCode(), sku));
		return map.values();
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}
}
