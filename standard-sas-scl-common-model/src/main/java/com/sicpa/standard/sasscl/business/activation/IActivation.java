package com.sicpa.standard.sasscl.business.activation;

import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.model.Code;

/**
 * Defines the methods to be implemented by the <code>Activation</code> module.
 * 
 * @author DIelsch
 * 
 */
public interface IActivation {

	/**
	 * Method called whenever a good or bad code is received.<br>
	 * Process the code using the current IActivationBehavior<br>
	 * Notify listeners with the new product created during the processing.
	 * 
	 * @param code
	 * @param good
	 */
	void receiveCode(final Code code, final boolean good);

}
