package com.sicpa.tt016.business.activation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.PlcActivationCounterCheck;
import com.sicpa.tt016.model.CameraResult;

public class TT016PlcActivationCounterCheck extends PlcActivationCounterCheck {

	private static Logger logger = LoggerFactory.getLogger(TT016PlcActivationCounterCheck.class);
	
	@Subscribe
	public void receiveNewCameraProduct(CameraResult event) {
		if (model.isEnabled()) {
			counterFromActivation.incrementAndGet();
		}
	}
	
	@Override
	@Subscribe
	public void notifyNewProduct(NewProductEvent evt) {
		// Don't increment counterFromActivation here
	}
}
