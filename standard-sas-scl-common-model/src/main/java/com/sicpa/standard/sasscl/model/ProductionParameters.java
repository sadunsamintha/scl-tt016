package com.sicpa.standard.sasscl.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.sicpa.standard.client.common.utils.ObjectUtils;
import com.sicpa.standard.sasscl.model.custom.Customizable;

@SuppressWarnings("serial")
public class ProductionParameters extends Customizable implements Serializable {

	protected ProductionMode productionMode;
	protected SKU sku;
	protected String barcode;

	public ProductionParameters() {
	}

	public ProductionParameters(final ProductionMode productionMode, final SKU sku, final String barcode) {
		this.productionMode = productionMode;
		this.sku = sku;
		this.barcode = barcode;
	}

	public ProductionMode getProductionMode() {
		return this.productionMode;
	}

	public void setProductionMode(final ProductionMode productionMode) {
		this.productionMode = productionMode;
	}

	public SKU getSku() {
		return this.sku;
	}

	public void setSku(final SKU sku) {
		this.sku = sku;
	}

	public String getBarcode() {
		return this.barcode;
	}

	public void setBarcode(final String barcode) {
		this.barcode = barcode;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.computeCompositeHashCode(this.productionMode, this.sku, this.barcode);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ProductionParameters) {
			return obj == this || EqualsBuilder.reflectionEquals(this, obj);
		}
		return false;
	}

	@Override
	public String toString() {
		String barcode = "";
		if (this.barcode != null) {
			barcode = ", barcode=" + this.barcode;
		}
		return "[productionMode=" + productionMode + ", sku=" + sku + barcode + "]";
	}
}
