package com.sicpa.standard.sasscl.model;

import java.io.Serializable;

import com.sicpa.standard.sasscl.model.custom.Customizable;

@SuppressWarnings("serial")
public class ProductionParameters extends Customizable implements Serializable {

	private ProductionMode productionMode;
	private SKU sku;
	private String barcode;

	public ProductionParameters() {
	}

	public ProductionParameters(ProductionMode productionMode, SKU sku, String barcode) {
		this.productionMode = productionMode;
		this.sku = sku;
		this.barcode = barcode;
	}

	public ProductionMode getProductionMode() {
		return this.productionMode;
	}

	public void setProductionMode(ProductionMode productionMode) {
		this.productionMode = productionMode;
	}

	public SKU getSku() {
		return sku;
	}

	public void setSku(SKU sku) {
		this.sku = sku;
	}

	public String getBarcode() {
		return this.barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	@Override
	public String toString() {
		String barcode = "";
		if (barcode != null) {
			barcode = ", barcode=" + barcode;
		}
		return "[productionMode=" + productionMode + ", sku=" + sku + barcode + "]";
	}
}
