package com.sicpa.standard.sasscl.view.startstop;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.flow.statemachine.executor.ExecutorStarting;
import com.sicpa.standard.sasscl.controller.hardware.ProductionDevicesCreatedEvent;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.printer.IPrinterAdaptor;
import com.sicpa.standard.sasscl.devices.printer.impl.PrinterAdaptorLeibinger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;
import static java.util.Arrays.asList;


public class StartStopViewController implements IStartStopViewListener {

	private final static Logger logger = LoggerFactory.getLogger(ExecutorStarting.class);

	private final List<ApplicationFlowState> enableStartState = asList(STT_CONNECTED);
	private final List<ApplicationFlowState> enableStopState = asList(STT_STARTED);

	private StartStopModel model;
	private IFlowControl flowControl;

	private boolean isNozzleDelayActive;
	private long nozzleDelayMS;

	public StartStopViewController(StartStopModel model) {
		this.model = model;
		this.isNozzleDelayActive = false;
	}

	public StartStopViewController() {
		this(new StartStopModel());
		this.isNozzleDelayActive = false;
	}

	@Subscribe
	public void processStateChanged(final ApplicationFlowStateChangedEvent evt) {
		//Delay added to prevent starting of production before the nozzle starts to close.
		if (evt.getPreviousState().equals(STT_STOPPING) && isNozzleDelayActive) {
			ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
			ses.schedule(() -> updateModel(evt), nozzleDelayMS, TimeUnit.MILLISECONDS);
			ses.shutdown();
		} else {
			updateModel(evt);
		}
	}

	@Subscribe
	public void setNozzleDelayMS(ProductionDevicesCreatedEvent evt) {
		for (IStartableDevice device : evt.getDevices()) {
			if (device instanceof  PrinterAdaptorLeibinger) {
				PrinterAdaptorLeibinger adaptorLeibinger = (PrinterAdaptorLeibinger) device;
				try {
					if (nozzleDelayMS < adaptorLeibinger.getNozzleDelayMS()) {
						nozzleDelayMS = adaptorLeibinger.getNozzleDelayMS();
					}
					isNozzleDelayActive = true;
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}
	}

	@Override
	public void start() {
		OperatorLogger.log("Start Production");
		flowControl.notifyStartProduction();
	}

	@Override
	public void stop() {
		OperatorLogger.log("Stop Production");
		flowControl.notifyStopProduction();
	}

	private void updateModel(ApplicationFlowStateChangedEvent evt) {
		model.setStartEnabled(enableStartState.contains(evt.getCurrentState()));
		model.setStopEnabled(enableStopState.contains(evt.getCurrentState()));
		model.notifyModelChanged();
	}

	public StartStopModel getModel() {
		return model;
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}
}
