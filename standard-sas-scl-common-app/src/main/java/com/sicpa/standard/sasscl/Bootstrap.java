package com.sicpa.standard.sasscl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.utils.PropertiesUtils;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.business.statistics.StatisticsRestoredEvent;
import com.sicpa.standard.sasscl.business.statistics.impl.Statistics;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.device.group.IGroupDevicesController;
import com.sicpa.standard.sasscl.controller.flow.IBootstrap;
import com.sicpa.standard.sasscl.controller.scheduling.RemoteServerScheduledJobs;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.plc.IPlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import com.sicpa.standard.sicpadata.spi.manager.IServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.manager.StaticServiceProviderManager;

public class Bootstrap implements IBootstrap {

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	private IRemoteServer server;
	private IGroupDevicesController startupDevicesGroup;
	private IStorage storage;
	private ProductionParameters productionParameters;
	private IPlcValuesLoader plcLoader;
	private SkuListProvider skuListProvider;
	private AuthenticatorProvider authenticatorProvider;
	private RemoteServerScheduledJobs remoteServerSheduledJobs;
	private SubsystemIdProvider subsystemIdProvider;
	private Statistics statistics;
	private IServiceProviderManager cryptoProviderManager;
	private List<PlcVariableGroup> linePlcVarGroup;
	private List<PlcVariableGroup> cabPlcVarGroups;

	@Override
	public void executeSpringInitTasks() {
		IStorage storage = BeanProvider.getBean(BeansName.STORAGE);
		restoreStatistics(storage);
		initPlc();
		initProductionParameter(storage);
		initAuthenticator(storage);
		initCrypto();
		addConnectionListenerOnServer();
		connectStartupDevices();
		restorePreviousSelectedProductionParams();
	}

	private void addConnectionListenerOnServer() {
		server.addDeviceStatusListener(evt -> {
			if (evt.getStatus() == DeviceStatus.CONNECTED) {
				initRemoteServerConnected();
			}
		});
	}

	private void connectStartupDevices() {
		startupDevicesGroup.start();
	}

	private void restorePreviousSelectedProductionParams() {
		ProductionParameters previous = storage.getSelectedProductionParameters();

		if (previous != null) {
			productionParameters.setBarcode(previous.getBarcode());
			productionParameters.setSku(previous.getSku());
			productionParameters.setProductionMode(previous.getProductionMode());
			EventBusService.post(new ProductionParametersEvent(previous));
		}
	}

	private void initPlc() {
		generateAllEditableVariableGroup(plcLoader.getValues());
	}

	private void initProductionParameter(IStorage storage) {
		skuListProvider.set(storage.getProductionParameters());
	}

	private void initAuthenticator(IStorage storage) {
		authenticatorProvider.set(storage.getAuthenticator());
	}

	private void initRemoteServerConnected() {
		getLineIdFromRemoteServerAndSaveItLocally();
		remoteServerSheduledJobs.executeInitialTasks();
	}

	private void getLineIdFromRemoteServerAndSaveItLocally() {
		long id = getLineIdFromRemoteServer();
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
		return server.getSubsystemID();
	}

	private void restoreStatistics(IStorage storage) {
		StatisticsValues statsValues = storage.getStatistics();
		boolean restored = false;
		if (statsValues != null) {
			logger.debug("Restoring statistics {} ", statsValues.getMapValues());
			restored = true;
		} else {
			statsValues = new StatisticsValues();
		}
		statistics.setValues(statsValues);
		if (restored) {
			EventBusService.post(new StatisticsRestoredEvent(statsValues));
		}
	}

	private void initCrypto() {
		StaticServiceProviderManager.register(cryptoProviderManager);
	}

	private void generateAllEditableVariableGroup(Map<Integer, StringMap> values) {
		generateCabinetGroup(values.get(0));
		generateAllLinesEditableVariable(values);
	}

	private void generateAllLinesEditableVariable(Map<Integer, StringMap> values) {
		for (int i = 1; i < plcLoader.getLineCount() + 1; i++) {
			generateLineEditableVariables(i, linePlcVarGroup, values.get(i));
		}
	}

	private void generateCabinetGroup(StringMap values) {
		for (PlcVariableGroup grp : cabPlcVarGroups) {
			for (PlcVariableDescriptor desc : grp.getPlcVars()) {
				desc.initValue(values.get(desc.getVarName()));
			}
		}
		EventBusService.post(new PlcVariableGroupEvent(cabPlcVarGroups, "cabinet"));
	}

	private void generateLineEditableVariables(int index, List<PlcVariableGroup> lineVarGroups, StringMap values) {
		List<PlcVariableGroup> groups = PlcLineHelper.replaceLinePlaceholder(lineVarGroups, index);
		for (PlcVariableGroup grp : groups) {
			for (PlcVariableDescriptor desc : grp.getPlcVars()) {
				desc.initValue(values.get(desc.getVarName()));
			}
		}
		EventBusService.post(new PlcVariableGroupEvent(groups, "" + index));
	}

	public void setServer(IRemoteServer server) {
		this.server = server;
	}

	public void setStartupDevicesGroup(IGroupDevicesController startupDevicesGroup) {
		this.startupDevicesGroup = startupDevicesGroup;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setPlcLoader(IPlcValuesLoader plcLoader) {
		this.plcLoader = plcLoader;
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	public void setAuthenticatorProvider(AuthenticatorProvider authenticatorProvider) {
		this.authenticatorProvider = authenticatorProvider;
	}

	public void setRemoteServerSheduledJobs(RemoteServerScheduledJobs remoteServerSheduledJobs) {
		this.remoteServerSheduledJobs = remoteServerSheduledJobs;
	}

	public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
		this.subsystemIdProvider = subsystemIdProvider;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	public void setCryptoProviderManager(IServiceProviderManager cryptoProviderManager) {
		this.cryptoProviderManager = cryptoProviderManager;
	}

	public void setLinePlcVarGroup(List<PlcVariableGroup> linePlcVarGroup) {
		this.linePlcVarGroup = linePlcVarGroup;
	}

	public void setCabPlcVarGroups(List<PlcVariableGroup> cabPlcVarGroups) {
		this.cabPlcVarGroups = cabPlcVarGroups;
	}
}
