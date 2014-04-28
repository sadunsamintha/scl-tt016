package com.sicpa.standard.sasscl.devices.barcode.simulator;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.barcode.reader.controller.BarcodeReaderException;
import com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController;
import com.sicpa.standard.barcode.reader.controller.IBarcodeReaderControllerListener;
import com.sicpa.standard.barcode.reader.controller.internal.AbstractBarcodeReaderState;
import com.sicpa.standard.barcode.reader.driver.IBarcodeReaderDriver;
import com.sicpa.standard.barcode.reader.driver.event.BarcodeReaderDriverEventArgs;
import com.sicpa.standard.barcode.reader.driver.event.BarcodeReaderDriverEventCode;
import com.sicpa.standard.barcode.reader.driver.event.BarcodeReaderDriverEventDescriptionCode;
import com.sicpa.standard.barcode.reader.driver.event.BarcodeReceivedDriverEventArgs;
import com.sicpa.standard.barcode.reader.driver.event.EventType;
import com.sicpa.standard.barcode.reader.parser.IBarcodeParser;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;

/**
 * 
 * Implementation for barcode reader simulator
 * 
 */
public class BarcodeReaderSimulator implements IBarcodeReaderController<BarcodeReaderSimulatorConfig> {

	protected final List<IBarcodeReaderControllerListener> barcodeReaderControllerListeners = new ArrayList<IBarcodeReaderControllerListener>();

	protected BarcodeReaderSimulatorConfig simulatorConfig;

	protected SimulatorControlView simulatorGui;

	public BarcodeReaderSimulator() {
	}

	public BarcodeReaderSimulator(BarcodeReaderSimulatorConfig simulatorConfig) {
		this.simulatorConfig = simulatorConfig;
	}

	/**
	 * notify listener related to barcode connection event
	 */
	protected void notifyOnBarcodeConnection(boolean connected) {
		if (connected) {
			this.notifyListeners(EventType.INFO, BarcodeReaderDriverEventCode.CONNECTED.getCode(),
					BarcodeReaderDriverEventDescriptionCode.NONE.getCode());
		} else {
			this.notifyListeners(EventType.FAULT, BarcodeReaderDriverEventCode.DISCONNECTED.getCode(),
					BarcodeReaderDriverEventDescriptionCode.NONE.getCode());
		}
	}

	protected void notifyListeners(EventType eventType, int code, int args) {
		synchronized (this.barcodeReaderControllerListeners) {
			for (IBarcodeReaderControllerListener listener : barcodeReaderControllerListeners) {
				listener.onBarcodeReaderEventReceived(this, new BarcodeReaderDriverEventArgs(eventType, code, args));
			}
		}
	}

	/**
	 * notify listeners when where is a barcode read
	 */
	public void notifyBarcodeRead(String code) {
		synchronized (this.barcodeReaderControllerListeners) {
			for (IBarcodeReaderControllerListener listener : barcodeReaderControllerListeners) {
				listener.onBarcodeReceived(code);
			}
		}
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#create()
	 */
	@Override
	public void create() throws BarcodeReaderException {
		this.create(this.simulatorConfig);
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#create(com.sicpa.standard.barcode.reader.model.IBarcodeReaderModel)
	 */
	@Override
	public void create(BarcodeReaderSimulatorConfig model) throws BarcodeReaderException {

		showGui();

		this.notifyOnBarcodeConnection(true);
	}

	protected void showGui() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (simulatorGui != null) {
					simulatorGui.addSimulator("Barcode reader", new BarcodeReaderSimulatorView(
							BarcodeReaderSimulator.this));
				}
			}
		});
	}

	protected void hideGui() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(simulatorGui!=null){
					simulatorGui.removeSimulator("Barcode reader");
				}
			}
		});
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#start()
	 */
	@Override
	public void start() throws BarcodeReaderException {

	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#stop()
	 */
	@Override
	public void stop() throws BarcodeReaderException {

	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#shutdown()
	 */
	@Override
	public void shutdown() throws BarcodeReaderException {
		hideGui();
		this.notifyOnBarcodeConnection(false);
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#addListener(com.sicpa.standard.barcode.reader.controller.IBarcodeReaderControllerListener)
	 */
	@Override
	public void addListener(IBarcodeReaderControllerListener listener) {
		synchronized (this.barcodeReaderControllerListeners) {
			this.barcodeReaderControllerListeners.add(listener);
		}
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#removeListener(com.sicpa.standard.barcode.reader.controller.IBarcodeReaderControllerListener)
	 */
	@Override
	public void removeListener(IBarcodeReaderControllerListener listener) {
		synchronized (this.barcodeReaderControllerListeners) {
			this.barcodeReaderControllerListeners.remove(listener);
		}
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#getDriver()
	 */
	@Override
	public IBarcodeReaderDriver getDriver() {
		return null;
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#setDriver(com.sicpa.standard.barcode.reader.driver.IBarcodeReaderDriver)
	 */
	@Override
	public void setDriver(IBarcodeReaderDriver driver) {
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#getState()
	 */
	@Override
	public AbstractBarcodeReaderState getState() {
		return null;
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#getModel()
	 */
	@Override
	public BarcodeReaderSimulatorConfig getModel() {
		return this.simulatorConfig;
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#setModel(com.sicpa.standard.barcode.reader.model.IBarcodeReaderModel)
	 */
	@Override
	public void setModel(BarcodeReaderSimulatorConfig model) {
		this.simulatorConfig = model;
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#setParser(com.sicpa.standard.barcode.reader.parser.IBarcodeParser)
	 */
	@Override
	public void setParser(IBarcodeParser parser) {
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController#getParser()
	 */
	@Override
	public IBarcodeParser getParser() {
		return null;
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.driver.IBarcodeReaderDriverListener#onBarcodeReaderDriverEventReceived(com.sicpa.standard.barcode.reader.driver.IBarcodeReaderDriver,
	 *      com.sicpa.standard.barcode.reader.driver.event.BarcodeReaderDriverEventArgs)
	 */
	@Override
	public void onBarcodeReaderDriverEventReceived(IBarcodeReaderDriver sender, BarcodeReaderDriverEventArgs eventArgs) {
	}

	/**
	 * @see com.sicpa.standard.barcode.reader.driver.IBarcodeReaderDriverListener#onBarcodeReceived(com.sicpa.standard.barcode.reader.driver.IBarcodeReaderDriver,
	 *      com.sicpa.standard.barcode.reader.driver.event.BarcodeReceivedDriverEventArgs)
	 */
	@Override
	public void onBarcodeReceived(IBarcodeReaderDriver sender, BarcodeReceivedDriverEventArgs eventArgs) {
	}

	public void setSimulatorGui(SimulatorControlView simulatorGui) {
		this.simulatorGui = simulatorGui;
	}
}
