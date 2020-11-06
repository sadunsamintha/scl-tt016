package com.sicpa.standard.sasscl.messages;

import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR_DEVICE;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR_DISPLAY;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.IGNORE;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.LOG;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.WARNING;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.messages.DefaultMessagesMapping;
import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.sasscl.messages.MessageEventKey.Activation;
import com.sicpa.standard.sasscl.messages.MessageEventKey.Alert;
import com.sicpa.standard.sasscl.messages.MessageEventKey.BIS;
import com.sicpa.standard.sasscl.messages.MessageEventKey.BRS;
import com.sicpa.standard.sasscl.messages.MessageEventKey.Camera;
import com.sicpa.standard.sasscl.messages.MessageEventKey.Coding;
import com.sicpa.standard.sasscl.messages.MessageEventKey.DevicesController;
import com.sicpa.standard.sasscl.messages.MessageEventKey.FlowControl;
import com.sicpa.standard.sasscl.messages.MessageEventKey.MAINTENACE;
import com.sicpa.standard.sasscl.messages.MessageEventKey.ModelEditing;
import com.sicpa.standard.sasscl.messages.MessageEventKey.PLC;
import com.sicpa.standard.sasscl.messages.MessageEventKey.Printer;
import com.sicpa.standard.sasscl.messages.MessageEventKey.Production;
import com.sicpa.standard.sasscl.messages.MessageEventKey.ProductionParameters;
import com.sicpa.standard.sasscl.messages.MessageEventKey.RemoteServer;
import com.sicpa.standard.sasscl.messages.MessageEventKey.Simulator;
import com.sicpa.standard.sasscl.messages.MessageEventKey.SkuCheck;
import com.sicpa.standard.sasscl.messages.MessageEventKey.SkuRecognition;
import com.sicpa.standard.sasscl.messages.MessageEventKey.Storage;

public class SASDefaultMessagesMapping extends DefaultMessagesMapping {
	private static final Logger logger = LoggerFactory.getLogger(SASDefaultMessagesMapping.class);
	public static final String PREFIX_OVERRIDING_PROPERTIES = "messages.mapping.custom.";

	private Properties overridingProperties;

	public void init() {
		PropertiesUtils.extractAndDo(PREFIX_OVERRIDING_PROPERTIES, overridingProperties,
				(key, val) -> overrideMessageMapping(key, val));
	}

	protected void populateMap() {

		addEntry(PLC.EXCEPTION_FAIL_SAVE_VARIABLE_TO_FILE, "[PLC_05]", WARNING);
		addEntry(PLC.PLC_STATE_NOT_RUNNING, "[PLC_06]", ERROR);
		addEntry(PLC.ERROR_SENDING_PARAM, "[PLC_07]", WARNING);
		addEntry(PLC.PLC_WAR_JAVA_WARNING, "[PLC_08]", IGNORE);
		addEntry(PLC.PLC_WAR_JAVA_CRITICAL_WARNING, "[PLC_09]", IGNORE);
		addEntry(PLC.PLC_WAR_COOLING_NOT_ACTIVATED, "[PLC_10]", WARNING);
		addEntry(PLC.PLC_WAR_SOFTWARE_VERSION_FAULT, "[PLC_11]", WARNING);
		addEntry(PLC.PLC_WAR_PLC_TEMPERATURE_TO_HIGH, "[PLC_12]", WARNING);
		addEntry(PLC.PLC_WAR_TEMPERATURE_EE_CABINET, "[PLC_13]", WARNING);
		addEntry(PLC.PLC_WAR_TEMPERATURE_AMBIANT, "[PLC_14]", WARNING);
		addEntry(PLC.PLC_WAR_RELATIVE_HUMIDITY_DEFAULT, "[PLC_15]", WARNING);
		addEntry(PLC.PLC_WAR_POWER_SUPPLY_DEFAULT, "[PLC_16]", WARNING);
		addEntry(PLC.PLC_WAR_BREAKER_CONTROL_DEFAULT, "[PLC_17]", WARNING);
		addEntry(PLC.PLC_WAR_AIR_PRESSURE_DEFAULT, "[PLC_18]", WARNING);
		addEntry(PLC.PLC_WAR_DOOR_SWITCH_EE_OPEN, "[PLC_20]", WARNING);
		addEntry(PLC.PLC_WAR_COOLING_FAN1_EE_CAB_DEFAULT, "[PLC_21]", WARNING);
		addEntry(PLC.PLC_WAR_COOLING_FAN2_EE_CAB_DEFAULT, "[PLC_22]", WARNING);
		addEntry(PLC.PLC_WAR_PLC_FAULT, "[PLC_23]", LOG);
		addEntry(PLC.PLC_WAR_TRIGGER_SHIFT_FLAG, "[PLC_24]", WARNING);
		addEntry(PLC.PLC_WAR_INVALID_PARAMETER, "[PLC_25]", WARNING);
		addEntry(PLC.PLC_WAR_LABEL_APP_OR_AIR_DRYER_WARNING, "[PLC_26]", WARNING);
		addEntry(PLC.PLC_WAR_DRS_ACQUISITION_ERROR, "[PLC_27]", WARNING);
		addEntry(PLC.PLC_WAR_DRS_UNKNOWN_ANSWER, "[PLC_28]", WARNING);
		addEntry(PLC.PLC_WAR_DRS_FIFOS_FAULT, "[PLC_29]", WARNING);
		addEntry(PLC.PLC_WAR_DRS_NOT_CONNECTED, "[PLC_30]", WARNING);
		addEntry(PLC.PLC_WAR_ENCODER_CHECK_THRESHOLD, "[PLC_31]", WARNING);
		addEntry(PLC.PLC_WAR_TEMPERATURE_IJ_CABINET, "[PLC_32]", WARNING);
		addEntry(PLC.PLC_WAR_TEMPERATURE_IJ_INK, "[PLC_33]", WARNING);
		addEntry(PLC.PLC_WAR_EXT_AIR_DRYER_WARNING, "[PLC_34]", WARNING);
		addEntry(PLC.PLC_WAR_DOOR_SWITCH_IJ_OPEN, "[PLC_35]", WARNING);
		addEntry(PLC.PLC_WAR_COOLING_FAN1_PRINTER_DEFAULT, "[PLC_36]", WARNING);
		addEntry(PLC.PLC_WAR_COOLING_FAN2_PRINTER_DEFAULT, "[PLC_37]", WARNING);
		addEntry(PLC.PLC_ERR_JAVA_ERROR, "[PLC_38]", IGNORE);
		addEntry(PLC.PLC_ERR_LIFE_CHECK, "[PLC_39]", ERROR);
		addEntry(PLC.PLC_ERR_PLC_FAULT, "[PLC_40]", ERROR);
		addEntry(PLC.PLC_ERR_COOLING_NOT_ACTIVATED, "[PLC_41]", ERROR);
		addEntry(PLC.PLC_ERR_PLC_TEMPERATURE_TOO_HIGH, "[PLC_42]", ERROR);
		addEntry(PLC.PLC_ERR_EMERGENCY_STOP, "[PLC_43]", ERROR);
		addEntry(PLC.PLC_ERR_TEMPERATURE_EE_CABINET, "[PLC_44]", ERROR);
		addEntry(PLC.PLC_ERR_TEMPERATURE_AMBIANT, "[PLC_45]", ERROR);
		addEntry(PLC.PLC_ERR_POWER_SUPPLY_DEFAULT, "[PLC_46]", ERROR);
		addEntry(PLC.PLC_ERR_BREAKER_CONTROL_DEFAULT, "[PLC_47]", ERROR);
		addEntry(PLC.PLC_ERR_AIR_PRESSURE_DEFAULT, "[PLC_48]", ERROR);
		addEntry(PLC.PLC_ERR_DOOR_SWITCH_EE_OPEN, "[PLC_50]", ERROR);
		addEntry(PLC.PLC_ERR_TRIGGER_FAULT, "[PLC_51]", ERROR);
		addEntry(PLC.PLC_ERR_MNFCT_LINE_ERROR, "[PLC_51]", ERROR);
		addEntry(PLC.PLC_ERR_INVALID_PARAMETER, "[PLC_52]", ERROR);
		addEntry(PLC.PLC_ERR_LABEL_APP_OR_AIR_DRYER_NOT_READY, "[PLC_53]", ERROR);
		addEntry(PLC.PLC_ERR_LABEL_APP_OR_AIR_DRYER_FAULT, "[PLC_54]", ERROR);
		addEntry(PLC.PLC_ERR_UNKNOWN_DRS, "[PLC_55]", WARNING);
		addEntry(PLC.PLC_ERR_DRS_NOT_CONNECTED, "[PLC_56]", WARNING);
		addEntry(PLC.PLC_ERR_TOO_CONSECUTIVE_INVALID_CODES, "[PLC_57]", ERROR);
		addEntry(PLC.PLC_ERR_EJECTION_SMALL_RATE_TOO_HIGH, "[PLC_58]", ERROR);
		addEntry(PLC.PLC_ERR_EJECTION_LARGE_RATE_TOO_HIGH, "[PLC_59]", ERROR);
		addEntry(PLC.PLC_ERR_VALID_CODE_IN_EXPORT_MODE, "[PLC_60]", ERROR);
		addEntry(PLC.PLC_ERR_TEMPERATURE_IJ_CABINET, "[PLC_61]", ERROR);
		addEntry(PLC.PLC_ERR_TEMPERATURE_IJ_INK, "[PLC_62]", ERROR);
		addEntry(PLC.PLC_ERR_DOOR_SWITCH_IJ_OPEN, "[PLC_63]", ERROR);
		addEntry(PLC.PLC_ERR_WIPER_LOC_AFTER_CAMERA, "[PLC_79]", ERROR);
		addEntry(PLC.PLC_ERR_ENCODER_CHECK_THRESHOLD, "[PLC_80]", ERROR);
		
		addEntry(PLC.PLC_ERR_PRINTER_LOC_AFTER_CAM, "[PLC_64]", ERROR);
		addEntry(PLC.PLC_ERR_INVALID_PROD_CTRL_EJ_DISTANCE, "[PLC_65]", ERROR);
		addEntry(PLC.PLC_ERR_AUDIO_VISUAL_LOC_BEFORE_EJ, "[PLC_66]", ERROR);
		addEntry(PLC.PLC_ERR_SECOND_LABEL_APP_NOT_READY, "[PLC_67]", ERROR);
		addEntry(PLC.PLC_ERR_SECOND_LABEL_APP_FAULT, "[PLC_68]", ERROR);
		addEntry(PLC.PLC_ERR_DOUBLE_LABEL_APP_ERROR, "[PLC_69]", ERROR);
		addEntry(PLC.PLC_ERR_TWO_MODES_SET_AT_SAMETIME, "[PLC_70]", ERROR);
		
		addEntry(PLC.PLC_WAR_CAM_LOC_AFT_EJ, "[PLC_71]", WARNING);
		addEntry(PLC.PLC_WAR_INCOHERENT_SMALL_REJ_LIMIT, "[PLC_72]", WARNING);
		addEntry(PLC.PLC_WAR_INCOHERENT_LARGE_REJ_LIMIT, "[PLC_73]", WARNING);
		addEntry(PLC.PLC_WAR_SECOND_LABEL_APP_OR_AIR_DRYER_WARNING, "[PLC_74]", WARNING);
		addEntry(PLC.PLC_WAR_INVALIDTYP_FOR_PRDCTRL_EJ, "[PLC_75]", WARNING);
		addEntry(PLC.PLC_WAR_INCOHERENT_LIFE_CHCK_TIMEOUT, "[PLC_76]", WARNING);
		addEntry(PLC.PLC_WAR_PC_DISTANCE_LOC_AFTER_PC_EJ, "[PLC_77]", WARNING);
		addEntry(PLC.PLC_WAR_INCOHERENT_JAVA_NOTIFICATION_DISTANCE, "[PLC_78]", WARNING);
		
		addEntry(PLC.PLC_WAR_UPS_LOW_BATTERY_ALARM, "[PLC_81]", WARNING);
		addEntry(PLC.PLC_WAR_UPS_BATTERY_MODE, "[PLC_82]", WARNING);
		addEntry(PLC.PLC_WAR_UPS_BATTERY_CHARGING, "[PLC_83]", WARNING);
		
		addEntry(Alert.TOO_MANY_CAMERA_ERROR, "[ALT_01]", ERROR);
		addEntry(Alert.DUPLICATED_CODE, "[ALT_03]", ERROR);
		addEntry(Alert.CAMERA_TRIGGER_TOO_FAST, "[ALT_04]", WARNING);
		addEntry(Alert.TOO_MUCH_CAMERA_IDLE_TIME, "[ALT_05]", ERROR);
		addEntry(Alert.PLC_ACTIVATION_CROSS_CHECK_FAILED, "[ALT_06]", ERROR);
		addEntry(Alert.TOO_MANY_CAMERA_WARNING, "[ALT_07]", WARNING);
		addEntry(Alert.PLC_TOO_MANY_NO_CAPS, "[ALT_08]", WARNING);


		addEntry(Camera.CAMERA_FAIL_LOAD_JOB, "[CA_01]", ERROR);
		addEntry(Activation.EXCEPTION_NOT_AUTHENTICATED, "[ACT_01]", ERROR);
		addEntry(Activation.EXCEPTION_CODE_TYPE_MISMATCH, "[ACT_02]", ERROR);
		addEntry(Activation.EXCEPTION_CODE_IN_EXPORT, "[ACT_03]", ERROR);
		addEntry(Activation.EXCEPTION_NO_AUTHENTICATOR, "[ACT_04]", ERROR);

		addEntry(SkuCheck.UNKOWN_SKU, "[SKC_01]", WARNING);
		addEntry(SkuCheck.WRONG_SKU_CRITICAL, "[SKC_02]", WARNING);
		addEntry(SkuCheck.WRONG_SKU_SMALL, "[SKC_03]", WARNING);
		addEntry(SkuCheck.UNREAD, "[SKC_04]", IGNORE);
		addEntry(SkuCheck.OK, "[SKC_05]", IGNORE);

		addEntry(Simulator.CAMERA, "[SIMU1]", WARNING);
		addEntry(Simulator.PLC, "[SIMU2]", WARNING);
		addEntry(Simulator.REMOTE_SERVER, "[SIMU3]", WARNING);
		addEntry(Simulator.PRINTER, "[SIMU4]", WARNING);
		addEntry(Simulator.ENCODER, "[SIMU5]", WARNING);
		addEntry(Simulator.AUTHENTICATOR, "[SIMU6]", WARNING);
		addEntry(Simulator.CAMERA_READ_CODE_FROM_FILE, "[SIMU7]", WARNING);
		addEntry(Simulator.BRS, "[SIMU8]", WARNING);

		addEntry(ProductionParameters.NO_LONGER_AVAILABLE, "[SKU_01]", WARNING);

		// ------------------

		addEntry(FlowControl.UNCAUGHT_EXCEPTION, "[EXC_GEN_001]", ERROR_DISPLAY);

		addEntry(Storage.ERROR_CANNOT_SAVE, "[STOR_01]", ERROR);
		addEntry(Storage.ERROR_CANNOT_LOAD, "[STOR_02]", WARNING);
		addEntry(Storage.ERROR_PACKAGE_FAIL, "[STOR_03]", WARNING);
		addEntry(Storage.ERROR_CLEANUP_FAIL, "[STOR_04]", WARNING);

		addEntry(Production.ERROR_MAX_SERIALIZATION_ERRORS, "[PROD_01]", ERROR);

		addEntry(RemoteServer.MAX_DOWNTIME, "[PROC_01]", ERROR);
		addEntry(FlowControl.START_TIMEOUT, "[PROC_02]", ERROR);
		addEntry(ProductionParameters.NO_LONGER_AVAILABLE, "[PROC_03]", ERROR);
		addEntry(ProductionParameters.NONE_AVAILABLE, "[PROC_04]", ERROR);
		addEntry(FlowControl.START_FAILED, "[PROC_05]", ERROR);
		addEntry(ProductionParameters.NO_PERMISSION_PRODUCTION_MODE, "[PROC_06]", WARNING);
		

		addEntry(ModelEditing.ERROR_FAILED_TO_SAVE_BEAN_PROPERTY, "[VW_01]");

		addEntry(DevicesController.FAILED_TO_CONNECT_DEVICE, "[DEV_02]", ERROR);
		addEntry(DevicesController.FAILED_TO_START_DEVICE, "[DEV_03]", ERROR);
		addEntry(FlowControl.DEVICES_CONNECT_FAILED, "[DEV_01]", ERROR);
		addEntry(DevicesController.DEVICE_DISCONNECT_WARNING, "[DEV_04]", WARNING);

		addEntry(BRS.BRS_WRONG_SKU, "[BRS_01]", ActionMessageType.ERROR);
		addEntry(BRS.BRS_TOO_MANY_UNREAD_BARCODES_WARNING, "[BRS_02]", WARNING);
		addEntry(BRS.BRS_TOO_MANY_UNREAD_BARCODES_ERROR, "[BRS_03]", ERROR);

		addEntry(MAINTENACE.TOO_MANY_PRODUCTS_COUNTED_ERROR, "[MAINTENACE_01]", ERROR);

		addEntry(BIS.BIS_ALERT, "[BIS_04]", WARNING);

		addEntry(Printer.CHARGE_FAULT, "[PR_01]", ERROR_DEVICE);
		addEntry(Printer.GUTTER_FAULT, "[PR_02]", ERROR_DEVICE);
		addEntry(Printer.HIGH_VOLTAGE_FAULT, "[PR_03]", ERROR_DEVICE);
		addEntry(Printer.INK_CARTRIDGE_TOO_LOW, "[PR_04]", WARNING);
		addEntry(Printer.INK_MAKE_UP_TOO_LOW, "[PR_05]", WARNING);
		addEntry(Printer.INK_RESERVOIR_LESS_THAN_24_HOURS, "[PR_06]", WARNING);
		addEntry(Printer.INK_RESERVOIR_LESS_THAN_2_HOURS, "[PR_07]", WARNING);
		addEntry(Printer.INK_RESERVOIR_TIME_OUT, "[PR_08]", ERROR_DEVICE);
		addEntry(Printer.INK_SYSTEM_FAULT, "[PR_09]", ERROR_DEVICE);
		addEntry(Printer.INK_VISCOSITY_OUT_OF_RANGE, "[PR_10]", WARNING);
		addEntry(Printer.MAKE_UP_CARTRIDGE_LOW, "[PR_11]", WARNING);
		addEntry(Printer.PUMP_EXCEEDING_NORMAL_RANGE, "[PR_12]", ERROR_DEVICE);
		addEntry(Printer.RESERVOIR_TOO_LOW, "[PR_13]", ERROR_DEVICE);
		addEntry(Printer.SEQUENCE_OFF, "[PR_14]", ERROR_DEVICE);
		addEntry(Printer.VISCOMETER_FAULT, "[PR_15]", WARNING);
		addEntry(Printer.WATCHDOG_RESET, "[PR_16]", ERROR_DEVICE);
		addEntry(Printer.NOT_READY_TO_PRINT, "[PR_17]", ERROR_DEVICE);
		addEntry(Printer.BLANK_MESSAGE, "[PR_18]", WARNING);
		addEntry(Printer.MAKE_UP_EMPTY, "[PR_19]", WARNING);
		addEntry(Printer.STROKE_RATE_TOO_FAST, "[PR_20]", WARNING);
		addEntry(Printer.INVALID_CONTROL_CHARACTER, "[PR_21]", WARNING);
		addEntry(Printer.MESSAGE_LONGER_THAN_APERTURE, "[PR_22]", WARNING);
		addEntry(Printer.PRINT_RATE_TOO_FAST, "[PR_23]", WARNING);
		addEntry(Printer.INVALID_LOGO, "[PR_24]", WARNING);
		addEntry(Printer.MESSAGE_NUMBER_NOT_IN_SEQUENCE, "[PR_25]", WARNING);
		addEntry(Printer.MESSAGE_CHECKSUM_INCORRECT, "[PR_26]", WARNING);
		addEntry(Printer.READY_TO_PRINT, "[PR_27]", WARNING);

		addEntry(Printer.GUTTER_DRY, "[PR_A01]", ERROR_DEVICE);
		addEntry(Printer.INK_LOW, "[PR_A02]", WARNING);
		addEntry(Printer.SUMP_EMPTY, "[PR_A03]", ERROR_DEVICE);
		addEntry(Printer.MC_MODN_FALL_BACK, "[PR_A04]", WARNING);
		addEntry(Printer.MAKEUP_LOW, "[PR_A05]", WARNING);
		addEntry(Printer.INK_HIGH, "[PR_A06]", WARNING);
		addEntry(Printer.MAKEUP_HIGH, "[PR_A07]", WARNING);
		addEntry(Printer.MAKEUP_EMPTY, "[PR_A08]", WARNING);
		addEntry(Printer.WAKEUP_ON, "[PR_A09]", WARNING);
		addEntry(Printer.BLEEDING_DAMPER, "[PR_A10]", WARNING);
		addEntry(Printer.PSB_BATTERY_LOW, "[PR_A11]", WARNING);
		addEntry(Printer.MAINTENANCE_DUE, "[PR_A12]", WARNING);
		addEntry(Printer.MAINTENANCE_2HOURS, "[PR_A13]", WARNING);
		addEntry(Printer.MAINTENANCE_NOW, "[PR_A14]", ERROR_DEVICE);

		// Leibinger messages
		addEntry(Printer.NO_FAULT, "[PR_28]", WARNING);
		addEntry(Printer.MAILING_BUFFER_FULL, "[PR_29]", WARNING);
		addEntry(Printer.LOW_BATTERY_VOLTAGE, "[PR_30]", WARNING);
		addEntry(Printer.PRINTGO_ERROR, "[PR_31]", WARNING);
		addEntry(Printer.TOO_MUCH_STROKE, "[PR_32]", WARNING);
		addEntry(Printer.POWER_IN_OVERLOAD, "[PR_33]", ERROR_DEVICE);
		addEntry(Printer.FAULTY_VISCO_MSMT, "[PR_34]", WARNING);
		addEntry(Printer.INK_TOO_THICK, "[PR_35]", WARNING);
		addEntry(Printer.INK_TOO_THIN, "[PR_36]", WARNING);
		addEntry(Printer.INK_PRESSURE_FAULT, "[PR_37]", ERROR_DEVICE);
		addEntry(Printer.PRESSURE_FAULT, "[PR_38]", ERROR_DEVICE);
		addEntry(Printer.INK_FLOW_SENSOR, "[PR_39]", ERROR_DEVICE);
		addEntry(Printer.INK_TANK_LOW, "[PR_40]", WARNING);
		addEntry(Printer.SOLVENT_TANK_LOW, "[PR_41]", WARNING);
		addEntry(Printer.HYDRAULIC_LEAKAGE, "[PR_42]", ERROR_DEVICE);
		addEntry(Printer.INK_TYPE_NOT_SUPPORTED, "[PR_43]", WARNING);
		addEntry(Printer.CHARGE_DIRTY, "[PR_44]", ERROR_DEVICE);
		addEntry(Printer.PHASING_ERROR, "[PR_45]", ERROR_DEVICE);
		addEntry(Printer.DROP_BREAKOFF_POINT, "[PR_46]", WARNING);
		addEntry(Printer.PHASING_WARNING, "[PR_47]", WARNING);
		addEntry(Printer.NOZZLE_OC_ERROR, "[PR_48]", ERROR_DEVICE);
		addEntry(Printer.MOTOR_DIRECTION_ERROR, "[PR_49]", ERROR_DEVICE);
		addEntry(Printer.NOZZLE_ALREADY_OPEN, "[PR_50]", ERROR_DEVICE);
		addEntry(Printer.HV_CURRENT_TOO_HIGH, "[PR_51]", ERROR_DEVICE);
		addEntry(Printer.STROKE_GO, "[PR_52]", WARNING);
		addEntry(Printer.PRINTING_SPEED_TOO_HIGH, "[PR_53]", WARNING);
		addEntry(Printer.DROP_CALCULATION_TOO_SLOW, "[PR_54]", WARNING);
		addEntry(Printer.MAILING_BUFFER_EMPTY, "[PR_55]", ERROR_DEVICE);
		addEntry(Printer.PRINTGO_DISTANCE, "[PR_56]", WARNING);
		addEntry(Printer.CHARGE_VOLTAGE_OVERLOAD, "[PR_57]", ERROR_DEVICE);
		addEntry(Printer.PIEZO_VOLTAGE_OVERLOAD, "[PR_58]", ERROR_DEVICE);
		addEntry(Printer.SERVICE_REQUIRED, "[PR_59]", WARNING);
		addEntry(Printer.HEAD_COVER_OPEN, "[PR_60]", ERROR_DEVICE);
		addEntry(Printer.NOZZLE_MOVES_UNCONTROLLED, "[PR_61]", ERROR_DEVICE);
		addEntry(Printer.NOZZLE_NOT_CLOSED, "[PR_62]", WARNING);
		addEntry(Printer.CANT_OPEN_NOZZLE, "[PR_63]", ERROR_DEVICE);
		addEntry(Printer.CANT_CLOSE_NOZZLE, "[PR_64]", WARNING);
		addEntry(Printer.NO_PHASING, "[PR_65]", ERROR_DEVICE);
		addEntry(Printer.CANT_LOAD_JOB, "[PR_66]", ERROR_DEVICE);
		addEntry(Printer.PRINTSTART_NOT_POSSIBLE, "[PR_67]", ERROR_DEVICE);
		addEntry(Printer.ACTIVATE_NEW_INK, "[PR_68]", WARNING);
		addEntry(Printer.ACTIVATE_NEW_SOLVENT, "[PR_69]", WARNING);
		addEntry(Printer.INK_JET_LOCKED, "[PR_70]", ERROR_DEVICE);
		addEntry(Printer.INK_REFILLED_WITHOUT_ACTIVATION, "[PR_71]", WARNING);
		addEntry(Printer.SOLVENT_REFILLED_WITHOUT_ACTIVATION, "[PR_72]", WARNING);
		addEntry(Printer.INK_ACTIVATION_OK, "[PR_73]", WARNING);
		addEntry(Printer.SOLVENT_ACTIVATION_OK, "[PR_74]", WARNING);
		addEntry(Printer.NOZZLE_NOT_OPENED, "[PR_75]", ERROR_DEVICE);
		addEntry(Printer.PRINTER_STOPPED_DURING_PRODUCTION, "[PR_76]", ERROR);
		addEntry(Printer.JOB_FILE_ERROR, "[PR_77]", ERROR);
		addEntry(Printer.JOB_LIST_EMPTY, "[PR_78]", WARNING);
		addEntry(Printer.PRINT_GO_DELAY_TOO_SHORT, "[PR_79]", WARNING);

		addEntry(Coding.ERROR_NO_ENCODERS_IN_STORAGE, "[COD_01]", ERROR);
		addEntry(Coding.ERROR_GETTING_CODES_FROM_ENCODER, "[COD_02]", ERROR);
		addEntry(Coding.INVALID_ENCODER, "[COD_03]", ERROR);
		addEntry(Coding.FAILED_TO_PROVIDE_CODES, "[COD_04]", ERROR);

		addEntry(SkuRecognition.UNEXPECTED_SKU_CHANGED, "[SKU_REC_01]", WARNING);
	}

	private void overrideMessageMapping(String key, String type) {
		try {
			logger.info("overriding messages mapping " + key + ":" + type);
			add(key, createMessageType(type));
		} catch (Exception e) {
			logger.error("failed to override message mapping for " + key + " type:" + type);
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected ActionMessageType createMessageType(String key) throws ClassNotFoundException {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String clazz = key;
		if (!clazz.contains(".")) {
			clazz = "com.sicpa.standard.sasscl.messages." + clazz;
		}
		Class<? extends ActionEvent> eventClass = (Class<? extends ActionEvent>) Class.forName(clazz);
		return new ActionMessageType(clazz, eventClass);
	}

	public void setOverridingProperties(Properties overridingProperties) {
		this.overridingProperties = overridingProperties;
	}

}