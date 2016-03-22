package com.sicpa.standard.sasscl.devices.brs.skuCheck;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.brs.event.BrsProductEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class BrsSkuCheck {

	private static final Logger logger = LoggerFactory.getLogger(BrsSkuCheck.class);

	private Set<String> validBarcodes = new HashSet<>();

	public BrsSkuCheck() {
	}

	public BrsSkuCheck(Set<String> validBarcodes) {
		this.validBarcodes = validBarcodes;
	}

	@Subscribe
	public void onProductionParametersChanged(ProductionParametersEvent evt) {

		this.validBarcodes.clear();

		if (evt.getProductionParameters().getProductionMode() == null) {

			logger.info("Setting BRS Valid Barcodes:" + evt.getProductionParameters().getSku().getBarCodes());

			this.validBarcodes.addAll(evt.getProductionParameters().getSku().getBarCodes());
			// TODO if the brs barcode is not compliance then we have to disable
			// the alert
		}
	}

	@Subscribe
	public void onBrsCodeReceived(BrsProductEvent evt) {
		logger.debug("BRS Code Received:" + evt.getCode());

		if (!validBarcodes.contains(evt.getCode())) {
			logger.info("Wrong SKU Detected, expected: {} , read: {}", validBarcodes, evt.getCode());
			EventBusService.post(new MessageEvent(MessageEventKey.BRS.BRS_WRONG_SKU));
		}
	}
}
