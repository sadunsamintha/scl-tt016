package com.sicpa.standard.sasscl.model.custom;

/**
 * 
 * String type of custom property
 * 
 */
public class StringCustomProperty extends CustomProperty<String> {

	public StringCustomProperty(String name, String defaultValue) {
		super(name, String.class, defaultValue);
	}

}
