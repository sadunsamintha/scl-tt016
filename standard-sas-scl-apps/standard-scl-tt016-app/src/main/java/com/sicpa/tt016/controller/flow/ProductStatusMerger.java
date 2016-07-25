package com.sicpa.tt016.controller.flow;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import com.sicpa.tt016.devices.plc.PlcCameraResultIndexManager;
import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResult;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.model.event.PlcCameraResultEvent;
import com.sicpa.tt016.model.event.TT016NewProductEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.PRODUCT_SCANNED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;

public class ProductStatusMerger extends StandardActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(ProductStatusMerger.class);

	private Queue<PlcCameraResult> plcCameraResults = new LinkedList<>();
	private Queue<Product> products = new LinkedList<>();

	private ProductionBatchProvider productionBatchProvider;
	private PlcCameraResultIndexManager plcCameraResultIndexManager;

	private final Object lock = new Object();

	@Override
	public Product receiveCode(Code code, boolean isValid) {
		handleNewCameraProduct(super.receiveCode(code, isValid));

		return null;
	}

	@Subscribe
	public void receiveNewCameraProduct(TT016NewProductEvent event) {
		handleNewCameraProduct(event.getProduct());
	}

	@Subscribe
	public void receivePlcCameraResult(PlcCameraResultEvent event) {
		PlcCameraResult plcCameraResult = event.getPlcCameraResult();

		logger.debug("PLC camera result received: [lastByteEncryptedCode={}, index={}, decodingTime={}, " +
						"productStatus={}]", plcCameraResult.getEncryptedCodeLastByte(), plcCameraResult.getIndex(),
				plcCameraResult.getDecodeTimeMs(), plcCameraResult.getPlcCameraProductStatus().getDescription());

		synchronized (lock) {
			insertMissingPlcCameraResultsIfNeeded(plcCameraResult.getIndex());

			plcCameraResults.add(plcCameraResult);

			if (isCameraStatusAvailable()) {
				mergeProductStatuses();
			}
		}
	}

	@Subscribe
	public void flushProductAndPlcCameraProductStatusesQueues(ApplicationFlowStateChangedEvent event) {
		if (event.getCurrentState().equals(STT_STARTING)) {
			if (!products.isEmpty()) {
				logger.warn("Removed {} elements from \"products\" queue", products.size());
				products.clear();
			}

			if (!plcCameraResults.isEmpty()) {
				logger.warn("Removed {} elements from \"plc camera product statuses\" queue", plcCameraResults.size());
				plcCameraResults.clear();
			}
		}
	}

	public void setPlcCameraResultIndexManager(PlcCameraResultIndexManager plcCameraResultIndexManager) {
		this.plcCameraResultIndexManager = plcCameraResultIndexManager;
	}

	public void setProductionBatchProvider(ProductionBatchProvider productionBatchProvider) {
		this.productionBatchProvider = productionBatchProvider;
	}

	private void handleNewCameraProduct(Product product) {
		synchronized (lock) {
			products.add(product);

			if (isPlcCameraStatusAvailable()) {
				mergeProductStatuses();
			}
		}
	}

	private boolean isCameraStatusAvailable() {
		return !products.isEmpty();
	}

	private boolean isPlcCameraStatusAvailable() {
		return !plcCameraResults.isEmpty();
	}

	private void mergeProductStatuses() {
		Product product = products.poll();
		PlcCameraResult plcCameraResult = plcCameraResults.poll();
		PlcCameraProductStatus plcCameraProductStatus = plcCameraResult.getPlcCameraProductStatus();

		if (!isPlcCameraProductStatusNotDefined(plcCameraProductStatus)) {
			if (isPlcCameraProductStatusEjected(plcCameraProductStatus)) {
				setProductAsEjected(product);
			} else {
				ProductValidator.validate(product, plcCameraResult);
			}
		}

		setProductProperties(product);

		fireNewProduct(product);
	}

	private void fireNewProduct(Product product) {
		if (!product.getStatus().equals(SENT_TO_PRINTER_WASTED)) {
			logger.debug("New product = {}", product);
		}

		MonitoringService.addSystemEvent(new BasicSystemEvent(PRODUCT_SCANNED));
		EventBusService.post(new NewProductEvent(product));
	}

	private void setProductProperties(Product product) {
		product.setProductionBatchId(productionBatchProvider.get());
		product.setQc(product.getCode().getSource());
	}

	private boolean isPlcCameraProductStatusEjected(PlcCameraProductStatus plcCameraProductStatus) {
		return plcCameraProductStatus.equals(PlcCameraProductStatus.EJECTED_PRODUCER);
	}

	private boolean isPlcCameraProductStatusNotDefined(PlcCameraProductStatus plcCameraProductStatus) {
		return plcCameraProductStatus.equals(PlcCameraProductStatus.NOT_DEFINED);
	}

	private void setProductAsEjected(Product product) {
		product.setStatus(TT016ProductStatus.EJECTED_PRODUCER);
	}

	private void insertMissingPlcCameraResultsIfNeeded(int index) {
		int indexDifference = plcCameraResultIndexManager.getIndexDifference(index);

		if (indexDifference > 1) {
			logger.warn("Missing PLC camera result! Number of results missing: " + indexDifference);

			for (int i = 1; i <= indexDifference; i++) {
				plcCameraResults.add(new PlcCameraResult((byte) 0, 0, 0, PlcCameraProductStatus.NOT_DEFINED));
			}
		}
	}
}