package com.sicpa.standard.sasscl.devices.printer.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.printer.controller.IPrinterControllerListener;
import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.printer.controller.model.SequenceStatus;
import com.sicpa.standard.printer.controller.model.command.Command;
import com.sicpa.standard.printer.controller.model.command.PrinterMessage;
import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;
import com.sicpa.standard.printer.driver.event.PrinterUnsentCodesEventArgs;
import com.sicpa.standard.sasscl.business.coding.CodeReceivedFailedException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.printer.AbstractPrinterAdaptor;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.devices.printer.event.PrinterStoppedEvent;
import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.messages.DeviceReadyEvent;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

public abstract class PrinterAdaptor extends AbstractPrinterAdaptor implements IPrinterControllerListener {

	private static final Logger logger = LoggerFactory.getLogger(PrinterAdaptor.class);

	protected IPrinterController controller;

	protected volatile boolean initialized = false;
	protected volatile boolean codeSent = false;
	protected volatile boolean startedOnce;
	protected SequenceStatus lastSequence = SequenceStatus.UNKNOWN;

	public PrinterAdaptor(IPrinterController controller) {
		this();
		this.controller = controller;
		this.controller.setListener(this);
	}

	public PrinterAdaptor() {

	}

	public void setController(IPrinterController controller) {
		this.controller = controller;
	}

	@Override
	protected void doConnect() throws PrinterAdaptorException {
		if (!initialized) {
			try {
				controller.init();
				initialized = true;
			} catch (com.sicpa.standard.printer.controller.PrinterException e) {
				throw new PrinterAdaptorException("connection failed", e);
			}
		}
	}

	@Override
	protected void doDisconnect() {
		try {
			controller.shutdown();
			initialized = false;
			EventBusService.unregister(this);
		} catch (PrinterException e) {
			logger.error(e.getMessage());
		}
		lastSequence = SequenceStatus.UNKNOWN;
		fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
	}

	@Override
	public void onConnectionStatusChanged(Object sender, boolean isConnected) {
		logger.debug((isConnected) ? "connection successful" : "disconnected");
		if (!isConnected) {
			lastSequence = SequenceStatus.UNKNOWN;
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	@Override
	public void onPrinterCodesNeeded(Object sender, long nbCodes) {
		logger.debug("request for {} number of codes", nbCodes);
		notifyRequestCodesToPrint(nbCodes);
	}

	@Override
	public void onSequenceStatusChanged(Object sender, SequenceStatus args) {

		if (args.equals(SequenceStatus.READY)) {
			notifyAllIssuesSolved();
		}
		if (!isConnected()) {
			// workaround because apparently "sometime" we don t get the
			// connected notification
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		}
		if (lastSequence == args) {
			return;
		}
		logger.debug("sequence status received: {}", args.name());

		if (!args.equals(SequenceStatus.READY) && status.isConnected()) {
			// if connected and the sequence is not ready
			fireMessage(PrinterMessageId.NOT_READY_TO_PRINT, args.name());
		}
		lastSequence = args;
	}

	protected void notifyAllIssuesSolved() {
		EventBusService.post(new DeviceReadyEvent(this));
	}

	@Override
	public void onPrinterStop() {
		EventBusService.post(new PrinterStoppedEvent(this));
	}

	@Override
	public void doStart() throws PrinterAdaptorException {
		try {
			codeSent = false;
			controller.start();
			if (startedOnce) {
				// the printer has already been started so it has codes
				fireDeviceStatusChanged(DeviceStatus.STARTED);
			} else {
				checkForCodeSent(0);
			}
			startedOnce = true;
		} catch (PrinterException e) {
			throw new PrinterAdaptorException("error when start printing", e);
		}
	}

	protected void checkForCodeSent(int counter) {
		if (status != DeviceStatus.CONNECTED) {
			return;
		}

		if (counter > 5 || codeSent) {
			fireDeviceStatusChanged(DeviceStatus.STARTED);
		} else {
			TaskExecutor.execute(() -> {
				ThreadUtils.sleepQuietly(500);
				checkForCodeSent(counter + 1);
			});
		}
	}

	@Override
	public void doStop() throws PrinterAdaptorException {
		try {
			if (status == DeviceStatus.STARTED) {
				controller.stop();
			}
		} catch (PrinterException e) {
			throw new PrinterAdaptorException("error when stop printing", e);
		} finally {
			fireDeviceStatusChanged(DeviceStatus.STOPPED);
		}
	}

	@Override
	public void provideCode(List<String> codes, Object requestor) throws CodeReceivedFailedException {
		try {
			sendCodesToPrint(codes);
		} catch (PrinterAdaptorException e) {
			throw new CodeReceivedFailedException(e);
		}
	}
	
	@Override
	public void provideCodePair(List<Pair<String, String>> codes, Object requestor) throws CodeReceivedFailedException {
		try {
			sendPairCodesToPrint(codes);
		} catch (PrinterAdaptorException e) {
			throw new CodeReceivedFailedException(e);
		}
	}

	public void resetCodes() throws PrinterAdaptorException {
		try {
			controller.reset();
		} catch (PrinterException e) {
			throw new PrinterAdaptorException("error reseting the code buffer", e);
		}
	}

	@Override
	public void switchOff() throws PrinterAdaptorException {
		try {
			controller.sequenceOff();
		} catch (PrinterException e) {
			throw new PrinterAdaptorException("error when sequencing off", e);
		}
	}

	@Override
	public void switchOn() throws PrinterAdaptorException {
		try {
			controller.sequenceOn();
		} catch (PrinterException e) {
			throw new PrinterAdaptorException("error when sequencing on", e);
		}
	}

	@Override
	public void onUnsentCodeInCodeBuffer(Object sender, PrinterUnsentCodesEventArgs args) {
	}

	@Override
	public boolean isBlockProductionStart() {
		return true;
	}

	@Override
	public void onMessageReceived(PrinterMessage... messages) {
		for (PrinterMessage msg : messages) {
			msg.setSource(this);
			onMessageReceived(msg);
		}
	}

	protected void onMessageReceived(PrinterMessage msg) {
		if (isMonitoringMsg(msg)) {
			monitoringMessageReceived(msg);
		} else if (isLogMsg(msg)) {
			logMessageReceived(msg);
		} else if (msg.isIssueSolved()) {
			fireIssueSolved(msg.getKey());
		} else {
			EventBusService.post(msg);
		}
	}

	private void logMessageReceived(PrinterMessage msg) {
		logger.info("log info from printer " + msg.getKey() + ":" + Arrays.toString(msg.getParams()));
	}

	private void monitoringMessageReceived(PrinterMessage msg) {
		SystemEventType sEvent = new SystemEventType(msg.getKey());
		MonitoringService.addSystemEvent(new BasicSystemEvent(sEvent, String.valueOf(msg.getParams()[0])));
	}

	private boolean isMonitoringMsg(PrinterMessage msg) {
		String[] keySplit = StringUtils.split(msg.getKey(), '.');
		return keySplit.length == 3 && keySplit[0].equals(ActionMessageType.MONITORING.toString());
	}

	private boolean isLogMsg(PrinterMessage msg) {
		String[] keySplit = StringUtils.split(msg.getKey(), '.');
		return keySplit.length == 3 && keySplit[0].equals(ActionMessageType.LOG.toString());
	}

	protected void fireMessage(String key, Object... params) {
		logger.debug("Device Message Changed: device - {}, message - {}", this, Messages.get(key));
		MessageEvent evt = new MessageEvent(this, key, params);
		EventBusService.post(evt);
	}

	@Override
	public void updateParameters(Command cmd) {
	}

}