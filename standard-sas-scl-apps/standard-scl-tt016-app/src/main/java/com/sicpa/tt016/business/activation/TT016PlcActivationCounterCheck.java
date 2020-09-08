package com.sicpa.tt016.business.activation;

import static com.sicpa.standard.sasscl.model.ProductStatus.OFFLINE;
import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;
import static com.sicpa.tt016.model.PlcCameraProductStatus.EJECTED_PRODUCER;
import static java.util.Arrays.asList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.PlcActivationCounterCheck;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt016.model.event.TT016ProductPlcCameraEvent;

public class TT016PlcActivationCounterCheck extends PlcActivationCounterCheck {

	private static Logger logger = LoggerFactory.getLogger(TT016PlcActivationCounterCheck.class);
	
	protected ProductionParameters productionParameters;

	@Override
	@Subscribe
	public void notifyNewProduct(NewProductEvent evt) {
		// To be handled by notifyNewTT016Product
	}
	
	@Subscribe
	public void notifyNewTT016Product(TT016ProductPlcCameraEvent evt) {
		if (model.isEnabled()) {
			if (acceptTT016Product(evt)) {
				counterFromActivation.incrementAndGet();
			}
		}
	}
	
	protected boolean acceptTT016Product(TT016ProductPlcCameraEvent evt) {
		if (evt.getPlcCameraProductStatus() != null && !evt.getPlcCameraProductStatus().equals(EJECTED_PRODUCER)) {
			return !asList(SENT_TO_PRINTER_WASTED, OFFLINE).contains(evt.getProduct().getStatus());
		}
		
		return false;
	}
	
	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
