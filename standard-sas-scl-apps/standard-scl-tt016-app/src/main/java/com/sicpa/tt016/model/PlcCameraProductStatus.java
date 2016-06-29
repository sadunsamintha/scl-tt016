package com.sicpa.tt016.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlcCameraProductStatus {

	protected final static Logger logger = LoggerFactory.getLogger(PlcCameraProductStatus.class);

	public final static PlcCameraProductStatus NOT_DEFINED = new PlcCameraProductStatus(0, "NOT_DEFINED");
	public final static PlcCameraProductStatus GOOD = new PlcCameraProductStatus(1, "GOOD_CODE");
	public final static PlcCameraProductStatus UNREADABLE = new PlcCameraProductStatus(2, "UNREADABLE_CODE");
	public final static PlcCameraProductStatus NO_INK = new PlcCameraProductStatus(3, "NO_INK");
	public final static PlcCameraProductStatus EJECTED_PRODUCER = new PlcCameraProductStatus(5, "EJECTED_PRODUCER");

	private int id;
	private String description;

	public PlcCameraProductStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public static PlcCameraProductStatus valueOf(int id) {
		switch (id) {
			case 1:
				return GOOD;
			case 2:
				return UNREADABLE;
			case 3:
				return NO_INK;
			case 5:
				return EJECTED_PRODUCER;
			default:
				throw new IllegalArgumentException("No product status for supplied id: " + id);
		}
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PlcCameraProductStatus that = (PlcCameraProductStatus) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
