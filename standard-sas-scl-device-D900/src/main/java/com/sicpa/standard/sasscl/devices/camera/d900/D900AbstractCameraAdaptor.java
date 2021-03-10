package com.sicpa.standard.sasscl.devices.camera.d900;

import java.awt.Image;
import java.util.List;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.d900.*;
import com.sicpa.standard.sasscl.model.Code;

public abstract class D900AbstractCameraAdaptor extends AbstractStartableDevice implements ID900CameraAdaptor {

	public D900AbstractCameraAdaptor() {
		setName("Camera");
	}

	protected void fireGoodCode(final String code) {
		D900CameraNewCodeEvent evt = new D900CameraNewCodeEvent(code);
		EventBusService.post(evt);
	}

	protected void fireBadCode(final String code) {
		D900CameraNewCodeEvent evt = new D900CameraNewCodeEvent(code);
		EventBusService.post(evt);
	}

	protected void fireCameraImage(final String code, final Image cameraImage) {
		
	}

	@Override
	protected abstract void doConnect() throws D900CameraAdaptorException;

	@Override
	protected abstract void doDisconnect() throws D900CameraAdaptorException;

	@Override
	public void start() throws D900CameraAdaptorException {
		try {
			super.start();
		} catch (DeviceException e) {
			throw new D900CameraAdaptorException(e);
		}
	}

	@Override
	public void stop() throws D900CameraAdaptorException {
		try {
			super.stop();
		} catch (DeviceException e) {
			throw new D900CameraAdaptorException(e);
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
