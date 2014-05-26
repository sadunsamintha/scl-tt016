package com.sicpa.standard.sasscl.controller.hardware.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IDeviceErrorRepository;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public abstract class AbstractHardwareControllerState implements IHardwareControllerState {

	private final static Logger logger = LoggerFactory.getLogger(AbstractHardwareControllerState.class);
	protected final List<IStartableDevice> startableDevices = new ArrayList<IStartableDevice>();
	protected IPlcAdaptor plc;
	protected IHardwareControllerStateSetter setter;
	protected IDeviceErrorRepository deviceErrorRepository;
	protected final AtomicBoolean active = new AtomicBoolean(false);

	public void connectDevices() {
		for (IStartableDevice startableDevice : startableDevices) {
			connectDevice(startableDevice);
		}
		connectDevice(plc);
	}

	protected void disconnectDevices() {
		for (IStartableDevice startableDevice : startableDevices) {
			disconnectDevice(startableDevice);
		}
		if (plc != null) {
			disconnectDevice(plc);
		}
	}

	protected void startDevices() {
		for (IStartableDevice startableDevice : startableDevices) {
			if (startableDevice.isConnected()) {
				startDevice(startableDevice);
			}
		}
		if (plc.isConnected()) {
			startDevice(plc);
		}
	}
	


	protected void runPlc() {
		if (plc.isConnected()) {
			logger.debug("Running plc");
			TaskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						plc.doRun();
					} catch (DeviceException e) {
						logger.error("", e);
						EventBusService.post(new MessageEvent(MessageEventKey.DevicesController.FAILED_TO_START_DEVICE));
					}
				}
			});
		}
	}

	protected void stopDevices() {
		// stop the plc first and directly in this thread
		if (plc.isConnected()) {
			try {
				plc.stop();
			} catch (PlcAdaptorException e) {
				logger.error("", e);
			}
		}
		// others devices are stop later in executorService
		for (IStartableDevice startableDevice : startableDevices) {
			if (startableDevice.isConnected()) {
				stoppingDevice(startableDevice);
			}
		}
	}

	@Override
	public void connect() {
		throw new NotImplementedException("currentState=" + getName());
	}

	@Override
	public void start() {
		throw new NotImplementedException("currentState=" + getName());
	}

	@Override
	public void stop() {
		throw new NotImplementedException("currentState=" + getName());
	}

	@Override
	public void disconnect() {
		throw new NotImplementedException("currentState=" + getName());
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void enter() {
		active.set(true);
		logger.debug("entering hw state {}", getName());
	}

	@Override
	public void leave() {
		startableDevices.clear();
		plc = null;
		active.set(false);
		logger.debug("leaving hw state state {}", getName());
	}

	@Override
	public void setDeviceErrorRepository(IDeviceErrorRepository devicesErrors) {
		this.deviceErrorRepository = devicesErrors;
	}

	@Override
	public void setSetter(IHardwareControllerStateSetter setter) {
		this.setter = setter;
	}

	@Override
	public void setPlc(IPlcAdaptor plcAdaptor) {
		this.plc = plcAdaptor;
	}

	@Override
	public void setStartableDevices(List<IStartableDevice> startableDevices) {
		this.startableDevices.clear();
		this.startableDevices.addAll(startableDevices);
	}

	protected void setNextState(IHardwareControllerState next) {
		setter.setCurrentState(next);
	}

	protected void connectDevice(final IStartableDevice device) {
		logger.debug("Connect device: {}", device.getName());
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					device.connect();
				} catch (DeviceException e) {
					logger.error("", e);
					EventBusService.post(new MessageEvent(MessageEventKey.DevicesController.FAILED_TO_CONNECT_DEVICE,
							device.getName()));
				}
			}
		});
	}

	/**
	 * start device with another thread
	 * 
	 * @param startableDevice
	 */
	protected void startDevice(final IStartableDevice device) {
		logger.debug("Starting device: {}", device.getName());
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					device.start();
				} catch (DeviceException e) {
					logger.error("", e);
					EventBusService.post(new MessageEvent(MessageEventKey.DevicesController.FAILED_TO_START_DEVICE));
				}
			}
		});
	}

	protected void stoppingDevice(final IStartableDevice device) {
		logger.debug("Stopping device: {}", device.getName());
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					device.stop();
				} catch (DeviceException e) {
					logger.error("", e);
				}
			}
		});
	}

	/**
	 * disconnect device with another thread
	 * 
	 * @param device
	 */
	protected void disconnectDevice(final IStartableDevice device) {
		logger.debug("disconnect device: {}", device.getName());
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				device.disconnect();
			}
		});
	}

	protected Collection<String> getErrorMessages() {
		Collection<String> res = new ArrayList<String>();
		for (String error : deviceErrorRepository.getErrors()) {
			res.add(error);
		}

		for (IStartableDevice dev : startableDevices) {
			if (dev.isBlockProductionStart()) {
				if (!dev.isConnected()) {
					res.add(dev.getName() + " not connected");
				}
			}
		}

		if (!plc.isConnected()) {
			res.add(plc.getName() + " not connected");
		}
		return res;
	}

	/**
	 * 
	 * button if every devices have the same status
	 * 
	 * @param deviceStatus
	 * @return
	 */
	protected boolean checkAllDeviceStatus(boolean checkOnlyBlockable, DeviceStatus... deviceStatus) {
		List<DeviceStatus> devicesStatusList = Arrays.asList(deviceStatus);
		for (IStartableDevice dev : startableDevices) {
			if (!checkOnlyBlockable || checkOnlyBlockable && dev.isBlockProductionStart()) {
				if (!devicesStatusList.contains(dev.getStatus())) {
					return false;
				}
			}
		}
		if (!devicesStatusList.contains(plc.getStatus())) {
			return false;
		}
		return true;
	}

	protected boolean isRecovering() {
		for (IStartableDevice startableDevice : startableDevices) {
			if (!startableDevice.getStatus().equals(DeviceStatus.DISCONNECTED)) {
				return true;
			}
		}
		if (!plc.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			return true;
		}
		return false;
	}

	protected boolean areAllDevicesStopped() {
		for (IStartableDevice dev : startableDevices) {
			if (!isDeviceStoppedOrDisconnected(dev)) {
				return false;
			}
		}
		if (!isDeviceStoppedOrDisconnected(plc)) {
			return false;
		}
		return true;
	}

	protected boolean isDeviceStoppedOrDisconnected(IDevice dev) {
		return dev.getStatus().equals(DeviceStatus.STOPPED) || dev.getStatus().equals(DeviceStatus.DISCONNECTED);
	}

	protected void fireStatusChanged(final HardwareControllerStatusEvent event) {
		EventBusService.post(event);
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
	}

	protected Collection<String> getDevicesErrors() {
		return new HashSet<String>(deviceErrorRepository.getErrors());
	}

	protected boolean areDevicesReady() {
		return allDevicesConnected() && deviceErrorRepository.isEmpty();
	}

	protected boolean allDevicesConnected() {
		for (IStartableDevice device : this.startableDevices) {
			if (device.isBlockProductionStart()) {
				if (!device.getStatus().isConnected()) {
					return false;
				}
			}
		}
		if (!plc.getStatus().isConnected()) {
			return false;
		}

		return true;
	}

	@Override
	public void errorMessageAdded() {

	}

	@Override
	public void errorMessageRemoved() {

	}
}
