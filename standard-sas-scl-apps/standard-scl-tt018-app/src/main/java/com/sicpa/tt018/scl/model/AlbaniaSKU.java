package com.sicpa.tt018.scl.model;

import java.util.List;

import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductPackagings;

public class AlbaniaSKU extends SKU {
	private static final long serialVersionUID = 5314601840673359757L;

	public static final String SKU = "SKU.DESCRIPTION";

	private String brand;
	private String variant;
	private String volume;
	private boolean isBlobEnabled;
	private ProductPackagings productPackaging;

	public AlbaniaSKU(final int id, final String description, final String volume, final String brand, final String variant, final List<String> barcodes, boolean isBlobEnabled, ProductPackagings productPackaging) {
		super(id, description, barcodes);
		this.brand = brand;
		this.variant = variant;
		this.volume = volume;
		this.isBlobEnabled = isBlobEnabled;
		this.productPackaging = productPackaging;
	}

	public AlbaniaSKU(final AlbaniaSKU sku) {
		this(sku.getId(), sku.getDescription(), sku.getVolume(), sku.getBrand(), sku.getVariant(), sku.getBarCodes(), sku.isBlobEnabled(), sku.getProductPackaging());
		setImage(sku.getImage());
	}

	public AlbaniaSKU(final int id, final String description, final String volume, final String brand, final String variant, final List<String> barcodes, boolean isBlobEnabled) {
		super(id, description, barcodes);
		this.brand = brand;
		this.variant = variant;
		this.volume = volume;
		this.isBlobEnabled = isBlobEnabled;
	}

	protected void setBrand(final String brand) {
		this.brand = brand;
	}

	public String getBrand() {
		return brand;
	}

	protected void setVariant(final String variant) {
		this.variant = variant;
	}

	public String getVariant() {
		return variant;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	public ProductPackagings getProductPackaging() {
		return productPackaging;
	}

	public void setProductPackaging(ProductPackagings productPackaging) {
		this.productPackaging = productPackaging;
	}

	public boolean isBlobEnabled() {
		return isBlobEnabled;
	}

	public void setBlobEnabled(boolean isBlobEnabled) {
		this.isBlobEnabled = isBlobEnabled;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

}
