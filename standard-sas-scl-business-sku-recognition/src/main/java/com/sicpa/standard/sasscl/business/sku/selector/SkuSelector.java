package com.sicpa.standard.sasscl.business.sku.selector;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_WITH_PREVIOUS;
import static com.sicpa.standard.sasscl.messages.MessageEventKey.SkuRecognition.UNEXPECTED_SKU_CHANGED;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.sku.IProductionChangeDetector;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.UnknownSkuProvider;
import com.sicpa.standard.sasscl.skureader.SkuNotRecognizedEvent;
import com.sicpa.standard.sasscl.skureader.SkuRecognizedEvent;

public class SkuSelector {

	private static final Logger logger = LoggerFactory.getLogger(SkuSelector.class);

	private ISkuRecognizedBuffer skuBuffer;
	private ISkuSelectionBehavior skuSelectionBehavior;
	private IProductionChangeDetector productionChangeDetector;
	private UnknownSkuProvider unknownSkuProvider;

	private SKU previousRecognizedSku;
	private Instant previousSkuEventTime;

	@Subscribe
	public void handleSkuRecognizedEvent(SkuRecognizedEvent evt) {
		skuEventReceived(evt.getSku());
	}

	@Subscribe
	public void handleSkuNotRecognizedEvent(SkuNotRecognizedEvent evt) {
		skuEventReceived(unknownSkuProvider.get());
	}

	private void skuEventReceived(SKU sku) {
		handleMaxDurationBetweenEvent();
		previousSkuEventTime = Instant.now();

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
		return !previousRecognizedSku.equals(newSku);
	}

	@Subscribe
	public void handleApplicationStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(STT_SELECT_WITH_PREVIOUS)) {
			reset();
		}
	}

	private void setSku(SKU sku) {
		ProductionParameters productionParameters = new ProductionParameters();
		productionParameters.setSku(sku);
		skuSelectionBehavior.duringProductionOnProductionParameterChanged(new ProductionParametersEvent(
				productionParameters));
	}

	private void reset() {
		skuBuffer.reset();
		previousRecognizedSku = null;
	}

	private boolean isTimeExceededSinceLastRecognizedSku() {
		if (previousSkuEventTime == null) {
			return false;
		}
		return productionChangeDetector.isPossibleProductionChange(previousSkuEventTime, Instant.now());
	}

	private void unexptedSkuChange(SKU newSku) {
		logger.info("unexptedSkuChange previous={} , new={}", previousRecognizedSku, newSku);
		setSku(newSku);
		EventBusService.post(new MessageEvent(this, UNEXPECTED_SKU_CHANGED, previousRecognizedSku, newSku));
	}

	private void firstSelection(SKU newSku) {
		setSku(newSku);
	}

	public void setSkuBuffer(ISkuRecognizedBuffer skuBuffer) {
		this.skuBuffer = skuBuffer;
	}

	public void setProductionChangeDetector(IProductionChangeDetector productionChangeDetector) {
		this.productionChangeDetector = productionChangeDetector;
	}

	public void setSkuSelectionBehavior(ISkuSelectionBehavior skuSelectionBehavior) {
		this.skuSelectionBehavior = skuSelectionBehavior;
	}

	public void setUnknownSkuProvider(UnknownSkuProvider unknownSkuProvider) {
		this.unknownSkuProvider = unknownSkuProvider;
	}
}
