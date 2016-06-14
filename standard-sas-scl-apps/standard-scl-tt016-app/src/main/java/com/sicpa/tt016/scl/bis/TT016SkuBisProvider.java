package com.sicpa.tt016.scl.bis;

import static com.sicpa.tt016.scl.model.TT016Sku.SKU_PHYSICAL_PROPERTY;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.sicpa.standard.sasscl.devices.bis.ISkuBisProvider;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class TT016SkuBisProvider implements ISkuBisProvider {

	private SkuListProvider skuListProvider;

	@Override
	public Collection<SKU> getSkusToSendToBIS() {
		Collection<SKU> skus = skuListProvider.getAvailableSKUs();
		return getFilteredSkus(skus);
	}

	private Collection<SKU> getFilteredSkus(Collection<SKU> skus) {
		Map<String, SKU> map = new TreeMap<>();

		for (SKU sku : skus) {
			String id = sku.getProperty(SKU_PHYSICAL_PROPERTY);
			map.put(id, sku);
		}
		return map.values();
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}
}
