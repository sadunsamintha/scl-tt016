package com.sicpa.standard.sasscl.devices.bis.skucheck;

import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.skucheck.SkuChecker;

public class BisSkuChecker extends SkuChecker{

	/*
	 * Match SKU by ID
	 * @see com.sicpa.standard.sasscl.skucheck.SkuChecker#getID(com.sicpa.standard.sasscl.model.SKU)
	 */
	@Override
	protected String getID(SKU sku) {
		return String.valueOf(sku.getId());
	}
	
	

}
