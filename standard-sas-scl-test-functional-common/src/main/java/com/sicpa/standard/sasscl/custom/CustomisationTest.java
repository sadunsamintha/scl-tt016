package com.sicpa.standard.sasscl.custom;

import static com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources.addProperties;
import static com.sicpa.standard.sasscl.ioc.BeansName.ACTIVATION;
import static com.sicpa.standard.sasscl.ioc.BeansName.ACTIVATION_CODE_FILTER;
import static com.sicpa.standard.sasscl.ioc.BeansName.ACTIVATION_EXPORT_BEHAVIOR;
import static com.sicpa.standard.sasscl.ioc.BeansName.ACTIVATION_MAINTENANCE_BEHAVIOR;
import static com.sicpa.standard.sasscl.ioc.BeansName.ACTIVATION_STANDARD_BEHAVIOR;
import static com.sicpa.standard.sasscl.ioc.BeansName.ALERT;
import static com.sicpa.standard.sasscl.ioc.BeansName.ALERT_CAMERA_COUNT;
import static com.sicpa.standard.sasscl.ioc.BeansName.ALERT_CAMERA_IDDLE;
import static com.sicpa.standard.sasscl.ioc.BeansName.ALERT_DUPLICATED_CODE;
import static com.sicpa.standard.sasscl.ioc.BeansName.APPLICATION_INITIALIZATION_TASKS;
import static com.sicpa.standard.sasscl.ioc.BeansName.AUTHENTICATOR_PROVIDER;
import static com.sicpa.standard.sasscl.ioc.BeansName.BARCODE_READER;
import static com.sicpa.standard.sasscl.ioc.BeansName.BEAN_CALL_PANEL;
import static com.sicpa.standard.sasscl.ioc.BeansName.CAMERA;
import static com.sicpa.standard.sasscl.ioc.BeansName.CAMERA_SIMULATOR;
import static com.sicpa.standard.sasscl.ioc.BeansName.CRYPTO_FIELDS_CONFIG;
import static com.sicpa.standard.sasscl.ioc.BeansName.CRYPTO_PROVIDER_MANAGER;
import static com.sicpa.standard.sasscl.ioc.BeansName.DEFAULT_SAVE_REMOTELY_CHANGED_BEAN_TASK;
import static com.sicpa.standard.sasscl.ioc.BeansName.ENCODER_PASSWORD_PROVIDER;
import static com.sicpa.standard.sasscl.ioc.BeansName.EXCEPTION_HANDLER;
import static com.sicpa.standard.sasscl.ioc.BeansName.FLOW_CONTROL;
import static com.sicpa.standard.sasscl.ioc.BeansName.MAIN_FRAME;
import static com.sicpa.standard.sasscl.ioc.BeansName.MBEAN_STATS_DELEGATE;
import static com.sicpa.standard.sasscl.ioc.BeansName.MBEAN_VIEWER;
import static com.sicpa.standard.sasscl.ioc.BeansName.MISC_PANEL;
import static com.sicpa.standard.sasscl.ioc.BeansName.MONITORING;
import static com.sicpa.standard.sasscl.ioc.BeansName.MONITORING_MODEL;
import static com.sicpa.standard.sasscl.ioc.BeansName.OFFLINE_COUNTING;
import static com.sicpa.standard.sasscl.ioc.BeansName.OTHER_DEVICES_CONTROLLER;
import static com.sicpa.standard.sasscl.ioc.BeansName.PLC;
import static com.sicpa.standard.sasscl.ioc.BeansName.PLC_PROVIDER;
import static com.sicpa.standard.sasscl.ioc.BeansName.PLC_SIMULATOR;
import static com.sicpa.standard.sasscl.ioc.BeansName.PLC_VARIABLES_PANEL;
import static com.sicpa.standard.sasscl.ioc.BeansName.PLC_VAR_SAVE_REMOTELY_CHANGED_BEAN_TASK;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_BATCH_PROVIDER;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_MODE_MAPPING;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_PARAMETERS;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_STATISTICS_PANEL;
import static com.sicpa.standard.sasscl.ioc.BeansName.REMOTE_CONTROL_MBEAN;
import static com.sicpa.standard.sasscl.ioc.BeansName.REMOTE_SERVER;
import static com.sicpa.standard.sasscl.ioc.BeansName.REMOTE_SERVER_PRODUCT_STATUS_MAPPING;
import static com.sicpa.standard.sasscl.ioc.BeansName.REMOTE_SERVER_SIMULATOR;
import static com.sicpa.standard.sasscl.ioc.BeansName.REMOTE_SERVER_STD;
import static com.sicpa.standard.sasscl.ioc.BeansName.SCHEDULING_REMOTE_SERVER_JOB;
import static com.sicpa.standard.sasscl.ioc.BeansName.SELECTION_MODEL_FACTORY;
import static com.sicpa.standard.sasscl.ioc.BeansName.SKU_LIST_PROVIDER;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATISTICS;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATISTICS_PRODUCTS_STATUS_MAPPER;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATS_MBEAN;
import static com.sicpa.standard.sasscl.ioc.BeansName.STORAGE;
import static com.sicpa.standard.sasscl.ioc.BeansName.SYSTEM_EVENT_PANEL;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.List;

import com.sicpa.standard.camera.controller.ICognexCameraController;
import com.sicpa.standard.camera.controller.model.ICameraModel;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.provider.IProvider;
import com.sicpa.standard.client.common.storage.ISimpleFileStorage;
import com.sicpa.standard.monitor.IMonitorTypesMapping;
import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.business.activation.IActivation;
import com.sicpa.standard.sasscl.business.activation.IActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.IBeforeActivationAction;
import com.sicpa.standard.sasscl.business.activation.offline.IOfflineCounting;
import com.sicpa.standard.sasscl.business.alert.IAlert;
import com.sicpa.standard.sasscl.business.alert.task.IAlertTask;
import com.sicpa.standard.sasscl.business.production.IProduction;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.business.statistics.mapper.IProductStatusToStatisticKeyMapper;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.device.IPlcIndependentDevicesController;
import com.sicpa.standard.sasscl.controller.flow.FlowControl;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.scheduling.RemoteServerScheduledJobs;
import com.sicpa.standard.sasscl.devices.barcode.IBarcodeReaderAdaptor;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;
import com.sicpa.standard.sasscl.devices.camera.transformer.IRoiCameraImageTransformer;
import com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorConfig;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.impl.RemoteServerModel;
import com.sicpa.standard.sasscl.devices.remote.impl.productionmodemapping.DefaultProductionModeMapping;
import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.devices.remote.simulator.ISimulatorGetEncoder;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.monitoring.IMonitoring;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.ISaveRemotlyUpdatedBeanTask;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.CodeListEncoder;
import com.sicpa.standard.sicpadata.spi.manager.IServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.password.UniquePasswordProvider;
import com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider;

public abstract class CustomisationTest extends AbstractFunctionnalTest {

	public void test() throws RemoteServerException, CryptographyException {
		init();

		generateSmallEncoder();
		setProductionParameter(1, 1, ProductionMode.EXPORT);

		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();
		checkBeans();
		stopProduction();
		checkApplicationStatusCONNECTED();

		exit();
	}

	protected void generateSmallEncoder() throws RemoteServerException, CryptographyException {
		IStorage storage = BeanProvider.getBean(BeansName.STORAGE);
		IEncoder encoder = ((ISimulatorGetEncoder) remoteServer).getEncoder(new CodeType(1));
		CodeListEncoder codeListEncoder = new CodeListEncoder(encoder.getId(), 1, encoder.getYear(),
				encoder.getSubsystemId(), Arrays.asList(encoder.getEncryptedCodes(1).get(0)), 1);
		storage.saveCurrentEncoder(codeListEncoder);
	}

	protected void checkBeans() {
		checkabean(ACTIVATION, IActivation.class);
		checkabean(ACTIVATION_CODE_FILTER, IBeforeActivationAction.class);
		checkabean(ACTIVATION_EXPORT_BEHAVIOR, IActivationBehavior.class);
		checkabean(ACTIVATION_MAINTENANCE_BEHAVIOR, IActivationBehavior.class);
		checkabean(ACTIVATION_STANDARD_BEHAVIOR, IActivationBehavior.class);
		checkabean(ALERT, IAlert.class);
		checkabean(ALERT_CAMERA_COUNT, IAlertTask.class);
		checkabean(ALERT_CAMERA_IDDLE, IAlertTask.class);
		checkabean(ALERT_DUPLICATED_CODE, IAlertTask.class);
		checkabean(AUTHENTICATOR_PROVIDER, IProvider.class);
		checkabean(BARCODE_READER, IBarcodeReaderAdaptor.class);

		checkabean(CRYPTO_FIELDS_CONFIG, ICryptoFieldsConfig.class);
		checkabean(CRYPTO_PROVIDER_MANAGER, IServiceProviderManager.class);
		checkabean(DEFAULT_SAVE_REMOTELY_CHANGED_BEAN_TASK, ISaveRemotlyUpdatedBeanTask.class);
		checkabean(OTHER_DEVICES_CONTROLLER, IPlcIndependentDevicesController.class);
		checkabean(ENCODER_PASSWORD_PROVIDER, UniquePasswordProvider.class);
		checkabean(EXCEPTION_HANDLER, UncaughtExceptionHandler.class);
		checkabean(MONITORING, IMonitoring.class);
		checkabean(MONITORING_MODEL, IMonitorTypesMapping.class);

		checkabean(OFFLINE_COUNTING, IOfflineCounting.class);
		checkabean(PLC_VAR_SAVE_REMOTELY_CHANGED_BEAN_TASK, ISaveRemotlyUpdatedBeanTask.class);
		checkabean(FLOW_CONTROL, IFlowControl.class);
		checkabean(PRODUCTION, IProduction.class);
		checkabean(PRODUCTION_MODE_MAPPING, IProductionModeMapping.class);
		checkabean(PLC_PROVIDER, IProvider.class);
		checkabean(PRODUCTION_BATCH_PROVIDER, IProvider.class);
		checkabean(PRODUCTION_PARAMETERS, ProductionParameters.class);
		checkabean(SKU_LIST_PROVIDER, IProvider.class);

		checkabean(REMOTE_SERVER, IRemoteServer.class);
		checkabean(REMOTE_SERVER_PRODUCT_STATUS_MAPPING, IRemoteServerProductStatusMapping.class);
		checkabean(REMOTE_SERVER_SIMULATOR, IRemoteServer.class);
		checkabean(REMOTE_SERVER_STD, IRemoteServer.class);
		checkabean(SCHEDULING_REMOTE_SERVER_JOB, RemoteServerScheduledJobs.class);
		checkabean(SELECTION_MODEL_FACTORY, ISelectionModelFactory.class);
		checkabean(STATISTICS, IStatistics.class);
		checkabean(STATISTICS_PRODUCTS_STATUS_MAPPER, IProductStatusToStatisticKeyMapper.class);
		checkabean(STORAGE, IStorage.class);

	}

	protected void checkabean(String beanName, Class<?> baseInterface) {
		Object o = BeanProvider.getBean(beanName);
		// test that the correct bean exist for the given name
		assertTrue(baseInterface.isAssignableFrom(o.getClass()));
		// test if the customisation impl is taken
		assertTrue("is not a ICUSTOM " + o.getClass(), ICustom.class.isAssignableFrom(o.getClass()));
	}

	@Override
	public void init() {
		initCustomClasses();
		super.init();
	}

	protected void initCustomClasses() {
		addProperties(ACTIVATION, activationCustom.class.getName());
		addProperties(ACTIVATION_EXPORT_BEHAVIOR, exportActivationBehaviorCustom.class.getName());
		addProperties(ACTIVATION_MAINTENANCE_BEHAVIOR, maintenanceActivationBehaviorCustom.class.getName());
		addProperties(ACTIVATION_STANDARD_BEHAVIOR, standardActivationBehaviorCustom.class.getName());
		addProperties(ACTIVATION_CODE_FILTER, activationCodeFilterCustom.class.getName());
		addProperties(ALERT, alertCustom.class.getName());
		addProperties(ALERT_CAMERA_COUNT, alertCameraCountCustom.class.getName());
		addProperties(ALERT_DUPLICATED_CODE, alertDuplicatedCodeCustom.class.getName());
		addProperties(ALERT_CAMERA_IDDLE, alertCameraIddleCustom.class.getName());
		addProperties(CAMERA, cameraCustom.class.getName());
		addProperties(CAMERA_SIMULATOR, cameraSimulatorCustom.class.getName());
		addProperties(OTHER_DEVICES_CONTROLLER, devicesControllerCustom.class.getName());
		addProperties(PLC, plcCustom.class.getName());
		addProperties(PLC_SIMULATOR, plcSimulatorCustom.class.getName());
		addProperties(FLOW_CONTROL, flowControllCustom.class.getName());
		addProperties(PRODUCTION, productionCustom.class.getName());
		addProperties(REMOTE_SERVER_SIMULATOR, remoteServerSimulatorCustom.class.getName());
		addProperties(REMOTE_SERVER_STD, stdRemoteServerCustom.class.getName());
		addProperties(SCHEDULING_REMOTE_SERVER_JOB, remoteServerSheduledJobsCustom.class.getName());
		addProperties(STATISTICS, statisticsCustom.class.getName());
		addProperties(STATISTICS_PRODUCTS_STATUS_MAPPER, productStatusToStatisticsKeyMapperCustom.class.getName());
		addProperties(STORAGE, storageCustom.class.getName());
		addProperties(EXCEPTION_HANDLER, exceptionHandlerCustom.class.getName());
		addProperties(MONITORING, monitoringCustom.class.getName());
		addProperties(MONITORING_MODEL, monitoringModelCustom.class.getName());
		addProperties(STATS_MBEAN, statsMBeanCustom.class.getName());
		addProperties(REMOTE_CONTROL_MBEAN, remoteControlMBeanCustom.class.getName());
		addProperties(MBEAN_STATS_DELEGATE, MBeanStatsDelegateCustom.class.getName());
		addProperties(MBEAN_VIEWER, MBeanViewerCustom.class.getName());
		addProperties(PRODUCTION_STATISTICS_PANEL, productionStatisticsPanelCustom.class.getName());
		addProperties(SYSTEM_EVENT_PANEL, systemEventPanelCustom.class.getName());
		addProperties(MISC_PANEL, miscPanelCustom.class.getName());
		addProperties(PLC_VARIABLES_PANEL, plcVariablesPanelCustom.class.getName());
		addProperties(MAIN_FRAME, mainFrameCustom.class.getName());
		addProperties(BEAN_CALL_PANEL, beanCallPanelGetterCustom.class.getName());
		addProperties(CRYPTO_FIELDS_CONFIG, cryptoFieldsConfigCustom.class.getName());
		addProperties(SELECTION_MODEL_FACTORY, selectionModelFactoryCustom.class.getName());
		addProperties(DEFAULT_SAVE_REMOTELY_CHANGED_BEAN_TASK, defaultSaveRemotelyChangedBeanTaskCustom.class.getName());
		addProperties(PLC_VAR_SAVE_REMOTELY_CHANGED_BEAN_TASK, plcVarSaveRemotelyChangedBeanTaskCustom.class.getName());
		addProperties(OFFLINE_COUNTING, offlineCountingCustom.class.getName());
		addProperties(REMOTE_SERVER_PRODUCT_STATUS_MAPPING, remoteServerProductStatusMappingCustom.class.getName());
		addProperties(APPLICATION_INITIALIZATION_TASKS, applicationInitializationTasksCustom.class.getName());
		addProperties(ENCODER_PASSWORD_PROVIDER, encoderPasswordProviderCustom.class.getName());
		addProperties(CRYPTO_PROVIDER_MANAGER, cryptoProviderManagerCustom.class.getName());
		addProperties(AUTHENTICATOR_PROVIDER, authenticatorProviderCustom.class.getName());
		addProperties(PLC_PROVIDER, plcProviderCustom.class.getName());
		addProperties(PRODUCTION_BATCH_PROVIDER, productionBatchProviderCustom.class.getName());
		addProperties(PRODUCTION_PARAMETERS, productionParameterCustom.class.getName());
		addProperties(SKU_LIST_PROVIDER, productionParametersProviderCustom.class.getName());
		addProperties(PRODUCTION_MODE_MAPPING, DefaultProductionModeMappingCustom.class.getName());
	}

	public interface ICustom {
	}

	// --------------------------

	public static class activationCustom extends com.sicpa.standard.sasscl.business.activation.impl.Activation
			implements ICustom {

		public activationCustom() {
			super();
		}
	}

	public static class exportActivationBehaviorCustom extends
			com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.ExportActivationBehavior implements
			ICustom {
	}

	public static class maintenanceActivationBehaviorCustom extends
			com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.MaintenanceActivationBehavior
			implements ICustom {
	}

	public static class standardActivationBehaviorCustom extends
			com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.StandardActivationBehavior
			implements ICustom {
	}

	public static class activationCodeFilterCustom extends
			com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.FilterDuplicatedCodeAction
			implements ICustom {
	}

	public static class alertCustom extends com.sicpa.standard.sasscl.business.alert.impl.Alert implements ICustom {
	}

	public static class alertCameraCountCustom extends
			com.sicpa.standard.sasscl.devices.camera.alert.CameraCountAlertTask implements ICustom {
		public alertCameraCountCustom() {
			super();
		}
	}

	public static class alertDuplicatedCodeCustom extends
			com.sicpa.standard.sasscl.devices.camera.alert.DuplicatedCodeAlertTask implements ICustom {
	}

	public static class alertCameraIddleCustom extends
			com.sicpa.standard.sasscl.devices.camera.alert.CameraIddleAlertTask implements ICustom {
		public alertCameraIddleCustom() {
			super();
		}
	}

	public static class cameraCustom extends com.sicpa.standard.sasscl.devices.camera.CameraAdaptor implements ICustom {

		public cameraCustom() {
			super();
		}

		public cameraCustom(ICognexCameraController<? extends ICameraModel> controller, IRoiCameraImageTransformer tr) {
			super(controller, tr);
		}
	}

	public static class cameraSimulatorCustom extends
			com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController implements ICustom {
		public cameraSimulatorCustom() {
			super();
		}

		public cameraSimulatorCustom(CameraSimulatorConfig config) {
			super(config);
		}
	}

	public static class devicesControllerCustom extends
			com.sicpa.standard.sasscl.controller.device.impl.OtherDevicesController implements ICustom {
	}

	public static class plcCustom extends com.sicpa.standard.sasscl.devices.plc.impl.PlcAdaptor implements ICustom {

		public plcCustom() {
			super();
		}

		public plcCustom(IPlcController<?> controller) {
			super(controller);
		}
	}

	public static class plcSimulatorCustom extends
			com.sicpa.standard.sasscl.devices.plc.simulator.PlcSimulatorController implements ICustom {

		public plcSimulatorCustom() {
			super();
		}

		public plcSimulatorCustom(PlcSimulatorConfig config, String title) {
			super(config, title);
		}

		public plcSimulatorCustom(PlcSimulatorConfig config) {
			super(config);
		}

	}

	public static class flowControllCustom extends FlowControl implements ICustom {
	}

	public static class productionCustom extends com.sicpa.standard.sasscl.business.production.impl.Production
			implements ICustom {

		public productionCustom(IStorage storage, IRemoteServer remoteServer) {
			super(storage, remoteServer);
		}
	}

	public static class remoteServerSimulatorCustom extends
			com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorNoCoding implements ICustom {

		public remoteServerSimulatorCustom(RemoteServerSimulatorModel model) throws RemoteServerException {
			super(model);
		}

		public remoteServerSimulatorCustom(String configFile) throws RemoteServerException {
			super(configFile);
		}
	}

	public static class stdRemoteServerCustom extends com.sicpa.standard.sasscl.devices.remote.impl.RemoteServer
			implements ICustom {

		public stdRemoteServerCustom(RemoteServerModel remoteServerModel) {
			super(remoteServerModel);
		}

		public stdRemoteServerCustom(String configFile) {
			super(configFile);
		}

	}

	public static class remoteServerSheduledJobsCustom extends
			com.sicpa.standard.sasscl.controller.scheduling.RemoteServerScheduledJobs implements ICustom {

		public remoteServerSheduledJobsCustom(IStorage storage, IRemoteServer remoteServer,
				SkuListProvider productionParametersProvider, AuthenticatorProvider authenticatorProvider) {
			super(storage, remoteServer, productionParametersProvider, authenticatorProvider);
		}
	}

	public static class productionScopeCustom extends com.sicpa.standard.client.common.ioc.session.SessionScope
			implements ICustom {

		public productionScopeCustom(String beanName) {
			super(beanName);
		}
	}

	public static class sessionCustom extends com.sicpa.standard.client.common.ioc.session.Session implements ICustom {

		public sessionCustom(List<Object>... beans) {
			super(beans);
		}

		public sessionCustom(Object... beans) {
			super(beans);
		}
	}

	public static class statisticsCustom extends com.sicpa.standard.sasscl.business.statistics.impl.Statistics
			implements ICustom {

		public statisticsCustom(IStorage storage) {
			super(storage);
		}
	}

	public static class productStatusToStatisticsKeyMapperCustom extends
			com.sicpa.standard.sasscl.business.statistics.mapper.DefaultProductStatusToStatisticsKeyMapper implements
			ICustom {
	}

	public static class storageCustom extends com.sicpa.standard.sasscl.common.storage.FileStorage implements ICustom {

		public storageCustom(String baseFolder, String internalFolder, String quarantineFolder,
				ISimpleFileStorage storageBehavior) {
			super(baseFolder, internalFolder, quarantineFolder, storageBehavior);
		}
	}

	public static class exceptionHandlerCustom extends com.sicpa.standard.sasscl.common.exception.ExceptionHandler
			implements ICustom {

		public exceptionHandlerCustom() {
			super();
		}
	}

	public static class monitoringCustom extends com.sicpa.standard.sasscl.monitoring.impl.Monitoring implements
			ICustom {

		public monitoringCustom(IMonitorTypesMapping mapping) {
			super(mapping);
		}
	}

	public static class monitoringModelCustom extends
			com.sicpa.standard.sasscl.monitoring.impl.DefaultMonitoringSerializerMapping implements ICustom {
	}

	public static class statsMBeanCustom extends com.sicpa.standard.sasscl.monitoring.mbean.sas.SasApp implements
			ICustom {
	}

	public static class remoteControlMBeanCustom extends
			com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSas implements ICustom {
	}

	@SuppressWarnings("serial")
	public static class MBeanStatsDelegateCustom extends
			com.sicpa.standard.sasscl.monitoring.mbean.sas.SasAppBeanStatistics implements ICustom {
	}

	public static class MBeanViewerCustom extends com.sicpa.standard.sasscl.view.monitoring.mbean.MBeanViewerGetter
			implements ICustom {
	}

	public static class productionStatisticsPanelCustom extends
			com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsPanelGetter implements ICustom {
	}

	public static class systemEventPanelCustom extends com.sicpa.standard.sasscl.view.monitoring.SystemEventPanelGetter
			implements ICustom {
	}

	public static class miscPanelCustom extends com.sicpa.standard.sasscl.view.config.misc.MiscConfigPanelGetter
			implements ICustom {
	}

	public static class plcVariablesPanelCustom extends
			com.sicpa.standard.sasscl.view.config.plc.PlcVariablesPanelGetter implements ICustom {
	}

	public static class mainFrameCustom extends com.sicpa.standard.sasscl.view.MainFrameGetter implements ICustom {
	}

	public static class beanCallPanelGetterCustom extends com.sicpa.standard.sasscl.view.forceCall.BeanCallPanelGetter
			implements ICustom {
	}

	public static class cryptoFieldsConfigCustom extends
			com.sicpa.standard.sasscl.devices.remote.stdCrypto.CryptoFieldsConfig implements ICustom {
	}

	public static class selectionModelFactoryCustom extends
			com.sicpa.standard.sasscl.productionParameterSelection.DefaultSelectionModelFactory implements ICustom {
	}

	public static class defaultSaveRemotelyChangedBeanTaskCustom extends
			com.sicpa.standard.sasscl.monitoring.mbean.sas.DefaultSaveRemotelyUpdateBeanTask implements ICustom {
	}

	public static class plcVarSaveRemotelyChangedBeanTaskCustom extends
			com.sicpa.standard.sasscl.devices.plc.variable.SavePlcVarRemotlyChanged implements ICustom {
	}

	public static class offlineCountingCustom extends
			com.sicpa.standard.sasscl.business.activation.offline.impl.PlcOfflineCounting implements ICustom {
	}

	public static class remoteServerProductStatusMappingCustom extends
			com.sicpa.standard.sasscl.devices.remote.impl.statusmapping.DefaultRemoteServerProductStatusMapping
			implements ICustom {
	}

	public static class applicationInitializationTasksCustom extends
			com.sicpa.standard.sasscl.ApplicationInitializationTasks implements ICustom {
	}

	public static class encoderPasswordProviderCustom extends
			com.sicpa.standard.sicpadata.spi.password.UniquePasswordProvider implements ICustom {

		public encoderPasswordProviderCustom(String password) {
			super(password);
		}
	}

	public static class cryptoProviderManagerCustom extends
			com.sicpa.standard.sasscl.sicpadata.CryptoServiceProviderManager implements ICustom {

		public cryptoProviderManagerCustom(UniquePasswordProvider passwordProvider,
				FileSequenceStorageProvider fileSequenceStorageProvider) {
			super(passwordProvider, fileSequenceStorageProvider);
		}
	}

	public static class authenticatorProviderCustom extends AuthenticatorProvider implements ICustom {
	}

	public static class plcProviderCustom extends PlcProvider implements ICustom {
	}

	public static class productionBatchProviderCustom extends
			com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider implements ICustom {
	}

	public static class productionParametersProviderCustom extends SkuListProvider implements ICustom {
	}

	public static class DefaultProductionModeMappingCustom extends DefaultProductionModeMapping implements ICustom {

	}

	@SuppressWarnings("serial")
	public static class productionParameterCustom extends ProductionParameters implements ICustom {

	}

}
