package com.sicpa.standard.sasscl.devices.printer.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.printer.controller.IPrinterControllerListener;
import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.printer.controller.model.SequenceStatus;
import com.sicpa.standard.printer.controller.model.command.Command;
import com.sicpa.standard.printer.controller.model.command.PrinterMessage;
import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;
import com.sicpa.standard.printer.driver.event.PrinterUnsentCodesEventArgs;
import com.sicpa.standard.printer.leibinger.driver.leibinger.LeibingerSpecificSettings;
import com.sicpa.standard.printer.leibinger.driver.leibinger.LeibingerUserLevel;
import com.sicpa.standard.sasscl.business.coding.CodeReceivedFailedException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.printer.AbstractPrinterAdaptor;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.devices.printer.xcode.IExCodeBehavior;
import com.sicpa.standard.sasscl.event.PrinterProfileEvent;
import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

/**
 * Handles the communication with the <code>Standard Printer</code> component.
 */
public class PrinterAdaptor extends AbstractPrinterAdaptor implements IPrinterControllerListener {

	private static final Logger logger = LoggerFactory.getLogger(PrinterAdaptor.class);

	protected IPrinterController controller;

	protected volatile boolean initialized = false;

	protected volatile boolean codeSent = false;

	protected SequenceStatus lastSequence = SequenceStatus.UNKNOWN;

	protected IExCodeBehavior extendedCodesBehavior;

	public PrinterAdaptor(final IPrinterController controller) {
		this();
		this.controller = controller;
		this.controller.setListener(this);
	}

	public PrinterAdaptor() {

	}

	public void setController(IPrinterController controller) {
		this.controller = controller;
	}

	protected IPrinterController getController() {
		return controller;
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
	public void sendCodesToPrint(final List<String> codes) throws PrinterAdaptorException {
		logger.debug("Printer sending codes to print");
		try {
			controller.sendExtendedCodes(extendedCodesBehavior.createExCodes(codes));
			codeSent = true;
		} catch (PrinterException e) {
			throw new PrinterAdaptorException("sending codes to printer failed", e);
		}
	}

	@Override
	public void onConnectionStatusChanged(final IPrinterController sender, final boolean isConnected) {
		logger.debug((isConnected) ? "connection successful" : "disconnected");

		if (!isConnected) {
			lastSequence = SequenceStatus.UNKNOWN;
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	@Override
	public void onPrinterCodesNeeded(final Object sender, long nbCodes) {
		logger.debug("request for {} number of codes", nbCodes);
		notifyRequestCodesToPrint(nbCodes);
	}

	@Override
	public void onSequenceStatusChanged(final Object sender, final SequenceStatus args) {
		if (lastSequence == args) {
			return;
		}
		logger.debug("sequence status received: {}", args.name());

		if (!isConnected()) {
			// workaround because apparently "sometime" we don t get the connected notification
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		}

		if (args.equals(SequenceStatus.READY)) {
			fireIssueSolved(PrinterMessageId.NOT_READY_TO_PRINT);
			fireIssueSolved(PrinterMessageId.CHARGE_FAULT);
			fireIssueSolved(PrinterMessageId.GUTTER_FAULT);
			fireIssueSolved(PrinterMessageId.HIGH_VOLTAGE_FAULT);
			fireIssueSolved(PrinterMessageId.INK_SYSTEM_FAULT);
			fireIssueSolved(PrinterMessageId.VISCOMETER_FAULT);
			fireIssueSolved(PrinterMessageId.WATCHDOG_RESET);
			fireIssueSolved(PrinterMessageId.POWER_IN_OVERLOAD);
			fireIssueSolved(PrinterMessageId.INK_PRESSURE_FAULT);
			fireIssueSolved(PrinterMessageId.PRESSURE_FAULT);
			fireIssueSolved(PrinterMessageId.HYDRAULIC_LEAKAGE);
			fireIssueSolved(PrinterMessageId.CHARGE_DIRTY);
			fireIssueSolved(PrinterMessageId.PHASING_ERROR);
			fireIssueSolved(PrinterMessageId.NOZZLE_OC_ERROR);
			fireIssueSolved(PrinterMessageId.MOTOR_DIRECTION_ERROR);
			fireIssueSolved(PrinterMessageId.HV_CURRENT_TOO_HIGH);
			fireIssueSolved(PrinterMessageId.MAILING_BUFFER_EMPTY);
			fireIssueSolved(PrinterMessageId.CHARGE_VOLTAGE_OVERLOAD);
			fireIssueSolved(PrinterMessageId.PIEZO_VOLTAGE_OVERLOAD);
			fireIssueSolved(PrinterMessageId.HEAD_COVER_OPEN);
			fireIssueSolved(PrinterMessageId.NOZZLE_MOVES_UNCONTROLLED);
			fireIssueSolved(PrinterMessageId.CANT_OPEN_NOZZLE);
			fireIssueSolved(PrinterMessageId.CANT_LOAD_JOB);
			fireIssueSolved(PrinterMessageId.NO_PHASING);
			fireIssueSolved(PrinterMessageId.PRINTSTART_NOT_POSSIBLE);
			fireIssueSolved(PrinterMessageId.INK_JET_LOCKED);

		} else if (status.isConnected()) {
			// if connected and the sequence is not ready
			fireMessage(PrinterMessageId.NOT_READY_TO_PRINT, args.name());
		}
		lastSequence = args;
	}

	protected volatile boolean startedOnce;

	/**
	 * Request printer to start printing
	 */
	@Override
	public void doStart() throws PrinterAdaptorException {
		if (status != DeviceStatus.CONNECTED) {
			throw new PrinterAdaptorException("error when start printing");
		}

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

	protected void checkForCodeSent(final int counter) {
		if (status != DeviceStatus.CONNECTED) {
			return;
		}

		if (counter > 5 || codeSent) {
			fireDeviceStatusChanged(DeviceStatus.STARTED);
		} else {
			TaskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					ThreadUtils.sleepQuietly(500);
					checkForCodeSent(counter + 1);
				}
			});
		}
	}

	/**
	 * Request printer to stop printing
	 * 
	 */
	@Override
	public void doStop() throws PrinterAdaptorException {
		try {
			if (status == DeviceStatus.STARTED) {
				this.controller.stop();
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

	/**
	 * By convention the key of a PrinterMessage respects the format IMPACT.ID
	 */
	@Override
	public void onMessageReceived(PrinterMessage... messages) {

		for (PrinterMessage msg : messages) {

			String[] keySplit = StringUtils.split(msg.getKey(), '.');
			msg.setSource(this);

			// for the moment a specific case for the PrinterMessage
			if (keySplit.length == 3 && keySplit[0].equals(ActionMessageType.MONITORING.name())) {

				SystemEventType sEvent = new SystemEventType(msg.getKey());
				MonitoringService.addSystemEvent(new BasicSystemEvent(sEvent, String.valueOf(msg.getParams()[0])));
				return;
			}

			if (msg.isIssueSolved()) {
				fireIssueSolved(msg.getKey());
			} else {
				EventBusService.post(msg);
			}
		}
	}

	@Override
	public void onMessagesReceived(Map<String, PrinterMessage> messageMap) {
		if (messageMap.isEmpty())
			return;
		PrinterMessage[] array = new PrinterMessage[messageMap.size()];
		messageMap.values().toArray(array);
		onMessageReceived(array);
	}

	protected void fireMessage(final String key, final Object... params) {
		logger.debug("Device Message Changed: device - {}, message - {}", this, Messages.get(key));
		MessageEvent evt = new MessageEvent(this, key, params);
		EventBusService.post(evt);
	}

	@Override
	public void updateParameters(Command cmd) {
	}

	@Subscribe
	public void setUserLevel(PrinterProfileEvent event) {
		try {
			// FIXME this has to be moved to some Leibinger specific implementation
			controller.sendSpecificSettings(LeibingerSpecificSettings.CMD_SET_USER_LEVEL.getValue(),
					LeibingerUserLevel.get(event.getLevel()));
		} catch (PrinterException e) {
			logger.error("", e.getMessage());
		}
	}

	public void setExtendedCodesBehavior(IExCodeBehavior extendedCodesBehavior) {
		this.extendedCodesBehavior = extendedCodesBehavior;
	}
}