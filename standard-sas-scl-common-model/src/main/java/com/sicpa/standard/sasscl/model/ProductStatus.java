package com.sicpa.standard.sasscl.model;

import java.io.Serializable;

public class ProductStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static ProductStatus UNREAD = new ProductStatus(0, "UNREAD");
	public final static ProductStatus NOT_AUTHENTICATED = new ProductStatus(1, "NOT_AUTHENTICATED");
	public final static ProductStatus AUTHENTICATED = new ProductStatus(2, "AUTHENTICATED");
	public final static ProductStatus TYPE_MISMATCH = new ProductStatus(3, "TYPE_MISMATCH");
	public final static ProductStatus EXPORT = new ProductStatus(4, "EXPORT");
	public final static ProductStatus MAINTENANCE = new ProductStatus(5, "MAINTENANCE");
	public final static ProductStatus SENT_TO_PRINTER_WASTED = new ProductStatus(6, "SENT_TO_PRINTER_WASTED");
	public final static ProductStatus SENT_TO_PRINTER_UNREAD = new ProductStatus(7, "SENT_TO_PRINTER_UNREAD");
	public final static ProductStatus COUNTING = new ProductStatus(8, "COUNTING");
	public final static ProductStatus NO_INK = new ProductStatus(9, "NO_INK");
	public final static ProductStatus OFFLINE = new ProductStatus(10, "OFFLINE");
	public final static ProductStatus REFEED = new ProductStatus(11, "REFEED");

	protected int id;
	protected String description;

	public ProductStatus(final int id, final String description) {
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(final Object obj) {

		if (obj instanceof ProductStatus) {
			return ((ProductStatus) obj).id == this.id;
		}
		return false;
	}

	@Override
	public String toString() {
		return this.description;
	}
}
