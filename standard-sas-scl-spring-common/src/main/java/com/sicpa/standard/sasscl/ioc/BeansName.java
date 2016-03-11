package com.sicpa.standard.sasscl.ioc;

public interface BeansName {

	String PLC_VALUES_LOADER = "plcValuesLoader";

	/**
	 * IActivation
	 */
	String ACTIVATION = "activation";
	/**
	 * IActivationBehavior
	 */
	String ACTIVATION_EXPORT_BEHAVIOR = "exportActivationBehavior";
	/**
	 * IActivationBehavior
	 */
	String ACTIVATION_MAINTENANCE_BEHAVIOR = "maintenanceActivationBehavior";
	/**
	 * IActivationBehavior
	 */
	String ACTIVATION_STANDARD_BEHAVIOR = "standardActivationBehavior";
	/**
	 * IBeforeActivationAction
	 */
	String ACTIVATION_CODE_FILTER = "activationCodeFilter";
	/**
	 * IAlert
	 */
	String ALERT = "alert";
	/**
	 * IAlertTask
	 */
	String ALERT_CAMERA_COUNT = "alertCameraCount";
	/**
	 * IAlertTask
	 */
	String ALERT_DUPLICATED_CODE = "alertDuplicatedCode";
	/**
	 * IAlertTask
	 */
	String ALERT_CAMERA_IDDLE = "alertCameraIddle";
	/**
	 * IProvider
	 */
	String AUTHENTICATOR_PROVIDER = "authenticatorProvider";
	/**
	 * IBarcodeReaderAdaptor
	 */
	String BARCODE_READER = "barcodeReader";
	/**
	 * ICameraAdaptor
	 */
	String CAMERA = "camera";
	/**
	 * ICameraController
	 */
	String CAMERA_SIMULATOR = "cameraSimulatorController";
	String CAMERA_MODEL_STD = "stdCameraModel";
	/**
	 * ICoding
	 */
	String CODING = "coding";

	/**
	 * IGroupDevicesController
	 */
	String DEVICES_GROUP_STARTUP = "startupDevicesGroup";
	/**
	 * IPrioritizedGroupDevicesController
	 */
	String ACTIVATION_GROUP_CONTROLLER = "productionDevicesGroupActivation";
	/**
	 * IPrioritizedGroupDevicesController
	 */
	String CODING_GROUP_CONTROLLER = "productionDevicesGroupCoding";
	/**
	 * <li>GlobalBean in SAS <li>GlobalConfigSCL in SCL
	 * 
	 */
	String GLOBAL_CONFIG = "globalConfig";
	/**
	 * IPlcAdaptor
	 */
	String PLC = "plc";
	/**
	 * IProvider
	 */
	String PLC_PROVIDER = "plcProvider";
	String PLC_MODEL = "stdPlcModel";
	/**
	 * IPlcController
	 */
	String PLC_SIMULATOR = "plcSimulator";
	String PLC_SIMULATOR_MODEL = "plcSimulatorModel";
	/**
	 * IPrinterAdaptor
	 */
	String PRINTER = "printer";
	String PRINTER_MODEL_STD = "stdPrinterModel";
	/**
	 * IPrinterController
	 */
	String PRINTER_SIMULATOR = "printerSimulatorController";
	/**
	 * IFlowcontrol
	 */
	String FLOW_CONTROL = "flowControl";
	String PROCESS_REPORT = "processReport";
	/**
	 * IProduction
	 */
	String PRODUCTION = "production";
	/**
	 * IProvider
	 */
	String PRODUCTION_BATCH_PROVIDER = "productionBatchProvider";
	/**
	 * ProductionParameter
	 */
	String PRODUCTION_PARAMETERS = "productionParameters";
	/**
	 * SkuListProvider
	 */
	String SKU_LIST_PROVIDER = "skuListProvider";
	/**
	 * IRemoteServer
	 */
	String REMOTE_SERVER = "remoteServer";
	/**
	 * RemoteServerModel
	 */
	String REMOTE_SERVER_STD_MODEL = "stdRemoteModel";
	/**
	 * RemoteServerSimulatorModel
	 */
	String REMOTE_SERVER_SIMULATOR_MODEL = "simulatorRemoteModel";
	/**
	 * IRemoteServer
	 */
	String REMOTE_SERVER_SIMULATOR = "remoteServerSimulator";
	/**
	 * IRemoteServer
	 */
	String REMOTE_SERVER_STD = "stdRemoteServer";
	/**
	 * ThreadPoolTaskScheduler
	 */
	String SCHEDULER = "SchedulerSasScl";
	/**
	 * <li>RemoteServerScheduledJobs in SAS <li>RemoteServerScheduledJobsSCL in SCL
	 */
	String SCHEDULING_REMOTE_SERVER_JOB = "remoteServerSheduledJobs";
	/**
	 * IStatistics
	 */
	String STATISTICS = "statistics";
	/**
	 * IProductStatusToStatisticKeyMapper
	 */
	String STATISTICS_PRODUCTS_STATUS_MAPPER = "productStatusToStatisticsKeyMapper";
	/**
	 * IStorage
	 */
	String STORAGE = "storage";
	/**
	 * MainFrameController
	 */
	String MAIN_FRAME_CONTROLLER = "mainFrameController";
	/**
	 * UncaughtExceptionHandler
	 */
	String EXCEPTION_HANDLER = "exceptionHandler";

	/**
	 * IMonitoring
	 */
	String MONITORING = "monitoring";
	/**
	 * IMonitorTypesMapping
	 */
	String MONITORING_MODEL = "monitoringModel";
	String STATS_MBEAN = "statsMBean";
	String REMOTE_CONTROL_MBEAN = "remoteControlMBean";
	String MBEAN_STATS_DELEGATE = "MBeanStatsDelegate";

	String MBEAN_VIEWER = "MBeanViewer";
	String PRODUCTION_STATISTICS_PANEL = "productionStatisticsPanel";
	String SYSTEM_EVENT_PANEL = "systemEventPanel";
	String MISC_PANEL = "miscPanel";
	String PLC_VARIABLES_PANEL = "plcVariablesPanel";
	String MAIN_FRAME = "mainFrame";
	String BEAN_CALL_PANEL = "beanCallPanelGetter";
	String BEAN_CALL_LIST = "beanCallList";

	/**
	 * IPostPackage
	 */
	String POST_PACKAGE = "postPackage";
	/**
	 * IPostPackageBehavior
	 */
	String POST_PACKAGE_BEHAVIOR = "postPackageBehavior";

	/**
	 * ICryptoFieldsConfig
	 */
	String CRYPTO_FIELDS_CONFIG = "cryptoFieldsConfig";

	/**
	 * ISelectionModelFactory
	 */
	String SELECTION_MODEL_FACTORY = "selectionModelFactory";

	/**
	 * IOfflineCounting
	 */
	String OFFLINE_COUNTING = "offlineCounting";

	/**
	 * IRemoteServerProductStatusMapping
	 */
	String REMOTE_SERVER_PRODUCT_STATUS_MAPPING = "remoteServerProductStatusMapping";

	/**
	 * IProductionModeDeviceGroupMapping
	 */
	String PRODUCTION_MODE_DEVICEGROUP_MAPPING = "productionModeDeviceGroupMapping";

	/**
	 * IBootstrap
	 */
	String BOOTSTRAP = "bootstrap";

	/**
	 * UniquePasswordProvider
	 */
	String ENCODER_PASSWORD_PROVIDER = "encoderPasswordProvider";
	/**
	 * IServiceProviderManager
	 */
	String CRYPTO_PROVIDER_MANAGER = "cryptoProviderManager";
	/**
	 * IProductionModeMapping
	 */
	String PRODUCTION_MODE_MAPPING = "productionModeMapping";

	/**
	 * Map<String-logicalVarName,String-nameInPlc>
	 */
	String PLC_VAR_MAPPING = "plcVarMap";

	/**
	 * IMessagesMapping
	 */
	String MESSAGES_MAPPING = "messagesMapping";

	/**
	 * IXStreamConfigurator
	 */
	String XSTREAM_CONFIGURATOR = "xstreamConfigurator";

	/**
	 * ApplicationInitializedProvider
	 */
	String APPLICATION_INITIALIZED_PROVIDER = "appInitDoneProvider";

	/**
	 * IIncomingSDGenStorageSpi<br>
	 * storage impl for the sicpa data fail over lib for std dms
	 */
	String SICPADATA_GENERATOR_STORAGE = "SicpaDataGeneratorStorage";

	/**
	 * ISicpaDataGeneratorRequestor<br>
	 * encoder requestor for std dms
	 */
	String SICPADATA_GENERATOR_REQUESTOR = "sicpaDataGeneratorRequestor";

	/**
	 * ISCLActivationInfos<br>
	 * 
	 */
	String ENCODER_VALIDATOR = "encoderSubsystemIdValidator";

	/**
	 * ISCLActivationInfos<br>
	 * used in JMX, remote monitoring
	 */
	String JMX_INFORMATION_HOLDER = "jmxInformationHolder";

	/**
	 * IDeviceFactory
	 */
	String DEVICE_FACTORY = "deviceFactory";

	String BIS = "bis";
	String IMPLEMENTATION_PROVIDER = "implementationProvider";
	/**
	 * IdeviceIncidentContext
	 */
	String ERRORS_REPOSITORY = "errorsRepository";

	String SUBSYSTEM_ID_PROVIDER = "subsystemIdProvider";

	/**
	 * IMappingExtendedCodeBehavior
	 */
	String MAPPING_EXTENDED_CODE_BEHAVIOR = "mappingExtendedCodeBehavior";
}
