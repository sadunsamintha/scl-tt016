package com.sicpa.standard.sasscl.monitoring.mbean.scl;

import com.sicpa.standard.sasscl.monitoring.mbean.sas.SasAppMBean;

public interface SclAppMBean extends SasAppMBean {

	String getEncoderID();

	String getInkLevel();

	String getMakeupLevel();

	// Printer Status
	/**
	 * Return the status of the Printers.
	 * 
	 * @return String
	 * 
	 *         format Printer_id:status| <-- delimiter
	 * 
	 * <br>
	 *         example: 
	 *         
	 *         printer1:0|printer2:1
	 * 
	 * <br>
	 * 
	 *         status value:
	 * 
	 *         <b>-1</b> UNKNOWN status<br>
	 *         <b>1</b> Printer is CONNECTED<br>
	 *         <b>2</b> Printer is DISCONNECTED<br>
	 */
	String getDevicePrinterStatus();

}
