package com.sicpa.standard.sasscl.view.advancedControl.component.printer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.printer.IPrinterAdaptor;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;

public class PrinterControlViewController implements IPrinterViewControlListener {

	private static final Logger logger = LoggerFactory.getLogger(PrinterControlViewController.class);

	private final PrinterControlViewModel model = new PrinterControlViewModel();
	private IHardwareController hardwareController;

	private IPrinterAdaptor findPrinter(String printerId) {
		Optional<IPrinterAdaptor> printer = hardwareController.getDevices().stream()
				.filter(d -> d.getName().equals(printerId)).map(d -> (IPrinterAdaptor) d).findFirst();
		if (printer.get() == null) {
			throw new IllegalArgumentException("no printer found for:" + printerId);
		}
		return printer.get();
	}

	@Override
	public void switchOn(String printerId) {
		IPrinterAdaptor printer = findPrinter(printerId);
		try {
			OperatorLogger.log("switching on printer {}", printerId);
			printer.switchOn();
		} catch (PrinterAdaptorException e) {
			logger.error("", e);
		}
	}

	@Override
	public void switchOff(String printerId) {
		IPrinterAdaptor printer = findPrinter(printerId);
		try {
			OperatorLogger.log("switching off printer {}", printerId);
			printer.switchOff();
		} catch (PrinterAdaptorException e) {
			logger.error("", e);
		}
	}

	public PrinterControlViewModel getModel() {
		return model;
	}

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

	@Subscribe
	public void handleApplicationStateChange(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState() == ApplicationFlowState.STT_DISCONNECTING_ON_PARAM_CHANGED) {
			model.reset();
			model.notifyModelChanged();
		}
		if (evt.getCurrentState() == ApplicationFlowState.STT_CONNECTING) {
			addAllPrinterToModel();
		}
	}

	private void addAllPrinterToModel() {
		for (IDevice device : hardwareController.getDevices()) {
			if (device instanceof IPrinterAdaptor) {
				model.addPrinter(device.getName());
			}
		}
		model.notifyModelChanged();
	}
}
