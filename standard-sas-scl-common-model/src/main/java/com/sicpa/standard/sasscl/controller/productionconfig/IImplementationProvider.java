package com.sicpa.standard.sasscl.controller.productionconfig;

public interface IImplementationProvider {

	/**
	 * called by the Factories to create implementations.
	 */
	Object getImplementation(String beanName);

}
