package com.sicpa.standard.sasscl.business.activation.offline;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.OfflineCountingSystemEvent;

public abstract class AbstractOfflineCounting implements IOfflineCounting {

	private static final Logger logger = LoggerFactory.getLogger(AbstractOfflineCounting.class);

	protected GlobalBean config;
	protected SKU offlineSku;
	protected String productionBatchId;

	public AbstractOfflineCounting() {
		offlineSku = new SKU(-1, "OFFLINE");
	}

	public void setConfig(GlobalBean config) {
		this.config = config;
	}

	/**
	 * generate "quantity" number of product giving each product a different timestamp from "from" to "to"
	 */
	protected void process(long from, long to, int quantity) {
		logger.info("offline couting - last stop {} , last product {} , qty {}", new Object[] { new Date(from),
				new Date(to), quantity });

		MonitoringService.addSystemEvent(new OfflineCountingSystemEvent(quantity, new Date(from), new Date(to)));
		long current = from;
		long interval = (to - from) / quantity;
		productionBatchId = String.valueOf(System.currentTimeMillis());
		for (int i = 0; i < quantity; i++) {
			notityProduct(new Date(current));
			current += interval;
		}
	}

	protected void notityProduct(Date time) {
		Product p = new Product();
		p.setActivationDate(time);
		p.setSubsystem(config.getSubsystemId());
		p.setSku(getSKU());
		p.setStatus(ProductStatus.OFFLINE);
		p.setProductionBatchId(productionBatchId);
		EventBusService.post(new NewProductEvent(p));
	}

	protected SKU getSKU() {
		return offlineSku;
	}
}
