package com.sicpa.standard.sasscl.devices.printer.simulator;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.camera.simulator.ICodeProvider;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.devices.printer.impl.PrinterAdaptor;

public class PrinterAdaptorSimulator extends PrinterAdaptor implements ICodeProvider {

	private static final Logger logger = LoggerFactory.getLogger(PrinterAdaptorSimulator.class);

	protected final List<String> codeBuffer = new LinkedList<>();
	protected final List<Pair<String, String>> codePairBuffer = new LinkedList<>();
	protected ScheduledFuture<?> scheduledAskTaskFutur;
	protected long askCodeDelayMs = 1000;

	public PrinterAdaptorSimulator() {
		super();
	}

	public PrinterAdaptorSimulator(IPrinterController controller) {
		super(controller);
	}

	@Override
	protected void doConnect() throws PrinterAdaptorException {
		initialized = true;
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
	}

	@Override
	protected void doDisconnect() {
		initialized = false;
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	@Override
	public void sendCodesToPrint(final List<String> codes) throws PrinterAdaptorException {
		logger.debug("Printer sending codes to print");
		codeBuffer.addAll(codes);
	}
	
	@Override
	public void sendPairCodesToPrint(final List<Pair<String, String>> codes) throws PrinterAdaptorException {
		logger.debug("Printer sending codes in pair to print");
		codePairBuffer.addAll(codes);
	}

	@Override
	public void onPrinterCodesNeeded(final Object sender, long nbCodes) {
		notifyRequestCodesToPrint(nbCodes);
	}

	@Override
	public void doStart() throws PrinterAdaptorException {
		resetCodes();
		if (askCodeDelayMs > 0) {
			scheduledAskTaskFutur = TaskExecutor.scheduleWithFixedDelay(new AskCodesTask(), 1000,
					TimeUnit.MILLISECONDS, "AskCodesTask");
		}
		fireDeviceStatusChanged(DeviceStatus.STARTED);
	}

	@Override
	public void doStop() throws PrinterAdaptorException {
		resetCodes();
		fireDeviceStatusChanged(DeviceStatus.STOPPED);
		if (scheduledAskTaskFutur != null) {
			scheduledAskTaskFutur.cancel(false);
		}
	}

	public void resetCodes() throws PrinterAdaptorException {
		codeBuffer.clear();
	}

	@Override
	public void switchOff() throws PrinterAdaptorException {
		codeBuffer.clear();
	}

	@Override
	public void switchOn() throws PrinterAdaptorException {

	}

	@Override
	public String requestCode() {
		synchronized (codeBuffer) {

			if (codeBuffer.size() == 0) {
				logger.warn("No codes received");
				return null;
			}

			String code = codeBuffer.remove(0);
			logger.debug("Printed Code: {}", code);
			return code;
		}
	}
	
	protected class AskCodesTask implements Runnable {
		@Override
		public void run() {
			if (codeBuffer.size() < 100)
				notifyRequestCodesToPrint(1000L);
		}
	}

	public void setAskCodeDelayMs(long askCodeDelayMs) {
		this.askCodeDelayMs = askCodeDelayMs;
	}
}
