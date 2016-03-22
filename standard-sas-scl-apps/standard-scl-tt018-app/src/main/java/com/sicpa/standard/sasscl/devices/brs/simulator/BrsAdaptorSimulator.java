package com.sicpa.standard.sasscl.devices.brs.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.brs.BrsAdaptor;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class BrsAdaptorSimulator extends BrsAdaptor {

	private static final Logger logger = LoggerFactory.getLogger(BrsAdaptorSimulator.class);

	private BrsSimulatorConfig config;

	private ProductionParameters productionParameters;

	private boolean simulationMode = false;

	public BrsAdaptorSimulator(BrsSimulatorConfig config, ProductionParameters productionParameters, String behavior) throws DeviceException {
		this();
		this.config = config;
		this.productionParameters = productionParameters;
		this.simulationMode = behavior.equalsIgnoreCase("simulator");
	}

	public BrsAdaptorSimulator() throws DeviceException {
		super();
	}

	@Override
	protected void doConnect() throws DeviceException {
		// EventBusService.post(new
		// MessageEvent(MessageEventKey.Simulator.BRS));
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
	}

	@Override
	public void doStart() throws DeviceException {
		fireDeviceStatusChanged(DeviceStatus.STARTED);
	}

	@Override
	public void doStop() throws DeviceException {
		fireDeviceStatusChanged(DeviceStatus.STOPPED);
	}

	@Subscribe
	public void receiveCameraCode(final CameraGoodCodeEvent evt) {
		// if(simulationMode) {
		// simulateReceiveBarcode();
		// }
	}

	@Subscribe
	public void receiveCameraCodeError(final CameraBadCodeEvent evt) {
		// if(simulationMode) {
		// simulateReceiveBarcode();
		// }
	}

}