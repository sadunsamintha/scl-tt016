package com.sicpa.standard.sasscl.business.activation;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;

/**
 * Defines what is done with a received code.
 * 
 * @author DIelsch
 * 
 */
public interface IActivationBehavior {

	/**
	 * Called when a new code is read by the camera. It's responsible for handling the code and return an product based
	 * on the implemented behavior.
	 * 
	 * @param code
	 *            code read by the camera
	 * @return the product created with the code
	 * @throws Exception
	 *             if an error occurred during the activation of the code
	 */
	Product receiveCode(Code code, boolean isValid) throws ActivationException;
}
