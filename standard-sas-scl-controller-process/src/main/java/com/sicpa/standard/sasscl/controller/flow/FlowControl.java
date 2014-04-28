package com.sicpa.standard.sasscl.controller.flow;

public class FlowControl extends AbstractFlowControl {

	public FlowControl() {

	}

	@Override
	public void notifyStartProduction() {
		moveToNextState(ActivityTrigger.TRG_START_PRODUCTION);
	}

	@Override
	public void notifyStopProduction() {
		moveToNextState(ActivityTrigger.TRG_STOP_PRODUCTION);
	}

	@Override
	public void notifyExitApplication() {
		moveToNextState(ActivityTrigger.TRG_EXIT_APPLICATION);
	}

	@Override
	public void notifyEnterSelectionScreen() {
		moveToNextState(ActivityTrigger.TRG_ENTERSELECTION);
	}

	@Override
	public void notifyExitSelectionScreen() {
		moveToNextState(ActivityTrigger.TRG_EXITSELECTION);

	}

	@Override
	public void notifyProductionParameterSelected() {
		moveToNextState(ActivityTrigger.TRG_SELECT);

	}

	@Override
	public void notifyHardwareControllerConnected() {
		moveToNextState(ActivityTrigger.TRG_HARDWARE_CONNECTED);

	}

	@Override
	public void notifyHardwareControllerStarted() {
		moveToNextState(ActivityTrigger.TRG_STARTED);

	}

	@Override
	public void notifyHardwareControllerStopping() {
		moveToNextState(ActivityTrigger.TRG_HARDWARE_STOPPING);

	}

	@Override
	public void notifyHardwareControllerDisconnected() {
		moveToNextState(ActivityTrigger.TRG_HARDWARE_DISCONNECTED);

	}

	@Override
	public ApplicationFlowState getCurrentState() {
		return stateMachine.getCurrentState();
	}

	@Override
	public void notifyRecoveringConnection() {
		moveToNextState(ActivityTrigger.TRG_RECOVERING_CONNECTION);
	}
}
