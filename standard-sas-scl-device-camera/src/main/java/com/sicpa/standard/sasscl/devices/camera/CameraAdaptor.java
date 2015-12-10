package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.camera.controller.CameraException;
import com.sicpa.standard.camera.controller.ICameraController;
import com.sicpa.standard.camera.controller.ICameraControllerListener;
import com.sicpa.standard.camera.controller.ICognexCameraController;
import com.sicpa.standard.camera.controller.model.ICameraModel;
import com.sicpa.standard.camera.driver.CameraDriverEventCode;
import com.sicpa.standard.camera.driver.event.CameraDriverEventArgs;
import com.sicpa.standard.camera.driver.event.CodeReceivedEventArgs;
import com.sicpa.standard.camera.driver.event.ImageReceivedEventArgs;
import com.sicpa.standard.camera.parser.event.CodeEventArgs;
import com.sicpa.standard.camera.parser.event.ErrorCodeEventArgs;
import com.sicpa.standard.camera.parser.event.MetricsEventArgs;
import com.sicpa.standard.camera.parser.event.UnknownCodeEventArgs;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.common.util.ThreadUtils;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.sasscl.controller.productionconfig.ConfigurationFailedException;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurable;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurator;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.devices.AbstractDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.CameraJobFileDescriptor;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.CameraJobFilesConfig;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.ICameraJobFilesConfig;
import com.sicpa.standard.sasscl.devices.camera.transformer.ICameraImageTransformer;
import com.sicpa.standard.sasscl.devices.camera.transformer.IRoiCameraImageTransformer;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CameraAdaptor extends AbstractCameraAdaptor implements ICameraControllerListener,
		IConfigurable<CameraConfig, CameraAdaptor> {

	public static final Logger logger = LoggerFactory.getLogger(AbstractDevice.class);

	protected ICognexCameraController<? extends ICameraModel> controller;

	/**
	 * switch camera job based on production parameters
	 */
	protected ProductionParameters productionParameters;

	/**
	 * camera job file configuration, to load camera job based on selected production parameter
	 */
	protected ICameraJobFilesConfig cameraJobFilesConfig;

	protected final List<ICameraImageTransformer> cameraImageTransformers = new ArrayList<ICameraImageTransformer>();

	/**
	 * needed for spring do not remove
	 */
	public CameraAdaptor() {
	}

	public CameraAdaptor(final ICognexCameraController<? extends ICameraModel> controller,
			IRoiCameraImageTransformer roiCameraImageTransformer) {
		this.controller = controller;
		this.controller.addListener(this);
		cameraImageTransformers.add(roiCameraImageTransformer);
		roiCameraImageTransformer.setCamera(this);
	}

	/**
	 * Request camera to start reading camera result
	 * 
	 * @throws CameraAdaptorException
	 */
	@Override
	protected void doStart() throws DeviceException {
		try {
			if (status == DeviceStatus.CONNECTED || status == DeviceStatus.STOPPED) {
				controller.startReading();
				fireDeviceStatusChanged(DeviceStatus.STARTED);
			}
		} catch (com.sicpa.standard.camera.controller.CameraException e) {
			throw new CameraAdaptorException("Camera error when start reading", e);
		}
	}

    /**
     * Request camera to stop reading result
     *
     * fix for mismatch between BRS and LS counters
     * - camera codes received after camera required to stop
     * - Added sleepQuietly
     * @throws CameraAdaptorException
     */
    @Override
    public void doStop() throws CameraAdaptorException {
        try {
            ThreadUtils.sleepQuietly(100);
            if (status == DeviceStatus.STARTED) {
                controller.stopReading();
            }
        } catch (com.sicpa.standard.camera.controller.CameraException e) {
            throw new CameraAdaptorException("Camera error when stop reading", e);
        } finally {
            fireDeviceStatusChanged(DeviceStatus.STOPPED);
        }
    }

	@Override
	public void onCameraCodeReceived(final ICameraController<?> sender, final CodeReceivedEventArgs codeReceivedEvent) {
	}

	@Override
	public void onCameraImageReceived(final ICameraController<?> sender, final ImageReceivedEventArgs imageReceivedEvent) {
		fireCameraImage(imageReceivedEvent.getCode(), transformImage(imageReceivedEvent.getImage()));
	}

	@Override
	public void onMetricsReceived(final ICameraController<?> sender, final MetricsEventArgs metricsEvent) {
	}

	@Override
	public void onErrorCameraCodeReceived(final ICameraController<?> sender, final ErrorCodeEventArgs errorCodeEvent) {
		logger.debug("Camera read bad code: {}", errorCodeEvent.getCode());
		fireBadCode(errorCodeEvent.getCode());
	}

	@Override
	public void onUnknownCameraCodeReceived(final ICameraController<?> sender,
			final UnknownCodeEventArgs unknownCodeEvent) {

	}

	@Override
	public void onValidCameraCodeReceived(final ICameraController<?> sender, final CodeEventArgs codeEvent) {
		logger.debug("Camera read good code: {}", codeEvent.getCode());
		fireGoodCode(codeEvent.getCode());
	}

	@Override
	public void onCameraDriverEventReceived(final ICameraController<?> sender, final CameraDriverEventArgs driverEvent) {

		CameraDriverEventCode eventCode = CameraDriverEventCode.valueOf(driverEvent.getEventCode());
		switch (eventCode) {

		case CONNECTED:
			if (needCameraJobToBeSet()) {
				setCameraJob();
			}
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
			break;

		case CAMERA_SET_OFFLINE_MANUALLY:
		case FAILED_TO_ESTABLISH_RESULT_CONNECTION:
		case FAILED_TO_ESTABLISH_ERROR_CONNECTION:
		case FAILED_TO_ESTABLISH_FTP_CONNECTION:
		case FAILED_TO_CONNECT_TO_HOST:
		case FAILED_TO_ESTABLISH_TELNET_CONNECTION:
		case FAILED_TO_LOAD_JOB:
		case DISCONNECTED:
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
			break;

		case CONNECTING:
			break;
		default:
			break;
		}
	}

	private boolean needCameraJobToBeSet() {
		return cameraJobFilesConfig != null && cameraJobFilesConfig.isSetCameraJob();
	}

	/**
	 * 
	 * IDs to retrieve camera job file consists of production mode Id & SKU Id. By default this implementation support
	 * setting camera job for each SKU
	 * 
	 */
	protected List<Integer> getCameraJobProductDescriptorIDs() {
		List<Integer> listOfIDs = new ArrayList<Integer>();
		Integer productionModeId = productionParameters.getProductionMode().getId();
		if (productionParameters.getSku() == null) {
			listOfIDs.add(productionModeId);
			return listOfIDs;
		} else {
			Integer skuModeId = productionParameters.getSku().getId();
			listOfIDs.add(productionModeId);
			listOfIDs.add(skuModeId);
			return listOfIDs;
		}
	}

	/**
	 * set camera job for the current production parameters
	 */
	protected void setCameraJob() {

		final CameraJobFileDescriptor jobFileDescriptor = findCameraJobFileDescriptor();

		// if camera job file is not defined for the current production mode, skip the set job action
		if (jobFileDescriptor != null) {
			try {
				this.controller.setActiveJob(jobFileDescriptor.getCameraJobFileName());
			} catch (com.sicpa.standard.camera.controller.CameraException e) {
				logger.error("", e);
				logger.error("Failed to set camera job", e);
				EventBusService.post(new MessageEvent(this, MessageEventKey.Camera.CAMERA_FAIL_LOAD_JOB));
			}
		}
	}

	protected CameraJobFileDescriptor findCameraJobFileDescriptor() {
		if (cameraJobFilesConfig.isUseDefaultCameraJobFile()) {
			return cameraJobFilesConfig.getDefaultCameraJobFile();
		} else {
			List<Integer> productDescriptorIDs = getCameraJobProductDescriptorIDs();
			return cameraJobFilesConfig.retrieveCameraJobConfiguration(productDescriptorIDs);
		}
	}

	@Override
	public void doConnect() throws CameraAdaptorException {
		try {

			controller.create();
		} catch (com.sicpa.standard.camera.controller.CameraException e) {
			throw new CameraAdaptorException(e);
		}
	}

	@Override
	public void doDisconnect() throws CameraAdaptorException {
		try {
			controller.shutdown();
			// shutdown method above is a synchronous method. It waits all the thread die before exit. If the method is
			// executed successfully, the connection is destroyed.
		} finally {
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	/**
	 * for subclass to access the internal structure - standard camera controller
	 * 
	 * @return
	 */
	protected ICameraController<? extends ICameraModel> getController() {
		return controller;
	}

	// getter and setter

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setCameraJobFilesConfig(final CameraJobFilesConfig cameraJobFilesConfig) {
		this.cameraJobFilesConfig = cameraJobFilesConfig;
	}

	@Override
	public boolean isBlockProductionStart() {
		return true;
	}

	@Override
	public String readParameter(String paramName) throws CameraAdaptorException {
		try {
			return controller.readAliasValue(paramName);
		} catch (CameraException e) {
			throw new CameraAdaptorException(e);
		}
	}

	@Override
	public void writeParameter(String paramName, String value) throws CameraAdaptorException {
		try {
			controller.writeAliasValue(paramName, value);
		} catch (CameraException e) {
			throw new CameraAdaptorException(e);
		}
	}

	protected BufferedImage transformImage(Image img) {
		BufferedImage res = ImageUtils.convertToBufferedImage(img);
		for (ICameraImageTransformer transformer : cameraImageTransformers) {
			res = transformer.transform(res);
		}
		return res;
	}

	protected void initImageTransformer(StringMap stringMap) {
		for (ICameraImageTransformer transformer : cameraImageTransformers) {
			transformer.init(stringMap);
		}
	}

	@Override
	public IConfigurator<CameraConfig, CameraAdaptor> getConfigurator() {
		return new IConfigurator<CameraConfig, CameraAdaptor>() {
			@Override
			public void execute(CameraConfig config, CameraAdaptor configurable) throws ConfigurationFailedException {
				configure(config);
			}
		};
	}

	protected void configure(CameraConfig config) {
		initImageTransformer(config.getProperties());
	}

	public void setCameraImageTransformers(List<ICameraImageTransformer> cameraImageTransformers) {
		this.cameraImageTransformers.addAll(cameraImageTransformers);
	}
}
