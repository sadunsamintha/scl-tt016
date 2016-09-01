package com.sicpa.tt016.view.selection.stop;

import static com.sicpa.tt016.controller.flow.TT016ActivityTrigger.TRG_STOP_REASON_SELECTED;
import static com.sicpa.tt016.monitoring.system.TT016SystemEventType.PROD_STOP_REASON;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION_REASON_SELECTED;

import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.tt016.event.ManualStopEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.FlowControl;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.view.flow.DefaultScreensFlow;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;

import java.util.concurrent.atomic.AtomicBoolean;

public class StopReasonViewController extends AbstractViewFlowController implements IStopReasonListener {

	private static final Logger logger = LoggerFactory.getLogger(StopReasonViewController.class);

	private DefaultScreensFlow screensFlow;
	private FlowControl flowControl;

	private volatile HardwareControllerStatus status;

	private AtomicBoolean stopButtonPressed = new AtomicBoolean(false);

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		logger.info("refreshing_panel,reason=language switch,lang=" + evt.getLanguage());
		StopReasonView srv = new StopReasonView();
		srv.setController(this);
		this.setView(srv);
	}


	@Subscribe
	public void handleStopProduction(ApplicationFlowStateChangedEvent event) {
		if (event.getPreviousState().equals(ApplicationFlowState.STT_STARTED) && event.getCurrentState().equals(ApplicationFlowState.STT_STOPPING)) {
			screensFlow.moveToNext(STOP_PRODUCTION);
		}
	}

	@Subscribe
	public void handleStopButtonPressed(ManualStopEvent evt){
		stopButtonPressed.set(true);
		logger.info("StopButtonPressed,user=" + evt.user.getLogin() + ",date=" + evt.date.toString());
	}

	@Subscribe
	public void handleHardwareConnecting(HardwareControllerStatusEvent evt) {
		status = evt.getStatus();
	}



	@Override
	protected void displayView() {
		if (stopButtonPressed.get()) {
			super.displayView();
			stopButtonPressed.set(false);
		} else {
			moveToNext();
		}
	}

	@Override
	public void stopReasonSelected(StopReason stopReason) {
		logger.info(Messages.format("stopreason.key", Messages.get(stopReason.getKey())));

		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.INFO, PROD_STOP_REASON, Messages.get(stopReason.getKey())));
		moveToNext();
	}

	private void moveToNext() {
		screensFlow.moveToNext(STOP_PRODUCTION_REASON_SELECTED);
		flowControl.moveToNextState(TRG_STOP_REASON_SELECTED);
	}

	public void setScreensFlow(DefaultScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	public void setFlowControl(FlowControl flowControl) {
		this.flowControl = flowControl;
	}
}
