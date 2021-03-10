package com.sicpa.standard.sasscl.devices.d900.simulator;

/**
 * 
 * Used by CameraSimulatorController to ask for code to be sent back when a send code event occurs
 * 
 * @See CameraSimulatorController
 * 
 * 
 */
public interface ID900CodeProvider {

	public String requestCode();

}
