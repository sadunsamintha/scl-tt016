package com.sicpa.standard.sasscl.business.activation.impl;

import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.postPackage.IPostPackage;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;

public class ActivationWithPostPackage extends Activation {

	protected IPostPackage postPackage;

	public ActivationWithPostPackage() {
		super();
	}

	@Override
	public void receiveCode(Code code, boolean good, ICameraAdaptor source) {
		if (postPackage != null && postPackage.isEnabled()) {
			List<Product> productsGeneratedByPostPackage;
			if (good) {
				productsGeneratedByPostPackage = postPackage.handleGoodCode(code, source);
				super.receiveCode(code, good, source);
			} else {
				productsGeneratedByPostPackage = postPackage.handleBadCode(code, source);
			}
			for (Product p : productsGeneratedByPostPackage) {
				fireNewProduct(p);
			}
		} else {
			super.receiveCode(code, good, source);
		}
	}

	public IPostPackage getPostPackage() {
		return postPackage;
	}

	public void setPostPackage(IPostPackage postPackage) {
		this.postPackage = postPackage;
	}

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {
		ApplicationFlowState currentState = evt.getCurrentState();
		if (currentState.equals(ApplicationFlowState.STT_EXIT)
				|| currentState.equals(ApplicationFlowState.STT_SELECT_WITH_PREVIOUS)) {
			for (Product p : postPackage.notifyProductionStopped()) {
				fireNewProduct(p);
			}
			postPackage.reset();
		}
	}
}
