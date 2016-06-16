package com.sicpa.standard.sasscl.messages;

import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;

public interface MessageEventKey {

	public static interface PLC {
		String ERROR_SENDING_PARAM = "PLC.ERR.SENT.PARAM";

		String EXCEPTION_FAIL_SAVE_VARIABLE_TO_FILE = "plc.save.file.fail";

		String PLC_STATE_NOT_RUNNING = "plc.not.running.state";

		/**
		 * WARNINGS Messages (CABINET)
		 */
		String PLC_WAR_JAVA_WARNING = "PLC.WARNING.JAVA_WARNING";
		String PLC_WAR_JAVA_CRITICAL_WARNING = "PLC.WARNING.JAVA_CRITICAL_WARNING";
		String PLC_WAR_COOLING_NOT_ACTIVATED = "PLC.WARNING.COOLING_NOT_ACTIVATED";
		String PLC_WAR_SOFTWARE_VERSION_FAULT = "PLC.WARNING.SOFTWARE_VERSION_FAULT";
		String PLC_WAR_PLC_TEMPERATURE_TO_HIGH = "PLC.WARNING.PLC.TEMPERATURE_TOO_HIGH";
		String PLC_WAR_TEMPERATURE_EE_CABINET = "PLC.WARNING.TEMPERATURE_EE_CABINET";
		String PLC_WAR_TEMPERATURE_AMBIANT = "PLC.WARNING.TEMPERATURE_AMBIANT";
		String PLC_WAR_RELATIVE_HUMIDITY_DEFAULT = "PLC.WARNING.RELATIVE_HUMIDITY_DEFAULT";
		String PLC_WAR_POWER_SUPPLY_DEFAULT = "PLC.WARNING.POWER_SUPPLY_DEFAULT";
		String PLC_WAR_BREAKER_CONTROL_DEFAULT = "PLC.WARNING.BREAKER_CONTROL_DEFAULT";
		String PLC_WAR_AIR_PRESSURE_DEFAULT = "PLC.WARNING.AIR_PRESSURE_DEFAULT";
		String PLC_WAR_DOOR_SWITCH_OCS_OPEN = "PLC.WARNING.DOOR_SWITCH_OCS_OPEN";
		String PLC_WAR_DOOR_SWITCH_EE_OPEN = "PLC.WARNING.DOOR_SWITCH_EE_OPEN";
		String PLC_WAR_COOLING_FAN1_EE_CAB_DEFAULT = "PLC.WARNING.COOLING_FAN1_EE_CAB_DEFAULT";
		String PLC_WAR_COOLING_FAN2_EE_CAB_DEFAULT = "PLC.WARNING.COOLING_FAN2_EE_CAB_DEFAULT";
		String PLC_WAR_PLC_FAULT = "PLC.WARNING.PLC_FAULT";

		/**
		 * WARNINGS Messages (CONVEYOR)
		 */
		String PLC_WAR_TRIGGER_SHIFT_FLAG = "PLC.WARNING.TRIGGER_SHIFT_FLAG";
		String PLC_WAR_INVALID_PARAMETER = "PLC.WARNING.INVALID_PARAMETER";
		String PLC_WAR_LABEL_APP_OR_AIR_DRYER_WARNING = "PLC.WARNING.LABEL_APP_OR_AIR_DRYER_WARNING";
		String PLC_WAR_DRS_ACQUISITION_ERROR = "PLC.WARNING.DRS_ACQUISITION_ERROR";
		String PLC_WAR_DRS_UNKNOWN_ANSWER = "PLC.WARNING.DRS_UNKNOWN_ANSWER";
		String PLC_WAR_DRS_FIFOS_FAULT = "PLC.WARNING.DRS_FIFOS_FAULT";
		String PLC_WAR_DRS_NOT_CONNECTED = "PLC.WARNING.DRS_NOT_CONNECTED";
		String PLC_WAR_ENCODER_FAULT = "PLC.WARNING.ENCODER_FAULT";
		String PLC_WAR_TEMPERATURE_IJ_CABINET = "PLC.WARNING.TEMPERATURE_IJ_CABINET";
		String PLC_WAR_TEMPERATURE_IJ_INK = "PLC.WARNING.TEMPERATURE_IJ_INK";
		String PLC_WAR_EXT_AIR_DRYER_WARNING = "PLC.WARNING.EXT_AIR_DRYER_WARNING";
		String PLC_WAR_DOOR_SWITCH_IJ_OPEN = "PLC.WARNING.DOOR_SWITCH_IJ_OPEN";
		String PLC_WAR_COOLING_FAN1_PRINTER_DEFAULT = "PLC.WARNING.COOLING_FAN1_PRINTER_DEFAULT";
		String PLC_WAR_COOLING_FAN2_PRINTER_DEFAULT = "PLC.WARNING.COOLING_FAN2_PRINTER_DEFAULT";

		/**
		 * ERRORS Messages (CABINET)
		 * 
		 */
		String PLC_ERR_JAVA_ERROR = "PLC.ERROR.JAVA_ERROR";
		String PLC_ERR_LIFE_CHECK = "PLC.ERROR.LIFE_CHECK";
		String PLC_ERR_PLC_FAULT = "PLC.ERROR.PLC_FAULT";
		String PLC_ERR_COOLING_NOT_ACTIVATED = "PLC.ERROR.COOLING_NOT_ACTIVATED";
		String PLC_ERR_PLC_TEMPERATURE_TOO_HIGH = "PLC.ERROR.TEMPERATURE_TOO_HIGH";
		String PLC_ERR_EMERGENCY_STOP = "PLC.ERROR.EMERGENCY_STOP";
		String PLC_ERR_TEMPERATURE_EE_CABINET = "PLC.ERROR.TEMPERATURE_EE_CABINET";
		String PLC_ERR_TEMPERATURE_AMBIANT = "PLC.ERROR.TEMPERATURE_AMBIANT";
		String PLC_ERR_POWER_SUPPLY_DEFAULT = "PLC.ERROR.POWER_SUPPLY_DEFAULT";
		String PLC_ERR_BREAKER_CONTROL_DEFAULT = "PLC.ERROR.BREAKER_CONTROL_DEFAULT";
		String PLC_ERR_AIR_PRESSURE_DEFAULT = "PLC.ERROR.AIR_PRESSURE_DEFAULT";
		String PLC_ERR_DOOR_SWITCH_OCS_OPEN = "PLC.ERROR.DOOR_SWITCH_OCS_OPEN";
		String PLC_ERR_DOOR_SWITCH_EE_OPEN = "PLC.ERROR.DOOR_SWITCH_EE_OPEN";

		/**
		 * ERRORS Messages (CONVEYOR)
		 */
		String PLC_ERR_TRIGGER_FAULT = "PLC.ERROR.TRIGGER_FAULT";
		String PLC_ERR_MNFCT_LINE_ERROR = "PLC.ERROR.MNFCT_LINE_ERROR";
		String PLC_ERR_INVALID_PARAMETER = "PLC.ERROR.INVALID_PARAMETER";
		String PLC_ERR_LABEL_APP_OR_AIR_DRYER_NOT_READY = "PLC.ERROR.LABEL_APP_OR_AIR_DRYER_NOT_READY";
		String PLC_ERR_LABEL_APP_OR_AIR_DRYER_FAULT = "PLC.ERROR.LABEL_APP_OR_AIR_DRYER_FAULT";
		String PLC_ERR_UNKNOWN_DRS = "PLC.ERROR.UNKNOWN_DRS";
		String PLC_ERR_DRS_NOT_CONNECTED = "PLC.ERROR.DRS_NOT_CONNECTED";
		String PLC_ERR_TOO_CONSECUTIVE_INVALID_CODES = "PLC.ERROR.TOO_CONSECUTIVE_INVALID_CODES";
		String PLC_ERR_EJECTION_SMALL_RATE_TOO_HIGH = "PLC.ERROR.EJECTION_SMALL_RATE_TOO_HIGH";
		String PLC_ERR_EJECTION_LARGE_RATE_TOO_HIGH = "PLC.ERROR.EJECTION_LARGE_RATE_TOO_HIGH";
		String PLC_ERR_VALID_CODE_IN_EXPORT_MODE = "PLC.ERROR.VALID_CODE_IN_EXPORT_MODE";
		String PLC_ERR_TEMPERATURE_IJ_CABINET = "PLC.ERROR.TEMPERATURE_IJ_CABINET";
		String PLC_ERR_TEMPERATURE_IJ_INK = "PLC.ERROR.TEMPERATURE_IJ_INK";
		String PLC_ERR_DOOR_SWITCH_IJ_OPEN = "PLC.ERROR.DOOR_SWITCH_IJ_OPEN";

		/**
		 * SECURE MODULE
		 */

		String UNABLE_AUTHENTICATE_USER = "UNABLE.AUTHENTICATE.USER";
	}

	public static interface Alert {
		String TOO_MUCH_CAMERA_ERROR = "CAMERA.ERR.TOO_MUCH_ERROR";
		String DUPLICATED_CODE = "CAMERA.ERR.DUPLICATED_CODE";
		String CAMERA_TRIGGER_TOO_FAST = "CAMERA.ERR.TRIGGER.TOO_FAST";
		String TOO_MUCH_CAMERA_IDLE_TIME = "CAMERA.ERR.TOO_MUCH_IDLE_TIME";
		String PLC_ACTIVATION_CROSS_CHECK_FAILED = "plc.activation.crosscheck.failed";
		String SKU_RECOGNITIONTOO_MANY_UNKNOWN = "sku.recognition.too.many.unknown";
	}

	public static interface Camera {
		String CAMERA_FAIL_LOAD_JOB = "CAMERA.ERR.LOAD.JOB.FAILED";
	}

	public static interface Activation {
		String EXCEPTION_NOT_AUTHENTICATED = "activation.err.notAuthenticated";
		String EXCEPTION_CODE_TYPE_MISMATCH = "activation.err.type.mismatch";
		String EXCEPTION_CODE_IN_EXPORT = "activation.err.export.code";
		String EXCEPTION_NO_AUTHENTICATOR = "activation.err.noAuth";
	}

	public static interface Simulator {
		String CAMERA = "simulator.camera";
		String PRINTER = "simulator.printer";
		String PLC = "simulator.plc";
		String REMOTE_SERVER = "simulator.remoteserver";
		String ENCODER = "simulator.encoder";
		String AUTHENTICATOR = "simulator.authenticator";
		String CAMERA_READ_CODE_FROM_FILE = "simulator.camera.from.file";
		String BRS = "simulator.brs";
	}

	public static interface ProductionParameters {
		String NONE_AVAILABLE = "sku.none.available";
		String NO_LONGER_AVAILABLE = "production.parameters.notAvailable";
	}

	public static interface Storage {
		String ERROR_CANNOT_SAVE = "storage.save.fail";
		String ERROR_CANNOT_LOAD = "storage.load.fail";
		String ERROR_PACKAGE_FAIL = "storage.package.fail";
		String ERROR_CLEANUP_FAIL = "storage.cleanup.fail";
	}

	public static interface Production {
		String ERROR_MAX_SERIALIZATION_ERRORS = "product.err.max.serialization";
	}

	public static interface RemoteServer {
		String MAX_DOWNTIME = "remoteServer.maxDownTime";
	}

	public static interface FlowControl {
		String START_TIMEOUT = "production.start.timeout";
		String START_FAILED = "production.start.failed";
		String UNCAUGHT_EXCEPTION = "exception.uncaugh";

		String DEVICES_CONNECT_FAILED = "production.devices.connect.exception";
	}

	public static interface ModelEditing {
		String ERROR_FAILED_TO_SAVE_BEAN_PROPERTY = "view.property.save.fail";
	}

	public static interface BRS {
		String BRS_WRONG_SKU = "brs.skucheck.wrong.sku";
		String BRS_TOO_MANY_UNREAD_BARCODES_WARNING = "brs.unread.toomany.warning";
		String BRS_TOO_MANY_UNREAD_BARCODES_ERROR = "brs.unread.toomany.error";
	}

	public static interface MAINTENACE {

		String TOO_MANY_PRODUCTS_COUNTED_ERROR = "maintenace.toomany.products.error";
	}

	public static interface SkuCheck {
		String UNKOWN_SKU = "sku.check.unknown";
		String WRONG_SKU_CRITICAL = "sku.check.wrong.critical";
		String WRONG_SKU_SMALL = "sku.check.wrong.small";
		String OK = "sku.check.ok";
		String UNREAD = "sku.check.unread";
	}

	public static interface Coding {
		String ERROR_NO_ENCODERS_IN_STORAGE = "coding.no.encoder";
		String ERROR_GETTING_CODES_FROM_ENCODER = "coding.getCode.fail";
		String INVALID_ENCODER = "encoder.invalid.move.quarantine";
		String FAILED_TO_PROVIDE_CODES = "encoder.code.provide.fail";
	}

	public static interface Printer extends PrinterMessageId {
		String INK_LOW = "PRINTER.W_INK_LOW";
		String SUMP_EMPTY = "PRINTER.F_SUMP_EMPTY";
		String MC_MODN_FALL_BACK = "PRINTER.W_MC_MODN_FALL_BACK";
		String GUTTER_DRY = "PRINTER.F_GUTTER_DRY";
		String MAKEUP_LOW = "PRINTER.W_MAKEUP_LOW";
		String INK_HIGH = "PRINTER.F_INK_HIGH";
		String MAKEUP_HIGH = "PRINTER.F_MAKEUP_HIGH";
		String MAKEUP_EMPTY = "PRINTER.F_MAKEUP_EMPTY";
		String WAKEUP_ON = "PRINTER.S_WAKEUP_ON";
		String BLEEDING_DAMPER = "PRINTER.W_BLEEDING_DAMPER";
		String PSB_BATTERY_LOW = "PRINTER.W_PSB_BATTERY_LOW";
		String MAINTENANCE_DUE = "PRINTER.W_MAINTENANCE_DUE";
		String MAINTENANCE_2HOURS = "PRINTER.W_MAINTENANCE_2HOURS";
		String MAINTENANCE_NOW = "PRINTER.F_MAINTENANCE_NOW";
	}

	public static interface DevicesController {
		String FAILED_TO_START_DEVICE = "device.start.fail";
		String FAILED_TO_CONNECT_DEVICE = "device.connect.fail";
	}

	public static interface BIS {
		String BIS_UNKNOWN_SKU = "bis.unknownSku.exceedThreshold";
		String BIS_ALERT = "bis.alert";
		String BIS_UNKNOWN_SKU_EXCEED_THRESHOLD = "bis.unknownSku.exceedWindowThreshold";
	}

}
