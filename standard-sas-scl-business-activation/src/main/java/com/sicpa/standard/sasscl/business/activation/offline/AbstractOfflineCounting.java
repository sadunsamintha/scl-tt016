package com.sicpa.standard.sasscl.business.activation.offline;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.OfflineCountingSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public abstract class AbstractOfflineCounting implements IOfflineCounting {

	private static final Logger logger = LoggerFactory.getLogger(AbstractOfflineCounting.class);

	protected SubsystemIdProvider subsystemIdProvider;
	protected ProductionParameters productionParameters;
	protected String productionBatchId;

	public AbstractOfflineCounting() {
	}

	/**
	 * generate "quantity" number of product giving each product a different timestamp from "from" to "to"
	 */
	protected void process(Date from, Date to, int quantity) {
		logger.info("offline counting - last stop {} , last product {} , qty {}", from, to, quantity);


		MonitoringService.addSystemEvent(new OfflineCountingSystemEvent(quantity, from, to));

		long current = from.getTime();
		long interval = (to.getTime() - from.getTime()) / quantity;
		productionBatchId = String.valueOf(System.currentTimeMillis());
		for (int i = 0; i < quantity; i++) {
			notifyProduct(new Date(current));
			current += interval;
		}
	}

	protected void notifyProduct(Date time) {
		Product p = new Product();
		p.setActivationDate(time);
		p.setSubsystem(subsystemIdProvider.get());
		p.setSku(getSKU());
		p.setStatus(ProductStatus.OFFLINE);
		p.setProductionBatchId(productionBatchId);
		EventBusService.post(new NewProductEvent(p));
	}

	protected SKU getSKU() {
		SKU ret = null;

		if (productionParameters.getSku() == null) {
			logger.info("not_able_to_determine_last_selected_sku_from_production_parameters");
		}else {
			ret = productionParameters.getSku();
		}

		return ret;
	}

	public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
		this.subsystemIdProvider = subsystemIdProvider;
	}
	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
