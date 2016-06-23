package com.sicpa.standard.sasscl.view.main;

import static com.sicpa.standard.gui.utils.ThreadUtils.invokeLater;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_RECOVERING;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.view.MainFrameController;

public class GreyScreenHandler {

	private MainFrameController mfController;
	private volatile String errorMsg;
	private IFlowControl flowControl;

	@Subscribe
	public void handleApplicationStatusChanged(ApplicationFlowStateChangedEvent evt) {
		refresh();
	}

	@Subscribe
	public void handleHardwareConnecting(HardwareControllerStatusEvent evt) {
		errorMsg = createErrorMessage(evt);
		refresh();
	}

	private String createErrorMessage(HardwareControllerStatusEvent evt) {
		String msg = "\n";
		if (evt.getStatus().equals(HardwareControllerStatus.CONNECTING)) {
			for (String error : evt.getErrors()) {
				msg += error + "\n";
			}
		}
		return msg;
	}

	protected void refresh() {
		invokeLater(() -> {
			mfController.removeAllErrorMainPanel();
			if (isDisplayGreyScreen()) {
				displayGreyScreen();
			}
		});
	}

	private void displayGreyScreen() {
		mfController.addErrorMainPanel("", "controller.device.recovering.waiting", errorMsg);
	}

	protected boolean isDisplayGreyScreen() {
		ApplicationFlowState state = flowControl.getCurrentState();
		return state.equals(STT_RECOVERING) || state.equals(STT_CONNECTING);
	}

	public void setMfController(MainFrameController mfController) {
		this.mfController = mfController;
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}
}
