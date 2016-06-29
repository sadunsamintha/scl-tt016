package com.sicpa.standard.sasscl.devices.camera;

import java.awt.Image;
import java.util.List;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.model.Code;

public abstract class AbstractCameraAdaptor extends AbstractStartableDevice implements ICameraAdaptor {

	public AbstractCameraAdaptor() {
		setName("Camera");
	}

	protected void fireGoodCode(final String code) {
		CameraGoodCodeEvent evt = new CameraGoodCodeEvent(new Code(code, getName()));
		EventBusService.post(evt);
	}

	protected void fireBadCode(final String code) {
		CameraBadCodeEvent evt = new CameraBadCodeEvent(new Code(code, getName()));
		EventBusService.post(evt);
	}

	protected void fireCameraImage(final String code, final Image cameraImage) {
		EventBusService.post(new CameraImageEvent(cameraImage, this));
	}

	@Override
	protected abstract void doConnect() throws CameraAdaptorException;

	@Override
	protected abstract void doDisconnect() throws CameraAdaptorException;

	@Override
	public void start() throws CameraAdaptorException {
		try {
			super.start();
		} catch (DeviceException e) {
			throw new CameraAdaptorException(e);
		}
	}

	@Override
	public void stop() throws CameraAdaptorException {
		try {
			super.stop();
		} catch (DeviceException e) {
			throw new CameraAdaptorException(e);
		}
	}

	/**
	 * 
	 * To retrieve the IDs from the product descriptors that are used to get the camera job file
	 * 
	 * @return a list of IDs that are used to get the camera job file
	 */
	protected abstract List<Integer> getCameraJobProductDescriptorIDs();
}
