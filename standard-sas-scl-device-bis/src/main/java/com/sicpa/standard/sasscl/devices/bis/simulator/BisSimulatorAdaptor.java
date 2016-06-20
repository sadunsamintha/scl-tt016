package com.sicpa.standard.sasscl.devices.bis.simulator;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.AbstractStartableDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.bis.IBisAdaptor;
import com.sicpa.standard.sasscl.devices.bis.ISkuBisProvider;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.skureader.ISkuFinder;
import com.sicpa.standard.sasscl.skureader.SkuNotRecognizedEvent;
import com.sicpa.standard.sasscl.skureader.SkuRecognizedEvent;

public class BisSimulatorAdaptor extends AbstractStartableDevice implements IBisAdaptor {

	private static final Logger logger = LoggerFactory.getLogger(BisSimulatorAdaptor.class);

	private boolean blockProduction;
	private SimulatorControlView simulatorGui;
	private ISkuFinder skuFinder;
	private ISkuBisProvider skuBisProvider;

	public BisSimulatorAdaptor() {
		setName("BIS");
	}

	@Override
	public boolean isBlockProductionStart() {
		return blockProduction;
	}

	@Override
	public void sendCredential(String user, String password) {
		logger.info("creadential sent to bis:" + user + "/" + password);
	}

	public void skuRecognized(int skuId) {
		Optional<SKU> sku = skuFinder.getSkuFromId(skuId);
		logger.info("sku from bis id:" + skuId + " - in app:" + sku);
		if (sku.isPresent()) {
			EventBusService.post(new SkuRecognizedEvent(sku.get()));
		} else {
			EventBusService.post(new SkuNotRecognizedEvent());
		}
	}

	@Override
	protected void doStart() throws DeviceException {
		fireDeviceStatusChanged(DeviceStatus.STARTED);
	}

	@Override
	protected void doStop() throws DeviceException {
		fireDeviceStatusChanged(DeviceStatus.STOPPED);
	}

	@Override
	protected void doConnect() throws DeviceException {
		showGui();
		onConnection();
	}

	public void onConnection() {
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		sendSkusToBis();
	}

	private void sendSkusToBis() {
		skuBisProvider.getSkusToSendToBIS().forEach(sku -> logger.info("sending sku to bis:" + sku));
	}

	@Override
	protected void doDisconnect() throws DeviceException {
		hideGui();
		onDisconnection();
	}

	public void onDisconnection() {
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	private void showGui() {
		if (simulatorGui != null) {
			ThreadUtils.invokeLater(() -> simulatorGui.addSimulator(getName(), new BisSimulatorView(this)));
		}
	}

	private void hideGui() {
		if (simulatorGui != null) {
			ThreadUtils.invokeLater(() -> simulatorGui.removeSimulator(getName()));
		}
	}

	public void setBlockProduction(boolean blockProduction) {
		this.blockProduction = blockProduction;
	}

	public void setSimulatorGui(SimulatorControlView simulatorGui) {
		this.simulatorGui = simulatorGui;
	}

	public void setSkuBisProvider(ISkuBisProvider skuBisProvider) {
		this.skuBisProvider = skuBisProvider;
	}

	public void setSkuFinder(ISkuFinder skuFinder) {
		this.skuFinder = skuFinder;
	}
}
