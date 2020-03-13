package com.sicpa.tt016.business.activation;

import static com.sicpa.standard.sasscl.model.ProductStatus.OFFLINE;
import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;
import static com.sicpa.tt016.model.TT016ProductStatus.EJECTED_PRODUCER;
import static java.util.Arrays.asList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.PlcActivationCounterCheck;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt016.model.CameraResult;

public class TT016PlcActivationCounterCheck extends PlcActivationCounterCheck {

	private static Logger logger = LoggerFactory.getLogger(TT016PlcActivationCounterCheck.class);
	
	protected ProductionParameters productionParameters;
	
	@Subscribe
	public void receiveNewCameraProduct(CameraResult event) {
		// Increment counterFromActivation here for Standard/Domestic Production Mode only
		
		if (model.isEnabled() && productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
			counterFromActivation.incrementAndGet();
		}
	}
	
	@Override
	@Subscribe
	public void notifyNewProduct(NewProductEvent evt) {
		// Don't increment counterFromActivation here for Standard/Domestic Production Mode
		
		if (model.isEnabled() && !productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
			if (acceptProduct(evt.getProduct())) {
				counterFromActivation.incrementAndGet();
			}
		}
	}
	
	@Override
	protected boolean acceptProduct(Product p) {
		return !asList(SENT_TO_PRINTER_WASTED, OFFLINE, EJECTED_PRODUCER).contains(p.getStatus());
	}
	
	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
