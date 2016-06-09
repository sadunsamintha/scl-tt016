package com.sicpa.standard.sasscl.business.sku.selector;

import com.sicpa.standard.sasscl.model.SKU;

public interface ISkuRecognizedBuffer {

	void reset();

	void add(SKU sku);

	boolean isReady();

	SKU getSku();

}
