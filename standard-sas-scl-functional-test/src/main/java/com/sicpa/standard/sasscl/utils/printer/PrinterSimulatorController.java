package com.sicpa.standard.sasscl.utils.printer;

import com.sicpa.standard.client.common.exception.InitializationRuntimeException;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.printer.controller.IPrinterControllerListener;
import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.printer.controller.model.AbstractPrinterModel;
import com.sicpa.standard.printer.controller.model.PrinterBufferStatus;
import com.sicpa.standard.printer.controller.model.SequenceStatus;
import com.sicpa.standard.printer.controller.model.command.PrinterMessage;
import com.sicpa.standard.printer.driver.IPrinterDriver;
import com.sicpa.standard.printer.driver.event.PrinterBufferStatusChangedEventArgs;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.printer.simulator.IPrinterControllerSimulator;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterSimulatorConfig;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PrinterSimulatorController implements IPrinterController, IPrinterControllerSimulator {

	protected static AtomicInteger idGetter = new AtomicInteger(0);
	protected int id = 0;

	/**
	 * Printer simulator configuration
	 */
	protected PrinterSimulatorConfig config;

	/**
	 * Is printer is connected
	 */
	protected boolean running;

	protected boolean connected;

	protected List<IPrinterControllerListener> listeners;

	protected ProductionParameters productionParameters;

	protected SimulatorControlView simulatorGui;

	public PrinterSimulatorController() {
		this(new PrinterSimulatorConfig());
	}

	/**
	 * Constructor
	 * 
	 * <p>
	 * Constructs a new <code>PrinterSimulatorController</code> instance by using the specified config file.
	 * </p>
	 * 
	 * @param configFile
	 *            the config file location in the filesystem
	 */
	public PrinterSimulatorController(final String configFile) {
		try {
			id = idGetter.getAndIncrement();
			this.listeners = new ArrayList<IPrinterControllerListener>();
			this.config = ConfigUtils.load(PrinterSimulatorConfig.class, configFile);
		} catch (Exception e) {
			throw new InitializationRuntimeException("init failed", e);
		}
	}

	/**
	 * Constructor
	 * 
	 * <p>
	 * Constructs a new <code>PrinterSimulatorController</code> instance by using the specified printer config file.
	 * </p>
	 * 
	 * @param config
	 *            instance of PrinterSimulatorConfig
	 */
	public PrinterSimulatorController(final PrinterSimulatorConfig config) {
		id = idGetter.getAndIncrement();
		this.config = config;
		listeners = new ArrayList<IPrinterControllerListener>();
	}

	public void askCodes() {
		if (productionParameters != null) {
			if (productionParameters.getProductionMode().equals(ProductionMode.EXPORT)) {
				// do not ask code in export
				return;
			}
		}
		fireOnPrinterBufferChanged();
	}

	protected int codeByRequest = 20;

	public void setCodeByRequest(int codeByRequest) {
		this.codeByRequest = codeByRequest;
	}

	public void fireOnPrinterBufferChanged() {
		PrinterBufferStatusChangedEventArgs args = new PrinterBufferStatusChangedEventArgs();
		args.setNeedCodes(true);
		args.setCodeNeeded(codeByRequest);
		args.setBufferStatus(PrinterBufferStatus.EMPTY);
		for (IPrinterControllerListener lis : this.listeners) {
			lis.onPrinterCodesNeeded(this, args.getCodeNeeded());
		}
	}

	public void firePrinterMessage(final PrinterMessage mess) {
		for (IPrinterControllerListener lis : this.listeners) {
			lis.onMessageReceived(mess);
		}
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Override
	public void setListener(final IPrinterControllerListener listener) {
		this.listeners.add(listener);

	}

	public void fireStatusChanged(final DeviceStatus status) {
		for (IPrinterControllerListener lis : this.listeners) {
			lis.onConnectionStatusChanged(this, status.isConnected());
			if (status.isConnected()) {
				lis.onSequenceStatusChanged(this, SequenceStatus.READY);
			} else {
				lis.onSequenceStatusChanged(this, SequenceStatus.SEQ_OFF);
			}
		}
	}

//	@Override
	public void create() {
		this.connected = true;
		fireStatusChanged(DeviceStatus.CONNECTED);
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				fireOnPrinterBufferChanged();
			}
		});
	}

//	@Override
	public void create(final PrinterSimulatorConfig config) {

	}

	public PrinterSimulatorConfig getModel() {
		return this.config;
	}

//	@Override
	public boolean isConnected() {
		return this.connected;
	}

	public boolean isStarted() {
		return this.running;
	}

	@Override
	public void reset() {

	}

	@Override
	public void sendCodes(final List<String> codes) {

	}

	@Override
	public void sequenceOff() {

	}

//	@Override
	public void setDriver(final IPrinterDriver driver) {

	}

	@Override
	public void shutdown() {
		this.connected = false;
		fireStatusChanged(DeviceStatus.DISCONNECTED);
	}

//	@Override
	public void update(final PrinterSimulatorConfig config) {

	}

	@Override
	public void start() throws com.sicpa.standard.printer.controller.PrinterException {
		if (!isConnected()) {
			throw new com.sicpa.standard.printer.controller.PrinterException(
					"Printer cannot start, printer not connected");
		}

		this.running = true;

		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				fireOnPrinterBufferChanged();
			}
		});

	}

	@Override
	public void stop() {
		this.running = false;
	}

	public void setSimulatorGui(final SimulatorControlView simulatorGui) {
		this.simulatorGui = simulatorGui;
	}

	public void setPrinterModel(PrinterSimulatorConfig printerModel) {
		this.config = printerModel;
	}

	@Override
	public String toString() {
		return "printerSimulator:" + id;
	}

	@Override
	public void sequenceOn() throws com.sicpa.standard.printer.controller.PrinterException {

	}

	@Override
	public String requestCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() throws PrinterException {
		// TODO Auto-generated method stub
		
	}

	public void setModel(AbstractPrinterModel printerModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionStatusChanged(boolean isConnected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendSpecificSettings(int settingID, Object... args) throws PrinterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendExtendedCodes(List<ExtendedCode> extendedCodeList)
			throws PrinterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeviceId() {
		// TODO Auto-generated method stub
		return null;
	}

}
