package com.sicpa.standard.sasscl.model.custom;

/**
 * 
 * Boolean type of custom property
 * 
 */
public class BooleanCustomProperty extends CustomProperty<Boolean> {

	public BooleanCustomProperty(String name, boolean defaultValue) {
		super(name, Boolean.class, defaultValue);
	}

}
