package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator;

import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;

/**
 * Responsible to check the product and the DecodeCameraCode and then set the correct status of the product
 * 
 * @author DIelsch
 * 
 */
public interface IProductValidator {

	/**
	 * check if the product is valid and then set the status of the product
	 * 
	 * @param product
	 *            the product created by the activation
	 * @param cameraCode
	 *            crytpo result
	 */
	void validate(Product product, DecodedCameraCode cameraCode);
}