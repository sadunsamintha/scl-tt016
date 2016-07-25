package com.sicpa.tt016.business.postpackage;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.postPackage.PostPackageBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.tt016.model.event.TT016NewProductEvent;

import java.util.Collections;
import java.util.List;

public class TT016PostPackageBehavior extends PostPackageBehavior {

	@Override
	public List<Product> handleBadCode(Code code) {
		List<Product> products = super.handleBadCode(code);

		EventBusService.post(new TT016NewProductEvent(products.get(0)));

		//We return an empty list because we want to fire a product event to the ProductStatusMerger first and only
		// later to the Statistics, Storage, and other modules. By returning an empty list the
		// ActivationWithPostPackage won't fire the event.
		return Collections.emptyList();
	}

	@Override
	public List<Product> handleGoodCode(Code code) {
		super.handleGoodCode(code);

		//We return an empty list because we don't want the SENT_TO_PRINTER_WASTED products to be sent to the
		// ProductStatusMerger because it would result in the desynchronization of the queues of products
		return Collections.emptyList();
	}
}
