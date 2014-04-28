package com.sicpa.standard.sasscl.model.custom;

/**
 * 
 * 
 * Default implementation @See CustomProperty
 * 
 * @param <T>
 * 
 */
public interface ICustomProperty<T> {

	/**
	 * 
	 * return the name of a custom property
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 
	 * return the class of a custom property
	 * 
	 * @return
	 */
	Class<T> getPropertyClass();

}
