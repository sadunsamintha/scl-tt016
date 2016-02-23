package com.sicpa.standard.sasscl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.sicpa.standard.client.common.app.profile.LoaderConfigWithProfile;
import com.sicpa.standard.client.common.config.history.BeanHistoryManager;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.client.common.descriptor.validator.Validators;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorsException;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.groovy.GroovyLoggerConfigurator;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.ioc.PropertiesFile;
import com.sicpa.standard.client.common.launcher.CommonMainApp;
import com.sicpa.standard.client.common.launcher.display.IProgressDisplay;
import com.sicpa.standard.client.common.launcher.spring.ILoadingMonitor;
import com.sicpa.standard.client.common.launcher.spring.impl.DefaultLoadingMonitor;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.client.common.xstream.IXStreamConfigurator;
import com.sicpa.standard.gui.screen.loader.AbstractApplicationLoader;
import com.sicpa.standard.gui.screen.machine.impl.SPL.AbstractSplFrame;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.statistics.StatisticsRestoredEvent;
import com.sicpa.standard.sasscl.business.statistics.impl.Statistics;
import com.sicpa.standard.sasscl.common.log.ILoggerBehavior;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.common.utils.LangUtils;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.device.IPlcIndependentDevicesController;
import com.sicpa.standard.sasscl.controller.device.group.DevicesGroup;
import com.sicpa.standard.sasscl.controller.scheduling.RemoteServerScheduledJobs;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcVariableMap;
import com.sicpa.standard.sasscl.devices.plc.variable.EditablePlcVariables;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesForAllVar;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.event.ApplicationVersionEvent;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.ioc.PropertyPlaceholderResourcesSASSCL;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.sasscl.view.lineid.LineIdWithAuthenticateButton;
import com.sicpa.standard.sicpadata.spi.manager.SimpleServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.manager.StaticServiceProviderManager;

public class MainApp extends CommonMainApp<LoaderConfigWithProfile> {

	private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

	private Properties loadedProperties;

	public MainApp() {
		PropertyPlaceholderResourcesSASSCL.init();
	}

	@Override
	public AbstractApplicationLoader createLoader(LoaderConfigWithProfile config, String... profiles) {
		return new Loader(config, true, profiles);
	}

	protected void executeModelValidator(MainFrameController controller) {
		if (controller != null) {
			Validators vali = controller.getBeanValidators();
			try {
				if (vali != null) {
					vali.validateAll();
				}
			} catch (ValidatorsException e) {
				for (final ValidatorException ex : e.getValidatorExceptions()) {

					// for each validator exception show it on the gui
					EventBusService.post(new MessageEvent(this, ex.getLangKey(), ex.getSource(), ex, ex.getParams()));
				}
			}
		}
	}

	@Override
	protected void initLog() {
		GroovyLoggerConfigurator lc = new GroovyLoggerConfigurator(new StringMap());
		lc.initLogger();
	}

	protected void executePlcVarValidator() {
		EditablePlcVariables plcvars = BeanProvider.getBean(BeansName.PLC_EDITABLE_VARIABLES);
		plcvars.validate();
	}

	protected void showMainFrame(MainFrameController controller) {
		if (controller != null) {
			logger.info("application version:" + getApplicationVersion());
			EventBusService.post(new ApplicationVersionEvent(getApplicationVersion()));
			controller.setVisible(true);
		}
	}

	protected void connectRemoteServer() {
		IPlcIndependentDevicesController controller = BeanProvider.getBean(BeansName.OTHER_DEVICES_CONTROLLER);
		controller.startDevicesGroup(DevicesGroup.STARTUP_GROUP);
	}

	private void connectPlcSecure() {
		final IPlcAdaptor plcSecure = BeanProvider.getBean(BeansName.PLC_SECURE);
		final PlcProvider plcSecProvider = BeanProvider.getBean(BeansName.PLC_SEC_PROVIDER);
		plcSecProvider.set(plcSecure);

		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					plcSecure.connect();
				} catch (final DeviceException e) {
					logger.error("Error while starting the PLC Secure module", e);
				}
			}
		});
	}

	protected void restorePreviousSelectedProductionParams() {
		IStorage storage = BeanProvider.getBean(BeansName.STORAGE);
		ProductionParameters productionParameters = BeanProvider.getBean(BeansName.PRODUCTION_PARAMETERS);
		ProductionParameters previous = storage.getSelectedProductionParameters();

		if (previous != null) {
			productionParameters.setBarcode(previous.getBarcode());
			productionParameters.setSku(previous.getSku());
			productionParameters.setProductionMode(previous.getProductionMode());
			EventBusService.post(new ProductionParametersEvent(previous));
		}
	}

	protected MainFrame getMainFrame() {
		IGUIComponentGetter compGetter = BeanProvider.getBean(BeansName.MAIN_FRAME);
		return (MainFrame) compGetter.getComponent();
	}

	private void initLanguage() {
		LangUtils.initLanguageFiles(getProperties().getProperty("language"));
	}

	@SuppressWarnings("rawtypes")
	protected void initPlc() {

		Map<String, String> plcVarMap = BeanProvider.getBean(BeansName.PLC_VAR_MAPPING);
		PlcVariableMap.addPlcVariables(plcVarMap);
		Map<String, String> plcSecureVarMap = BeanProvider.getBean(BeansName.PLC_SECURE_VAR_MAPPING);
		PlcVariableMap.addPlcVariables(plcSecureVarMap);

		IPlcValuesLoader loader = BeanProvider.getBean(BeansName.PLC_VALUES_LOADER);
		List<IPlcVariable> variables = BeanProvider.getBean(BeansName.PLC_PARAMETERS);
		PlcValuesForAllVar values = BeanProvider.getBean(BeansName.PLC_CABINET_VARIABLES_VALUE);

		loader.load(variables, values);

		List<IPlcVariable> variablesSecure = BeanProvider.getBean(BeansName.PLC_SECURE_PARAMETERS);
		PlcValuesForAllVar valuesSecure = BeanProvider.getBean(BeansName.PLC_SECURE_VARIABLE_VALUES);

		loader.load(variablesSecure, valuesSecure);
	}

	protected void initOperatorLogger() {
		ILoggerBehavior loggerBehavior = BeanProvider.getBean(BeansName.OPERATOR_LOGGER);
		OperatorLogger.setLoggerBehavior(loggerBehavior);
	}

	protected void initProductionParameter(IStorage storage) {
		SkuListProvider ppc = BeanProvider.getBean(BeansName.SKU_LIST_PROVIDER);
		ppc.set(storage.getProductionParameters());
	}

	protected void initAuthenticator(IStorage storage) {
		AuthenticatorProvider authProvider = BeanProvider.getBean(BeansName.AUTHENTICATOR_PROVIDER);
		authProvider.set(storage.getAuthenticator());
	}

	protected void initRemoteServerConnected() {
		getLineIdFromRemoteServerAndSaveItLocally();

		RemoteServerScheduledJobs remoteJobs = BeanProvider.getBean(BeansName.SCHEDULING_REMOTE_SERVER_JOB);
		remoteJobs.executeInitialTasks();
	}

	protected void getLineIdFromRemoteServerAndSaveItLocally() {
		long id = getLineIdFromRemoteServer();
		SubsystemIdProvider subsystemIdProvider = BeanProvider.getBean(BeansName.SUBSYSTEM_ID_PROVIDER);
		subsystemIdProvider.set(id);
		saveSubsystemId(id);
	}

	private void saveSubsystemId(long id) {
		try {
			
			
			
			File globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();

			PropertiesUtils.savePropertiesKeepOrderAndComment(globalPropertiesFile, "subsystemId", Long.toString(id));
			PropertiesUtils.savePropertiesKeepOrderAndComment(globalPropertiesFile, "lineId", Long.toString(id));

		} catch (Exception ex) {
			logger.error("Failed to Get and Save Line Id", ex);
		}
	}

	private long getLineIdFromRemoteServer() {
		IRemoteServer remote = BeanProvider.getBean(BeansName.REMOTE_SERVER);
		return remote.getSubsystemID();
	}

	protected void restoreStatistics(IStorage storage) {
		Statistics stats = BeanProvider.getBean(BeansName.STATISTICS);
		StatisticsValues statsValues = storage.getStatistics();
		boolean restored = false;
		if (statsValues != null) {
			logger.debug("Restoring statistics {} ", statsValues.getMapValues());
			restored = true;
		} else {
			statsValues = new StatisticsValues();
		}
		stats.setValues(statsValues);
		if (restored) {
			EventBusService.post(new StatisticsRestoredEvent(statsValues));
		}
	}

	protected void initCustomGuiComponent() {
		AbstractSplFrame.mapPanelClasses.put(AbstractSplFrame.KEY_LINE_ID, LineIdWithAuthenticateButton.class);
	}

	@Override
	protected void doInitEnd() {

		try {
			IStorage storage = BeanProvider.getBean(BeansName.STORAGE);

			restoreStatistics(storage);

			initPlc();

			// needed to init the selection model for the gui
			initProductionParameter(storage);

			initAuthenticator(storage);
			initCrypto();

			handleHistoryBean();

			// --add listener on remote server to query the remote server when
			// connected
			final IRemoteServer server = BeanProvider.getBean(BeansName.REMOTE_SERVER);
			IDeviceStatusListener lis = new IDeviceStatusListener() {
				@Override
				public void deviceStatusChanged(final DeviceStatusEvent evt) {
					if (evt.getDevice() instanceof IRemoteServer && evt.getStatus() == DeviceStatus.CONNECTED) {
						initRemoteServerConnected();
					}
				}
			};
			server.addDeviceStatusListener(lis);

		} catch (Exception e) {
			logger.error("", e);
		}

		ApplicationInitializationTasks initTasksBean = BeanProvider.getBean(BeansName.APPLICATION_INITIALIZATION_TASKS);
		// used by optional module to add some init task after spring has been initialized
		for (Runnable task : initTasksBean.getInitTasks()) {
			task.run();
		}

		initOperatorLogger();

		initCustomGuiComponent();

		MainFrame main = getMainFrame();
		MainFrameController controller = null;
		if (main != null) {
			controller = main.getController();
		}

		// this need the view so cannot put in init
		connectRemoteServer();

		connectPlcSecure();

		// restoreStatistics();

		// this need the view so cannot put in init
		restorePreviousSelectedProductionParams();

		showMainFrame(controller);

		executeModelValidator(controller);

		executePlcVarValidator();

		logger.info("Application is started");
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.INFO, SystemEventType.APP_STARTED));
	}

	@Override
	protected void initSpring(LoaderConfigWithProfile config, String... profiles) {

		setLoadingProgress("init event bus", 0);
		initEventBus();
		setLoadingProgress("init xtream", 0);
		initXstreamConfig();
		setLoadingProgress("init language", 0);

		initLanguage();

		setLoadingProgress("init spring", 10);

		BeanProvider.initSpring(config.getContext(), createProgressMonitor(progressDisplay));
		if (config.getInitTask() != null) {
			config.getInitTask().run();
		}

	}

	protected void initCrypto() {
		SimpleServiceProviderManager provider = BeanProvider.getBean(BeansName.CRYPTO_PROVIDER_MANAGER);
		StaticServiceProviderManager.register(provider);
	}

	protected void handleHistoryBean() {
		BeanHistoryManager manager = BeanProvider.getBean(BeansName.BEAN_HISTORY_MANAGER);
		manager.checkForModification();
	}

	private void initEventBus() {
		initContext("spring/eventBus.xml");
	}

	private void initXstreamConfig() {
		try {
			// it has to be executed before the main spring has been initialized
			ClassPathXmlApplicationContext context = initContext("spring/xstream.xml");
			IXStreamConfigurator configurator = (IXStreamConfigurator) context.getBean(BeansName.XSTREAM_CONFIGURATOR);
			configurator.configure(ConfigUtils.getXStream());
			context.close();
		} catch (Exception e) {
			logger.error("", e);
			System.exit(-1);
		}
	}

	protected Properties getProperties() {
		if (loadedProperties != null) {
			return loadedProperties;
		}

		loadedProperties = new PropertiesFile(PropertyPlaceholderResourcesSASSCL.getAllFiles(),
				PropertyPlaceholderResourcesSASSCL.getAllFolders(),
				PropertyPlaceholderResourcesSASSCL.getOverridingProperties());

		return loadedProperties;
	}

	private ClassPathXmlApplicationContext initContext(String... path) {
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
			addPropertyPlaceholder(context);
			context.setConfigLocations(path);
			context.refresh();
			return context;
		} catch (Exception e) {
			logger.error("", e);
			System.exit(-1);
		}
		return null;
	}

	@Override
	protected ILoadingMonitor createProgressMonitor(IProgressDisplay display) {
		return new DefaultLoadingMonitor(display, 10);
	}

	private void setLoadingProgress(String loadingItem, int progress) {
		progressDisplay.setText(loadingItem);
		progressDisplay.setProgress(progress);
	}

	protected void addPropertyPlaceholder(AbstractApplicationContext context) {
		PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		configurer.setProperties(getProperties());
		context.addBeanFactoryPostProcessor(configurer);
	}
}
