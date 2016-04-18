package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.invalidCode;

import com.sicpa.standard.sasscl.model.Product;

/**
 * Defines what to do when the activation received an invalid code
 */
public interface IActivationInvalidCodeHandler {

	/**
	 * called by the activation when an invalid camera code is received
	 */
	void handleInvalidCode(Product product);

}
