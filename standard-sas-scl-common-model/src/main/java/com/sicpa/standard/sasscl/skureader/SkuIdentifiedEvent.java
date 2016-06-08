package com.sicpa.standard.sasscl.skureader;

import com.sicpa.standard.sasscl.model.SKU;

public class SkuIdentifiedEvent {

	private SKU sku;

	public SkuIdentifiedEvent(SKU sku) {
		super();
		this.sku = sku;
	}

	public SKU getSku() {
		return sku;
	}

	@Override
	public String toString() {
		return "SkuIdentifiedEvent [sku=" + sku + "]";
	}

}
