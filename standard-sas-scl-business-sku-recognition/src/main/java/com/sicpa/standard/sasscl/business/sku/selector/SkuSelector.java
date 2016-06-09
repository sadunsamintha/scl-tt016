package com.sicpa.standard.sasscl.business.sku.selector;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_WITH_PREVIOUS;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.sku.IProductionChangeDetector;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.skureader.SkuNotRecognizedEvent;
import com.sicpa.standard.sasscl.skureader.SkuRecognizedEvent;

public class SkuSelector {

	private static final Logger logger = LoggerFactory.getLogger(SkuSelector.class);

	private ISkuRecognizedBuffer skuBuffer;
	private ProductionParameters productionParameters;
	private IProductionChangeDetector productionChangeDetector;

	private SKU previousRecognizedSku;
	private Instant lastSkuRecognizedTime;

	@Subscribe
	public void handleSkuRecognizedEvent(SkuRecognizedEvent evt) {
		skuEventReceived(evt.getSku());
	}

	@Subscribe
	public void handleSkuNotRecognizedEvent(SkuNotRecognizedEvent evt) {
		skuEventReceived(getUnknownSku());
	}

	private SKU getUnknownSku() {
		return new SKU(-1, "unknown SKU");
	}

	private void skuEventReceived(SKU sku) {
		handleMaxDurationBetweenEvent();

		skuBuffer.add(sku);

		if (skuBuffer.isReady()) {
			SKU newSku = skuBuffer.getSku();
			if (isFirstTimeSkuIdentification()) {
				firstSelection(newSku);
			} else if (isUnexptedSkuChange(newSku)) {
				unexptedSkuChange(newSku);
			}
			previousRecognizedSku = newSku;
		}
	}

	private void handleMaxDurationBetweenEvent() {
		if (isTimeExceededSinceLastRecognizedSku()) {
			logger.info("Time Exceeded Since Last Identified Sku -> reset sku buffer");
			reset();
		}
	}

	private boolean isFirstTimeSkuIdentification() {
		return previousRecognizedSku == null;
	}

	private boolean isUnexptedSkuChange(SKU newSku) {
		return previousRecognizedSku.equals(newSku);
	}

	@Subscribe
	public void handleApplicationStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(STT_SELECT_WITH_PREVIOUS)) {
			reset();
		}
	}

	private void setSku(SKU sku) {
		logger.info("setting sku:" + sku);
		productionParameters.setSku(sku);
	}

	private void reset() {
		skuBuffer.reset();
		previousRecognizedSku = null;
	}

	private boolean isTimeExceededSinceLastRecognizedSku() {
		return productionChangeDetector.isPossibleProductionChange(lastSkuRecognizedTime, Instant.now());
	}

	private void unexptedSkuChange(SKU newSku) {
		logger.info("unexptedSkuChange previous={} , new={}", previousRecognizedSku, newSku);
		setSku(newSku);
		EventBusService.post(new UnexpectedSkuChangedEvent());
	}

	private void firstSelection(SKU newSku) {
		setSku(newSku);
	}

	public void setSkuBuffer(ISkuRecognizedBuffer skuBuffer) {
		this.skuBuffer = skuBuffer;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setProductionChangeDetector(IProductionChangeDetector productionChangeDetector) {
		this.productionChangeDetector = productionChangeDetector;
	}
}
