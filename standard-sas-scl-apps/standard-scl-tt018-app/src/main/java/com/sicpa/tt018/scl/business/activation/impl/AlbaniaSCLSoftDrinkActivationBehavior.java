package com.sicpa.tt018.scl.business.activation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.ExportActivationBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt018.scl.business.activation.constants.AlbaniaSCLActivationMessages;
import com.sicpa.tt018.scl.model.AlbaniaProduct;
import com.sicpa.tt018.scl.model.AlbaniaProductStatus;
import com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode;
import com.sicpa.tt018.scl.utils.AlbaniaCameraConstants;

public class AlbaniaSCLSoftDrinkActivationBehavior extends ExportActivationBehavior {

	private static Logger logger = LoggerFactory.getLogger(AlbaniaSCLSoftDrinkActivationBehavior.class);

	@Override
	public Product receiveCode(final Code code, final boolean valid) {

		logger.debug("Code received = {} , Is good code = {}", code, valid);

		AlbaniaProduct product = new AlbaniaProduct(AlbaniaProductionMode.SOFT_DRINK);

		if (productionParameters.getSku() != null) {
			product.setSku(productionParameters.getSku());
		}

		if (valid || AlbaniaCameraConstants.CAMERA_ERROR_BLOB_DETECTION_CODE.equals(code)) {
			product.setStatus(ProductStatus.UNREAD);
			EventBusService.post(new MessageEvent(AlbaniaSCLActivationMessages.EXCEPTION_CODE_IN_SOFT_DRINK));
		} else {
			product.setStatus(AlbaniaProductStatus.SOFT_DRINK);
		}
		product.setCode(code);
		return product;

	}

}
