package com.sicpa.standard.sasscl.skureader;

import com.sicpa.standard.sasscl.model.SKU;

public class SkuRecognizedEvent {

	private SKU sku;

	public SkuRecognizedEvent(SKU sku) {
		super();
		this.sku = sku;
	}

	public SKU getSku() {
		return sku;
	}

	@Override
	public String toString() {
		return "SkuRecognizedEvent [sku=" + sku + "]";
	}

}
