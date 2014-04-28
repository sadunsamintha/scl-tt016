package com.sicpa.standard.sasscl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.sicpa.standard.sasscl.model.custom.Customizable;

public class SKU extends Customizable implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	protected CodeType codeType;

	protected int id;
	protected String description;
	protected ImageIcon image;
	protected List<String> barcodes;

	/**
	 * @param codeType
	 * @param productionMode
	 * @param id
	 */
	public SKU(final int id, final String description, final List<String> barcodes) {
		this.barcodes = barcodes;
		this.id = id;
		this.description = description;
	}

	public SKU() {
		this(-1, "", null);
	}

	public SKU(final int id) {
		this(id, "", null);
	}

	public SKU(final int id, final String description) {
		this(id, description, null);
	}

	public int getId() {
		return this.id;
	}

	public CodeType getCodeType() {
		return this.codeType;
	}

	public void setCodeType(final CodeType codeType) {
		this.codeType = codeType;
	}

	public void setId(final int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.description;
	}

	@Override
	public boolean equals(final Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof SKU) {
			if (id == -1 || ((SKU) obj).id == -1) {
				return (this.description.equals(((SKU) obj).description));
			} else {
				return (this.id == ((SKU) obj).id);
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

	public void setImage(final ImageIcon image) {
		this.image = image;
	}

	public boolean containsBarcode(final String barcode) {
		if (this.barcodes == null) {
			return false;
		}
		return this.barcodes.contains(barcode);
	}

	public List<String> getBarCodes() {
		return new ArrayList<String>(barcodes);
	}

	public SKU copySkuForProductionData() {
		SKU res = new SKU();
		res.barcodes = null;
		res.image = null;
		res.codeType = codeType;
		res.description = description;
		res.id = id;
		res.setProperties(copyProperties());

		return res;
	}

}
