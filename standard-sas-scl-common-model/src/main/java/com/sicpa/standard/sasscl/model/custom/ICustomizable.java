package com.sicpa.standard.sasscl.model.custom;

import java.util.Map;

/**
 * Interface that is implemented by class that needs to have additional properties
 * 
 * Purpose of having this interface is to provide a way for a class that has the high possibility to be extended during
 * project customization to add additional attributes. Those classes can implement this interface to keep the customized
 * properties so that during customization it doesn't have to be extended and overridden.
 * 
 */
public interface ICustomizable {

	/**
	 * to set the value of a custom property
	 * 
	 * @param <T>
	 * @param customProperty
	 * @param object
	 */
	public <T> void setProperty(ICustomProperty<T> customProperty, T object);

	/**
	 * to set a list of custom properties
	 * 
	 * @param <T>
	 * @param properties
	 */
	public void setProperties(Map<ICustomProperty<?>, Object> properties);

	/**
	 * 
	 * to get the value of a property
	 * 
	 * @param <T>
	 * @param customProperty
	 * @return
	 */
	public <T> T getProperty(ICustomProperty<T> customProperty);

	/**
	 * to remove the property value from the instance
	 * 
	 * 
	 * @param <T>
	 * @param customProperty
	 */
	public <T> void clearProperty(ICustomProperty<T> customProperty);

	/**
	 * make a copy of all the properties
	 */
	public Map<ICustomProperty<?>, Object> copyProperties();

}
