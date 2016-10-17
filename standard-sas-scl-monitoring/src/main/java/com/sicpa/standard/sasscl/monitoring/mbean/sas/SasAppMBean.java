package com.sicpa.standard.sasscl.monitoring.mbean.sas;


public interface SasAppMBean {

	boolean isRunning();

	// Production Status
	/**
	 * check if the application is in production
	 * 
	 * @return int<br>
	 *         <b>0x01</b> In production<br>
	 *         <b>0x00</b> Stopped<br>
	 *         <b>-0x01</b> Unknown<br>
	 */
	int getIsInProduction();

	// Number of valid products
	/**
	 * Return the current number of valid products scanned by the Application.
	 * 
	 * @return int
	 */
	int getNbValidProducts();

	// Number of invalid products
	/**
	 * Return the current number of invalid products scanned by the Application
	 * 
	 * @return int
	 */
	int getNbInvalidProducts();

	// Get Warnings
	/**
	 * Return the list of warnings occurred during the production. Warning format : Each warning is surrounded by hook
	 * [WARNING1][WARNING1].
	 * 
	 * @return String
	 */
	String getWarnings();

	// Get Errors
	/**
	 * Return the list of errors occurred during the production Error format : Each error is surrounded by hook
	 * [ERROR1][ERROR1].
	 * 
	 * @return String
	 */
	String getErrors();

	// Camera Status
	/**
	 * Return the status of the cameras.
	 * 
	 * @return String
	 * 
	 *         format: camera_id:status| <-- delimiter
	 * 
	 * <br>
	 *         example:
	 * 
	 *         camera1:0|camera2:1
	 * 
	 * <br>
	 * 
	 *         status value:
	 * 
	 *         <b>-1</b> UNKNOWN status<br>
	 *         <b>1</b> Camera is CONNECTED<br>
	 *         <b>2</b> Camera is DISCONNECTED<br>
	 */
	String getDeviceCameraStatus();

	// PLC Status
	/**
	 * Return the status of the Plc.
	 * 
	 * @return int<br>
	 *         <b>-1</b> UNKNOWN status<br>
	 *         <b>1</b> Plc is CONNECTED<br>
	 *         <b>2</b> Plc is DISCONNECTED<br>
	 */
	int getDevicePlcStatus();

	// Master Status
	/**
	 * Return the status of the Master.
	 * 
	 * @return int<br>
	 *         <b>-1</b> UNKNOWN status<br>
	 *         <b>1</b> Master is CONNECTED<br>
	 *         <b>2</b> Master is DISCONNECTED<br>
	 */
	int getDeviceMasterStatus();

	String getLastProductScannedDate();

	// Last Product Scanned
	/**
	 * Return the number of ms since the last product scanned.
	 * 
	 * @return long
	 */
	long getLastProductScanned();

	String getApplicationStatus();

	String getApplicationLastRunningStartDate();

	// Stop Time
	/**
	 * Return time of production stopped.
	 * 
	 * @return String
	 */
	String getApplicationLastRunningStopDate();

	/**
	 * Return the line identification. This identification is used by the client for the Master authentification. It's
	 * although useful for the master to know which line asked encoder/decoder/sku...
	 * 
	 * @return long
	 */
	long getSubsystem();

	String getProductionMode();

	String getSKU();

	String getDeviceDisconnected();

	String getStatistics();

	// Last Successful Sending Date
	/**
	 * Return the date of the last successful client sending.
	 * 
	 * @return String
	 */
	String getLastSucessfullSynchronisationWithRemoteServerDate();

	// Last Successful Sending Number
	/**
	 * Return the number of products of the last successful client sending.
	 * 
	 * @return String return number in string
	 */
	String getLastSucessfullSynchronisationWithRemoteServerProduct();

	// Last Sending Date
	/**
	 * Return the date of the last client sending.
	 * 
	 * @return String
	 */
	String getLastSynchronisationWithRemoteServerDate();

	// Last Sending Status
	/**
	 * Return the status of the last client sending.
	 * 
	 * @return String<br>
	 *         <b>OK</b> SUCCESS<br>
	 *         <b>ERROR</b> FAILED<br>
	 *         <b>empty string</b> UNKNOWN
	 */
	String getLastSynchronisationWithRemoteServerStatus();

	// Size of packaged folder
	/**
	 * Return the byte size of the packaged folder. This folder contains the not-sent production
	 * 
	 * @return String
	 */
	String getSizeOfPackagedFolder();

	// Size of sent folder
	/**
	 * Return the byte size of the sent folder. This folder contains the sent production
	 * 
	 * @return String
	 */
	String getSizeOfSentFolder();

	// Size of buffer folder
	/**
	 * Return the byte size of the buffer folder. This folder contains the unpackaged production data
	 * 
	 * @return String
	 */
	String getSizeOfBufferFolder();

	// Oldest File
	/**
	 * Return the modification date of the oldest file from packaged folder.
	 * 
	 * @return String
	 */
	String getPackagedFolderOldestFileDate();

	// Quarantined Files
	/**
	 * Return the number of files contained in the Quarantine folder.
	 * 
	 * @return integer
	 */
	int getNumberOfQuarantineProductionFile();

	// Offline Counting Quantity
	/**
	 * Return the number of offline counting quantity variable
	 * 
	 * @return integer
	 */
	int getOfflineCountingQuantity();

	// Offline last stop time variable
	/**
	 * Return the time of offline counting last stop time
	 * 
	 * @return String
	 */
	String getOfflineCountingLastStopDate();

	// Offline last production time variable
	/**
	 * Return the time of offline counting last production time
	 * 
	 * @return String
	 */
	String getOfflineCountingLastProductionDate();

	/**
	 * Return the number of products of the last client sending.
	 * 
	 * @return String
	 */
	String getLastSendingNumberOfProducts();

	String getStorageInfo();

	String getPlcVersion();

	String getPlcInfoVars();

	String getAppVersion();

	/**
	 * Returns the values of the trilight for all lines (green, yellow and red).<br>
	 *
	 * 0 - light is off<br>
	 * 1 - light is on<br>
	 * 2 - light is blinking<br>
	 *
	 * E.g.: line1[green:1|yellow:1|red:0]line2[green:0|yellow:0|red:1]
	 *
	 * @return String
	 */
	String getTrilightStatus();
}
