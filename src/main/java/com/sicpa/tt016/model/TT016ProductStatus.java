package com.sicpa.tt016.model;

import com.sicpa.standard.sasscl.model.ProductStatus;


public class TT016ProductStatus {

	public final static ProductStatus EJECTED_PRODUCER = new ProductStatus(12, "EJECTED_PRODUCER");
	public final static ProductStatus REFEED_NO_INK = new ProductStatus(13, "REFEED_NO_INK");
	public final static ProductStatus AGING = new ProductStatus(14, "AGING");
}
