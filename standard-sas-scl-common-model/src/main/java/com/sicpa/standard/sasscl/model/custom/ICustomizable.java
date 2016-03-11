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

	<T> void setProperty(ICustomProperty<T> customProperty, T object);

	void setProperties(Map<ICustomProperty<?>, Object> properties);

	<T> T getProperty(ICustomProperty<T> customProperty);

	<T> void clearProperty(ICustomProperty<T> customProperty);

	Map<ICustomProperty<?>, Object> copyProperties();
}
