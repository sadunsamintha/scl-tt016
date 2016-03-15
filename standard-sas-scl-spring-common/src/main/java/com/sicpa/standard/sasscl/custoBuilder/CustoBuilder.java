package com.sicpa.standard.sasscl.custoBuilder;

import static com.sicpa.standard.sasscl.ioc.BeansName.ALERT_CAMERA_COUNT;
import static com.sicpa.standard.sasscl.ioc.BeansName.DEVICES_GROUP_STARTUP;
import static com.sicpa.standard.sasscl.ioc.BeansName.MESSAGES_MAPPING;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_CONFIG_MAPPING;
import static com.sicpa.standard.sasscl.ioc.BeansName.PRODUCTION_MODE_MAPPING;
import static com.sicpa.standard.sasscl.ioc.BeansName.REMOTE_SERVER_PRODUCT_STATUS_MAPPING;
import static com.sicpa.standard.sasscl.ioc.BeansName.SELECTION_MODEL_FACTORY;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATISTICS_PRODUCTS_STATUS_MAPPER;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATISTICS_VIEW_MAPPER;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.messages.IMessageCodeMapper;
import com.sicpa.standard.client.common.messages.IMessagesMapping;
import com.sicpa.standard.client.common.messages.MessageType;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.business.alert.IAlert;
import com.sicpa.standard.sasscl.business.alert.task.AbstractAlertTask;
import com.sicpa.standard.sasscl.business.alert.task.IAlertTask;
import com.sicpa.standard.sasscl.business.statistics.IStatisticsKeyToViewDescriptorMapping;
import com.sicpa.standard.sasscl.business.statistics.mapper.IProductStatusToStatisticKeyMapper;
import com.sicpa.standard.sasscl.controller.device.group.impl.SimpleGroupDevicesController;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.camera.CameraCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.alert.CameraCountAlertTask;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.sicpa.standard.sasscl.model.custom.ICustomizable;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory.IConfigFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.SelectionModel;

public class CustoBuilder {

	public static void setProductionModePermission(ProductionMode mode, Permission permission) {

		ISelectionModelFactory modelFactory = BeanProvider.getBean(SELECTION_MODEL_FACTORY);
		modelFactory.addConfigFlowModelTask(new IConfigFlowModel() {
			@Override
			public void config(AbstractSelectionFlowModel flowmodel) {
				if (flowmodel instanceof SelectionModel) {
					((SelectionModel) flowmodel).getPermissions().put(mode, permission);
				} else {
					throw new IllegalArgumentException("cannot customize " + flowmodel.getClass()
							+ " - expected class:" + SelectionModel.class);
				}
			}
		});
	}

	/**
	 * 
	 * @param productionMode
	 * @param configId
	 *            is the name(without the .xml extension) of the file that will be loaded when the production mode is
	 *            selected
	 */
	public static void addProductionMode(ProductionMode productionMode, String configId, int idOnRemoteServer) {
		IProductionConfigMapping mapping = BeanProvider.getBean(PRODUCTION_CONFIG_MAPPING);
		mapping.put(productionMode, configId);
		addToRemoteMapping(productionMode, idOnRemoteServer);
	}

	public static void addNewProductStatus(ProductStatus status, StatisticsKey statsKey, int idOnRemote,
			Color colorOnScreen, int indexOnScreen, String langKey, boolean isProductActivated) {

		handleNewStatistic(status, statsKey, colorOnScreen, indexOnScreen, langKey);
		try {
			addToRemoteMapping(status, idOnRemote);
		} catch (NoSuchBeanDefinitionException e) {
			// ignore as bean only present with standard core 5 server
		}
		setPackagerType(status, isProductActivated);
	}

	public static void addMessage(String langKey, String msgCode, MessageType type) {
		IMessagesMapping typeMapping = BeanProvider.getBean(MESSAGES_MAPPING);
		IMessageCodeMapper codeMapping = (IMessageCodeMapper) typeMapping;
		typeMapping.add(langKey, type);
		codeMapping.add(msgCode, langKey);
	}

	public static void setMessageType(String langKey, MessageType type) {
		IMessagesMapping typeMapping = BeanProvider.getBean(MESSAGES_MAPPING);
		typeMapping.add(langKey, type);
	}

	public static void addDeviceToStartUp(IDevice device) {
		SimpleGroupDevicesController group = BeanProvider.getBean(DEVICES_GROUP_STARTUP);
		group.addDevice(device);
	}

	public static void addToRemoteMapping(ProductionMode mode, int idOnRemoteServer) {
		IProductionModeMapping mapping = BeanProvider.getBean(PRODUCTION_MODE_MAPPING);
		mapping.add(mode, idOnRemoteServer);
	}

	private static void setPackagerType(ProductStatus status, boolean isProductActivated) {
		IRemoteServer server = BeanProvider.getBean(BeansName.REMOTE_SERVER);
		if (isProductActivated) {
			server.addToActivatedPackager(status);
		} else {
			server.addToCounterPackager(status);
		}
	}

	public static void addToRemoteMapping(ProductStatus status, int idOnRemote) {
		IRemoteServerProductStatusMapping mapping = BeanProvider.getBean(REMOTE_SERVER_PRODUCT_STATUS_MAPPING);
		mapping.add(status, idOnRemote);
	}

	/**
	 * Call to this method has to be done before spring init<br>
	 * some processes are done by naming convention, so respect the following<br>
	 * <li>NTF_LINE_XXX -> jmx line report <li>
	 * NTF_CAB_XXX -> jmx cabinet report<li>line placeholder is PlcLineHelper.LINE_INDEX_PLACEHOLDER<br>
	 * see plcVars.groovy for options
	 */
	public static void addVariable(String logicalName, String nameOnPlc, PlcUtils.PLC_TYPE type, Map<String, ?> options) {
		Map<String, Object> map = new HashMap<>();
		map.put("v", nameOnPlc);
		map.put("t", type);
		map.putAll(options);
		PlcUtils.custoInfo.put(logicalName, map);
	}

	/**
	 * This method allows a property to be added to a class. After adding the property, an instance implementing the
	 * ICustomizable interface, can set the value of the added property. Setting the value of a property without
	 * previously adding it to the class, will result in an exception.
	 * 
	 * @param classToCustomize
	 *            class to add property to
	 * @param property
	 *            custom property to add
	 * @param <T>
	 *            the type of custom property to ad
	 * @see CustoBuilderTest.testModelAddCustomPropertySuccess
	 *
	 */
	public static <T> void addPropertyToClass(Class<? extends ICustomizable> classToCustomize,
			CustomProperty<T> property) {
		CustomizablePropertyFactory.getCustomizablePropertyDefinition().addProperty(classToCustomize, property);
	}

	public static void addActionOnStartingProduction(Runnable task) {
		Object listener = new Object() {
			@Subscribe
			public void handleApplicationStateChanged(ApplicationFlowStateChangedEvent evt) {
				if (evt.getCurrentState().equals(ApplicationFlowState.STT_STARTING)) {
					task.run();
				}
			}
		};
		EventBusService.register(listener);
	}

	public static void handleNewStatistic(ProductStatus status, StatisticsKey statsKey, Color colorOnScreen,
			int indexOnScreen, String langKey) {
		addToStatisticsMapper(status, statsKey);
		addStatisticsOnView(status, statsKey, colorOnScreen, indexOnScreen, langKey);
	}

	private static void addToStatisticsMapper(ProductStatus status, StatisticsKey statsKey) {
		IProductStatusToStatisticKeyMapper statsMapping = BeanProvider.getBean(STATISTICS_PRODUCTS_STATUS_MAPPER);
		statsMapping.add(status, statsKey);
	}

	private static void addStatisticsOnView(ProductStatus status, StatisticsKey statsKey, Color colorOnScreen,
			int indexOnScreen, String langKey) {
		IStatisticsKeyToViewDescriptorMapping viewMapping = BeanProvider.getBean(STATISTICS_VIEW_MAPPER);
		viewMapping.add(statsKey, colorOnScreen, indexOnScreen, langKey);
	}

	public static void addAlert(IAlertTask alertTask) {
		IAlert alert = BeanProvider.getBean(BeansName.ALERT);
		alert.addTask(alertTask);
	}

	/**
	 * let the alert task know if the code event is consider valid or invalid
	 */
	public static void setCameraCountAlertCodeValidator(Function<CameraCodeEvent, Boolean> validator) {
		CameraCountAlertTask task = BeanProvider.getBean(ALERT_CAMERA_COUNT);
		task.setCodeValidator(validator);
	}

	/**
	 * provide a way to enabled/disabled an alert task
	 */
	public static void setAlertEnabler(Function<AbstractAlertTask, Boolean> enabler, String alertTaskBeanName) {
		AbstractAlertTask task = BeanProvider.getBean(alertTaskBeanName);
		task.setEnabler(enabler);
	}
}
