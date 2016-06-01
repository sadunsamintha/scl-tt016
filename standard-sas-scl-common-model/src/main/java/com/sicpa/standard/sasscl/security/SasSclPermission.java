package com.sicpa.standard.sasscl.security;

import com.sicpa.standard.client.common.security.Permission;

public interface SasSclPermission {

	Permission EXIT = new Permission("EXIT");

	Permission PRODUCTION_CHANGE_PARAMETERS = new Permission("PRODUCTION_CHANGE_PARAMETERS");
	Permission PRODUCTION_MODE_ALL = new Permission("PRODUCTION_MODE_ALL");
	Permission PRODUCTION_MODE_STANDARD = new Permission("PRODUCTION_MODE_STANDARD");
	Permission PRODUCTION_MODE_EXPORT = new Permission("PRODUCTION_MODE_EXPORT");
	Permission PRODUCTION_MODE_COUNTING = new Permission("PRODUCTION_MODE_COUNTING");
	Permission PRODUCTION_MODE_MAINTENANCE = new Permission("PRODUCTION_MODE_MAINTENANCE");
	Permission PRODUCTION_MODE_REFEED_NORMAL = new Permission("PRODUCTION_MODE_REFEED_NORMAL");
	Permission PRODUCTION_MODE_REFEED_CORRECTION = new Permission("PRODUCTION_MODE_REFEED_CORRECTION");

	Permission PRODUCTION_START = new Permission("PRODUCTION_START");
	Permission PRODUCTION_STOP = new Permission("PRODUCTION_STOP");
	Permission PRODUCTION_VIEW_STATISTICS = new Permission("PRODUCTION_VIEW_STATISTICS");
	Permission PRODUCTION_REPORT = new Permission("PRODUCTION_REPORT");

	Permission EDIT_MODEL_PROPERTY = new Permission("EDIT_MODEL_PROPERTY");
	Permission EDIT_PLC_VARIABLES = new Permission("EDIT_PLC_VARIABLES");
	Permission EDIT_PLC_SECURE_VARIABLES = new Permission("EDIT_PLC_SECURE_VARIABLES");
	Permission EDIT_MISC_PROPERTY = new Permission("EDIT_MISC_PROPERTY");

	Permission MONITORING_SYSTEM_EVENT = new Permission("MONITORING_SYSTEM_EVENT");
	Permission MONITORING_PRODUCTION_STATISTICS = new Permission("MONITORING_PRODUCTION_STATISTICS");
	Permission MONITORING_MBEAN = new Permission("MONITORING_MBEAN");
	Permission VIEW_PLC_VAR = new Permission("VIEW_PLC_VAR");

	Permission BEAN_CALL = new Permission("BEAN_CALL");

	Permission DISPLAY_ENCODERS_VIEW = new Permission("DISPLAY_ENCODERS_VIEW");
	Permission SCREENSHOT = new Permission("SCREENSHOT");

	Permission DEVICE_CONTEXT_CONSOLE = new Permission("DEVICE_CONTEXT_CONSOLE");
	
	Permission ADVANCED_CONTROL = new Permission("ADVANCED_CONTROL");
	Permission ADVANCED_MONITORING = new Permission("ADVANCED_MONITORING");

}
