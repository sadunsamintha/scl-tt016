package com.sicpa.tt016.model.event;

import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.tt016.model.PlcCameraProductStatus;

public class TT016ProductPlcCameraEvent {

	private Product product;
	private PlcCameraProductStatus plcCameraProductStatus;

	public TT016ProductPlcCameraEvent(Product product, PlcCameraProductStatus plcCameraProductStatus) {
		this.product = product;
		this.plcCameraProductStatus = plcCameraProductStatus;
	}

	public Product getProduct() {
		return product;
	}
	
	public PlcCameraProductStatus getPlcCameraProductStatus() {
		return plcCameraProductStatus;
	}
}

