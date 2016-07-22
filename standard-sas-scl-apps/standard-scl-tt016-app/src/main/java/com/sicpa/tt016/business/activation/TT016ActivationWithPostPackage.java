package com.sicpa.tt016.business.activation;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.impl.ActivationWithPostPackage;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.tt016.model.event.TT016NewProductEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.PRODUCT_SCANNED;

public class TT016ActivationWithPostPackage extends ActivationWithPostPackage {

	private static final Logger logger = LoggerFactory.getLogger(TT016ActivationWithPostPackage.class);

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
}
