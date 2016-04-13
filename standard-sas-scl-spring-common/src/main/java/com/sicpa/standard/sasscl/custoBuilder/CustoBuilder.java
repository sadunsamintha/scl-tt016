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
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.EXIT;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import com.sicpa.standard.client.common.view.screensflow.IScreenGetter;
import com.sicpa.standard.client.common.view.screensflow.IScreensFlow;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransitionTrigger;
import com.sicpa.standard.sasscl.controller.flow.statemachine.DefaultFlowControlWiring;
import com.sicpa.standard.sasscl.controller.flow.statemachine.FlowTransition;
import com.sicpa.standard.sasscl.controller.flow.statemachine.IFlowControlWiring;
import com.sicpa.standard.sasscl.controller.view.flow.DefaultScreensFlow;
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
import com.sicpa.standard.sasscl.controller.hardware.ProductionDevicesCreatedEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.camera.CameraCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.alert.CameraCountAlertTask;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimuCodeTransformer;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.ICameraAdaptorSimulator;
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

/**
 * helper class for customization<br>
 * all the methods have to be called from <code>Bootstrap.executeSpringInitTasks</code> if not specified otherwise
 */
public class CustoBuilder {

	/**
	 * let a production mode be only accessible to user with the given permission
	 */
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
	 * Make a new production mode available, the associated market type with id=idOnRemoteServer must be available in
	 * the tree of production parameters downloaded from the server to be actually available
	 * 
	 * @param configId
	 *            is the name(without the .xml extension) of the file that will be loaded when the production mode is
	 *            selected
	 */
	public static void addProductionMode(ProductionMode productionMode, String configId, int idOnRemoteServer) {
		IProductionConfigMapping mapping = BeanProvider.getBean(PRODUCTION_CONFIG_MAPPING);
		mapping.put(productionMode, configId);
		addToRemoteMapping(productionMode, idOnRemoteServer);
	}

	/**
	 * add a new product status available in the application<br/>
	 * the statistics increased for this product status will have the given StatisticsKey<br>
	 * It will be handled as productDto or as a counter when reporting to the server based on
	 * <code>isProductActivated</code>
	 */
	public static void addProductStatus(ProductStatus status, StatisticsKey statsKey, int idOnRemote,
			Color colorOnScreen, int indexOnScreen, String langKey, boolean isProductActivated) {

		handleNewStatistic(status, statsKey, colorOnScreen, indexOnScreen, langKey);
		try {
			addToRemoteMapping(status, idOnRemote);
		} catch (NoSuchBeanDefinitionException e) {
			// ignore as bean only present with standard core 5 server
		}
		setPackagerType(status, isProductActivated);
	}

	/**
	 * add a message to the message mapping. send the message using the following code:<br/>
	 * <code>EventBusService.post(new MessageEvent(langKey))</code>
	 */
	public static void addMessage(String langKey, String msgCode, MessageType type) {
		IMessagesMapping typeMapping = BeanProvider.getBean(MESSAGES_MAPPING);
		IMessageCodeMapper codeMapping = (IMessageCodeMapper) typeMapping;
		typeMapping.add(langKey, type);
		codeMapping.add(msgCode, langKey);
	}

	/**
	 * change the message type for a given message from the default message mapping
	 */
	public static void setMessageType(String langKey, MessageType type) {
		IMessagesMapping typeMapping = BeanProvider.getBean(MESSAGES_MAPPING);
		typeMapping.add(langKey, type);
	}

	/**
	 * the connect method of the given device will be called during the application startup
	 */
	public static void addDeviceToStartUp(IDevice device) {
		SimpleGroupDevicesController group = BeanProvider.getBean(DEVICES_GROUP_STARTUP);
		group.addDevice(device);
	}

	/**
	 * add or replace in the mapping: ProductionMode, on scl app <---> market type id, on server <br/>
	 * this mapping is used when converting the tree of production parameter
	 */
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

	/**
	 * add or replace in the mapping: ProductStatus, on scl app <---> product dto status id, on server <br/>
	 * this mapping is used when sending production to the server
	 */
	public static void addToRemoteMapping(ProductStatus status, int idOnRemote) {
		IRemoteServerProductStatusMapping mapping = BeanProvider.getBean(REMOTE_SERVER_PRODUCT_STATUS_MAPPING);
		mapping.add(status, idOnRemote);
	}

	/**
	 * Call to this method has to be done before spring init<br>
	 * <br>
	 * some processes are done by naming convention, so respect the following<br>
	 * <li>NTF_LINE_XXX -> jmx line report <li>
	 * NTF_CAB_XXX -> jmx cabinet report<li>line placeholder is <code>PlcLineHelper.LINE_INDEX_PLACEHOLDER</code><br>
	 * see plcVars.groovy for options
	 */
	public static void addPlcVariable(String logicalName, String nameOnPlc, PlcUtils.PLC_TYPE type, Map<String, ?> options) {
		Map<String, Object> map = new HashMap<>();
		map.put("v", nameOnPlc);
		map.put("t", type);
		map.putAll(options);
		PlcUtils.custoInfo.put(logicalName, map);
	}

	/**
	 * add a new available property to a class without extending it.<br/>
	 * It is working only for class implementing ICustomizable: <li>SKU <li>Code <li>CodeType <li>Product <li>
	 * ProductionParameter
	 * 
	 * @see CustoBuilderCustomizableTest.testModelAddCustomPropertySuccess
	 *
	 */
	public static void addPropertyToClass(Class<? extends ICustomizable> classToCustomize, CustomProperty<?> property) {
		CustomizablePropertyFactory.getCustomizablePropertyDefinition().addProperty(classToCustomize, property);
	}

	/**
	 * add a task that will be executed in the STARTING state
	 */
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

	/**
	 * add a new statistics by line based on the product status
	 */
	public static void handleNewStatistic(ProductStatus status, StatisticsKey statsKey, Color colorOnScreen,
			int indexOnScreen, String langKey) {
		addToStatisticsMapper(status, statsKey);
		addStatisticsOnView(status, statsKey, colorOnScreen, indexOnScreen, langKey);
	}

	/**
	 * let the application know that it has to increase the statistics with the given StatisticsKey when a product with
	 * the given status is created
	 */
	public static void addToStatisticsMapper(ProductStatus status, StatisticsKey statsKey) {
		IProductStatusToStatisticKeyMapper statsMapping = BeanProvider.getBean(STATISTICS_PRODUCTS_STATUS_MAPPER);
		statsMapping.add(status, statsKey);
	}

	/**
	 * display on the screen statistics of the given StatisticsKey
	 */
	public static void addStatisticsOnView(ProductStatus status, StatisticsKey statsKey, Color colorOnScreen,
			int indexOnScreen, String langKey) {
		IStatisticsKeyToViewDescriptorMapping viewMapping = BeanProvider.getBean(STATISTICS_VIEW_MAPPER);
		viewMapping.add(statsKey, colorOnScreen, indexOnScreen, langKey);
	}

	/**
	 * add an alert task that will be started when the production is starting
	 */
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

	/**
	 * for the given camera, when in simulation, the decision to generate a valid/invalid and the code itself will be
	 * delegated to the given <code>CameraSimuCodeTransformer</code>
	 */
	public static void setCameraSimulatorCodeTransformer(String cameraId, CameraSimuCodeTransformer custoTransformer) {

		Properties props = BeanProvider.getBean(BeansName.ALL_PROPERTIES);
		String cameraBehavior = props.getProperty("camera.behavior");
		if (!cameraBehavior.equalsIgnoreCase("simulator")) {
			return;
		}

		Object o = new Object() {
			@Subscribe
			public void handleDeviceCreated(ProductionDevicesCreatedEvent evt) {
				ICameraAdaptorSimulator camera = null;
				for (IDevice dev : evt.getDevices()) {
					if (dev.getName().equals(cameraId)) {
						camera = (ICameraAdaptorSimulator) dev;
					}
				}
				((CameraSimulatorController) camera.getSimulatorController())
						.setCustoCodesGeneratedTransformer(custoTransformer);
			}
		};
		EventBusService.register(o);
	}

	/**
	 * This method adds a new screen to the screens flow. After the screen is added, it's imperative to add a screen
	 * transition to navigate to the panel and out of it.
	 *
	 * @see CustoBuilder.addScreenTransition
	 *
	 * @param screen screen to be added
	 */
	public static void addScreen(IScreenGetter screen) {
		IScreensFlow screensFlow = BeanProvider.getBean(BeansName.SCREENS_FLOW);
		screensFlow.addTransitions(screen);
	}

	/**
	 * This method adds a new transition between two screens. The transition is triggered by the specified trigger
	 * parameter.
	 *
	 * @param from the origin screen
	 * @param to the destination screen
	 * @param trigger the trigger which fires the transition
	 */
	public static void addScreenTransition(IScreenGetter from, IScreenGetter to, ScreenTransitionTrigger trigger) {
		IScreensFlow screensFlow = BeanProvider.getBean(BeansName.SCREENS_FLOW);
		screensFlow.addTransitions(from, new ScreenTransition(trigger, to));
	}

	/**
	 * Sets the next possible states for the specified state. All previous possible states will be removed and
	 * the specified flow transitions will be added as next possible states.
	 *
	 * @param current current state for which we want to define new possible states
	 * @param flowTransitions the transitions which define the new possible states and the associated trigger
	 */
	public static void setStateNextPossibleStates(ApplicationFlowState state, FlowTransition... flowTransitions) {
		state.reset();

		DefaultFlowControlWiring flowControl = BeanProvider.getBean(BeansName.DEFAULT_FLOW_CONTROL_WIRING);
		flowControl.addNext(state, flowTransitions);
	}
}
