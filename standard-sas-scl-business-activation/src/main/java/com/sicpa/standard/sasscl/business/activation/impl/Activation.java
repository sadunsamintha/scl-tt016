package com.sicpa.standard.sasscl.business.activation.impl;

import com.sicpa.standard.sasscl.model.ProductStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.activation.ActivationException;
import com.sicpa.standard.sasscl.business.activation.IActivation;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.BeforeActivationResult;
import com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.IBeforeActivationAction;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.ActivationBehaviorProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;

/**
 * Activation receives code from the camera and then process then via a IActivationBehavior.<br>
 * registered listeners will be notified of products created during the activation process
 * 
 * @author DIelsch
 * 
 */
public class Activation implements IActivation {

	private static final Logger logger = LoggerFactory.getLogger(Activation.class);

	protected ActivationBehaviorProvider activationBehaviorProvider;

	protected ProductionBatchProvider productionBatchProvider;

	protected IBeforeActivationAction preActivationAction;

	public Activation() {

	}

	public void setActivationBehaviorProvider(ActivationBehaviorProvider activationBehaviorProvider) {
		this.activationBehaviorProvider = activationBehaviorProvider;
	}

	public void setProductionBatchProvider(final ProductionBatchProvider productionBatchProvider) {
		this.productionBatchProvider = productionBatchProvider;
	}

	/**
	 * Method called whenever a good or bad code is received.<br>
	 * Process the code using the current IActivationBehavior<br>
	 * Notify listeners with the new product created during the processing.
	 * 
	 * @param code
	 *            the object that represents the code read by the camera
	 * @param good
	 *            true if the code is good, false otherwise
	 */
	@Override
	public void receiveCode(final Code code, final boolean good, ICameraAdaptor source) {

		String cameraName = source.getName();

		logger.debug("Code received at {} = {} , Is good code = {}", new Object[] { cameraName, code, good });

		Code codeAfterPreAction;
		boolean goodAfterPreAction;
		if (preActivationAction != null) {
			BeforeActivationResult res = preActivationAction.receiveCode(code, good, cameraName);
			if (res.isFiltered()) {
				return;
			}
			codeAfterPreAction = res.getCode();
			goodAfterPreAction = res.isValid();
		} else {
			codeAfterPreAction = code;
			goodAfterPreAction = good;
		}

		Product product = null;

		if (activationBehaviorProvider.get() == null) {
			logger.error("Activation behavior is null");
			return;
		}
		try {
			product = activationBehaviorProvider.get().receiveCode(codeAfterPreAction, goodAfterPreAction);

			if (product != null) {
				if (productionBatchProvider != null) {
					product.setProductionBatchId(productionBatchProvider.get());
				}
				product.setQc(cameraName);

				fireNewProduct(product);
			}
		} catch (ActivationException e) {
			logger.error("", e);
		}
	}

	/**
	 * notify the listeners that the specified product has been processed.
	 * 
	 * @param product
	 *            the created product
	 * @param cameraError
	 *            true if code read is good, false otherwise
	 */
	protected void fireNewProduct(final Product product) {
        if(!product.getStatus().equals(ProductStatus.SENT_TO_PRINTER_WASTED)) {
            logger.debug("New product = {}", product);
        }
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventType.PRODUCT_SCANNED));
		EventBusService.post(new NewProductEvent(product));
	}

	@Subscribe
	public synchronized void receiveCameraCode(final CameraGoodCodeEvent evt) {
		receiveCode(evt.getCode(), true, evt.getSource());
	}

	@Subscribe
	public synchronized void receiveCameraCodeError(final CameraBadCodeEvent evt) {
		receiveCode(evt.getCode(), false, evt.getSource());
	}

	public void setPreActivationAction(final IBeforeActivationAction preActivationAction) {
		this.preActivationAction = preActivationAction;
	}
}
