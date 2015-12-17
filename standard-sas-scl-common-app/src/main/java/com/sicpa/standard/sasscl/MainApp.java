package com.sicpa.standard.sasscl;

import com.sicpa.standard.client.common.config.history.BeanHistoryManager;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.client.common.descriptor.validator.Validators;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorsException;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.groovy.GroovyLoggerConfigurator;
import com.sicpa.standard.client.common.ioc.AbstractSpringConfig;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.launcher.CommonMainApp;
import com.sicpa.standard.client.common.launcher.LoaderConfig;
import com.sicpa.standard.client.common.launcher.display.IProgressDisplay;
import com.sicpa.standard.client.common.launcher.spring.ILoadingMonitor;
import com.sicpa.standard.client.common.launcher.spring.impl.DefaultLoadingMonitor;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.gui.screen.loader.AbstractApplicationLoader;
import com.sicpa.standard.gui.screen.machine.impl.SPL.AbstractSplFrame;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.statistics.StatisticsRestoredEvent;
import com.sicpa.standard.sasscl.business.statistics.impl.Statistics;
import com.sicpa.standard.sasscl.common.log.ILoggerBehavior;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.common.utils.LangUtils;
import com.sicpa.standard.sasscl.config.xstream.IXStreamConfigurator;
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
import com.sicpa.standard.sasscl.ioc.SpringConfig;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainApp extends CommonMainApp<LoaderConfig> {

	private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

	public MainApp() {
		PropertyPlaceholderResourcesSASSCL.init();
	}

	@Override
	public AbstractApplicationLoader createLoader(LoaderConfig config, String... profiles) {
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

	protected void initLanguage(String lang) {
		try {
			LangUtils.initLanguageFiles(lang);
		} catch (Exception e) {
			logger.error("", e);
		}
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

	/**
	 * init spring and everything that can be done before gui
	 */
	@Override
	protected void initApplication(LoaderConfig config, String... profiles) {
		super.initApplication(config, profiles);
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

	protected Properties initProperties() {
		AbstractSpringConfig sc = new AbstractSpringConfig() {
		};
		sc.put(SpringConfig.PROPERTIES_PLACEHOLDER, "spring/propertyPlaceholderConfigurer.xml");
		ClassPathXmlApplicationContext appcontext = new ClassPathXmlApplicationContext(sc.getPaths());
		Properties config = (Properties) appcontext.getBean("allProperties");
		appcontext.close();
		return config;
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

			PropertiesUtils.savePropertiesKeepOrderAndComment(globalPropertiesFile,
					"subsystemId", Long.toString(id));
			PropertiesUtils.savePropertiesKeepOrderAndComment(globalPropertiesFile,
					"lineId", Long.toString(id));

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
			EventBusService.post(new StatisticsRestoredEvent());
		}
	}

	protected void initCustomGuiComponent() {
		AbstractSplFrame.mapPanelClasses.put(AbstractSplFrame.KEY_LINE_ID, LineIdWithAuthenticateButton.class);
	}

	@Override
	protected void doInitEnd() {

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
	protected void initSpring(LoaderConfig config, String... profiles) {

		setLoadingProgress("init event bus", 0);
		initEventBus();
		setLoadingProgress("init xtream", 0);
		initXstreamConfig();
		setLoadingProgress("init language", 0);

		Properties props = initProperties();
		initLanguage(props.getProperty("language"));

		setLoadingProgress("init spring", 10);

		super.initSpring(config, profiles);

		ApplicationInitializationTasks initTasksBean = BeanProvider.getBean(BeansName.APPLICATION_INITIALIZATION_TASKS);
		// used by optional module to add some init task after spring has been initialized
		for (Runnable task : initTasksBean.getInitTasks()) {
			task.run();
		}
		initOperatorLogger();
	}

	protected void initCrypto() {
		SimpleServiceProviderManager provider = BeanProvider.getBean(BeansName.CRYPTO_PROVIDER_MANAGER);
		StaticServiceProviderManager.register(provider);
	}

	protected void handleHistoryBean() {
		BeanHistoryManager manager = BeanProvider.getBean(BeansName.BEAN_HISTORY_MANAGER);
		manager.checkForModification();
	}

	protected void initEventBus() {
		try {
			// it has to be executed before the main spring has been initialized
			AbstractSpringConfig sc = new AbstractSpringConfig() {
			};
			sc.put(SpringConfig.EVENT_BUS, "spring/eventBus.xml");
			sc.put(SpringConfig.PROPERTIES_PLACEHOLDER, "spring" + File.separator + "propertyPlaceholderConfigurer.xml");
			new ClassPathXmlApplicationContext(sc.getPaths());
		} catch (Exception e) {
			logger.error("", e);
			System.exit(-1);
		}
	}

	protected void initXstreamConfig() {
		try {
			// it has to be executed before the main spring has been initialized
			AbstractSpringConfig sc = new AbstractSpringConfig() {
			};
			sc.put(SpringConfig.XSTREAM_CONFIG, "spring" + File.separator + "xstream.xml");
			sc.put(SpringConfig.PROPERTIES_PLACEHOLDER, "spring" + File.separator + "propertyPlaceholderConfigurer.xml");
			ClassPathXmlApplicationContext aFactory = new ClassPathXmlApplicationContext(sc.getPaths());
			IXStreamConfigurator configurator = (IXStreamConfigurator) aFactory.getBean(BeansName.XSTREAM_CONFIGURATOR);
			configurator.configure(ConfigUtils.getXStream());
		} catch (Exception e) {
			logger.error("", e);
			System.exit(-1);
		}
	}

	@Override
	protected ILoadingMonitor createProgressMonitor(IProgressDisplay display) {
		return new DefaultLoadingMonitor(display, 10);
	}

	/**
	 * This method sends the progress and the text to display in the progress bar.
	 */
	protected void setLoadingProgress(String loadingItem, int progress) {
		progressDisplay.setText(loadingItem);
		progressDisplay.setProgress(progress);
	}
}
