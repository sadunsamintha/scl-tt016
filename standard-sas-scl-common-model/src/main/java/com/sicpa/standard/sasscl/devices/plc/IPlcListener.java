package com.sicpa.standard.sasscl.devices.plc;

import java.util.List;

import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;

/**
 * 
 * Listen to PLC event
 * 
 */
public interface IPlcListener {

	/**
	 * 
	 * call back method for any PLC event
	 * 
	 * @param event
	 */
	public void onPlcEvent(PlcEvent event);

	/**
	 * return all variables that are listening by this listener
	 * 
	 * null indicate that this listener is listening to all variables
	 * 
	 * @return
	 */
	public List<String> getListeningVariables();

}
