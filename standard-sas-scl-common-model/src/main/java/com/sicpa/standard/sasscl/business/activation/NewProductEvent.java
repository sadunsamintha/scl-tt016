package com.sicpa.standard.sasscl.business.activation;

import com.sicpa.standard.sasscl.model.Product;

/**
 * The class <code>NewProductEvent</code> encapsulates a product. Created when communicating between
 * <code>Activation</code> module and other modules. Can contain more business objects.
 * 
 * @author DIelsch
 * 
 */
public class NewProductEvent {

	protected Product product;

	/**
	 * Constructs a <code>NewProductEvent</code> with the specified <code>product</code> and
	 * <code>cameraCodeValid</code> arguments.
	 * 
	 * @param product
	 *            the activated product
	 * @param cameraCodeValid
	 *            the value that identifies if the code read by the camera is valid
	 */
	public NewProductEvent(final Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return this.product;
	}
}
