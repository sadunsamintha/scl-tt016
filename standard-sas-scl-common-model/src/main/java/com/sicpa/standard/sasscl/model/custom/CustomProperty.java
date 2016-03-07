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

	private final T defaultValue;

	public CustomProperty(String name, Class<T> propertyClass, T defaultValue) {
		this.name = name;
		this.propertyClass = propertyClass;
		this.defaultValue = defaultValue;
	}

	public Class<T> getPropertyClass() {
		return this.propertyClass;
	}

	public String getName() {
		return name;
	}

	public T getDefaultValue() {
		return defaultValue;
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
