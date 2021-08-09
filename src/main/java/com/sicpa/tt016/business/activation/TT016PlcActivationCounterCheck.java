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
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.model.event.TT016ProductPlcCameraEvent;

public class TT016PlcActivationCounterCheck extends PlcActivationCounterCheck {

	private static Logger logger = LoggerFactory.getLogger(TT016PlcActivationCounterCheck.class);
	
	protected ProductionParameters productionParameters;

	@Override
	@Subscribe
	public void notifyNewProduct(NewProductEvent evt) {
		// Production Mode that is not Standard/Domestic will be here
		
		if (model.isEnabled() && !productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
			if (acceptTT016ProductNotStandard(evt.getProduct())) {
				counterFromActivation.incrementAndGet();
			}
		}
	}
	
	@Subscribe
	public void notifyNewTT016Product(TT016ProductPlcCameraEvent evt) {
		// Production Mode that is Standard/Domestic will be here (triggered by ProductStatusMerger and ProductStatusMergerSAS)
		
		if (model.isEnabled() && productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
			if (acceptTT016ProductStandard(evt)) {
				counterFromActivation.incrementAndGet();
			}
		}
	}
	
	protected boolean acceptTT016ProductStandard(TT016ProductPlcCameraEvent evt) {
		if (evt.getPlcCameraProductStatus() != null && !evt.getPlcCameraProductStatus().equals(EJECTED_PRODUCER)) {
			return !asList(SENT_TO_PRINTER_WASTED, OFFLINE).contains(evt.getProduct().getStatus());
		}
		
		return false;
	}
		
	protected boolean acceptTT016ProductNotStandard(Product p) {
		return !asList(SENT_TO_PRINTER_WASTED, OFFLINE, TT016ProductStatus.EJECTED_PRODUCER).contains(p.getStatus());
	}
	
	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
