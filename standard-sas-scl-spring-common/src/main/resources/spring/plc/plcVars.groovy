import com.sicpa.standard.client.common.device.plc.PLCVariableMap
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.groovy.SwingEnabledGroovyApplicationContext;
import com.sicpa.standard.plc.controller.actions.*
import com.sicpa.standard.plc.value.*
import com.sicpa.standard.sasscl.devices.plc.DefaultPlcRequestExecutor;
import com.sicpa.standard.sasscl.devices.plc.IPlcRequestExecutor;
import com.sicpa.standard.sasscl.devices.plc.PlcRequest;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcBooleanVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcByteVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcShortVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariablePulseParamDescriptor;

import static com.sicpa.standard.sasscl.devices.plc.PlcRequest.*
import static com.sicpa.standard.sasscl.devices.plc.PlcUtils.*

beans {

	String d='distance'
	String i='int'
	String s='short'
	String b='bool'
	String by='byte'

	String iCreateVar='createInt32Var'
	String sCreateVar='createShortVar'
	String bCreateVar='createBooleanVar'
	String byCreateVar='createByteVar'


	def plcMap=new HashMap()
	//v=> var name
	//t=> type (i)nt/(s)hort/(b)ool,(str)ing, (by)te , in fact contain the method name to call to create the var
	//novar => if you want the var to be accessible in the plcvarmap but not create an associated plcvar (used for error template msg)
	//pulseConvertParam => if the var is part of the pulse by mm parameters


	plcMap['PARAM_LINE_IS_ACTIVE']=[v:'.com.stLine[#x].bLine_is_active' ,t:b]
	plcMap['PARAM_LINE_COM_STRUC_STORED_IN_RAM']=[v:'.com.stLine[#x].stParameters.bComStructureStoredInRAM' ,t:b]
	plcMap['PARAM_LINE_COUNTER_FILTER_TYPE']=[v:'.com.stLine[#x].stParameters.bCounterFilterType' ,t:b]
	plcMap['PARAM_LINE_PRODUCT_DETECTOR_FILTER_TYPE']=[v:'.com.stLine[#x].stParameters.bProductDetectorFilterType' ,t:b]
	plcMap['PARAM_LINE_CAMERA_DISTANCE_TYPE']=[v:'.com.stLine[#x].stParameters.bCameraDistanceType' ,t:b]
	plcMap['PARAM_LINE_CAMERA_LENGTH_TYPE']=[v:'.com.stLine[#x].stParameters.bCameraLengthType' ,t:b]
	plcMap['PARAM_LINE_VALIDE_CODE_TIMEOUT_TYPE']=[v:'.com.stLine[#x].stParameters.bValideCodeTimeoutType' ,t:b]
	plcMap['PARAM_LINE_AUDIO_VISUAL_EMISSION_DISTANCE_TYPE']=[v:'.com.stLine[#x].stParameters.bAudioVisualEmissionDistanceType' ,t:b]
	plcMap['PARAM_LINE_AUDIO_VISUAL_EMISSION_LENGTH_TYPE']=[v:'.com.stLine[#x].stParameters.bAudioVisualEmissionLengthType' ,t:b]
	plcMap['PARAM_LINE_PRINTER_DISTANCE_TYPE']=[v:'.com.stLine[#x].stParameters.bPrinterDistanceType' ,t:b]
	plcMap['PARAM_LINE_PRINTER_LENGTH_TYPE']=[v:'.com.stLine[#x].stParameters.bPrinterLengthType' ,t:b]
	plcMap['PARAM_LINE_PC_EJECTION_RECEPTION_DISTANCE_TYPE']=[v:'.com.stLine[#x].stParameters.bPCEjectionReceptionDistanceType' ,t:b]
	plcMap['PARAM_LINE_PC_EJECTION_RECEIPTION_LENGTH_TYPE']=[v:'.com.stLine[#x].stParameters.bPCEjectionReceptionLengthType' ,t:b]
	plcMap['PARAM_LINE_PROD_CONTROL_DETECTOR_FILTER_TYPE']=[v:'.com.stLine[#x].stParameters.bProdControlDetectorFilterType' ,t:b]
	plcMap['PARAM_LINE_EJECTION_EMISSION_DISTANCE_TYPE']=[v:'.com.stLine[#x].stParameters.bEjectionEmissionDistanceType' ,t:b]
	plcMap['PARAM_LINE_EJECTION_EMISSION_LENGTH_TYPE']=[v:'.com.stLine[#x].stParameters.bEjectionEmissionLengthType' ,t:b]
	plcMap['PARAM_LINE_JAVA_NTF_DISTANCE_TYPE']=[v:'.com.stLine[#x].stParameters.bJavaNotificationDistanceType' ,t:b]
	plcMap['PARAM_LINE_COUNTER_FILTER']=[v:'.com.stLine[#x].stParameters.nCounterFilter' ,t:d]
	plcMap['PARAM_LINE_PRODUCT_DETECTOR_FILTER']=[v:'.com.stLine[#x].stParameters.nProductDetectorFilter' ,t:i]
	plcMap['PARAM_LINE_CAMERA_DISTANCE']=[v:'.com.stLine[#x].stParameters.nCameraDistance' ,t:d]
	plcMap['PARAM_LINE_CAMERA_LENGTH']=[v:'.com.stLine[#x].stParameters.nCameraLength' ,t:d]
	plcMap['PARAM_LINE_VALIDE_CODE_TIMEOUT']=[v:'.com.stLine[#x].stParameters.nValideCodeTimeout' ,t:d]
	plcMap['PARAM_LINE_AUDIO_VISUAL_EMISSION_DISTANCE']=[v:'.com.stLine[#x].stParameters.nAudioVisualEmissionDistance' ,t:d]
	plcMap['PARAM_LINE_AUDIO_VISUAL_EMISSION_LENGTH']=[v:'.com.stLine[#x].stParameters.nAudioVisualEmissionLength' ,t:d]
	plcMap['PARAM_LINE_PRINTER_DISTANCE']=[v:'.com.stLine[#x].stParameters.nPrinterDistance' ,t:d]
	plcMap['PARAM_LINE_PRINTER_LENGTH']=[v:'.com.stLine[#x].stParameters.nPrinterLength' ,t:d]
	plcMap['PARAM_LINE_PC_EJECTION_RECEPTION_DISTANCE']=[v:'.com.stLine[#x].stParameters.nPCEjectionReceptionDistance' ,t:d]
	plcMap['PARAM_LINE_PC_EJECTION_RECEPTION_LENGTH']=[v:'.com.stLine[#x].stParameters.nPCEjectionReceptionLength' ,t:i]
	plcMap['PARAM_LINE_PROD_CONTROL_DETECTOR_FILTER']=[v:'.com.stLine[#x].stParameters.nProdControlDetectorFilter' ,t:d]
	plcMap['PARAM_LINE_EJECTION_EMISSION_DISTANCE']=[v:'.com.stLine[#x].stParameters.nEjectionEmissionDistance' ,t:d]
	plcMap['PARAM_LINE_EJECTION_EMISSION_LENGTH']=[v:'.com.stLine[#x].stParameters.nEjectionEmissionLength' ,t:d]
	plcMap['PARAM_LINE_JAVA_NTF_DISTANCE']=[v:'.com.stLine[#x].stParameters.nJavaNotificationDistance' ,t:d]
	plcMap['PARAM_LINE_ENCODER_RESOLUTION']=[v:'.com.stLine[#x].stParameters.nEncoderResolution' ,t:i ,pulseConvertParam:'true']
	plcMap['PARAM_LINE_SHAPE_DIAMETER']=[v:'.com.stLine[#x].stParameters.nShapeDiameter' ,t:i ,pulseConvertParam:'true']
	plcMap['PARAM_LINE_MAX_CONSECUTIVE_INVALID_CODES']=[v:'.com.stLine[#x].stParameters.nMaxConsecutiveInvalidCodes' ,t:i]
	plcMap['PARAM_LINE_STATS_SMALL_THRESHOLD']=[v:'.com.stLine[#x].stParameters.nStatsSmallThreshold' ,t:i]
	plcMap['PARAM_LINE_STATS_SMALL_REJECTION_LIMIT']=[v:'.com.stLine[#x].stParameters.nStatsSmallRejectionLimit' ,t:i]
	plcMap['PARAM_LINE_STATS_LARGE_THRESHOLD']=[v:'.com.stLine[#x].stParameters.nStatsLargeThreshold' ,t:i]
	plcMap['PARAM_LINE_STATS_LARGE_REJECTION_LIMIT']=[v:'.com.stLine[#x].stParameters.nStatsLargeRejectionLimit' ,t:i]
	plcMap['PARAM_LINE_TRIGGER_CONTROL_MAX_DIFF_VALUE']=[v:'.com.stLine[#x].stParameters.nTriggerControlMaxDiffValue' ,t:i]
	plcMap['PARAM_LINE_TRIGGER_CONTROL_MAX_CHECKS_VALUE']=[v:'.com.stLine[#x].stParameters.nTriggerControlMaxChecksValue' ,t:i]
	plcMap['PARAM_LINE_TRIGGER_CONTROL_MAX_ABS_ERRORS']=[v:'.com.stLine[#x].stParameters.nTriggerControlMaxAbsErrors' ,t:i]
	plcMap['PARAM_LINE_ENCODER_MODULE_FOLD_EVALUATION']=[v:'.com.stLine[#x].stParameters.nEncoderModuleFoldEvaluation' ,t:i ,pulseConvertParam:'true']
	plcMap['PARAM_LINE_TEST_ADDITIONAL_CHECK']=[v:'.com.stLine[#x].stParameters.bTestAdditionalCheck' ,t:b]
	plcMap['PARAM_LINE_TRIGGER_ACT_ON_FAILING_EDGE']=[v:'.com.stLine[#x].stParameters.bTriggerActOnFallingEdge' ,t:b]
	plcMap['PARAM_LINE_SIMULATE_PC_EJECTION_IF_ADD_CHECK']=[v:'.com.stLine[#x].stParameters.bSimulatePCEjectionIfAddCheck' ,t:b]
	plcMap['PARAM_LINE_TRIG_CAMERA_IF_PC_EJECTION_SIMULATED']=[v:'.com.stLine[#x].stParameters.bTrigCameraIfPCEjectionSimulated' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_PC_EJECTION_IF_ADD_CHECK']=[v:'.com.stLine[#x].stParameters.bInhibitPCEjectionIfAddCheck' ,t:b]
	plcMap['PARAM_LINE_TRIG_EJECTION_ONLY_FOR_SICPA_EJECTIONS']=[v:'.com.stLine[#x].stParameters.bTrigEjectionOnlyForSicpaEjections' ,t:b]
	plcMap['PARAM_LINE_TRIG_AUDIO_VISUAL_ONLY_FOR_SICPA_EJECTIONS']=[v:'.com.stLine[#x].stParameters.bTrigAudioVisualOnlyForSicpaEjections' ,t:b]
	plcMap['PARAM_LINE_SYSTEM_TYPE']=[v:'.com.stLine[#x].stParameters.nSystemType' ,t:s]
	plcMap['PARAM_LINE_ENCODER_USED']=[v:'.com.stLine[#x].stParameters.bEncoderUsed' ,t:b]
	plcMap['PARAM_LINE_INDEXED_PRODUCTS']=[v:'.com.stLine[#x].stParameters.bIndexedProducts' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_PRINTER']=[v:'.com.stLine[#x].stParameters.bInhibitPrinter' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_CAMERA']=[v:'.com.stLine[#x].stParameters.bInhibitCamera' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_PC_EJECTION_RECEPTION']=[v:'.com.stLine[#x].stParameters.bInhibitPCEjectionReception' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_EJECTION_EMISSION']=[v:'.com.stLine[#x].stParameters.bInhibitEjectionEmission' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_EJECTION_AUDIO_VISUALS']=[v:'.com.stLine[#x].stParameters.bInhibitEjectionAudiovisuals' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_PC_EJECTION_SIMULATION']=[v:'.com.stLine[#x].stParameters.bInhibitPCEjectionSimulation' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_JAVA_NTF_EMISSION']=[v:'.com.stLine[#x].stParameters.bInhibitJavaNtfEmission' ,t:b]
	plcMap['PARAM_LINE_INHIBIT_LBL_APP_OR_AIR_DRYER']=[v:'.com.stLine[#x].stParameters.bInhibitLblAppOrAirDryer' ,t:b]
	plcMap['PARAM_LINE_EXPORT_MODE_ENABLED']=[v:'.com.stLine[#x].stParameters.bExportModeEnabled' ,t:b]
	plcMap['PARAM_LINE_MAINTENANCE_MODE_ENABLED']=[v:'.com.stLine[#x].stParameters.bMaintenanceModeEnabled' ,t:s]
	plcMap['PARAM_LINE_REFEED_MODE_ENABLED']=[v:'.com.stLine[#x].stParameters.bRefeedModeEnabled' ,t:b]
	plcMap['PARAM_LINE_DRS_TYPE']=[v:'.com.stLine[#x].stParameters.nDRS_Type' ,t:s]
	plcMap['PARAM_LINE_EJECTION_TYPE']=[v:'.com.stLine[#x].stParameters.nEjectionType' ,t:s]
	plcMap['PARAM_LINE_WAR_TEMP_IJ_CAB_LVL']=[v:'.com.stLine[#x].stParameters.nWar_Temperature_IJ_cabinet_level' ,t:s]
	plcMap['PARAM_LINE_WAR_TEMP_IJ_INK_LVL']=[v:'.com.stLine[#x].stParameters.nWar_Temperature_IJ_ink_level' ,t:s]
	plcMap['PARAM_LINE_ERR_TEMP_IJ_CAB_LVL']=[v:'.com.stLine[#x].stParameters.nErr_Temperature_IJ_cabinet_level' ,t:s]
	plcMap['PARAM_LINE_ERR_TEMP_IJ_INK_LVL']=[v:'.com.stLine[#x].stParameters.nErr_Temperature_IJ_ink_level' ,t:s]
	plcMap['PARAM_LINE_WAR_FANS_PRINTER_CAB_MIN_FREQ']=[v:'.com.stLine[#x].stParameters.nWar_Fans_Printer_cabinet_MIN_freq' ,t:s]
	plcMap['PARAM_LINE_WAR_FANS_PRINTER_CAB_MAX_FREQ']=[v:'.com.stLine[#x].stParameters.nWar_Fans_Printer_cabinet_MAX_freq' ,t:s]
	plcMap['PARAM_LINE_FANS_TEMP_ON_THRESHOLD_PRINTER']=[v:'.com.stLine[#x].stParameters.nFans_Temp_ON_threshold_Printer' ,t:s]
	plcMap['PARAM_LINE_FANS_TEMP_OFF_DELTA_HYST_PRINTER']=[v:'.com.stLine[#x].stParameters.nFans_Temp_OFF_delta_hyst_Printer' ,t:s]
	plcMap['PARAM_LINE_INK_VORTEX_ON_THRESHOLD']=[v:'.com.stLine[#x].stParameters.nInk_Vortex_ON_threshold' ,t:s]
	plcMap['PARAM_LINE_INK_VORTEX_OFF_DELTA_HYSTERESIS']=[v:'.com.stLine[#x].stParameters.nInk_Vortex_OFF_delta_hysteresis' ,t:s]
	plcMap['PARAM_LINE_PRODUCT_DETECTOR_ACTIVE_LOW']=[v:'.com.stLine[#x].stParameters.bProductDetectorIsActiveLow' ,t:b]


	plcMap['NTF_LINE_SPEED']=[v:'.com.stLine[#x].stNotifications.nLineSpeed' ,t:i ,notif:'true']
	plcMap['NTF_LINE_PRODS_PER_SECOND']=[v:'.com.stLine[#x].stNotifications.nProdsPerSecond' ,t:i ]
	plcMap['NTF_LINE_STATE']=[v:'.com.stLine[#x].stNotifications.nState' ,t:i ,notif:'true']
	plcMap['NTF_LINE_GOOD_TRIGS']=[v:'.com.stLine[#x].stNotifications.nGoodTrigs' ,t:i ]
	plcMap['NTF_LINE_BAD_TRIGS']=[v:'.com.stLine[#x].stNotifications.nBadTrigs' ,t:i ]
	plcMap['NTF_LINE_NO_CAP_TRIGS']=[v:'.com.stLine[#x].stNotifications.nNoCapTrigs' ,t:i ]
	plcMap['NTF_LINE_COUNTER_TRIGS']=[v:'.com.stLine[#x].stNotifications.nCounterTrigs' ,t:i ]
	plcMap['NTF_LINE_PRODUCT_DETECTOR_TRIGS']=[v:'.com.stLine[#x].stNotifications.nProductDetectorTrigs' ,t:i ,notif:'true']
	plcMap['NTF_LINE_PRODUCTS_FREQ']=[v:'.com.stLine[#x].stNotifications.nProductsFrequency' ,t:i ]
	plcMap['NTF_LINE_TRILIGHT_GREEN']=[v:'.com.stLine[#x].stNotifications.nLineTrilightGreen' ,t:i ]
	plcMap['NTF_LINE_TRILIGHT_YELLOW']=[v:'.com.stLine[#x].stNotifications.nLineTrilightYellow' ,t:i ]
	plcMap['NTF_LINE_TRILIGHT_RED']=[v:'.com.stLine[#x].stNotifications.nLineTrilightRed' ,t:i ]
	plcMap['NTF_LINE_CPT_PRODUCTS_EJECTIONS']=[v:'.com.stLine[#x].stNotifications.nCptProductsEjections' ,t:i ]
	plcMap['NTF_LINE_CPT_PRODUCER_EJECTIONS']=[v:'.com.stLine[#x].stNotifications.nCptProducerEjections' ,t:i ]
	plcMap['NTF_LINE_JAVA_FIFO_BUFFER_SIZE']=[v:'.com.stLine[#x].stNotifications.nJavaFifoBufferSize' ,t:i ]
	plcMap['NTF_LINE_JAVA_SICPA_FIFO_READ_IDX']=[v:'.com.stLine[#x].stNotifications.nJavaSicpaFifoReadIndex' ,t:i ]
	plcMap['NTF_LINE_JAVA_SICPA_FIFO_WRITE_IDX']=[v:'.com.stLine[#x].stNotifications.nJavaSicpaFifoWriteIndex' ,t:i ]
	plcMap['NTF_LINE_JAVA_PROD_FIFO_READ_IDX']=[v:'.com.stLine[#x].stNotifications.nJavaProdFifoReadIndex' ,t:i ]
	plcMap['NTF_LINE_JAVA_PROD_FIFO_WRITE_IDX']=[v:'.com.stLine[#x].stNotifications.nJavaProdFifoWriteIndex' ,t:i ]
	plcMap['NTF_LINE_JAVA_PROD_STATUS_NTF']=[v:'.com.stLine[#x].stNotifications.nJavaProdStatusNotification' ,t:i ]
	plcMap['NTF_LINE_TEMP_IJ_CAB']=[v:'.com.stLine[#x].stNotifications.nTemperature_IJ_cabinet' ,t:s ]
	plcMap['NTF_LINE_TEMP_IJ_INK']=[v:'.com.stLine[#x].stNotifications.nTemperature_IJ_ink' ,t:s ]
	plcMap['NTF_LINE_REL_HUMIDITY_AMBIANT']=[v:'.com.stLine[#x].stNotifications.nRelative_humidity_ambiant' ,t:i ]
	plcMap['NTF_LINE_REL_HUMIDITY_VOLTAGE_LVL']=[v:'.com.stLine[#x].stNotifications.nRel_humidity_voltage_level' ,t:i ]
	plcMap['NTF_LINE_AIR_PRESS_LVL']=[v:'.com.stLine[#x].stNotifications.nAir_pressure_level' ,t:i ]
	plcMap['NTF_LINE_AIR_PRESS_VOLTAGE_LVL']=[v:'.com.stLine[#x].stNotifications.nAir_pressure_voltage_level' ,t:i ]
	plcMap['NTF_LINE_WAR_ERR_REGISTER']=[v:'.com.stLine[#x].stNotifications.nWar_err_register' ,t:i ,notif:'true']
	plcMap['NTF_LINE_CPT_PRINTER_TRIGS']=[v:'.com.stLine[#x].stNotifications.nCptPrinterTrigs' ,t:i ]
	plcMap['NTF_LINE_CPT_CAMERA_TRIGS']=[v:'.com.stLine[#x].stNotifications.nCptCameraTrigs' ,t:i ]
	plcMap['NTF_LINE_JAVA_CPT_GOOD_PRODUCTS']=[v:'.com.stLine[#x].stNotifications.nJavaCpt_GoodProducts' ,t:i ]
	plcMap['NTF_LINE_JAVA_CPT_UNREADABLES']=[v:'.com.stLine[#x].stNotifications.nJavaCpt_Unreadables' ,t:i ]
	plcMap['NTF_LINE_JAVA_CPT_NO_INK_DETECTED']=[v:'.com.stLine[#x].stNotifications.nJavaCpt_NoInkDetected' ,t:i ]
	plcMap['NTF_LINE_JAVA_CPT_ACQ_ERRORS']=[v:'.com.stLine[#x].stNotifications.nJavaCpt_AcquisitionErrors' ,t:i ]

	//	<!-- CABINET -->
	plcMap['PARAM_CAB_TIMEOUT_LIFECHECK']=[v:'.com.stCabinet.stParameters.nTimeoutLifeCheck' ,t:i]
	plcMap['PARAM_CAB_COOLING_ERR_ACTIVATION_TIMEOUT']=[v:'.com.stCabinet.stParameters.nCoolingErrorActivationTimeout' ,t:i]
	plcMap['PARAM_CAB_COM_STRUC_STORED_IN_RAM']=[v:'.com.stCabinet.stParameters.bComStructureStoredInRAM' ,t:b]
	plcMap['PARAM_CAB_INHIBIT_RAM_WRITING']=[v:'.com.stCabinet.stParameters.bInhibitRamWriting' ,t:b]
	plcMap['PARAM_CAB_MULTILINE_REQ_MODE_ENABLED']=[v:'.com.stCabinet.stParameters.bMultiLineRequestsModeEnabled' ,t:b]
	plcMap['PARAM_CAB_WAR_TEMP_EE_CAB_LVL']=[v:'.com.stCabinet.stParameters.nWar_Temperature_EE_cabinet_level' ,t:i]
	plcMap['PARAM_CAB_WAR_TEMP_AMBIANT_LVL']=[v:'.com.stCabinet.stParameters.nWar_Temperature_ambiant_level' ,t:i]
	plcMap['PARAM_CAB_WAR_TEMP_BYPASS_LVL']=[v:'.com.stCabinet.stParameters.nWar_Temperature_bypass_level' ,t:i]
	plcMap['PARAM_CAB_ERR_TEMP_EE_CAB_LVL']=[v:'.com.stCabinet.stParameters.nErr_Temperature_EE_cabinet_level' ,t:i]
	plcMap['PARAM_CAB_ERR_TEMP_AMBIANT_LVL']=[v:'.com.stCabinet.stParameters.nErr_Temperature_ambiant_level' ,t:i]
	plcMap['PARAM_CAB_ERR_TEMP_BYPASS_LVL']=[v:'.com.stCabinet.stParameters.nErr_Temperature_bypass_level' ,t:i]
	plcMap['PARAM_CAB_WAR_AIR_PRESS_LVL']=[v:'.com.stCabinet.stParameters.nWar_air_pressure_level' ,t:i]
	plcMap['PARAM_CAB_ERR_AIR_PRESS_LVL']=[v:'.com.stCabinet.stParameters.nErr_air_pressure_level' ,t:i]
	plcMap['PARAM_CAB_WAR_FANS_EE_CAB_MIN_FREQ']=[v:'.com.stCabinet.stParameters.nWar_Fans_EE_cabinet_MIN_freq' ,t:i]
	plcMap['PARAM_CAB_WAR_FANS_EE_CAB_MAX_FREQ']=[v:'.com.stCabinet.stParameters.nWar_Fans_EE_cabinet_MAX_freq' ,t:i]
	plcMap['PARAM_CAB_WAR_FANS_BYPASS_CAB_MIN_FREQ']=[v:'.com.stCabinet.stParameters.nWar_Fans_Bypass_cabinet_MIN_freq' ,t:i]
	plcMap['PARAM_CAB_WAR_FANS_BYPASS_CAB_MAX_FREQ']=[v:'.com.stCabinet.stParameters.nWar_Fans_Bypass_cabinet_MAX_freq' ,t:i]
	plcMap['PARAM_CAB_FANS_TEMP_ON_THRESHOLD_EE']=[v:'.com.stCabinet.stParameters.nFans_Temp_ON_threshold_EE' ,t:i]
	plcMap['PARAM_CAB_FANS_TEMP_OFF_DELTA_HYST_EE']=[v:'.com.stCabinet.stParameters.nFans_Temp_OFF_delta_hyst_EE' ,t:i]
	plcMap['PARAM_CAB_FANS_TEMP_ON_THRESHOLD_BYPASS']=[v:'.com.stCabinet.stParameters.nFans_Temp_ON_threshold_Bypass' ,t:i]
	plcMap['PARAM_CAB_FANS_TEMP_OFF_DELTA_HYST_BYPASS']=[v:'.com.stCabinet.stParameters.nFans_Temp_OFF_delta_hyst_Bypass' ,t:i]


	plcMap['NTF_CAB_VERSION_HIGH']=[v:'.com.stCabinet.stNotifications.nVersionHigh' ,t:i ]
	plcMap['NTF_CAB_VERSION_MEDIUM']=[v:'.com.stCabinet.stNotifications.nVersionMedium' ,t:i ]
	plcMap['NTF_CAB_VERSION_LOW']=[v:'.com.stCabinet.stNotifications.nVersionLow' ,t:i ]
	plcMap['NTF_CAB_CAB_TRILIGHT_GREEN']=[v:'.com.stCabinet.stNotifications.nCabTrilightGreen' ,t:i ]
	plcMap['NTF_CAB_CAB_TRILIGHT_YELLOW']=[v:'.com.stCabinet.stNotifications.nCabTrilightYellow' ,t:i ]
	plcMap['NTF_CAB_CAB_TRILIGHT_RED']=[v:'.com.stCabinet.stNotifications.nCabTrilightRed' ,t:i ]
	plcMap['NTF_CAB_CX_BOARD_TEMPERATURE']=[v:'.com.stCabinet.stNotifications.nCX_Board_temperature' ,t:by ]
	plcMap['NTF_CAB_CX_CPU_TEMPERATURE']=[v:'.com.stCabinet.stNotifications.nCX_CPU_temperature' ,t:by ]
	plcMap['NTF_CAB_TEMPERATURE_EE_CAB']=[v:'.com.stCabinet.stNotifications.nTemperature_EE_cabinet' ,t:s ]
	plcMap['NTF_CAB_TEMPERATURE_AMBIANT']=[v:'.com.stCabinet.stNotifications.nTemperature_ambiant' ,t:s ]
	plcMap['NTF_CAB_TEMPERATURE_BYPASS']=[v:'.com.stCabinet.stNotifications.nTemperature_bypass' ,t:s ]
	plcMap['NTF_CAB_REL_HUMIDITY_AMBIANT']=[v:'.com.stCabinet.stNotifications.nRelative_humidity_ambiant' ,t:i ]
	plcMap['NTF_CAB_REL_HUMIDITY_VOLTAGE_LVL']=[v:'.com.stCabinet.stNotifications.nRel_humidity_voltage_level' ,t:i ]
	plcMap['NTF_CAB_AIR_PRESS_LVL']=[v:'.com.stCabinet.stNotifications.nAir_pressure_level' ,t:i ]
	plcMap['NTF_CAB_AIR_PRESS_VOLTAGE_LVL']=[v:'.com.stCabinet.stNotifications.nAir_pressure_voltage_level' ,t:i ]
	plcMap['NTF_CAB_WAR_ERR_REGISTER']=[v:'.com.stCabinet.stNotifications.nWar_err_register' ,t:i  ,notif:'true']

	//		 REQUEST
	plcMap['REQUEST_START']=[v:'.com.stMultilineRequests.bStart' ,t:b]
	plcMap['REQUEST_RUN']=[v:'.com.stMultilineRequests.bRun' ,t:b]
	plcMap['REQUEST_STOP']=[v:'.com.stMultilineRequests.bStop' ,t:b]
	plcMap['REQUEST_RELOAD_CONFIG']=[v:'.com.stMultilineRequests.bReloadConfig' ,t:b]
	plcMap['REQUEST_LIFE_CHECK']=[v:'.com.stCabinet.stRequests.bLifeCheck' ,t:b]
	plcMap['REQUEST_JAVA_WARNINGS_AND_ERRORS_REGISTER']=[v:'.com.nJavaWarningsAndErrorsRegister' ,t:i]

	//        OFFLINE
	plcMap['OFFLINE_COUNTING_QTY']=[v:'.offline.counting.qty' ,t:i]
	plcMap['OFFLINE_COUNTING_LAST_STOP']=[v:'.offline.counting.last.stop' ,t:i]
	plcMap['OFFLINE_COUNTING_LAST_PRODUCT']=[v:'.offline.counting.last.product' ,t:i]


	def allVars = new ArrayList<IPlcVariable>()
	def lineParams= new ArrayList<IPlcVariable>()
	def cabParams= new ArrayList<IPlcVariable>()
	def lineNotifs= new ArrayList<IPlcVariable>()
	def cabNotifs= new ArrayList<IPlcVariable>()
	def PLCVarMapInternal=new HashMap<String,String>();

	for ( e in plcMap ) {

		String logic=e.key
		String phy=e.value['v']
		PLCVarMapInternal[logic]= phy

		if(e.value["novar"]=='true'){
			continue
		}

		//create the plc var
		String typeVar=e.value['t']
		IPlcVariable var=null
		PlcVariableDescriptor desc = null

		switch(typeVar){
			case d:
				var = createPlcVar(iCreateVar,phy)
				desc = createPlcDistanceDesc(var,logic,plcMap)
				break
			case i:
				var = createPlcVar(iCreateVar,phy)
				boolean pulseParamConverter=Boolean.parseBoolean(e.value['pulseConvertParam']);
				if(pulseParamConverter){
					desc =createPlcUnitConverterParamDesc(var)
				}else{
					desc = createPlcIntegerDesc(var)
				}
				break
			case b:
				var = createPlcVar(bCreateVar,phy)
				desc = createPlcBooleanDesc(var)
				break
			case s:
				var = createPlcVar(sCreateVar,phy)
				desc = createPlcShortDesc(var)
				break
			case by:
				var = createPlcVar(byCreateVar,phy)
				desc = createPlcByteDesc(var)
				break
		}
		allVars.add(var)

		registerSingleton("${logic}_var",var)
		registerSingleton("${logic}_desc",desc)

		//inject plc provider into the plc var descriptor
		"${logic}_desc_inject"(){bean->
			bean.factoryBean=desc
			bean.factoryMethod='setPlcProvider'
			arguments=[ref('plcProvider')]
		}

		if(logic.startsWith('PARAM_LINE')) {
			lineParams.add(var)
		}
		if(logic.startsWith('NTF_LINE')) {
			lineNotifs.add(var)
		}
		if(logic.startsWith('PARAM_CAB')) {
			cabParams.add(var)
		}
		if(logic.startsWith('NTF_CAB')) {
			cabNotifs.add(var)
		}
	}

	registerSingleton('allPlcVars',allVars)
	registerSingleton('plcLineParamsTemplate',lineParams)
	registerSingleton('plcCabinetParameters',cabParams)
	registerSingleton('plcLineNotifs',cabParams)
	registerSingleton('plcCabNotifs',cabNotifs)
	registerSingleton('plcVarMap',PLCVarMapInternal)



	//---PLC REQUEST---

	def mapRequestRoot= [
		(START):[REQUEST_START:true],
		(RUN):[REQUEST_RUN:true],
		(STOP):[REQUEST_STOP:false],
		(RELOAD_PLC_PARAM):[REQUEST_RELOAD_PLC_PARAM:true]
	]


	Map<PlcRequest, IPlcRequestExecutor> mapRequest = new HashMap<>();
	for(e in mapRequestRoot){
		def key=e.key
		def actions=new ArrayList<IPlcAction>()
		for(reqStep in e.value){
			String var=PLCVarMapInternal[reqStep.key]
			IPlcAction action=PlcAction.request(var,reqStep.value)
			actions.add(action)
		}
		DefaultPlcRequestExecutor exec=new DefaultPlcRequestExecutor(actions.toArray(new IPlcAction[actions.size]))
		mapRequest[key]=exec
	}
	registerSingleton('mapPlcRequestAction',mapRequest)
}


def static IPlcVariable createPlcVar(String method,String physName){
	return PlcVariable."$method"(physName)
}

def static PlcVariableDescriptor createPlcDistanceDesc(IPlcVariable var,String logicVarName, Map<String,String> plcMap){

	String unitVarName=plcMap.get(logicVarName+'_TYPE')['v']
	if(unitVarName==null){
		throw new IllegalArgumentException('unit var not found for '+logicVarName)
	}
	IPlcVariable<Boolean> unitPlcVar = PlcVariable.createBooleanVar(unitVarName, false)

	PlcPulseVariableDescriptor desc= new PlcPulseVariableDescriptor()
	desc.setVariable(var)
	desc.setUnitPlcVar(unitPlcVar)
	desc.setMinPulse(0)
	desc.setMaxPulse(999999)
	desc.setMinMs(0)
	desc.setMaxMs(999999)


	EventBusService.register(desc)
	return desc
}
