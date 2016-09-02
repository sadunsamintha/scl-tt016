package com.sicpa.standard.sasscl.business.production.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.provider.IProvider;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.SKU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static com.sicpa.standard.client.common.utils.TaskExecutor.schedule;
import static java.util.Collections.synchronizedList;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ProductionWithUnknownSkuHandling extends Production {

	private static final Logger logger = LoggerFactory.getLogger(ProductionWithUnknownSkuHandling.class);

	private IProvider<SKU> unknownSkuProvider;

	private int unknownsBufferSize;
	private int stillUnknownCheckDelaySec;

	private final List<Product> unknownsBuffer = synchronizedList(new LinkedList<>());
	private ScheduledFuture<?> stillUnknownTaskCheck;

	@Override
	public void notifyNewProduct(NewProductEvent evt) {
		synchronized (unknownsBuffer) {
			Product product = evt.getProduct();

			if (isUnknown(product)) {
				handleUnknownProduct(product);
			} else {
				handleKnownProduct(product);
			}
		}
	}

	private void handleUnknownProduct(Product product) {
		logger.debug("adding to unknown buffer:{}", product);
		unknownsBuffer.add(product);
		if (isTooManyUnknown()) {
			movePreviousUnknownToProductionBuffer();
		} else {
			if (stillUnknownTaskCheck != null) {
				stillUnknownTaskCheck = schedule(() -> stillUnknownCheck(), stillUnknownCheckDelaySec, SECONDS, "");
			}
		}
	}

	private void stillUnknownCheck() {
		synchronized (unknownsBuffer) {
			movePreviousUnknownToProductionBuffer();
			stillUnknownTaskCheck = null;
		}
	}

	private void handleKnownProduct(Product product) {
		resetStillUnknownTask();
		if (!unknownsBuffer.isEmpty()) {
			logger.info("known product received when unknown buffer not empty");
			setSkuToPreviousUnknown(product.getSku());
			movePreviousUnknownToProductionBuffer();
		}
		moveToProductionBuffer(product);
	}

	private void resetStillUnknownTask() {
		if (stillUnknownTaskCheck != null) {
			stillUnknownTaskCheck.cancel(false);
			stillUnknownTaskCheck = null;
		}
	}

	private void moveToProductionBuffer(Product product) {
		super.notifyNewProduct(new NewProductEvent(product));
	}

	private boolean isTooManyUnknown() {
		return unknownsBuffer.size() > unknownsBufferSize;
	}

	@Subscribe
	public void handleApplicationStateChange(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(ApplicationFlowState.STT_SELECT_WITH_PREVIOUS)) {
			synchronized (unknownsBuffer) {
				movePreviousUnknownToProductionBuffer();
			}
		}
	}

	private boolean isUnknown(Product p) {
		return unknownSkuProvider.get().equals(p.getSku());
	}

	private void setSkuToPreviousUnknown(SKU sku) {
		logger.info("setting sku on unknown buffer:" + sku);
		unknownsBuffer.forEach(p -> p.setSku(sku));
	}

	private void movePreviousUnknownToProductionBuffer() {
		logger.info("moving unknown buffer to production buffer");
		unknownsBuffer.forEach(p -> moveToProductionBuffer(p));
		unknownsBuffer.clear();
	}

	public void setUnknownSkuProvider(IProvider<SKU> unknownSkuProvider) {
		this.unknownSkuProvider = unknownSkuProvider;
	}

	public void setUnknownsBufferSize(int unknownsBufferSize) {
		this.unknownsBufferSize = unknownsBufferSize;
	}

	public void setStillUnknownCheckDelaySec(int stillUnknownCheckDelaySec) {
		this.stillUnknownCheckDelaySec = stillUnknownCheckDelaySec;
	}
}
