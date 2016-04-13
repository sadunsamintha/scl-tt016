package com.sicpa.tt016.scl;

import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.statemachine.FlowTransition;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.tt016.view.TT016ScreenFlowTriggers;
import com.sicpa.tt016.view.selection.stop.StopReasonViewController;

import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.*;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;

public class TT016Bootstrap extends Bootstrap {

	private MainPanelGetter mainPanelGetter;
	private StopReasonViewController stopReasonViewController;

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		//TODO here add customization

		CustoBuilder.addScreen(stopReasonViewController);
		CustoBuilder.addScreenTransition(mainPanelGetter, stopReasonViewController, TT016ScreenFlowTriggers
				.STOP_PRODUCTION);
		CustoBuilder.addScreenTransition(stopReasonViewController, mainPanelGetter, TT016ScreenFlowTriggers
				.STOP_PRODUCTION_REASON_SELECTED);

		CustoBuilder.setStateNextPossibleStates(ApplicationFlowState.STT_STOPPING,
				new FlowTransition(TRG_STOP_REASON_SELECTED, STT_CONNECTED),
				new FlowTransition(TRG_RECOVERING_CONNECTION, STT_RECOVERING),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));
	}

	public void setMainPanelGetter(MainPanelGetter mainPanelGetter) {
		this.mainPanelGetter = mainPanelGetter;
	}

	public void setStopReasonViewController(StopReasonViewController stopReasonViewController) {
		this.stopReasonViewController = stopReasonViewController;
	}
}