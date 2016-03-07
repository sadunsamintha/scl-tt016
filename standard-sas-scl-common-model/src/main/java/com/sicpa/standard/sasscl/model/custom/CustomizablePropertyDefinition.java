package com.sicpa.standard.sasscl.model.custom;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 */
public class CustomizablePropertyDefinition implements ICustomizablePropertyDefinition {

	private Map<Class<?>, Map<String, ICustomProperty<?>>> customPropertiesMap = new HashMap<>();

	@Override
	public Map<Class<?>, Map<String, ICustomProperty<?>>> getPropertyDefinition() {
		return customPropertiesMap;
	}

	@Override
	public void addProperty(Class<? extends ICustomizable> clazz, ICustomProperty<?> customProperty) {
		Map<String, ICustomProperty<?>> customProperties = this.customPropertiesMap.get(clazz);
		if (customProperties == null) {
			customProperties = new HashMap<>();
			this.customPropertiesMap.put(clazz, customProperties);
		}
		customProperties.put(customProperty.getName(), customProperty);
	}

	@Override
	public boolean isPropertyAvailable(Class<? extends ICustomizable> clazz, ICustomProperty<?> customProperty) {
		Map<String, ICustomProperty<?>> customProperties = this.customPropertiesMap.get(clazz);
		if (customProperties == null)
			return false;
		if (customProperties.containsKey(customProperty.getName()))
			return true;
		return false;
	}

}
