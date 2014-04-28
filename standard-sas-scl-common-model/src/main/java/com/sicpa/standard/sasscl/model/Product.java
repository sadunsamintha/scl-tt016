package com.sicpa.standard.sasscl.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.sicpa.standard.sasscl.model.custom.Customizable;

public class Product extends Customizable implements Serializable {

	private static final long serialVersionUID = 1L;
	protected Code code;
	protected ProductStatus status;
	protected SKU sku;
	protected String productionBatchId;
	protected Date activationDate;
	protected Long subsystem;
	// camera id
	protected String qc;

	// to know if the product is from sas or scl
	// a Boolean is needed to be nullable when serialization is done
	// in order to put the printed property on the packagedProducts and then to null the prop on the product so it
	// doesn't appear in the serialisation file

	protected Boolean printed;

	public Product() {
		setStatus(ProductStatus.UNREAD);
		this.activationDate = new Date();
	}

	public Product(Code code, ProductStatus status, SKU sku, String productionBatchId, Date activationDate,
			long subsystem) {
		this.code = code;
		this.status = status;
		this.sku = sku;
		this.productionBatchId = productionBatchId;
		this.activationDate = activationDate;
		this.subsystem = subsystem;
	}

	public Code getCode() {
		return this.code;
	}

	public void setCode(final Code code) {
		this.code = code;
	}

	public void setStatus(final ProductStatus status) {
		this.status = status;
	}

	public ProductStatus getStatus() {
		return this.status;
	}

	public SKU getSku() {
		return this.sku;
	}

	public void setSku(final SKU sku) {
		this.sku = sku;
	}

	public String getProductionBatchId() {
		return productionBatchId;
	}

	public void setProductionBatchId(String productionBatchId) {
		this.productionBatchId = productionBatchId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activationDate == null) ? 0 : activationDate.hashCode());
		result = prime * result + ((productionBatchId == null) ? 0 : productionBatchId.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((this.qc == null) ? 0 : this.qc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (activationDate == null) {
			if (other.activationDate != null)
				return false;
		} else if (!activationDate.equals(other.activationDate))
			return false;
		if (productionBatchId == null) {
			if (other.productionBatchId != null)
				return false;
		} else if (!productionBatchId.equals(other.productionBatchId))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;

		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;

		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;

		if (this.qc == null) {
			if (other.qc != null)
				return false;
		} else if (!this.qc.equals(other.qc))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Date getActivationDate() {
		return this.activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Long getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(Long subsystem) {
		this.subsystem = subsystem;
	}

	public String getQc() {
		return qc;
	}

	public void setQc(String cameraRead) {
		this.qc = cameraRead;
	}

	public void setPrinted(Boolean printed) {
		this.printed = printed;
	}

	public Boolean isPrinted() {
		return printed == null ? false : printed;
	}
}
