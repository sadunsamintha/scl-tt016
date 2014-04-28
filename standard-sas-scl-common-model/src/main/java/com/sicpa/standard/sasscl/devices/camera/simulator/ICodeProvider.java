package com.sicpa.standard.sasscl.devices.camera.simulator;

/**
 * 
 * Used by CameraSimulatorController to ask for code to be sent back when a send code event occurs
 * 
 * @See CameraSimulatorController
 * 
 * 
 */
public interface ICodeProvider {

	public String requestCode();

}
