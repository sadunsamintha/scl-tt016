package com.sicpa.standard.sasscl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PackagedProducts implements Serializable {

	private static final long serialVersionUID = 1L;

	protected final List<Product> products;
	protected final long UID;

	protected transient String fileName;
	protected ProductStatus productStatus;
	protected String productionBatchId;
	protected long subsystem;
	protected boolean printed;

	public PackagedProducts(long uID, ProductStatus productStatus, String productionBatchId, long subsystem,
			boolean printed) {
		this(new ArrayList<Product>(), uID, productStatus, productionBatchId, subsystem, printed);
	}

	public PackagedProducts(List<Product> products, long uID, ProductStatus productStatus, String productionBatchId,
			long subsystem, boolean printed) {
		this.products = products;
		UID = uID;
		this.productStatus = productStatus;
		this.productionBatchId = productionBatchId;
		this.subsystem = subsystem;
		this.printed = printed;
	}

	public List<Product> getProducts() {
		return this.products;
	}

	public long getUID() {
		return this.UID;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public ProductStatus getProductStatus() {
		return productStatus;
	}

	public void setProductionBatchId(String productionBatchId) {
		this.productionBatchId = productionBatchId;
	}

	public String getProductionBatchId() {
		return productionBatchId;
	}

	public long getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(long subsystem) {
		this.subsystem = subsystem;
	}

	public boolean isPrinted() {
		return printed;
	}
}
