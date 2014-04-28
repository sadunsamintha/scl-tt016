package com.sicpa.standard.sasscl.security;

import com.sicpa.standard.client.common.security.Permission;

public class SasSclPermission {

	public static final Permission EXIT = new Permission("EXIT");

	public static final Permission PRODUCTION_CHANGE_PARAMETERS = new Permission("PRODUCTION_CHANGE_PARAMETERS");
	public static final Permission PRODUCTION_MODE_ALL = new Permission("PRODUCTION_MODE_ALL");
	public static final Permission PRODUCTION_MODE_STANDARD = new Permission("PRODUCTION_MODE_STANDARD");
	public static final Permission PRODUCTION_MODE_EXPORT = new Permission("PRODUCTION_MODE_EXPORT");
	public static final Permission PRODUCTION_MODE_COUNTING = new Permission("PRODUCTION_MODE_COUNTING");
	public static final Permission PRODUCTION_MODE_MAINTENANCE = new Permission("PRODUCTION_MODE_MAINTENANCE");
	public static final Permission PRODUCTION_MODE_REFEED_NORMAL = new Permission("PRODUCTION_MODE_REFEED_NORMAL");
	public static final Permission PRODUCTION_MODE_REFEED_CORRECTION = new Permission("PRODUCTION_MODE_REFEED_CORRECTION");

	public static final Permission PRODUCTION_START = new Permission("PRODUCTION_START");
	public static final Permission PRODUCTION_STOP = new Permission("PRODUCTION_STOP");
	public static final Permission PRODUCTION_VIEW_STATISTICS = new Permission("PRODUCTION_VIEW_STATISTICS");
	public static final Permission PRODUCTION_REPORT = new Permission("PRODUCTION_REPORT");

	public static final Permission EDIT_MODEL_PROPERTY = new Permission("EDIT_MODEL_PROPERTY");
	public static final Permission EDIT_PLC_VARIABLES = new Permission("EDIT_PLC_VARIABLES");
	public static final Permission EDIT_PLC_SECURE_VARIABLES = new Permission("EDIT_PLC_SECURE_VARIABLES");
	public static final Permission EDIT_MISC_PROPERTY = new Permission("EDIT_MISC_PROPERTY");

	public static final Permission MONITORING_SYSTEM_EVENT = new Permission("MONITORING_SYSTEM_EVENT");
	public static final Permission MONITORING_PRODUCTION_STATISTICS = new Permission("MONITORING_PRODUCTION_STATISTICS");
	public static final Permission MONITORING_MBEAN = new Permission("MONITORING_MBEAN");
	
	public static final Permission BEAN_CALL = new Permission("BEAN_CALL");
	
	public static final Permission DISPLAY_ENCODERS_VIEW = new Permission("DISPLAY_ENCODERS_VIEW");
	public static final Permission SCREENSHOT = new Permission("SCREENSHOT");	

}
