package com.sicpa.standard.sasscl.model;

import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import com.sicpa.standard.sasscl.model.custom.Customizable;

public class SKU extends Customizable implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	public static final int UNKNOWN_SKU_ID = -1;

	private CodeType codeType;
	private int id;
	private String description;
	private ImageIcon image;
	private final List<String> barcodes = new ArrayList<>();

	public SKU(int id, String description, List<String> barcodes) {
		this.barcodes.addAll(barcodes);
		this.id = id;
		this.description = description;
	}

	public SKU() {
		this(-1, "", emptyList());
	}

	public SKU(int id) {
		this(id, "");
	}

	public SKU(int id, String description) {
		this(id, description, Collections.emptyList());
	}

	public int getId() {
		return this.id;
	}

	public CodeType getCodeType() {
		return this.codeType;
	}

	public void setCodeType(CodeType codeType) {
		this.codeType = codeType;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public String toString() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof SKU) {
			if (id == -1 || ((SKU) obj).id == -1) {
				return (description.equals(((SKU) obj).description));
			} else {
				return (id == ((SKU) obj).id);
			}
		}
		return false;
	}

	public String getDescription() {
		return this.description;
	}

	public ImageIcon getImage() {
		return this.image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public boolean containsBarcode(String barcode) {
		return barcodes.contains(barcode);
	}

	public List<String> getBarCodes() {
		return Collections.unmodifiableList(barcodes);
	}

	public void addBarcode(String barcode) {
		barcodes.add(barcode);
	}

	public SKU copySkuForProductionData() {
		SKU res = new SKU();
		res.image = null;
		res.codeType = codeType;
		res.description = description;
		res.id = id;
		res.setProperties(copyProperties());

		return res;
	}

}
