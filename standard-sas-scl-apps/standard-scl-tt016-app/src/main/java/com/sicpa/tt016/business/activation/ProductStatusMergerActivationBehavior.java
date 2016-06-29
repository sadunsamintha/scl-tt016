package com.sicpa.tt016.business.activation;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import com.sicpa.tt016.devices.plc.PlcCameraResultIndexManager;
import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResultEvent;
import com.sicpa.tt016.model.TT016ProductStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.PRODUCT_SCANNED;

public class ProductStatusMergerActivationBehavior extends StandardActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(ProductStatusMergerActivationBehavior.class);

	private Queue<PlcCameraProductStatus> plcCameraProductStatuses = new LinkedList<>();
	private Queue<Product> products = new LinkedList<>();

	private ProductionBatchProvider productionBatchProvider;
	private PlcCameraResultIndexManager plcCameraResultIndexManager;

	private final Object lock = new Object();

	@Override
	public Product receiveCode(Code code, boolean isValid) {
		Product product = super.receiveCode(code, isValid);

		synchronized (lock) {
			products.add(product);

			if (isPlcCameraStatusAvailable()) {
				mergeProductStatuses();
			}
		}

		return null;
	}

	@Subscribe
	public void receivePlcCameraResult(PlcCameraResultEvent event) {
		logger.debug("PLC camera result received: [index=" + event.getIndex() + ", decodingTime=" + event
				.getDecodeTimeMs() + ", productStatus=" + event.getPlcCameraProductStatus().getDescription() + "]");

		synchronized (lock) {
			insertMissingPlcCameraResultsIfNeeded(event.getIndex());

			plcCameraProductStatuses.add(event.getPlcCameraProductStatus());

			if (isCameraStatusAvailable()) {
				mergeProductStatuses();
			}
		}
	}

	private void fireNewProduct(Product product) {
		if (!product.getStatus().equals(SENT_TO_PRINTER_WASTED)) {
			logger.debug("New product = {}", product);
		}

		MonitoringService.addSystemEvent(new BasicSystemEvent(PRODUCT_SCANNED));
		EventBusService.post(new NewProductEvent(product));
	}

	private boolean isCameraStatusAvailable() {
		return !products.isEmpty();
	}

	private boolean isPlcCameraStatusAvailable() {
		return !plcCameraProductStatuses.isEmpty();
	}

	private void mergeProductStatuses() {
		Product product = products.poll();
		PlcCameraProductStatus plcCameraProductStatus = plcCameraProductStatuses.poll();

		setProductAsEjectedIfPlcEjected(product, plcCameraProductStatus);
		product.setProductionBatchId(productionBatchProvider.get());
		product.setQc(product.getCode().getSource());

		fireNewProduct(product);
	}

	private void setProductAsEjectedIfPlcEjected(Product product, PlcCameraProductStatus plcCameraProductStatus) {
		if (plcCameraProductStatus.equals(PlcCameraProductStatus.EJECTED_PRODUCER)) {
			product.setStatus(TT016ProductStatus.EJECTED_PRODUCER);
		}
	}

	private void insertMissingPlcCameraResultsIfNeeded(int index) {
		int indexDifference = plcCameraResultIndexManager.getIndexDifference(index);

		if (indexDifference > 1) {
			logger.warn("Missing PLC camera result! Number of results missing: " + indexDifference);

			for (int i = 1; i <= indexDifference; i++) {
				plcCameraProductStatuses.add(PlcCameraProductStatus.valueOf(PlcCameraProductStatus.NOT_DEFINED.getId()
				));
			}
		}
	}

	public void setProductionBatchProvider(ProductionBatchProvider productionBatchProvider) {
		this.productionBatchProvider = productionBatchProvider;
	}

	public void setPlcCameraResultIndexManager(PlcCameraResultIndexManager plcCameraResultIndexManager) {
		this.plcCameraResultIndexManager = plcCameraResultIndexManager;
	}
}
