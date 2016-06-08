package com.sicpa.standard.sasscl.skureader;

import java.util.Optional;

import com.sicpa.standard.sasscl.model.SKU;

public interface ISkuFinder {
	
	Optional<SKU> getSkuFromBarcode(String barcode);
	
	Optional<SKU> getSkuFromId(int id);

}
