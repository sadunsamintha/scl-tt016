package com.sicpa.standard.sasscl.model.custom;

/**
 * 
 * Class that encapsulates property name and property class
 * 
 * @param <T>
 */
public class CustomProperty<T> implements ICustomProperty<T> {

	private final String name;

	private final Class<T> propertyClass;

	/**
	 * 
	 * @param name
	 * @param propertyClass
	 */
	public CustomProperty(String name, Class<T> propertyClass) {
		this.name = name;
		this.propertyClass = propertyClass;
	}

	public Class<T> getPropertyClass() {
		return this.propertyClass;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
		CustomProperty<?> other = (CustomProperty<?>) obj;
		if (!this.name.equals(other.getName())) {
			return false;
		}
		return true;
	}
}
