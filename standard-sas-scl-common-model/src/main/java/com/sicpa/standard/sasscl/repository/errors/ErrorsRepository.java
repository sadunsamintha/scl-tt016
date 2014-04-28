package com.sicpa.standard.sasscl.repository.errors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.IMessageCodeMapper;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.hardware.IDeviceErrorRepository;
import com.sicpa.standard.sasscl.controller.hardware.ProductionDevicesCreatedEvent;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.messages.ActionEventDeviceError;
import com.sicpa.standard.sasscl.messages.ActionEventError;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class ErrorsRepository implements IErrorsRepository {

	protected IMessageCodeMapper messageCodeMapper;

	protected IRemoteServer server;

	protected PlcProvider plcProvider;

	protected final List<AppMessage> applicationErrors = new ArrayList<AppMessage>();
	protected final List<AppMessage> applicationWarnings = new ArrayList<AppMessage>();

	protected IDeviceErrorRepository deviceErrorRepository;

	// include camera / printer / bis , DO NOT INLCUDE THE PLC
	protected final Collection<IStartableDevice> currentStartableDevices = new ArrayList<IStartableDevice>();

	public ErrorsRepository() {
	}

	@Subscribe
	public void handleProductionParametersChanged(ProductionParametersEvent evt) {
		applicationErrors.clear();
		applicationWarnings.clear();
	}

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {
		resetOnProcessStateChanged(evt.getPreviousState(), evt.getCurrentState());
	}

	@Subscribe
	public void handleErrorMessage(ActionEventError error) {
		String messageCode = messageCodeMapper.getMessageCode(error.getKey());
		AppMessage msg = new AppMessage(messageCode, error.getKey());
		applicationErrors.add(msg);
	}

	@Subscribe
	public void handleErrorMessage(ActionEventDeviceError error) {
		String messageCode = messageCodeMapper.getMessageCode(error.getKey());
		AppMessage msg = new AppMessage(messageCode, error.getKey());
		applicationErrors.add(msg);
	}

	@Subscribe
	public void handleWarningMessage(ActionEventWarning w) {

		String messageCode = messageCodeMapper.getMessageCode(w.getKey());
		AppMessage msg = new AppMessage(messageCode, w.getKey());
		applicationWarnings.add(msg);

	}

	public DeviceStatus getDeviceStatus(String deviceName) {
		for (IStartableDevice dev : currentStartableDevices) {
			if (dev.getName().equals(deviceName)) {
				return dev.getStatus();
			}
		}
		return null;
	}

	/***
	 * Return the errors associated with device
	 */
	public Collection<String> getErrors(String deviceName) {
		return deviceErrorRepository.getErrors(deviceName);
	}

	/****
	 * Performs reset of device errors/warnings upon receiving a process state change event
	 */
	protected void resetOnProcessStateChanged(ApplicationFlowState oldProcessState, ApplicationFlowState newProcessState) {
		if (newProcessState.equals(ApplicationFlowState.STT_STARTED)) {
			applicationErrors.clear();
			applicationWarnings.clear();
		}
	}

	@Override
	public Map<String, DeviceStatus> getDevicesStatus() {
		Map<String, DeviceStatus> map = new HashMap<String, DeviceStatus>();
		for (IStartableDevice dev : currentStartableDevices) {
			map.put(dev.getName(), dev.getStatus());
		}
		if (plcProvider.get() != null) {
			map.put(plcProvider.get().getName(), plcProvider.get().getStatus());
		}
		return map;
	}

	@Subscribe
	public void startableDevicesCreated(ProductionDevicesCreatedEvent evt) {
		currentStartableDevices.clear();
		currentStartableDevices.addAll(evt.getDevices());
	}

	public IMessageCodeMapper getMessageCodeMapper() {
		return messageCodeMapper;
	}

	public void setMessageCodeMapper(IMessageCodeMapper messageCodeMapper) {
		this.messageCodeMapper = messageCodeMapper;
	}

	@Override
	public DeviceStatus getPlcStatus() {
		return plcProvider.get() == null ? null : plcProvider.get().getStatus();
	}

	@Override
	public List<AppMessage> getApplicationErrors() {
		return applicationErrors;
	}

	@Override
	public List<AppMessage> getApplicationWarnings() {
		return applicationWarnings;
	}

	@Override
	public DeviceStatus getRemoteServerStatus() {
		return server.getStatus();
	}

	public void setServer(IRemoteServer server) {
		this.server = server;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}
}
