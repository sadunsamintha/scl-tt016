package com.sicpa.tt016.business.activation;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.impl.Activation;
import com.sicpa.standard.sasscl.business.postPackage.IPostPackage;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.tt016.model.event.TT016NewProductEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;
import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.PRODUCT_SCANNED;

public class TT016ActivationWithPostPackage extends Activation {

	private static final Logger logger = LoggerFactory.getLogger(TT016ActivationWithPostPackage.class);

	protected IPostPackage postPackage;

	public TT016ActivationWithPostPackage() {
		super();
	}

	protected void fireNewProduct(Product product) {
		ProductStatus productStatus = product.getStatus();

		if (!productStatus.equals(SENT_TO_PRINTER_WASTED)) {
			logger.debug("New product = {}", product);
		}

		MonitoringService.addSystemEvent(new BasicSystemEvent(PRODUCT_SCANNED));

		EventBusService.post(!productStatus.equals(SENT_TO_PRINTER_WASTED)
				? new TT016NewProductEvent(product)
				: new NewProductEvent(product));
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
		if (currentState.equals(STT_EXIT) || currentState.equals(STT_SELECT_WITH_PREVIOUS)) {
			notifyProductionStoppedOnPostPackage(true);
		}

		//actually Connected state is matching Stopped state.
		if (currentState.equals(STT_CONNECTED)) {
			notifyProductionStoppedOnPostPackage(false);
		}
	}

	private void notifyProductionStoppedOnPostPackage(boolean resetPostPackage) {
		postPackage.notifyProductionStopped().forEach(this::fireNewProduct);

		if (resetPostPackage) {
			postPackage.reset();
		}
	}
}
