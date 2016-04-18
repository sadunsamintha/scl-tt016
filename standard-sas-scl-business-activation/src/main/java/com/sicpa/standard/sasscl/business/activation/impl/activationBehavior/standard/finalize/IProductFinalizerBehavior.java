package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.finalize;

import com.sicpa.standard.sasscl.model.Product;

/**
 * Defines what to do at the end of the activation process for a valid code
 */
public interface IProductFinalizerBehavior {

	/**
	 * last method called by the standard activation behavior
	 * 
	 * @param product
	 *            the product that has gone through the activation
	 */
	void finalize(Product product);

}
