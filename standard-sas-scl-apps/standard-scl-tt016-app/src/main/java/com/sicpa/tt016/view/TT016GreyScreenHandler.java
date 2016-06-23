package com.sicpa.tt016.view;

import static com.sicpa.standard.gui.utils.ThreadUtils.invokeLater;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.standard.sasscl.view.main.GreyScreenHandler;
import com.sicpa.tt016.view.selection.stop.StopReasonSelectedEvent;

public class TT016GreyScreenHandler extends GreyScreenHandler {

	private AbstractViewFlowController stopReasonControler;

	@Override
	protected boolean isDisplayGreyScreen() {
		if (stopReasonControler.isActive()) {
			return false;
		}
		return super.isDisplayGreyScreen();
	}

	@Subscribe
	public void HandleStopReasonSelectedEvent(StopReasonSelectedEvent evt) {
		invokeLater(() -> refresh());
	}

	public void setStopReasonControler(AbstractViewFlowController stopReasonControler) {
		this.stopReasonControler = stopReasonControler;
	}
}
