package com.sicpa.tt018.scl.model;

import java.util.List;

import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductPackagings;

public class AlbaniaSKU extends SKU {

	private static final long serialVersionUID = 1L;

	public static String SKU = "SKU.DESCRIPTION";

	private String brand;
	private String variant;
	private String volume;
	private boolean isBlobEnabled;
	private ProductPackagings productPackaging;

	public AlbaniaSKU(int id, String description, String volume, String brand, String variant, List<String> barcodes,
			boolean isBlobEnabled, ProductPackagings productPackaging) {
		super(id, description, barcodes);
		this.brand = brand;
		this.variant = variant;
		this.volume = volume;
		this.isBlobEnabled = isBlobEnabled;
		this.productPackaging = productPackaging;
	}

	public AlbaniaSKU(int id, String description, String volume, String brand, String variant, List<String> barcodes,
			boolean isBlobEnabled) {
		super(id, description, barcodes);
		this.brand = brand;
		this.variant = variant;
		this.volume = volume;
		this.isBlobEnabled = isBlobEnabled;
	}

	public String getBrand() {
		return brand;
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
