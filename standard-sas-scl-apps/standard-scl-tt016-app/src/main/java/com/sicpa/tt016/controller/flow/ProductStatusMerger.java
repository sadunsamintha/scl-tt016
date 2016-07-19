package com.sicpa.tt016.controller.flow;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt016.devices.plc.PlcCameraResultIndexManager;
import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResult;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.model.event.PlcCameraResultEvent;
import com.sicpa.tt016.model.event.TT016NewProductEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;

public class ProductStatusMerger {

	private static final Logger logger = LoggerFactory.getLogger(ProductStatusMerger.class);

	private Queue<PlcCameraResult> plcCameraResults = new LinkedList<>();
	private Queue<Product> products = new LinkedList<>();

	private PlcCameraResultIndexManager plcCameraResultIndexManager;

	private final Object lock = new Object();

	@Subscribe
	public void receiveNewProduct(TT016NewProductEvent event) {
		synchronized (lock) {
			products.add(event.getProduct());

			if (isPlcCameraStatusAvailable()) {
				mergeProductStatuses();
			}
		}
	}

	@Subscribe
	public void receivePlcCameraResult(PlcCameraResultEvent event) {
		PlcCameraResult plcCameraResult = event.getPlcCameraResult();

		logger.debug("PLC camera result received: [index={}, decodingTime={}, productStatus={}]", plcCameraResult
						.getIndex(), plcCameraResult.getDecodeTimeMs(), plcCameraResult.getPlcCameraProductStatus()
				.getDescription());

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
				logger.warn("Removed {} elements from \"plc camera product statuses\" queue",
						plcCameraResults.size());
				plcCameraResults.clear();
			}
		}
	}

	public void setPlcCameraResultIndexManager(PlcCameraResultIndexManager plcCameraResultIndexManager) {
		this.plcCameraResultIndexManager = plcCameraResultIndexManager;
	}

	private void fireNewProduct(Product product) {
		logger.debug("New product after status merged = {}", product);

		EventBusService.post(new NewProductEvent(product));
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
			} else if (!ProductStatusComparator.isEqual(plcCameraProductStatus, product.getStatus())) {
				logger.warn("Product status from PLC and camera not matching! plc:{}, camera:{}",
						plcCameraProductStatus.getDescription(), product.getStatus().toString());
			}
		}

		fireNewProduct(product);
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
				plcCameraResults.add(new PlcCameraResult(0, 0, 0, PlcCameraProductStatus.NOT_DEFINED));
			}
		}
	}

	private static class CodeComparator {
		public static boolean isEqual(String code, int lastByteEncryptedCode) {
			byte[] codeBytes = code.getBytes();

			return ((int) codeBytes[codeBytes.length - 1]) == lastByteEncryptedCode;
		}
	}

	private static class ProductStatusComparator {

		private static Map<PlcCameraProductStatus, ProductStatus> statusesMapping;

		static {
			statusesMapping = new HashMap<>(4);
			statusesMapping.put(PlcCameraProductStatus.GOOD, ProductStatus.AUTHENTICATED);
			statusesMapping.put(PlcCameraProductStatus.UNREADABLE, ProductStatus.UNREAD);
			statusesMapping.put(PlcCameraProductStatus.NO_INK, ProductStatus.NO_INK);
			statusesMapping.put(PlcCameraProductStatus.EJECTED_PRODUCER, TT016ProductStatus.EJECTED_PRODUCER);
		}

		public static boolean isEqual(PlcCameraProductStatus plcStatus, ProductStatus cameraStatus) {
			return statusesMapping.get(plcStatus).equals(cameraStatus);
		}
	}
}
