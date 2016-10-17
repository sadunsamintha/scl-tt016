package com.sicpa.standard.sasscl.monitoring.mbean;

import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_PACKAGED;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_SAVED;
import static com.sicpa.standard.sasscl.common.storage.FileStorage.FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;

import com.sicpa.standard.sasscl.common.storage.QuarantineReason;

public class StandardMonitoringMBeanConstants {

	public static final String PACKAGED_FOLDER = "data/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_PACKAGED;

	public static final String BUFFER_FOLDER = "data/" + FOLDER_PRODUCTION + "/" + FOLDER_PRODUCTION_SAVED;

	public static final String SENT_FOLDER = "data/" + FOLDER_PRODUCTION + "/"
			+ FOLDER_PRODUCTION_SEND_TO_REMOTE_SERVER;

	public static final String ERROR_LOAD_FOLDER = String.format("quarantine%s%s", "/",
			QuarantineReason.LOAD_ERROR.getSubFolder());

	public static final String ERROR_BUSINESS_FOLDER = String.format("quarantine%s%s", "/",
			QuarantineReason.REMOTE_SERVER_BUSINESS_ERROR.getSubFolder());

	// -------------------------------------------------------------------------
	// RESPONSE
	// -------------------------------------------------------------------------

	public static final String UNDEFINED = " - ";
	public static final int UNKNOWN = -1;
	public static final int STOPPED = 0;
	public static final int INPRODUCTION = 1;

	// -----------------------------------------------------------------------
	public static String DEVICE_STATUS_DELIMITER = "|";
	public static String ERROR_MARKUP_OPEN = "[ERROR";
	public static String WARNING_MARKUP_OPEN = "[WARNING";
	public static String MARKUP_CLOSE = "]";

	// device status

	public static final int CONNECTED = 1;
	public static final int DISCONNECTED = 2;

	// ---------------------------------------------------------------------------

}
