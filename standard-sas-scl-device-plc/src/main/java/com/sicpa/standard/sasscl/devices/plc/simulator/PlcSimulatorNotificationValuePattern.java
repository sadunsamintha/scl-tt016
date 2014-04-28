/**
 * Author	: YYang
 * Date		: Oct 6, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc.simulator;

/**
 * 
 * Used in PLC simulator to define how the notification is sent from the simulator
 * 
 */
public enum PlcSimulatorNotificationValuePattern {

	/**
	 * notification value is the same as the initial value
	 */
	STATIC,

	/**
	 * notification value will be increased
	 */
	INCREMENTAL,

	/**
	 * notification value is sent from the UI
	 */
	UI,

	/**
	 * notification value based on random number
	 */
	RANDOM;

}
