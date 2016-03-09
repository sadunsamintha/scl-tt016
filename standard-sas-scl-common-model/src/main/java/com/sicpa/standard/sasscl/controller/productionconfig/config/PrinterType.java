package com.sicpa.standard.sasscl.controller.productionconfig.config;

public class PrinterType {

	public static final PrinterType DOMINO = new PrinterType("DOMINO");
    public static final PrinterType OI2JET = new PrinterType("OI2JET");
    public static final PrinterType DOD = new PrinterType("DOD");
	public static final PrinterType LEIBINGER = new PrinterType("LEIBINGER");

	protected final String description;

	public PrinterType(String description) {
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
		PrinterType other = (PrinterType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return description;
	}
}
