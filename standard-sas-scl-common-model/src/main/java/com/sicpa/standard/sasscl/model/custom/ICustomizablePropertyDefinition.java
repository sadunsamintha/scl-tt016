package com.sicpa.standard.sasscl.model.custom;

import java.util.Map;

/**
 * 
 * Interface that provides definition of custom properties of the main objects. It also defines methods to check the
 * property.
 * 
 */
public interface ICustomizablePropertyDefinition {

	/**
	 * to return the property definition
	 * 
	 * @return
	 */
	Map<Class<?>, Map<String, ICustomProperty<?>>> getPropertyDefinition();

	/**
	 * to add an entry of the property definition of a class
	 * 
	 * @param clazz
	 * @param customProperty
	 */
	void addProperty(Class<? extends ICustomizable> clazz, ICustomProperty<?> customProperty);

	/**
	 * 
	 * check if a property is available for a given class
	 * 
	 * @param clazz
	 * @param customProperty
	 * @return
	 */
	boolean isPropertyAvailable(Class<? extends ICustomizable> clazz, ICustomProperty<?> customProperty);
}
