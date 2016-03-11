package com.sicpa.standard.sasscl.model.custom;

/**
 * 
 * Integer type of custom property
 * 
 */
public class IntegerCustomProperty extends CustomProperty<Integer> {

	public IntegerCustomProperty(String name, int defaultValue) {
		super(name, Integer.class, defaultValue);
	}

}
