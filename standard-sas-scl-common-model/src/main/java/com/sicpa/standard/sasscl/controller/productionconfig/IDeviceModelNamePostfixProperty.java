package com.sicpa.standard.sasscl.controller.productionconfig;

/**
 * used to set the postFix of a device<br>
 * the post fix used in the application is the printedElementId
 * 
 * @author DIelsch
 * 
 */
public interface IDeviceModelNamePostfixProperty {

	void set(String postfix);

	String get();

}
