package com.sicpa.standard.sasscl.controller.productionconfig.config;

public class CameraType {

	public static final CameraType COGNEX = new CameraType("COGNEX");
	public static final CameraType OCV = new CameraType("OCV");
	public static final CameraType OCR = new CameraType("OCR");
	public static final CameraType DRS = new CameraType("DRS");

	protected final String description;

	public CameraType(String description) {
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
		CameraType other = (CameraType) obj;
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
