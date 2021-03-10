package com.sicpa.standard.sasscl.devices.camera.d900;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.camera.d900.controller.ID900CameraControllerListener;
import com.sicpa.standard.camera.d900.controller.ID900StandardCameraController;
import com.sicpa.standard.camera.d900.controller.model.ID900CameraModel;
import com.sicpa.standard.camera.d900.driver.D900CameraDriverEventCode;
import com.sicpa.standard.camera.d900.driver.event.D900CameraDriverEventArgs;
import com.sicpa.standard.camera.d900.driver.event.D900CodeReceivedEventArgs;
import com.sicpa.standard.camera.d900.driver.event.D900ImageReceivedEventArgs;
import com.sicpa.standard.camera.d900.parser.event.D900CodeEventArgs;
import com.sicpa.standard.camera.d900.parser.event.D900ErrorCodeEventArgs;
import com.sicpa.standard.camera.d900.parser.event.D900MetricsEventArgs;
import com.sicpa.standard.camera.d900.parser.event.D900UnknownCodeEventArgs;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurable;
import com.sicpa.standard.sasscl.controller.productionconfig.config.D900CameraConfig;
import com.sicpa.standard.sasscl.devices.d900.D900CameraAdaptorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.camera.d900.controller.D900CameraException;
import com.sicpa.standard.camera.d900.controller.ID900CameraController;
import com.sicpa.standard.common.util.ThreadUtils;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.sasscl.controller.productionconfig.ConfigurationFailedException;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurator;
import com.sicpa.standard.sasscl.devices.AbstractDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class D900CameraAdaptor extends D900AbstractCameraAdaptor implements ID900CameraControllerListener,
		IConfigurable<D900CameraConfig, D900CameraAdaptor> {

	public static final Logger logger = LoggerFactory.getLogger(AbstractDevice.class);

	private ID900CameraController<? extends ID900CameraModel> controller;
	private ProductionParameters productionParameters;

	public D900CameraAdaptor() {
	}

	public D900CameraAdaptor(final ID900CameraController<? extends ID900CameraModel> controller) {
		this.controller = controller;
		this.controller.addListener(this);
	}

	@Override
	protected void doStart() throws DeviceException {
		try {
			if (status == DeviceStatus.CONNECTED || status == DeviceStatus.STOPPED) {
				controller.startReading();
				fireDeviceStatusChanged(DeviceStatus.STARTED);
			}
		} catch (com.sicpa.standard.camera.d900.controller.D900CameraException e) {
			throw new D900CameraAdaptorException("Camera error when start reading", e);
		}
	}

	@Override
	public void doStop() throws D900CameraAdaptorException {
		try {
			// fix for mismatch between BRS and LS counters - camera codes received after camera required to stop -
			// Added sleepQuietly
			ThreadUtils.sleepQuietly(100);
			if (status == DeviceStatus.STARTED) {
				controller.stopReading();
			}
		} catch (com.sicpa.standard.camera.d900.controller.D900CameraException e) {
			throw new D900CameraAdaptorException("Camera error when stop reading", e);
		} finally {
			fireDeviceStatusChanged(DeviceStatus.STOPPED);
		}
	}

	@Override
	public void onCameraCodeReceived(ID900StandardCameraController<?> sender, D900CodeReceivedEventArgs codeReceivedEvent) {
		logger.info("D900 code received ");
	}

	@Override
	public void onCameraImageReceived(ID900StandardCameraController<?> sender, D900ImageReceivedEventArgs imageReceivedEvent) {
		fireCameraImage(imageReceivedEvent.getCode(), transformImage(imageReceivedEvent.getImage()));
	}

	@Override
	public void onMetricsReceived(ID900StandardCameraController<?> sender, D900MetricsEventArgs metricsEvent) {
	}

	@Override
	public void onErrorCameraCodeReceived(ID900StandardCameraController<?> sender, D900ErrorCodeEventArgs errorCodeEvent) {
		logger.debug("D900Camera read bad code: {}", errorCodeEvent.getCode());
		fireBadCode(errorCodeEvent.getCode());
	}

	@Override
	public void onUnknownCameraCodeReceived(ID900StandardCameraController<?> sender, D900UnknownCodeEventArgs unknownCodeEvent) {

	}

	@Override
	public void onValidCameraCodeReceived(ID900StandardCameraController<?> sender, D900CodeEventArgs codeEvent) {
		logger.debug("D900Camera read good code: {}", codeEvent.getCode());
		fireGoodCode(codeEvent.getCode());
	}

	@Override
	public void onCameraDriverEventReceived(ID900StandardCameraController<?> sender, D900CameraDriverEventArgs driverEvent) {

		D900CameraDriverEventCode eventCode = D900CameraDriverEventCode.valueOf(driverEvent.getEventCode());
		switch (eventCode) {

		case CONNECTED:
			onConnected();
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
			default:
			break;
		}
	}

	private void onConnected() {
		try {
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		} catch (Exception e) {
			logger.error("", e);
		}
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

	@Override
	public void doConnect() throws D900CameraAdaptorException {
		try {

			controller.create();
		} catch (D900CameraException e) {
			throw new D900CameraAdaptorException(e);
		}
	}

	@Override
	public void doDisconnect() throws D900CameraAdaptorException {
		try {
			controller.shutdown();
		} finally {
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	protected ID900CameraController<? extends ID900CameraModel> getController() {
		return controller;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Override
	public boolean isBlockProductionStart() {
		return true;
	}

	@Override
	public String readParameter(String paramName) throws D900CameraAdaptorException {
		try {
			return controller.readAliasValue(paramName);
		} catch (D900CameraException e) {
			throw new D900CameraAdaptorException(e);
		}
	}

	@Override
	public void writeParameter(String paramName, String value) throws D900CameraAdaptorException {
		try {
			controller.writeAliasValue(paramName, value);
		} catch (D900CameraException e) {
			throw new D900CameraAdaptorException(e);
		}
	}

	private BufferedImage transformImage(Image img) {
		BufferedImage res = ImageUtils.convertToBufferedImage(img);
		return res;
	}

	@Override
	public IConfigurator<D900CameraConfig, D900CameraAdaptor> getConfigurator() {
		return new IConfigurator<D900CameraConfig, D900CameraAdaptor>() {
			@Override
			public void execute(D900CameraConfig config, D900CameraAdaptor configurable) throws ConfigurationFailedException {
				configure(config);
			}
		};
	}

	private void configure(D900CameraConfig config) {
		//initImageTransformer(config.getProperties());
	}

	public void writeIntValue(String name, Integer value) {
		try {
			controller.writeAliasValueInt(name, value);
		} catch (D900CameraException e) {
			logger.error("", e);
		}
	}

	public void writeStringValue(String name, String value) {
		try {
			controller.writeAliasValueString(name, value);
		} catch (D900CameraException e) {
			logger.error("", e);
		}
	}

	public void writeAliasValue(String name, String value) {
		try {
			controller.writeAliasValue(name, value);
		} catch (D900CameraException e) {
			logger.error("", e);
		}
	}
}
