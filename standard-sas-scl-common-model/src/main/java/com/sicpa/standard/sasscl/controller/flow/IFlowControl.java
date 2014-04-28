package com.sicpa.standard.sasscl.controller.flow;

/**
 * determine what activity has to be done based on the current activity and the trigger
 * 
 * @author DIelsch
 * 
 */
public interface IFlowControl {

	void notifyStartProduction();

	void notifyStopProduction();

	void notifyExitApplication();

	void notifyEnterSelectionScreen();

	void notifyExitSelectionScreen();

	void notifyProductionParameterSelected();

	void notifyHardwareControllerConnected();

	void notifyRecoveringConnection();

	void notifyHardwareControllerStarted();

	void notifyHardwareControllerStopping();

	void notifyHardwareControllerDisconnected();

	ApplicationFlowState getCurrentState();

}
