package com.sicpa.tt018.scl.model;

import com.sicpa.standard.sasscl.model.ProductStatus;

public class AlbaniaProductStatus {

	private static final int SENT_TO_PRINTER_BLOB_ID = 100;
	private static final int SOFT_DRINK_ID = 101;

	private static final String SENT_TO_PRINTER_BLOB_DESC = "BLOB DETECTION";
	private static final String SOFT_DRINK_DESC = "SOFT DRINK";

	public final static ProductStatus SENT_TO_PRINTER_BLOB = new ProductStatus(SENT_TO_PRINTER_BLOB_ID, SENT_TO_PRINTER_BLOB_DESC);
	public final static ProductStatus SOFT_DRINK = new ProductStatus(SOFT_DRINK_ID, SOFT_DRINK_DESC);
}
