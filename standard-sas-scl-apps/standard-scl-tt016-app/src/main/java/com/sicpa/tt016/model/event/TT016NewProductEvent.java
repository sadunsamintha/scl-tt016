package com.sicpa.tt016.model.event;

import com.sicpa.standard.sasscl.model.Product;

/**
 * The class <code>TT016NewProductEvent</code> encapsulates a product. Created when communicating between
 * <code>Activation</code> module and the <code>ProductStatusMerger</code>.
 *
 * @author CDeAlmeida
 *
 */
public class TT016NewProductEvent {

	private Product product;

	public TT016NewProductEvent(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}
}