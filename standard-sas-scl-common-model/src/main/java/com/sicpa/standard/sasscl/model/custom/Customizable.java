package com.sicpa.standard.sasscl.model.custom;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of ICustomizable - use HashMap to keep the value of each property
 * 
 */
public class Customizable implements ICustomizable, Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	private HashMap<ICustomProperty<?>, Object> customPropertiesMap = new HashMap<ICustomProperty<?>, Object>();

	@Override
	public <T> void setProperty(ICustomProperty<T> customProperty, T object) {
		if (!CustomizablePropertyFactory.isPropertyAvailable(this.getClass(), customProperty)) {
			throw new IllegalArgumentException(MessageFormat.format("Property {0} is not available for class {1} !",
					customProperty.getName(), this.getClass().getName()));
		}
		customPropertiesMap.put(customProperty, object);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProperty(ICustomProperty<T> customProperty) {
		if (!CustomizablePropertyFactory.isPropertyAvailable(this.getClass(), customProperty)) {
			throw new IllegalArgumentException(MessageFormat.format("Property {0} is not available for class {1} !",
					customProperty.getName(), this.getClass().getName()));
		}
		return (T) customPropertiesMap.get(customProperty);
	}

	@Override
	public <T> void clearProperty(ICustomProperty<T> customProperty) {
		if (!CustomizablePropertyFactory.isPropertyAvailable(this.getClass(), customProperty)) {
			throw new IllegalArgumentException(MessageFormat.format("Property {0} is not available for class {1} !",
					customProperty.getName(), this.getClass().getName()));
		}
		customPropertiesMap.remove(customProperty);
	}

	@Override
	public void setProperties(Map<ICustomProperty<?>, Object> properties) {
		this.customPropertiesMap.putAll(properties);
	}

	@Override
	public Map<ICustomProperty<?>, Object> copyProperties() {
		return new HashMap<ICustomProperty<?>, Object>(this.customPropertiesMap);
	}

}
