package com.sicpa.tt016.controller.flow;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;
import static com.sicpa.standard.sasscl.model.ProductStatus.SENT_TO_PRINTER_WASTED;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.PRODUCT_SCANNED;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.tt016.devices.plc.PlcCameraResultIndexManager;
import com.sicpa.tt016.model.CameraResult;
import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResult;
import com.sicpa.tt016.model.TT016Code;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.model.event.PlcCameraResultEvent;
import com.sicpa.tt016.model.event.TT016NewProductEvent;

public class ProductStatusMerger extends StandardActivationBehavior {

	private static final Logger logger = LoggerFactory.getLogger(ProductStatusMerger.class);

	private Queue<PlcCameraResult> plcCameraResults = new LinkedList<>();
	private Queue<CameraResult> cameraResults = new LinkedList<>();

	private ProductionBatchProvider productionBatchProvider;
	private PlcCameraResultIndexManager plcCameraResultIndexManager;
	
	protected BlobDetectionUtils blobDetectionUtils;
	protected boolean qrCodeActivation;

	private final Object lock = new Object();
	
	@Override
	public Product receiveCode(Code code, boolean isValid) {
		handleNewCameraProduct(new CameraResult(new TT016Code(code, isValid)));

		return null;
	}
	
	@Override
	protected DecodedCameraCode getDecodedCameraCode(Code code) {
		try {
			return (DecodedCameraCode) authenticatorProvider.get().decode(authenticatorModeProvider.get(),
					code.getStringCode(), code.getCodeType());
		} catch (CryptographyException e) {
			logger.error("", e);
		}
		return null;
	}

	@Subscribe
	public void receiveNewCameraProduct(TT016NewProductEvent event) {
		handleNewCameraProduct(new CameraResult(event.getProduct()));
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
			if (!cameraResults.isEmpty()) {
				logger.warn("Removed {} elements from \"camera results\" queue", cameraResults.size());
				cameraResults.clear();
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

	private void handleNewCameraProduct(CameraResult cameraResult) {
		synchronized (lock) {
			cameraResults.add(cameraResult);

			if (isPlcCameraStatusAvailable()) {
				mergeProductStatuses();
			}
		}
	}

	private boolean isCameraStatusAvailable() {
		return !cameraResults.isEmpty();
	}

	private boolean isPlcCameraStatusAvailable() {
		return !plcCameraResults.isEmpty();
	}

	private void mergeProductStatuses() {
		Product product = getProductFromCameraResult(cameraResults.poll());
		
		// Check if Blob is detected then set Product Status to Ink Detected
		if (!qrCodeActivation && blobDetectionUtils.isBlobDetected(product.getCode())) {
			product.setStatus(ProductStatus.INK_DETECTED);
		}
		
		PlcCameraResult plcCameraResult = plcCameraResults.poll();
		logger.debug("Java App received status cameraStatus:{} , plcStatus:{}",product.getStatus() ,plcCameraResult.getPlcCameraProductStatus());
		PlcCameraProductStatus plcCameraProductStatus = plcCameraResult.getPlcCameraProductStatus();

		if (!isPlcCameraProductStatusNotDefined(plcCameraProductStatus)) {
			if (isPlcCameraProductStatusEjected(plcCameraProductStatus)) {
				setProductAsEjected(product);
			} else if (isPlcCameraProductStatusAcquisitionError(plcCameraProductStatus)) {
				setProductAsUnread(product);
			} else if (!qrCodeActivation && isPlcCameraProductStatusInkDetected(plcCameraProductStatus)) {
				setProductAsInkDetected(product);
			} else if (isPlcCameraProductStatusNoInk(plcCameraProductStatus)) {
				setProductAsUnread(product);
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
	
	private boolean isPlcCameraProductStatusAcquisitionError(PlcCameraProductStatus plcCameraProductStatus) {
		return plcCameraProductStatus.equals(PlcCameraProductStatus.ACQUISITION_ERROR);
	}
	
	private boolean isPlcCameraProductStatusInkDetected(PlcCameraProductStatus plcCameraProductStatus) {
		return plcCameraProductStatus.equals(PlcCameraProductStatus.INK_DETECTED);
	}
	
	private boolean isPlcCameraProductStatusNoInk(PlcCameraProductStatus plcCameraProductStatus) {
		return plcCameraProductStatus.equals(PlcCameraProductStatus.NO_INK);
	}

	private void setProductAsEjected(Product product) {
		product.setStatus(TT016ProductStatus.EJECTED_PRODUCER);
	}
	
	private void setProductAsUnread(Product product) {
		product.setStatus(ProductStatus.SENT_TO_PRINTER_UNREAD);
	}
	
	private void setProductAsInkDetected(Product product) {
		product.setStatus(ProductStatus.INK_DETECTED);
	}

	private void insertMissingPlcCameraResultsIfNeeded(int index) {
		int indexDifference = plcCameraResultIndexManager.getIndexDifference(index);

		if (indexDifference > 1) {
			logger.warn("Missing PLC camera result! Number of results missing: " + indexDifference);

			/*
			for (int i = 1; i <= indexDifference; i++) {
				plcCameraResults.add(new PlcCameraResult((byte) 0, 0, 0, PlcCameraProductStatus.NOT_DEFINED));
			}
			*/
		}
	}

	private Product getProductFromCameraResult(CameraResult cameraResult) {
		Product product = cameraResult.getProduct();

		return product != null ? product : getActivationBehaviorProduct(cameraResult.getCode());
	}

	private Product getActivationBehaviorProduct(TT016Code code) {
		return super.receiveCode(code.getCode(), code.isValid());
	}
	
	public void setBlobDetectionUtils(BlobDetectionUtils blobDetectionUtils) {
		this.blobDetectionUtils = blobDetectionUtils;
	}

	public void setQrCodeActivation(boolean qrCodeActivation) {
		this.qrCodeActivation = qrCodeActivation;
	}
}