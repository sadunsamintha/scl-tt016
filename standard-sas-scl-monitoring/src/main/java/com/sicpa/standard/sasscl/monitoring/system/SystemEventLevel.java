package com.sicpa.standard.sasscl.monitoring.system;

import java.io.Serializable;

public class SystemEventLevel implements Serializable {

	private static final long serialVersionUID = 1L;
	public static SystemEventLevel ERROR = new SystemEventLevel("ERROR");
	public static SystemEventLevel WARNING = new SystemEventLevel("WARNING");
	public static SystemEventLevel INFO = new SystemEventLevel("INFO");

	protected String name;

	public SystemEventLevel(final String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SystemEventLevel other = (SystemEventLevel) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
