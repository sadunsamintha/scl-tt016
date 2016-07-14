package com.sicpa.tt016.scl;

import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_EXIT_APPLICATION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_RECOVERING_CONNECTION;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_EXIT;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_RECOVERING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STOPPING;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.*;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.WARNING;
import static com.sicpa.tt016.controller.flow.TT016ActivityTrigger.TRG_STOP_REASON_SELECTED;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION;
import static com.sicpa.tt016.view.TT016ScreenFlowTriggers.STOP_PRODUCTION_REASON_SELECTED;

import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.flow.statemachine.FlowTransition;
import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.provider.impl.UnknownSkuProvider;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.tt016.view.selection.stop.StopReasonViewController;

public class TT016Bootstrap extends Bootstrap {

	private MainPanelGetter mainPanelGetter;
	private StopReasonViewController stopReasonViewController;
	private UnknownSkuProvider unknownSkuProvider;
	private int codeTypeId;

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		messageCusto();
		noStopIfDmxDetectedInExport();
		selectStopReasonWhenProductionStop();
		setUnknownSkuCodeType();
	}

	private void messageCusto() {
		// let's override the severity from error to warning
		setMessageType(MessageEventKey.Alert.TOO_MANY_CAMERA_ERROR, WARNING);
	}

	private void setUnknownSkuCodeType() {
		unknownSkuProvider.get().setCodeType(new CodeType(codeTypeId));
	}

	private void noStopIfDmxDetectedInExport() {
		setMessageType(MessageEventKey.Activation.EXCEPTION_CODE_IN_EXPORT, ActionMessageType.WARNING);
	}

	private void selectStopReasonWhenProductionStop() {
		addScreen(stopReasonViewController);
		addScreenTransitions(mainPanelGetter, new ScreenTransition(STOP_PRODUCTION, stopReasonViewController));
		addScreenTransitions(stopReasonViewController, new ScreenTransition(STOP_PRODUCTION_REASON_SELECTED,
				mainPanelGetter));
		
		setStateNextPossibleStates(STT_STOPPING, new FlowTransition(TRG_STOP_REASON_SELECTED, STT_CONNECTED),
				new FlowTransition(TRG_RECOVERING_CONNECTION, STT_RECOVERING), new FlowTransition(TRG_EXIT_APPLICATION,
						STT_EXIT));
	}

	public void setMainPanelGetter(MainPanelGetter mainPanelGetter) {
		this.mainPanelGetter = mainPanelGetter;
	}

	public void setStopReasonViewController(StopReasonViewController stopReasonViewController) {
		this.stopReasonViewController = stopReasonViewController;
	}

	public void setUnknownSkuProvider(UnknownSkuProvider unknownSkuProvider) {
		this.unknownSkuProvider = unknownSkuProvider;
	}

	public void setCodeTypeId(int codeTypeId) {
		this.codeTypeId = codeTypeId;
	}

}
