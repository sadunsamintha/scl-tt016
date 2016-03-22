package com.sicpa.standard.sasscl.devices.brs.skuCheck;

import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProduct;
import com.sicpa.tt018.scl.model.AlbaniaSKU;

public class AlbaniaCompliantProductSkuResolver implements CompliantProduct<AlbaniaSKU> {

	@Override
	public boolean isCompliant(AlbaniaSKU sku) {
		return true;
	}

}
