package com.sicpa.standard.sasscl.controller.productionconfig.config;

public class D900CameraType {

	public static final D900CameraType D900 = new D900CameraType("D900");

	protected final String description;

	public D900CameraType(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		D900CameraType other = (D900CameraType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReaderType [description=" + description + "]";
	}

	public String getDescription() {
		return description;
	}
}
