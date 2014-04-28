package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import com.sicpa.standard.sasscl.model.Code;

/**
 * 
 * Do something before code is sent to the activation behavior
 * 
 * @author DIelsch
 * 
 */
public interface IBeforeActivationAction {

	/**
	 * Do something before code is sent to the activation behavior<br>
	 * return null if you want the code to be filtered by the activation
	 */
	BeforeActivationResult receiveCode(final Code code, final boolean good, String cameraName);
}
