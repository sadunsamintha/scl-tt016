package com.sicpa.standard.sasscl.controller.hardware;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.sasscl.controller.hardware.state.IHardwareControllerStateSetter;
import com.sicpa.standard.sasscl.controller.view.event.ErrorViewEvent;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.printer.IPrinterAdaptor;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.messages.ActionEventDeviceError;
import com.sicpa.standard.sasscl.messages.IssueSolvedMessage;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class HardwareController implements IHardwareController, IHardwareControllerStateSetter, IDeviceStatusListener {
	
	private static Logger logger = LoggerFactory.getLogger(HardwareController.class);

	protected IHardwareControllerState currentState;

	protected final List<IStartableDevice> allStartableDevices = new ArrayList<IStartableDevice>();
	protected PlcProvider plcProvider;
	protected final Object lock = new Object();
	protected IDeviceErrorRepository devicesErrorsRepository;

	public HardwareController() {

	}

	@Override
	public void connect() {
		synchronized (lock) {
			currentState.connect();
		}
	}

	@Override
	public void disconnect() {
		synchronized (lock) {
			currentState.disconnect();
		}
	}

	@Override
	public void start() {
		synchronized (lock) {
			currentState.start();
		}
	}

	@Override
	public void stop() {
		synchronized (lock) {
			currentState.stop();
		}
	}
	
	@Override
	public void switchOff() {
		synchronized (lock) {
			for (IStartableDevice dev : allStartableDevices) {
				if (dev instanceof IPrinterAdaptor) {
					try {
						((IPrinterAdaptor) dev).switchOff();
					} catch (PrinterAdaptorException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void setDevices(Collection<IStartableDevice> devices) {
		for (IStartableDevice dev : allStartableDevices) {
			dev.removeDeviceStatusListener(this);
		}
		allStartableDevices.clear();

		allStartableDevices.addAll(devices);
		for (IStartableDevice dev : devices) {
			dev.addDeviceStatusListener(this);
		}
	}

	@Override
	public Collection<IStartableDevice> getDevices() {
		List<IStartableDevice> devs = new ArrayList<IStartableDevice>();
		devs.addAll(allStartableDevices);
		return devs;
	}

	@Override
	public void setCurrentState(IHardwareControllerState state) {
		synchronized (lock) {
			if (currentState != null) {
				currentState.leave();
			}
			currentState = state;
			currentState.setDeviceErrorRepository(devicesErrorsRepository);
			currentState.setStartableDevices(allStartableDevices);
			currentState.setPlc(plcProvider.get());
			currentState.setSetter(this);
			currentState.enter();
		}
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		synchronized (lock) {
			currentState.deviceStatusChanged(evt);
		}
	}

	public PlcProvider getPlcProvider() {
		return plcProvider;
	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Object previousPlc = evt.getOldValue();
				if (previousPlc != null) {
					((IPlcAdaptor) previousPlc).removeDeviceStatusListener(HardwareController.this);
				}
				plcProvider.get().addDeviceStatusListener(HardwareController.this);
			}
		});
	}

	@Subscribe
	public void handleIssueSolved(IssueSolvedMessage event) {
		
		String sourceKey;
		if (event.getSource() instanceof IDevice) {
			sourceKey = ((IDevice) event.getSource()).getName();
		} else {
			sourceKey = event.getSource() + "";
		}
		devicesErrorsRepository.removeError(sourceKey, event.getKey());
		currentState.errorMessageRemoved();
	}

	@Subscribe
	public void handleDeviceError(ActionEventDeviceError event) {
		String sourceKey;
		if (event.getSource() instanceof IDevice) {
			sourceKey = ((IDevice) event.getSource()).getName();
		} else {
			sourceKey = event.getSource() + "";
		}

		if (devicesErrorsRepository.addError(sourceKey, event.getKey(),
				Messages.format(event.getKey(), event.getParams()))) {
			// if the error is a new one
			String key = event.getKey();
			Object[] params = event.getParams();

			MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.ERROR,
					SystemEventType.APPLICATION_MESSAGE, MessageFormat.format(key, params)));
			EventBusService.post(new ErrorViewEvent(key, null, true, params));

			currentState.errorMessageAdded();
		}
	}

	public void setDevicesErrorsRepository(IDeviceErrorRepository devicesErrorsRepository) {
		this.devicesErrorsRepository = devicesErrorsRepository;
	}
}
