package com.sicpa.standard.sasscl.ioc;

import com.sicpa.standard.client.common.ioc.AbstractSpringConfig;

/**
 * config the files that should be loaded by spring
 * 
 * @author DIelsch
 * 
 */
public class SpringConfig extends AbstractSpringConfig {

	// prefix is used to make sure the std spring files are loaded before the customs spring files
	protected static final String PREFIX = "0000";

	public static final String ACTIVATION = PREFIX + "activation";
	public static final String STATISTICS = PREFIX + "statistics";
	public static final String PRODUCTION = PREFIX + "production";
	public static final String PRODUCTION_CONFIG = PREFIX + "productionConfig";
	public static final String PROVIDER = PREFIX + "provider";
	public static final String CAMERA = PREFIX + "camera";
	public static final String GLOBAL_CONFIG = PREFIX + "globalconfig";
	public static final String SCHEDULER = PREFIX + "scheduler";
	public static final String ALERT = PREFIX + "alert";
	public static final String ALERT_CAMERA_COUNT_TASK = PREFIX + "alertCameraCount";
	public static final String ALERT_DUPLICATED_CODE = PREFIX + "alertDuplicatedCode";
	public static final String ALERT_CAMERA_IDDLE_TASK = PREFIX + "alertCameraIddle";

	public static final String STORAGE = PREFIX + "storage";
	public static final String MISC = PREFIX + "misc";
	public static final String REMOTE_SERVER = PREFIX + "remoteServer";
	public static final String VIEW = PREFIX + "view";

	public static final String DEVICES_CONTROLLER = PREFIX + "devicesController";
	public static final String HARDWARE_CONTROLLER = PREFIX + "hardwareController";

	public static final String MODEL_EDITABLE_PROPERTIES = PREFIX + "editableProperties";

	public static final String PLC = PREFIX + "plc";
	public static final String PLC_SECURE = PREFIX + "plcSecure";
	public static final String PLC_JMX_INFO = PREFIX + "plcjmxinfo";

	public static final String PLC_NOTIFICATIONS_LINE = PREFIX + "plcNotificationsLine";
	public static final String PLC_NOTIFICATIONS_CABINET = PREFIX + "plcNotificationsCabinet";
	public static final String PLC_ERROR_REGISTER_CABINET = PREFIX + "plcCabinetErrorRegister";
	public static final String PLC_ERROR_REGISTER_LINE = PREFIX + "plcLineErrorRegister";

	public static final String PLC_REQUESTS = PREFIX + "plcRequests";
	public static final String PLC_PARAMETERS_CABINET = PREFIX + "plcParametersCabinet";
	public static final String PLC_PARAMETERS_LINE = PREFIX + "plcParametersLine";
	public static final String PLC_VAR_MAP = PREFIX + "plcvarmap";

	public static final String FLOW_CONTROL = PREFIX + "flowControl";

	// fix should be loaded first thus the 000000000000
	public static final String PROPERTIES_PLACEHOLDER = PREFIX + "00000000000000propertyPlaceholderConfigurer";
	public static final String BARCODE = PREFIX + "barcode";
	public static final String DESCRIPTORS_CAMERA = PREFIX + "cameraDescriptors";
	public static final String DESCRIPTORS_PLC = PREFIX + "plcDescriptors";
	public static final String DESCRIPTORS_BARCODE = PREFIX + "barocdeDescriptors";
	public static final String DESCRIPTORS_GLOBAL_CONFIG = PREFIX + "globalConfigDescriptors";

	public static final String EDITABLE_PLC_CABINET_VARIABLES = PREFIX + "editablePlcCabinetVariables";
	public static final String EDITABLE_PLC_LINE_VARIABLES = PREFIX + "editablePlcLineVariables";

	public static final String MESSAGES_MAPPING = PREFIX + "messagesMapping";
	public static final String DUPLICATED_CODE_FILTER = PREFIX + "duplicatedCodeFilter";
	public static final String MONITORING = PREFIX + "monitoring";
	public static final String SECURITY = PREFIX + "security";
	public static final String OFFLINE_COUNTING = "offlineCounting";

	public static final String CRYPTO = PREFIX + "crypto";
	public static final String XSTREAM_CONFIG = PREFIX + "xstreamconfig";

	public static final String ALERT_PLC_ACTIVATION_CROSSCHECK = PREFIX + "alertPlcActivationCrosscheck";
	public static final String CUSTOMIZABLE_PROPERTIES = PREFIX + "customizableProperties";
	public static final String ERRORS_REPOSITORY = PREFIX + "errorsRepository";

	public static final String BIS = PREFIX + "bis";
	public static final String SKU_CHECK = PREFIX + "skuCheck";
	public static final String SKU_CHECK_VIEW = PREFIX + "skuCheckView";

	public static final String EVENT_BUS = PREFIX + "eventBus";

	public SpringConfig() {
		config.put(CUSTOMIZABLE_PROPERTIES, "spring/customizableProperties.xml");
		config.put(ACTIVATION, "spring/activation.xml");
		config.put(STATISTICS, "spring/statistics.xml");
		config.put(PRODUCTION, "spring/production.xml");
		config.put(PROVIDER, "spring/provider.xml");
		config.put(CAMERA, "spring/camera.xml");
		config.put(GLOBAL_CONFIG, "spring/config.xml");
		config.put(SCHEDULER, "spring/scheduler.xml");
		config.put(ALERT, "spring/alert.xml");
		config.put(ALERT_CAMERA_COUNT_TASK, "spring/alertCameraCountTask.xml");
		config.put(ALERT_DUPLICATED_CODE, "spring/alertDuplicatedCodeTask.xml");
		config.put(ALERT_CAMERA_IDDLE_TASK, "spring/alertCameraIddleTask.xml");
		config.put(STORAGE, "spring/storage.xml");
		config.put(MISC, "spring/misc.xml");
		config.put(REMOTE_SERVER, "spring/remoteServer.xml");
		config.put(VIEW, "spring/view.xml");
		config.put(DEVICES_CONTROLLER, "spring/devicesController.xml");
		config.put(HARDWARE_CONTROLLER, "spring/hardwareController.xml");
		config.put(PLC, "spring/plc.xml");
		config.put(PLC_SECURE, "spring/plcSecureModule.xml");
		config.put(PLC_JMX_INFO, "spring/plcJmxInfo.xml");
		config.put(PLC_NOTIFICATIONS_LINE, "spring/plcLineNotificationsTemplate.xml");
		config.put(PLC_NOTIFICATIONS_CABINET, "spring/plcCabinetNotifications.xml");
		config.put(PLC_ERROR_REGISTER_CABINET, "spring/plcCabinetErrorRegister.xml");
		config.put(PLC_ERROR_REGISTER_LINE, "spring/plcLineErrorRegister.xml");
		config.put(PLC_REQUESTS, "spring/plcRequests.xml");
		config.put(PLC_PARAMETERS_CABINET, "spring/plcCabinetParameters.xml");
		config.put(PLC_PARAMETERS_LINE, "spring/plcLineParametersTemplate.xml");
		config.put(FLOW_CONTROL, "spring/flowControl.xml");
		config.put(PROPERTIES_PLACEHOLDER, "spring/propertyPlaceholderConfigurer.xml");
		config.put(BARCODE, "spring/barcode.xml");
//        config.put(ALERT_PLC_ACTIVATION_CROSSCHECK, "spring/alertPlcActivationCrossCheckTask.xml");

		// PLC variables descriptors for cabinet
		config.put(EDITABLE_PLC_CABINET_VARIABLES, "spring/descriptors/plcCabinetVariablesDescriptors.xml");

		// PLC variables descriptors for line [this is used in configuration panel for lines]
		config.put(EDITABLE_PLC_LINE_VARIABLES, "spring/descriptors/plcLineVariablesDescriptorsTemplate.xml");

		config.put(MESSAGES_MAPPING, "spring/messages.xml");
		config.put(DUPLICATED_CODE_FILTER, "spring/duplicatedCodeFilter.xml");
		config.put(MONITORING, "spring/monitoring.xml");
		config.put(SECURITY, "spring/security.xml");

		config.put(CRYPTO, "spring/crypto.xml");
		config.put(ERRORS_REPOSITORY, "spring/errorsRepository.xml");

		config.put(PRODUCTION_CONFIG, "spring/productionConfig.xml");

		config.put(MODEL_EDITABLE_PROPERTIES, "spring/descriptors/modelEditableProperties.xml");
		config.put(PLC_VAR_MAP, "spring/plcVarMap.xml");
	}
}
