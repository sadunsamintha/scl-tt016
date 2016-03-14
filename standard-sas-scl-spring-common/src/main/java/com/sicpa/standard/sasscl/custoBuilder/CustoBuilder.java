package com.sicpa.standard.sasscl.custoBuilder;

import static com.sicpa.standard.sasscl.ioc.BeansName.REMOTE_SERVER_PRODUCT_STATUS_MAPPING;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATISTICS_PRODUCTS_STATUS_MAPPER;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATISTICS_VIEW_MAPPER;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.messages.IMessageCodeMapper;
import com.sicpa.standard.client.common.messages.IMessagesMapping;
import com.sicpa.standard.client.common.messages.MessageType;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.business.statistics.IStatisticsKeyToViewDescriptorMapping;
import com.sicpa.standard.sasscl.business.statistics.mapper.IProductStatusToStatisticKeyMapper;
import com.sicpa.standard.sasscl.controller.device.group.impl.SimpleGroupDevicesController;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils;
import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyDefinition;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.sicpa.standard.sasscl.model.custom.ICustomizable;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory.IConfigFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.SelectionModel;

public class CustoBuilder {

	public static abstract class Production {
		public static void setProductionModePermission(final ProductionMode mode, final Permission permission) {

			ISelectionModelFactory modelFactory = BeanProvider.getBean(BeansName.SELECTION_MODEL_FACTORY);
			modelFactory.addConfigFlowModelTask(new IConfigFlowModel() {
				@Override
				public void config(AbstractSelectionFlowModel flowmodel) {
					if (flowmodel instanceof SelectionModel) {
						((SelectionModel) flowmodel).getPermissions().put(mode, permission);
					}
				}
			});
		}
	}

	public static abstract class Messages {
		public static void addMessage(String langKey, String msgCode, MessageType type) {
			IMessagesMapping typeMapping = BeanProvider.getBean(BeansName.MESSAGES_MAPPING);
			IMessageCodeMapper codeMapping = (IMessageCodeMapper) typeMapping;
			typeMapping.add(langKey, type);
			codeMapping.add(msgCode, langKey);
		}

		public static void setMessageType(String langKey, MessageType type) {
			IMessagesMapping typeMapping = BeanProvider.getBean(BeansName.MESSAGES_MAPPING);
			typeMapping.add(langKey, type);
		}
	}

	public static abstract class Devices {

		public static abstract class Group {

			public static void addDeviceToStartUp(IDevice device) {
				SimpleGroupDevicesController group = BeanProvider.getBean(BeansName.DEVICES_GROUP_STARTUP);
				group.addDevice(device);
			}
		}

		public static abstract class RemoteServer {

			public static void productStatusAddMappingEntry(ProductStatus status, int idOnRemoteServer) {
				IRemoteServerProductStatusMapping mapping = BeanProvider
						.getBean(BeansName.REMOTE_SERVER_PRODUCT_STATUS_MAPPING);
				mapping.add(status, idOnRemoteServer);
			}

			public static void productionModeAddMappingEntry(ProductionMode mode, int idOnRemoteServer) {
				IProductionModeMapping mapping = BeanProvider.getBean(BeansName.PRODUCTION_MODE_MAPPING);
				mapping.add(mode, idOnRemoteServer);
			}
		}

		public static abstract class Plc {
			/**
			 * Call to this method has to be done before spring init<br>
			 * some processes are done by naming convention, so respect the following<br>
			 * <li>NTF_LINE_XXX -> jmx line report <li>
			 * NTF_CAB_XXX -> jmx cabinet report<li>line placeholder is PlcLineHelper.LINE_INDEX_PLACEHOLDER<br>
			 * see plcVars.groovy for options
			 */
			public static void addVariable(String logicalName, String nameOnPlc, PlcUtils.PLC_TYPE type,
					Map<String, ?> options) {
				Map<String, Object> map = new HashMap<>();
				map.put("v", nameOnPlc);
				map.put("t", type);
				map.putAll(options);
				PlcUtils.custoInfo.put(logicalName, map);
			}
		}
	}

	public static abstract class Model {

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
			CustomizablePropertyDefinition definition = new CustomizablePropertyDefinition();
			definition.addProperty(classToCustomize, property);
			CustomizablePropertyFactory.setCustomizablePropertyDefinition(definition);
		}
	}

	public static abstract class ProcessFlow {

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
	}

	public static class Statistics {

		public static void addStatistics(ProductStatus status, StatisticsKey statsKey, int idOnRemote,
				Color colorOnScreen, int indexOnScreen, String langKey) {

			IProductStatusToStatisticKeyMapper statsMapping = BeanProvider.getBean(STATISTICS_PRODUCTS_STATUS_MAPPER);
			statsMapping.add(status, statsKey);

			try {
				// the bean exist only if CORE-5 standard
				IRemoteServerProductStatusMapping remoteMapping = BeanProvider
						.getBean(REMOTE_SERVER_PRODUCT_STATUS_MAPPING);
				remoteMapping.add(status, idOnRemote);
			} catch (NoSuchBeanDefinitionException e) {
				// ignore
			}

			IStatisticsKeyToViewDescriptorMapping viewMapping = BeanProvider.getBean(STATISTICS_VIEW_MAPPER);
			viewMapping.add(statsKey, colorOnScreen, indexOnScreen, langKey);
		}
	}
}
