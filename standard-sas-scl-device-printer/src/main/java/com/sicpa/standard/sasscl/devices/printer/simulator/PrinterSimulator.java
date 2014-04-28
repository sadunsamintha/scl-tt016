package com.sicpa.standard.sasscl.devices.printer.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.printer.controller.internal.DominoPrinterController;
import com.sicpa.standard.printer.controller.model.DominoPrinterModel;
import com.sicpa.standard.printer.controller.model.SequenceStatus;
import com.sicpa.standard.printer.driver.DominoParameter;
import com.sicpa.standard.printer.driver.internal.comm.simulator.WorkerIdGenerator;
import com.sicpa.standard.printer.driver.internal.comm.simulator.scd2.Scd2SimulatorServer;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PrinterSimulator extends DominoPrinterController implements IPrinterControllerSimulator {

	protected Scd2SimulatorServer server;
	protected DominoPrinterModel model;

	protected PrinterSimulatorView view;

	protected SimulatorControlView simulatorGui;

	protected String deviceId;

	public static int idCount = 0;

	public PrinterSimulator() {
		super();
	}

	public PrinterSimulator(PrinterSimulatorConfig config) {
		super();
		DominoParameter param = new DominoParameter();
		// param.setCodeLenght(22);
		// param.setScd2BufferSizeInByte(15000);
		model = new DominoPrinterModel(param);
		model.setName("SCD2");
		model.setDeviceId("1234567890123456789012345678901234567890");
		model.setTcpPort(config.getPort());
		deviceId = "printer" + idCount++;
	}

	public PrinterSimulator(PrinterSimulatorConfig config, SimulatorControlView simulatorGui) {
		this(config);
		this.simulatorGui = simulatorGui;
	}

	public void create() throws PrinterException {
		EventBusService.post(new MessageEvent(MessageEventKey.Simulator.PRINTER));
		startServer();
		showGui();
		super.setModel(model);
//		super.init();
		sequenceStatus = SequenceStatus.READY;
		listener.onSequenceStatusChanged(this, sequenceStatus);
	}
	
	@Override
	public void init() throws PrinterException {
		create();
	}
	
	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public String requestCode() {
		if (server != null && server.getPrinterSimulator() != null) {
			return server.getPrinterSimulator().printCode();
		} else {
			return null;
		}
	}

	@Override
	public void shutdown() {
		hideGui();
//		super.shutdown();
		stopServer();
	}
	
	@Override
	public void start() throws PrinterException {
	}
	
	@Override
	public void stop() throws PrinterException {
	}

	public void stopServer() {
		if (server != null) {
			server.stop();
			server = null;
			try {
				// Allow server ample time to release all resources
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void startServer() {
		if (server == null) {
			server = new Scd2SimulatorServer(new WorkerIdGenerator(),
					new HashMap<Byte, com.sicpa.standard.printer.driver.internal.comm.simulator.PrinterSimulator>(),
					model.getTcpPort());
		}
	}

	public List<String> getCodesReadyToPrint() {
		if (server != null && server.getPrinterSimulator() != null) {
			return server.getPrinterSimulator().getCodesReceived();
		} else {
			return new ArrayList<String>();
		}
	}

	protected void showGui() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (simulatorGui != null) {
					view = new PrinterSimulatorView(PrinterSimulator.this);
					simulatorGui.addSimulator(deviceId, view);
					view.startTimerUpdateCodeList();
				}
			}
		});
	}

	protected void hideGui() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (simulatorGui != null) {
					simulatorGui.removeSimulator(deviceId);
					if (view != null) {
						view.stopTimerUpdateCodeList();
						view = null;
					}
				}
			}
		});
	}

	public void setSimulatorGui(SimulatorControlView simulatorGui) {
		this.simulatorGui = simulatorGui;
	}

	public void setSequenceStatus(SequenceStatus sequenceStatus) {
		server.getPrinterSimulator().setSequenceStatus(sequenceStatus);
	}

	@Override
	public void sendCodes(List<String> codes) throws PrinterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() throws PrinterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sequenceOff() throws PrinterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sequenceOn() throws PrinterException {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public boolean isStarted() {
//		return true;
//	}
}
