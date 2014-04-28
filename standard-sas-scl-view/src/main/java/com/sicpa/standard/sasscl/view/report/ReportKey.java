package com.sicpa.standard.sasscl.view.report;

public class ReportKey implements Comparable<ReportKey> {
	protected String date;
	protected String sku;
	protected String productionMode;

	public String getDate() {
		return this.date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public String getSku() {
		return this.sku;
	}

	public void setSku(final String sku) {
		this.sku = sku;
	}

	public String getProductionMode() {
		return this.productionMode;
	}

	public void setProductionMode(final String productionMode) {
		this.productionMode = productionMode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((productionMode == null) ? 0 : productionMode.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
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
		ReportKey other = (ReportKey) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (productionMode == null) {
			if (other.productionMode != null)
				return false;
		} else if (!productionMode.equals(other.productionMode))
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		return true;
	}

	@Override
	public int compareTo(final ReportKey o) {
		int res = this.date.compareTo(o.date);
		if (res == 0 && this.productionMode != null) {
			res = this.productionMode.compareTo(o.productionMode);
			if (res == 0 && this.sku != null) {
				res = this.sku.compareTo(o.sku);
			}
		}
		return res;
	}

	@Override
	public String toString() {
		return "ReportKey [date=" + date + ", sku=" + sku + ", productionMode=" + productionMode + "]";
	}
}
