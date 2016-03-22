package com.sicpa.tt018.scl.model;

import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class AlbaniaProduct extends Product {

	private static final long serialVersionUID = 1L;

	private ProductionMode prodMode;

	public AlbaniaProduct(ProductionMode prodMode) {

		super();
		this.prodMode = prodMode;
	}

	public ProductionMode getProdMode() {
		return prodMode;
	}

}
