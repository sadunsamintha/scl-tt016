package com.sicpa.standard.sasscl;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.sicpa.standard.client.common.config.history.BeanHistoryManager;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.client.common.descriptor.validator.Validators;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorsException;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.statistics.StatisticsRestoredEvent;
import com.sicpa.standard.sasscl.business.statistics.impl.Statistics;
import com.sicpa.standard.sasscl.common.log.ILoggerBehavior;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.device.IPlcIndependentDevicesController;
import com.sicpa.standard.sasscl.controller.device.group.DevicesGroup;
import com.sicpa.standard.sasscl.controller.flow.IBootstrap;
import com.sicpa.standard.sasscl.controller.scheduling.RemoteServerScheduledJobs;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.plc.variable.EditablePlcVariables;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesForAllVar;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.sicpadata.spi.manager.SimpleServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.manager.StaticServiceProviderManager;

public class Bootstrap implements IBootstrap {

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	@Override
	public void executeSpringInitTasks() {
		IStorage storage = BeanProvider.getBean(BeansName.STORAGE);
		restoreStatistics(storage);
		initPlc();
		initProductionParameter(storage);
		initAuthenticator(storage);
		initCrypto();
		handleHistoryBean();
		addConnectionListenerOnServer();
		initOperatorLogger();
		connectRemoteServer();
		restorePreviousSelectedProductionParams();
		executeModelValidator();
		executePlcVarValidator();
	}

	private void addConnectionListenerOnServer() {
		IRemoteServer server = BeanProvider.getBean(BeansName.REMOTE_SERVER);
		server.addDeviceStatusListener(evt -> {
			if (evt.getDevice() instanceof IRemoteServer && evt.getStatus() == DeviceStatus.CONNECTED) {
				initRemoteServerConnected();
			}
		});
	}

	private void executeModelValidator() {
		MainFrame main = getMainFrame();
		MainFrameController controller = null;
		if (main != null) {
			controller = main.getController();
		}

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

	private void executePlcVarValidator() {
		EditablePlcVariables plcvars = BeanProvider.getBean(BeansName.PLC_EDITABLE_VARIABLES);
		plcvars.validate();
	}

	private void connectRemoteServer() {
		IPlcIndependentDevicesController controller = BeanProvider.getBean(BeansName.OTHER_DEVICES_CONTROLLER);
		controller.startDevicesGroup(DevicesGroup.STARTUP_GROUP);
	}

	private void restorePreviousSelectedProductionParams() {
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

	private MainFrame getMainFrame() {
		IGUIComponentGetter compGetter = BeanProvider.getBean(BeansName.MAIN_FRAME);
		return (MainFrame) compGetter.getComponent();
	}

	@SuppressWarnings("rawtypes")
	private void initPlc() {
		IPlcValuesLoader loader = BeanProvider.getBean(BeansName.PLC_VALUES_LOADER);
		List<IPlcVariable> variables = BeanProvider.getBean(BeansName.PLC_PARAMETERS);
		PlcValuesForAllVar values = BeanProvider.getBean(BeansName.PLC_CABINET_VARIABLES_VALUE);

		loader.load(variables, values);
	}

	private void initOperatorLogger() {
		ILoggerBehavior loggerBehavior = BeanProvider.getBean(BeansName.OPERATOR_LOGGER);
		OperatorLogger.setLoggerBehavior(loggerBehavior);
	}

	private void initProductionParameter(IStorage storage) {
		SkuListProvider ppc = BeanProvider.getBean(BeansName.SKU_LIST_PROVIDER);
		ppc.set(storage.getProductionParameters());
	}

	private void initAuthenticator(IStorage storage) {
		AuthenticatorProvider authProvider = BeanProvider.getBean(BeansName.AUTHENTICATOR_PROVIDER);
		authProvider.set(storage.getAuthenticator());
	}

	private void initRemoteServerConnected() {
		getLineIdFromRemoteServerAndSaveItLocally();

		RemoteServerScheduledJobs remoteJobs = BeanProvider.getBean(BeansName.SCHEDULING_REMOTE_SERVER_JOB);
		remoteJobs.executeInitialTasks();
	}

	private void getLineIdFromRemoteServerAndSaveItLocally() {
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

	private void restoreStatistics(IStorage storage) {
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

	private void initCrypto() {
		SimpleServiceProviderManager provider = BeanProvider.getBean(BeansName.CRYPTO_PROVIDER_MANAGER);
		StaticServiceProviderManager.register(provider);
	}

	private void handleHistoryBean() {
		BeanHistoryManager manager = BeanProvider.getBean(BeansName.BEAN_HISTORY_MANAGER);
		manager.checkForModification();
	}

}
