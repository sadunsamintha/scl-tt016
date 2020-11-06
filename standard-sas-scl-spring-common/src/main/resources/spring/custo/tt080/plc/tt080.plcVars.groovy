import com.sicpa.standard.client.common.device.plc.PLCVariableMap
import com.sicpa.standard.plc.controller.actions.IPlcAction
import com.sicpa.standard.plc.controller.actions.PlcAction
import com.sicpa.standard.plc.value.IPlcVariable
import com.sicpa.standard.plc.value.PlcVariable
import com.sicpa.standard.sasscl.devices.plc.DefaultPlcRequestExecutor
import com.sicpa.standard.sasscl.devices.plc.IPlcRequestExecutor
import com.sicpa.standard.sasscl.devices.plc.PlcRequest
import com.sicpa.standard.sasscl.devices.plc.PlcUtils
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor
import groovy.transform.Field

import static com.sicpa.standard.sasscl.devices.plc.PlcRequest.*
import static com.sicpa.standard.sasscl.devices.plc.PlcUtils.*
import static com.sicpa.standard.sasscl.devices.plc.PlcUtils.PLC_TYPE.*

@Field def allVars = new ArrayList<IPlcVariable>()
@Field def lineJmxReport= new ArrayList<IPlcVariable>()
@Field def cabJmxReport= new ArrayList<IPlcVariable>()
@Field def lineNotif= new ArrayList<IPlcVariable>()
@Field def cabNotif= new ArrayList<IPlcVariable>()
@Field def plcVarMapping=new HashMap<String,String>();
@Field def plcMap=new HashMap()
@Field def requestMapping=new HashMap()

@Field def lineGroups=new TreeMap<String, PlcVariableGroup>()
@Field def cabGroups=new TreeMap<String, PlcVariableGroup>()

@Field def typeByVar=new HashMap<String, PLC_TYPE>()


beans {

	//v=> var name
	//t=> type (I)nt/(S)hort/(B)ool,(BY)te
	//cabNtf => notif on cabinet var
	//lineNtf => notif on line
	//lineGrp => add the var to the edit line var gui, grouped with over var sharing the same group name
	//cabGrp => add the var to the edit cabinet var gui, grouped with over var sharing the same group name



	//LINE PARAM
	plcMap['PARAM_LINE_IS_ACTIVE']=[v:LINE+'bLine_is_active' ,t:B]
	plcMap['PARAM_LINE_TRIGGERS_FILTER_TYPE']=[v:LINE_PRM+'bTriggersFilterType' ,t:B]
	plcMap['PARAM_LINE_CAMERA_DISTANCE_TYPE']=[v:LINE_PRM+'bCameraDistanceType' ,t:B]
	plcMap['PARAM_LINE_CAMERA_LENGTH_TYPE']=[v:LINE_PRM+'bCameraLengthType' ,t:B]
	plcMap['PARAM_LINE_VALIDE_CODE_TIMEOUT_TYPE']=[v:LINE_PRM+'bValideCodeTimeoutType' ,t:B]
	plcMap['PARAM_LINE_AUDIO_VISUAL_EMISSION_DISTANCE_TYPE']=[v:LINE_PRM+'bAudioVisualEmissionDistanceType' ,t:B]
	plcMap['PARAM_LINE_AUDIO_VISUAL_EMISSION_LENGTH_TYPE']=[v:LINE_PRM+'bAudioVisualEmissionLengthType' ,t:B]
	plcMap['PARAM_LINE_PRINTER_DISTANCE_TYPE']=[v:LINE_PRM+'bPrinterDistanceType' ,t:B]
	plcMap['PARAM_LINE_PRINTER_LENGTH_TYPE']=[v:LINE_PRM+'bPrinterLengthType' ,t:B]
	plcMap['PARAM_LINE_PC_EJECTION_RECEPTION_DISTANCE_TYPE']=[v:LINE_PRM+'bPCEjectionReceptionDistanceType' ,t:B]
	plcMap['PARAM_LINE_PC_EJECTION_RECEPTION_LENGTH_TYPE']=[v:LINE_PRM+'bPCEjectionReceptionLengthType' ,t:B]
	plcMap['PARAM_LINE_EJECTION_EMISSION_DISTANCE_TYPE']=[v:LINE_PRM+'bEjectionEmissionDistanceType' ,t:B]
	plcMap['PARAM_LINE_EJECTION_EMISSION_LENGTH_TYPE']=[v:LINE_PRM+'bEjectionEmissionLengthType' ,t:B]
	plcMap['PARAM_LINE_JAVA_NTF_DISTANCE_TYPE']=[v:LINE_PRM+'bJavaNotificationDistanceType' ,t:B]
	plcMap['PARAM_LINE_TRIGGERS_FILTER']=[v:LINE_PRM+'nTriggersFilter' ,t:D ,lineGrp:'misc']
	plcMap['PARAM_LINE_CAMERA_DISTANCE']=[v:LINE_PRM+'nCameraDistance' ,t:D ,lineGrp:'camera']
	plcMap['PARAM_LINE_CAMERA_LENGTH']=[v:LINE_PRM+'nCameraLength' ,t:D ,lineGrp:'camera']
	plcMap['PARAM_LINE_VALIDE_CODE_TIMEOUT']=[v:LINE_PRM+'nValideCodeTimeout' ,t:D ,lineGrp:'misc']
	plcMap['PARAM_LINE_AUDIO_VISUAL_EMISSION_DISTANCE']=[v:LINE_PRM+'nAudioVisualEmissionDistance' ,t:D ,lineGrp:'misc']
	plcMap['PARAM_LINE_AUDIO_VISUAL_EMISSION_LENGTH']=[v:LINE_PRM+'nAudioVisualEmissionLength' ,t:D ,lineGrp:'misc']
	plcMap['PARAM_LINE_PRINTER_DISTANCE']=[v:LINE_PRM+'nPrinterDistance' ,t:D,lineGrp:'printer']
	plcMap['PARAM_LINE_PRINTER_LENGTH']=[v:LINE_PRM+'nPrinterLength' ,t:D ,lineGrp:'printer']
	plcMap['PARAM_LINE_PC_EJECTION_RECEPTION_DISTANCE']=[v:LINE_PRM+'nPCEjectionReceptionDistance' ,t:D ,lineGrp:'ejection']
	plcMap['PARAM_LINE_PC_EJECTION_RECEPTION_LENGTH']=[v:LINE_PRM+'nPCEjectionReceptionLength' ,t:I]
	plcMap['PARAM_LINE_EJECTION_EMISSION_DISTANCE']=[v:LINE_PRM+'nEjectionEmissionDistance' ,t:D ,lineGrp:'ejection']
	plcMap['PARAM_LINE_EJECTION_EMISSION_LENGTH']=[v:LINE_PRM+'nEjectionEmissionLength' ,t:D ,lineGrp:'ejection']
	plcMap['PARAM_LINE_JAVA_NTF_DISTANCE']=[v:LINE_PRM+'nJavaNotificationDistance' ,t:D ,lineGrp:'misc']
	plcMap['PARAM_LINE_ENCODER_RESOLUTION']=[v:LINE_PRM+'nEncoderResolution' ,t:I ,pulseConvertParam:'true' ,lineGrp:'system']
	plcMap['PARAM_LINE_SHAPE_DIAMETER']=[v:LINE_PRM+'nShapeDiameter' ,t:I ,pulseConvertParam:'true' ,lineGrp:'system']
	plcMap['PARAM_LINE_MAX_CONSECUTIVE_INVALID_CODES']=[v:LINE_PRM+'nMaxConsecutiveInvalidCodes' ,t:I ,lineGrp:'stats']
	plcMap['PARAM_LINE_STATS_SMALL_THRESHOLD']=[v:LINE_PRM+'nStatsSmallThreshold' ,t:I ,lineGrp:'stats']
	plcMap['PARAM_LINE_STATS_SMALL_REJECTION_LIMIT']=[v:LINE_PRM+'nStatsSmallRejectionLimit' ,t:I ,lineGrp:'stats']
	plcMap['PARAM_LINE_STATS_LARGE_THRESHOLD']=[v:LINE_PRM+'nStatsLargeThreshold' ,t:I ,lineGrp:'stats']
	plcMap['PARAM_LINE_STATS_LARGE_REJECTION_LIMIT']=[v:LINE_PRM+'nStatsLargeRejectionLimit' ,t:I ,lineGrp:'stats']
	plcMap['PARAM_LINE_TRIGGER_CONTROL_MAX_DIFF_VALUE']=[v:LINE_PRM+'nTriggerControlMaxDiffValue' ,t:I ,lineGrp:'misc']
	plcMap['PARAM_LINE_TRIGGER_CONTROL_MAX_CHECKS_VALUE']=[v:LINE_PRM+'nTriggerControlMaxChecksValue' ,t:I ,lineGrp:'misc']
	plcMap['PARAM_LINE_TRIGGER_CONTROL_MAX_ABS_ERRORS']=[v:LINE_PRM+'nTriggerControlMaxAbsErrors' ,t:I ,lineGrp:'misc']
	plcMap['PARAM_LINE_ENCODER_MODULE_FOLD_EVALUATION']=[v:LINE_PRM+'nEncoderModuleFoldEvaluation' ,t:I ,pulseConvertParam:'true' ,lineGrp:'system']
	plcMap['PARAM_LINE_TEST_ADDITIONAL_CHECK']=[v:LINE_PRM+'bTestAdditionalCheck' ,t:B ,lineGrp:'misc']
	plcMap['PARAM_LINE_TRIGGER_ACT_ON_FAILING_EDGE']=[v:LINE_PRM+'bTriggerActOnFallingEdge' ,t:B ,lineGrp:'misc']
	plcMap['PARAM_LINE_SIMULATE_PC_EJECTION_IF_ADD_CHECK']=[v:LINE_PRM+'bSimulatePCEjectionIfAddCheck' ,t:B ,lineGrp:'ejection']
	plcMap['PARAM_LINE_TRIG_EJECTION_ONLY_FOR_SICPA_EJECTIONS']=[v:LINE_PRM+'bTrigEjectionOnlyForSicpaEjections' ,t:B ,lineGrp:'ejection']
	plcMap['PARAM_LINE_ENCODER_USED']=[v:LINE_PRM+'bEncoderUsed' ,t:B ,lineGrp:'system']
	plcMap['PARAM_LINE_INDEXED_PRODUCTS']=[v:LINE_PRM+'bIndexedProducts' ,t:B ,lineGrp:'misc']
	plcMap['PARAM_LINE_INHIBIT_PRINTER']=[v:LINE_PRM+'bInhibitPrinter' ,t:B ,lineGrp:'printer']
	plcMap['PARAM_LINE_INHIBIT_CAMERA']=[v:LINE_PRM+'bInhibitCamera' ,t:B ,lineGrp:'camera']
	plcMap['PARAM_LINE_INHIBIT_PC_EJECTION_RECEPTION']=[v:LINE_PRM+'bInhibitPCEjectionReception' ,t:B ,lineGrp:'ejection']
	plcMap['PARAM_LINE_INHIBIT_EJECTION_EMISSION']=[v:LINE_PRM+'bInhibitEjectionEmission' ,t:B ,lineGrp:'ejection']
	plcMap['PARAM_LINE_INHIBIT_EJECTION_AUDIO_VISUALS']=[v:LINE_PRM+'bInhibitEjectionAudiovisuals' ,t:B ,lineGrp:'ejection']
	plcMap['PARAM_LINE_INHIBIT_JAVA_NTF_EMISSION']=[v:LINE_PRM+'bInhibitJavaNtfEmission' ,t:B ,lineGrp:'misc']
	plcMap['PARAM_LINE_INHIBIT_LBL_APP_OR_AIR_DRYER']=[v:LINE_PRM+'bInhibitLblAppOrAirDryer' ,t:B ,lineGrp:'misc']
	plcMap['PARAM_LINE_EXPORT_MODE_ENABLED']=[v:LINE_PRM+'bExportModeEnabled' ,t:B]
	plcMap['PARAM_LINE_MAINTENANCE_MODE_ENABLED']=[v:LINE_PRM+'bMaintenanceModeEnabled' ,t:B]
	plcMap['PARAM_LINE_REFEED_MODE_ENABLED']=[v:LINE_PRM+'bRefeedModeEnabled' ,t:B]
	plcMap['PARAM_LINE_DRS_TYPE']=[v:LINE_PRM+'nDRS_Type' ,t:S]
	plcMap['PARAM_LINE_EJECTION_TYPE']=[v:LINE_PRM+'nEjectionType' ,t:S ,lineGrp:'ejection']
	plcMap['PARAM_LINE_WAR_TEMP_IJ_CAB_LVL']=[v:LINE_PRM+'nWar_Temperature_IJ_cabinet_level' ,t:S ,lineGrp:'temp']
	plcMap['PARAM_LINE_WAR_TEMP_IJ_INK_LVL']=[v:LINE_PRM+'nWar_Temperature_IJ_ink_level' ,t:S ,lineGrp:'temp']
	plcMap['PARAM_LINE_ERR_TEMP_IJ_CAB_LVL']=[v:LINE_PRM+'nErr_Temperature_IJ_cabinet_level' ,t:S ,lineGrp:'temp']
	plcMap['PARAM_LINE_ERR_TEMP_IJ_INK_LVL']=[v:LINE_PRM+'nErr_Temperature_IJ_ink_level' ,t:S ,lineGrp:'temp']
	plcMap['PARAM_LINE_WAR_FANS_PRINTER_CAB_MIN_FREQ']=[v:LINE_PRM+'nWar_Fans_Printer_cabinet_MIN_freq' ,t:S ,lineGrp:'fan']
	plcMap['PARAM_LINE_WAR_FANS_PRINTER_CAB_MAX_FREQ']=[v:LINE_PRM+'nWar_Fans_Printer_cabinet_MAX_freq' ,t:S ,lineGrp:'fan']
	plcMap['PARAM_LINE_FANS_TEMP_ON_THRESHOLD_PRINTER']=[v:LINE_PRM+'nFans_Temp_ON_threshold_Printer' ,t:S ,lineGrp:'temp']
	plcMap['PARAM_LINE_INK_VORTEX_ON_THRESHOLD']=[v:LINE_PRM+'nInk_Vortex_ON_threshold' ,t:S ,lineGrp:'misc']
	plcMap['PARAM_LINE_PRODUCT_DETECTOR_ACTIVE_LOW']=[v:LINE_PRM+'bProductDetectorIsActiveLow' ,t:B, lineGrp:'misc']
	plcMap['PARAM_LINE_INHIBIT_STOP_LINE_IF_CODE_IN_EXPORT_MODE']=[v:LINE_PRM+'bInhibitStopLineIfCodeInExportMode' ,t:B, lineGrp:'misc']
	plcMap['PARAM_LINE_LABEL_APPLICATOR_DISTANCE']=[v:LINE_PRM+'nLabelApplicatorDistance' ,t:D]
	plcMap['PARAM_LINE_LABEL_APPLICATOR_DISTANCE_TYPE']=[v:LINE_PRM+'bLabelApplicatorDistanceType' ,t:B]
	plcMap['PARAM_LINE_LABEL_APPLICATOR_LENGTH']=[v:LINE_PRM+'nLabelApplicatorLength' ,t:D]
	plcMap['PARAM_LINE_LABEL_APPLICATOR_LENGTH_TYPE']=[v:LINE_PRM+'bLabelApplicatorLengthType' ,t:B]
	plcMap['PARAM_LINE_LABEL_APPLICATOR_2_DISTANCE']=[v:LINE_PRM+'n2ndLabelApplicatorDistance' ,t:D]
	plcMap['PARAM_LINE_LABEL_APPLICATOR_2_DISTANCE_TYPE']=[v:LINE_PRM+'b2ndLabelApplicatorDistanceType' ,t:B]
	plcMap['PARAM_LINE_LABEL_APPLICATOR_2_LENGTH']=[v:LINE_PRM+'n2ndLabelApplicatorLength' ,t:D]
	plcMap['PARAM_LINE_LABEL_APPLICATOR_2_LENGTH_TYPE']=[v:LINE_PRM+'b2ndLabelApplicatorLengthType' ,t:B]
	plcMap['PARAM_LINE_ENCODER_CHECK_WARNING_THRESHOLD']=[v:LINE_PRM+'nEncoderCheckWarningThreshold' ,t:I ,lineGrp:'encoder']
	plcMap['PARAM_LINE_ENCODER_CHECK_ERROR_THRESHOLD']=[v:LINE_PRM+'nEncoderCheckErrorThreshold' ,t:I ,lineGrp:'encoder']

	def productionConfigFolder=props['production.config.folder'].trim().toUpperCase()
	if(productionConfigFolder == "PRODUCTIONCONFIG-SAS") {
		plcMap['PARAM_LINE_VALVE_DISTANCE'] = [v: LINE_PRM + 'nValveDistance', t: I, lineGrp: 'wipper']
		plcMap['PARAM_LINE_VALVE_LENGTH'] = [v: LINE_PRM + 'nValveLength', t: I, lineGrp: 'wipper']
		plcMap['PARAM_LINE_RATIO_ENCODER_MOTOR'] = [v: LINE_PRM + 'nRatioEncoderMotor', t: S, lineGrp: 'wipper']
		plcMap['PARAM_LINE_PULSES_PER_MM'] = [v: LINE_PRM + 'nPulsesPerMM', t: S, lineGrp: 'wipper']
	}

	//LINE NOTIF
	plcMap['NTF_LINE_SPEED']=[v:LINE_NTF+'nLineSpeed' ,t:I ,lineNtf:'true']
	plcMap['NTF_LINE_PRODS_PER_SECOND']=[v:LINE_NTF+'nProdsPerSecond' ,t:I ]
	plcMap['NTF_LINE_STATE']=[v:LINE_NTF+'nState' ,t:I ,lineNtf:'true']
	plcMap['NTF_LINE_GOOD_TRIGS']=[v:LINE_NTF+'nGoodTrigs' ,t:I ]
	plcMap['NTF_LINE_BAD_TRIGS']=[v:LINE_NTF+'nBadTrigs' ,t:I ]
	plcMap['NTF_LINE_NO_CAP_TRIGS']=[v:LINE_NTF+'nNoCapTrigs' ,t:I ]
	plcMap['NTF_LINE_COUNTER_TRIGS']=[v:LINE_NTF+'nCounterTrigs' ,t:I ]
	plcMap['NTF_LINE_PRODUCT_DETECTOR_TRIGS']=[v:LINE_NTF+'nProductDetectorTrigs' ,t:I ,lineNtf:'true']
	plcMap['NTF_LINE_PRODUCTS_FREQ']=[v:LINE_NTF+'nProductsFrequency' ,t:I ]
	plcMap['NTF_LINE_TRILIGHT_GREEN']=[v:LINE_NTF+'nLineTrilightGreen' ,t:I ]
	plcMap['NTF_LINE_TRILIGHT_YELLOW']=[v:LINE_NTF+'nLineTrilightYellow' ,t:I ]
	plcMap['NTF_LINE_TRILIGHT_RED']=[v:LINE_NTF+'nLineTrilightRed' ,t:I ]
	plcMap['NTF_LINE_CPT_PRODUCTS_EJECTIONS']=[v:LINE_NTF+'nCptProductsEjections' ,t:I ]
	plcMap['NTF_LINE_CPT_PRODUCER_EJECTIONS']=[v:LINE_NTF+'nCptProducerEjections' ,t:I ]
	plcMap['NTF_LINE_JAVA_PROD_STATUS_NTF']=[v:LINE_NTF+'nJavaProdStatusNotification' ,t:I ]
	plcMap['NTF_LINE_TEMP_IJ_CAB']=[v:LINE_NTF+'nTemperature_IJ_cabinet' ,t:S ]
	plcMap['NTF_LINE_TEMP_IJ_INK']=[v:LINE_NTF+'nTemperature_IJ_ink' ,t:S ]
	plcMap['NTF_LINE_REL_HUMIDITY_AMBIANT']=[v:LINE_NTF+'nRelative_humidity_ambiant' ,t:I ]
	plcMap['NTF_LINE_REL_HUMIDITY_VOLTAGE_LVL']=[v:LINE_NTF+'nRel_humidity_voltage_level' ,t:I ]
	plcMap['NTF_LINE_AIR_PRESS_LVL']=[v:LINE_NTF+'nAir_pressure_level' ,t:I ]
	plcMap['NTF_LINE_AIR_PRESS_VOLTAGE_LVL']=[v:LINE_NTF+'nAir_pressure_voltage_level' ,t:I ]
	plcMap['NTF_LINE_WAR_ERR_REGISTER']=[v:LINE_NTF+'nWar_err_register' ,t:I ,lineNtf:'true']
	plcMap['NTF_LINE_CPT_PRINTER_TRIGS']=[v:LINE_NTF+'nCptPrinterTrigs' ,t:I ]
	plcMap['NTF_LINE_CPT_CAMERA_TRIGS']=[v:LINE_NTF+'nCptCameraTrigs' ,t:I ]
	plcMap['NTF_LINE_JAVA_CPT_GOOD_PRODUCTS']=[v:LINE_NTF+'nJavaCpt_GoodProducts' ,t:I ]
	plcMap['NTF_LINE_JAVA_CPT_UNREADABLES']=[v:LINE_NTF+'nJavaCpt_Unreadables' ,t:I ]
	plcMap['NTF_LINE_JAVA_CPT_NO_INK_DETECTED']=[v:LINE_NTF+'nJavaCpt_NoInkDetected' ,t:I ]
	plcMap['NTF_LINE_JAVA_CPT_ACQ_ERRORS']=[v:LINE_NTF+'nJavaCpt_AcquisitionErrors' ,t:I ]
	plcMap['NTF_LINE_JAVA_PRODUCT_STATUS']=[v:LINE_NTF+'nDRSValueForJava' ,t:I, lineNtf:'true']
	plcMap['NTF_LINE_JAVA_PRODUCT_COUNTER']=[v:LINE_NTF+'nJavaProductCounter' ,t:I]
	plcMap['NTF_LINE_WAR_ERR_SECONDARY_REGISTER']=[v:LINE_NTF+'nWar_Err_Secondary_Register' ,t:I ,lineNtf:'true']


	//CABINET PARAM
	plcMap['PARAM_CAB_SYSTEM_TYPE']=[v:CAB_PRM+'nSystemType' ,t:S ,cabGrp:'system']
	plcMap['PARAM_CAB_TIMEOUT_LIFECHECK']=[v:CAB_PRM+'nTimeoutLifeCheck' ,t:I ,cabGrp:'system']
	plcMap['PARAM_CAB_COOLING_ENABLED']=[v:CAB_PRM+'bCoolingSystemEnabled' ,t:B ,cabGrp:'system']
	plcMap['PARAM_CAB_COOLING_ERR_ACTIVATION_TIMEOUT']=[v:CAB_PRM+'nCoolingErrorActivationTimeout' ,t:I ,cabGrp:'system']
	plcMap['PARAM_CAB_MULTILINE_REQ_MODE_ENABLED']=[v:CAB_PRM+'bMultiLineRequestsModeEnabled' ,t:B ,cabGrp:'system']
	plcMap['PARAM_CAB_WAR_TEMP_EE_CAB_LVL']=[v:CAB_PRM+'nWar_Temperature_EE_cabinet_level' ,t:S ,cabGrp:'temp']
	plcMap['PARAM_CAB_WAR_TEMP_AMBIANT_LVL']=[v:CAB_PRM+'nWar_Temperature_ambiant_level' ,t:S ,cabGrp:'temp']
	plcMap['PARAM_CAB_WAR_TEMP_BYPASS_LVL']=[v:CAB_PRM+'nWar_Temperature_bypass_level' ,t:S ,cabGrp:'temp']
	plcMap['PARAM_CAB_ERR_TEMP_EE_CAB_LVL']=[v:CAB_PRM+'nErr_Temperature_EE_cabinet_level' ,t:S ,cabGrp:'temp']
	plcMap['PARAM_CAB_ERR_TEMP_AMBIANT_LVL']=[v:CAB_PRM+'nErr_Temperature_ambiant_level' ,t:S ,cabGrp:'temp']
	plcMap['PARAM_CAB_ERR_TEMP_BYPASS_LVL']=[v:CAB_PRM+'nErr_Temperature_bypass_level' ,t:S ,cabGrp:'temp']
	plcMap['PARAM_CAB_WAR_AIR_PRESS_LVL']=[v:CAB_PRM+'nWar_air_pressure_level' ,t:S ,cabGrp:'air']
	plcMap['PARAM_CAB_ERR_AIR_PRESS_LVL']=[v:CAB_PRM+'nErr_air_pressure_level' ,t:S ,cabGrp:'air']
	plcMap['PARAM_CAB_WAR_FANS_EE_CAB_MIN_FREQ']=[v:CAB_PRM+'nWar_Fans_EE_cabinet_MIN_freq' ,t:S ,cabGrp:'fan']
	plcMap['PARAM_CAB_WAR_FANS_EE_CAB_MAX_FREQ']=[v:CAB_PRM+'nWar_Fans_EE_cabinet_MAX_freq' ,t:S ,cabGrp:'fan']
	plcMap['PARAM_CAB_WAR_FANS_BYPASS_CAB_MIN_FREQ']=[v:CAB_PRM+'nWar_Fans_Bypass_cabinet_MIN_freq' ,t:S ,cabGrp:'fan']
	plcMap['PARAM_CAB_WAR_FANS_BYPASS_CAB_MAX_FREQ']=[v:CAB_PRM+'nWar_Fans_Bypass_cabinet_MAX_freq' ,t:S ,cabGrp:'fan']
	plcMap['PARAM_CAB_FANS_TEMP_ON_THRESHOLD_EE']=[v:CAB_PRM+'nFans_Temp_ON_threshold_EE' ,t:S ,cabGrp:'fan']
	plcMap['PARAM_CAB_FANS_TEMP_ON_THRESHOLD_BYPASS']=[v:CAB_PRM+'nFans_Temp_ON_threshold_Bypass' ,t:S ,cabGrp:'fan']
	plcMap['PARAM_CAB_FANS_TEMP_OFF_DELTA_HYST_BYPASS']=[v:CAB_PRM+'nTemp_OFF_delta_hysteresis' ,t:S ,cabGrp:'fan']
	

	//CABINET NOTIF
	plcMap['NTF_CAB_VERSION_HIGH']=[v:CAB_NTF+'nVersionHigh' ,t:I ]
	plcMap['NTF_CAB_VERSION_MEDIUM']=[v:CAB_NTF+'nVersionMedium' ,t:I ]
	plcMap['NTF_CAB_VERSION_LOW']=[v:CAB_NTF+'nVersionLow' ,t:I ]
	plcMap['NTF_CAB_CAB_TRILIGHT_GREEN']=[v:CAB_NTF+'nCabTrilightGreen' ,t:I ]
	plcMap['NTF_CAB_CAB_TRILIGHT_YELLOW']=[v:CAB_NTF+'nCabTrilightYellow' ,t:I ]
	plcMap['NTF_CAB_CAB_TRILIGHT_RED']=[v:CAB_NTF+'nCabTrilightRed' ,t:I ]
	plcMap['NTF_CAB_CX_BOARD_TEMPERATURE']=[v:CAB_NTF+'nCX_Board_temperature' ,t:BY ]
	plcMap['NTF_CAB_CX_CPU_TEMPERATURE']=[v:CAB_NTF+'nCX_CPU_temperature' ,t:BY ]
	plcMap['NTF_CAB_TEMPERATURE_EE_CAB']=[v:CAB_NTF+'nTemperature_EE_cabinet' ,t:S ]
	plcMap['NTF_CAB_TEMPERATURE_AMBIANT']=[v:CAB_NTF+'nTemperature_ambiant' ,t:S ]
	plcMap['NTF_CAB_TEMPERATURE_BYPASS']=[v:CAB_NTF+'nTemperature_bypass' ,t:S ]
	plcMap['NTF_CAB_REL_HUMIDITY_AMBIANT']=[v:CAB_NTF+'nRelative_humidity_ambiant' ,t:I ]
	plcMap['NTF_CAB_REL_HUMIDITY_VOLTAGE_LVL']=[v:CAB_NTF+'nRel_humidity_voltage_level' ,t:I ]
	plcMap['NTF_CAB_AIR_PRESS_LVL']=[v:CAB_NTF+'nAir_pressure_level' ,t:I ]
	plcMap['NTF_CAB_AIR_PRESS_VOLTAGE_LVL']=[v:CAB_NTF+'nAir_pressure_voltage_level' ,t:I ]
	plcMap['NTF_CAB_WAR_ERR_REGISTER']=[v:CAB_NTF+'nWar_err_register' ,t:I  ,cabNtf:'true']

	//REQUEST
	plcMap['REQUEST_START']=[v:'.com.stMultilineRequests.bStart' ,t:B]
	plcMap['REQUEST_RUN']=[v:'.com.stMultilineRequests.bRun' ,t:B]
	plcMap['REQUEST_STOP']=[v:'.com.stMultilineRequests.bStop' ,t:B]
	plcMap['REQUEST_RELOAD_PLC_PARAM']=[v:'.com.stMultilineRequests.bReloadConfig' ,t:B]
	plcMap['REQUEST_LIFE_CHECK']=[v:CAB + 'stRequests.bLifeCheck' ,t:B]
	plcMap['REQUEST_JAVA_WARNINGS_AND_ERRORS_REGISTER']=[v:'.com.nJavaWarningsAndErrorsRegister' ,t:I]
	plcMap['REQUEST_RELOAD_DATE_TIME']=[v:CAB + 'stRequests.bUpdateDateTime' ,t:B]
	plcMap['REQUEST_RESET_JAVA_PRODUCT_COUNTER']=[v:LINE + 'stRequests.bResetJavaProductCounter' ,t:B]

	//OFFLINE
	plcMap['OFFLINE_COUNTING_QTY']=[v:OFFLINE + 'nProductsCounterOFF' ,t:I]

	plcMap['OFFLINE_COUNTING_LAST_STOP_YEAR']=[v:DATE_TIME_WHEN_STOPPED + 'wYear' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_STOP_MONTH']=[v:DATE_TIME_WHEN_STOPPED + 'wMonth' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_STOP_DAY']=[v:DATE_TIME_WHEN_STOPPED + 'wDay' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_STOP_HOUR']=[v:DATE_TIME_WHEN_STOPPED + 'wHour' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_STOP_MINUTE']=[v:DATE_TIME_WHEN_STOPPED + 'wMinute' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_STOP_SECOND']=[v:DATE_TIME_WHEN_STOPPED + 'wSecond' ,t:S]

	plcMap['OFFLINE_COUNTING_LAST_PRODUCT_YEAR']=[v:DATE_TIME_LAST_PRODUCT + 'wYear' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_PRODUCT_MONTH']=[v:DATE_TIME_LAST_PRODUCT + 'wMonth' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_PRODUCT_DAY']=[v:DATE_TIME_LAST_PRODUCT + 'wDay' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_PRODUCT_HOUR']=[v:DATE_TIME_LAST_PRODUCT + 'wHour' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_PRODUCT_MINUTE']=[v:DATE_TIME_LAST_PRODUCT + 'wMinute' ,t:S]
	plcMap['OFFLINE_COUNTING_LAST_PRODUCT_SECOND']=[v:DATE_TIME_LAST_PRODUCT + 'wSecond' ,t:S]

	plcMap['OFFLINE_RESET_COUNTERS']=[v:OFFLINE + 'bResetOffCounters' ,t:B]

	//UPDATE DATE TIME
	plcMap['UPDATE_DATE_TIME_YEAR']=[v:UPDATE_DATE_TIME + 'wYear', t:S]
	plcMap['UPDATE_DATE_TIME_MONTH']=[v:UPDATE_DATE_TIME + 'wMonth', t:S]
	plcMap['UPDATE_DATE_TIME_DAY_OF_WEEK']=[v:UPDATE_DATE_TIME + 'wDayOfWeek', t:S]
	plcMap['UPDATE_DATE_TIME_DAY']=[v:UPDATE_DATE_TIME + 'wDay', t:S]
	plcMap['UPDATE_DATE_TIME_HOUR']=[v:UPDATE_DATE_TIME + 'wHour', t:S]
	plcMap['UPDATE_DATE_TIME_MINUTE']=[v:UPDATE_DATE_TIME + 'wMinute', t:S]
	plcMap['UPDATE_DATE_TIME_SECOND']=[v:UPDATE_DATE_TIME + 'wSecond', t:S]
	plcMap['UPDATE_DATE_TIME_MILLISECONDS']=[v:UPDATE_DATE_TIME + 'wMilliseconds', t:S]

	injectCustoVar();

	//REQUEST
	requestMapping[(START)]=[REQUEST_START:true]
	requestMapping[(RUN)]=[REQUEST_RUN:true]
	requestMapping[(STOP)]=[REQUEST_STOP:true]
	requestMapping[(RELOAD_PLC_PARAM)]=[REQUEST_RELOAD_PLC_PARAM:true]
	requestMapping[(RELOAD_DATE_TIME)]=[REQUEST_RELOAD_DATE_TIME:true]

	for ( e in plcMap ) {
		String logic=e.key
		String phy=e.value['v']
		plcVarMapping[logic]= phy

		Map vd=createVarAndDescriptor(phy, logic, e.value)
		IPlcVariable var=vd['var']
		PlcVariableDescriptor desc =vd['desc']

		registerSingleton("${logic}_var",var)
		if (desc != null) {
			registerSingleton("${logic}_desc",desc)
		}

		addVarToLists(var, logic, e.value)
		insertVarToGroup(desc,e.value)
	}

	registerSingleton('linePlcVarGroup',createLineGroupList())
	registerSingleton('cabPlcVarGroups',createCabGroupList())

	registerSingleton('allPlcVars',allVars)
	registerSingleton('typeByPlcVar',typeByVar)
	registerSingleton('plcLineJmxReport',lineJmxReport)
	registerSingleton('plcCabJmxReport',cabJmxReport)
	registerSingleton('plcLineNtfTemplate',lineNotif)
	registerSingleton('plcCabinetNtf',cabNotif)
	registerSingleton('plcVarMap',new PLCVariableMap(plcVarMapping))

	registerSingleton('mapPlcRequestAction',createRequests())
}

def void injectCustoVar(){
	for(e in PlcUtils.custoInfo){
		String logic=e.key
		def map=e.value
		plcMap[logic]=map
	}
}

def void addVarToLists(def var,String logicName,def varInfo){
	allVars.add(var)
	typeByVar.putAt(logicName, varInfo['t'])

	if(isLineJmxReport(logicName)) {
		lineJmxReport.add(var)
	}
	if(isCabinetJmxReport(logicName)) {
		cabJmxReport.add(var)
	}
	if(isCabinetNotif(varInfo)){
		cabNotif.add(var)
	}
	if(isLineNotif(varInfo)){
		lineNotif.add(var)
	}
}

def List<PlcVariableGroup> createCabGroupList(){
	return createGroupList(cabGroups)
}

def List<PlcVariableGroup> createLineGroupList(){
	return createGroupList(lineGroups)
}

def List<PlcVariableGroup> createGroupList(Map<String, PlcVariableGroup> map){
	return new ArrayList<>(map.values())
}

def void insertVarToGroup(PlcVariableDescriptor desc,def varInfo){
	String lineGrp=varInfo['lineGrp']
	String cabGrp=varInfo['cabGrp']

	if(lineGrp!=null){
		insertVarToLineGroups(desc,varInfo)
	}else if(cabGrp!=null){
		insertVarToCabGroups(desc,varInfo)
	}
}

def void insertVarToLineGroups(PlcVariableDescriptor desc,def varInfo){
	def groupPrefix='plc.config.line.group.'
	def grpName=groupPrefix+varInfo['lineGrp']

	insertVarToGroup(lineGroups, grpName,  desc, varInfo)
}
def void insertVarToCabGroups(PlcVariableDescriptor desc,def varInfo){
	def groupPrefix='plc.config.cabinet.group.'
	def grpName=groupPrefix+varInfo['cabGrp']

	insertVarToGroup(cabGroups, grpName,  desc, varInfo)
}

def void insertVarToGroup(Map<String, PlcVariableGroup> groups,String grpName, PlcVariableDescriptor desc,def varInfo){
	def grp=groups[grpName]
	if(grp==null){
		grp=createGroup(grpName)
		groups.put(grpName,grp)
	}
	grp.addDescriptor(desc)
}

def PlcVariableGroup createGroup(String grpName){
	PlcVariableGroup res=new PlcVariableGroup()
	res.setDescription(grpName)
	return res;
}

def  Map<PlcRequest, IPlcRequestExecutor> createRequests(){
	Map<PlcRequest, IPlcRequestExecutor> mapRequest = new HashMap<>();
	for(e in requestMapping){
		def key=e.key
		def actions=new ArrayList<IPlcAction>()
		for(reqStep in e.value){
			String var=plcVarMapping[reqStep.key]
			IPlcAction action=PlcAction.request(var,reqStep.value)
			actions.add(action)
		}
		DefaultPlcRequestExecutor exec=new DefaultPlcRequestExecutor(actions.toArray(new IPlcAction[actions.size]))
		mapRequest[key]=exec
	}
	return mapRequest
}

def Map createVarAndDescriptor(String varPhyName,varLogicName,def varInfo){
	def typeVar=varInfo['t']
	def res = new HashMap()

	PlcVariableDescriptor desc
	IPlcVariable var
	switch(typeVar){
		case D:
			var = createIntVar(varPhyName)
			desc = createPlcDistanceDesc(var,varLogicName)
			break
		case I:
			var = createIntVar(varPhyName)
			if(isPulseConverterParam(varInfo)){
				desc =createPlcUnitConverterParamDesc(varLogicName)
			}else{
				desc = createPlcIntegerDesc(varLogicName)
			}
			break
		case B:
			var = createBooleanVar(varPhyName)
			desc = createPlcBooleanDesc(varLogicName)
			break
		case S:
			var = createShortVar(varPhyName)
			desc = createPlcShortDesc(varLogicName)
			break
		case BY:
			var = createByteVar(varPhyName)
			desc = createPlcByteDesc(varLogicName)
			break
		case STR:
			var = createStrVar(varPhyName, varInfo['l'])
			desc = null
			break
	}
	res['var']=var
	res['desc']=desc
	return res;
}

def  IPlcVariable createBooleanVar(String physName){
	return createVar('createBooleanVar', physName)
}
def  IPlcVariable createByteVar(String physName){
	return createVar('createByteVar', physName)
}
def  IPlcVariable createShortVar(String physName){
	return createVar('createShortVar', physName)
}
def  IPlcVariable createIntVar(String physName){
	return createVar('createInt32Var', physName)
}
def  IPlcVariable createStrVar(String physName, int length){
	return PlcVariable.createStringVar(physName, length);
}
def  IPlcVariable createVar(String method,String physName){
	return PlcVariable."$method"(physName)
}

def  PlcVariableDescriptor createPlcDistanceDesc(IPlcVariable var,String logicVarName){
	String unitVarName=plcMap.get(logicVarName+'_TYPE')['v']
	if(unitVarName==null){
		throw new IllegalArgumentException('unit var not found for '+logicVarName)
	}
	PlcPulseVariableDescriptor desc= new PlcPulseVariableDescriptor()
	desc.setVarName(logicVarName)
	return desc
}
def  boolean isCabinetJmxReport(String varName){
	return varName.startsWith('NTF_CAB')
}
def  boolean isLineJmxReport(String varName){
	return varName.startsWith('NTF_LINE')
}
def  boolean isCabinetNotif(def map){
	return Boolean.parseBoolean(map['cabNtf'])
}
def  boolean isLineNotif(def map){
	return Boolean.parseBoolean(map['lineNtf'])
}
def  boolean isPulseConverterParam(def map){
	return Boolean.parseBoolean(map['pulseConvertParam'])
}