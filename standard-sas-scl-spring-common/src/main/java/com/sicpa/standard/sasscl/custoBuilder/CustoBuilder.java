package com.sicpa.standard.sasscl.custoBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sicpa.standard.client.common.descriptor.AbstractPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.DescriptorWrapper;
import com.sicpa.standard.client.common.descriptor.IPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.ModelEditableProperties;
import com.sicpa.standard.client.common.descriptor.impl.BooleanPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.ConstantPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.DateDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.FreeTextDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.IPPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.PortDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.RangeDoublePropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.RangeIntegerPropertyDescriptor;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.messages.IMessageCodeMapper;
import com.sicpa.standard.client.common.messages.IMessagesMapping;
import com.sicpa.standard.client.common.messages.MessageType;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectionFlowViewFactory;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.controller.device.group.impl.SimpleGroupDevicesController;
import com.sicpa.standard.sasscl.controller.productionconfig.factory.utils.SpringImplementationProvider;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.plc.variable.EditablePlcVariables;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcBooleanVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.devices.remote.mapping.IProductionModeMapping;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory.IConfigFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.SelectionModel;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.view.MainFrameGetter;

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

	public static abstract class Statistics {

		// public static void addStatistics(ProductStatus status, StatisticsDescriptor statisticsDescriptor) {
		// MainFrameController view = BeanProvider.getBean(BeansName.MAIN_FRAME_CONTROLLER);
		// // TODO view.addStatisticsDescriptors(statisticsDescriptor);
		// changeStatisticsMapping(status, statisticsDescriptor.getKey());
		// }

//		public static void changeStatisticsMapping(ProductStatus status, StatisticsKey statisticsDescriptor) {
//			IProductStatusToStatisticKeyMapper mapper = BeanProvider
//					.getBean(BeansName.STATISTICS_PRODUCTS_STATUS_MAPPER);
//			mapper.add(status, statisticsDescriptor);
//		}
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

	public static abstract class ModelDescriptor {
		public static void addModelDescriptor(AbstractPropertyDescriptor descriptor, Object bean, String property,
				String description, String screenKey, boolean editableWhenProduction) {

			descriptor.setProperty(property);
			descriptor.setPropertyDescription(description);
			descriptor.setEnabledWhenProductionRunning(editableWhenProduction);

			// We have to assign this property descriptor to a screen and to a bean
			// this is done by declaring a DescriptorWrapper
			DescriptorWrapper wrapper = new DescriptorWrapper();
			// key: the screen title; it’s also used as an ID for the screen.
			// If a wrapper already exists with the same key, they will be merged
			wrapper.setKey(screenKey);
			wrapper.setBean(bean);

			List<IPropertyDescriptor> desc = new ArrayList<IPropertyDescriptor>();
			desc.add(descriptor);
			wrapper.setDescriptors(desc);

			// The above descriptor wrapper has to be added to the full list of editable properties
			ModelEditableProperties modelEditableProperties = BeanProvider.getBean(BeansName.MODEL_EDITABLE_PROPERTIES);
			modelEditableProperties.add(wrapper);
		}

		public static void addModelBooleanDescriptor(Object bean, String property, String description,
				String screenKey, boolean editableWhenProduction) {

			addModelDescriptor(new BooleanPropertyDescriptor(), bean, property, description, screenKey,
					editableWhenProduction);
		}

		public static void addModelIntegerDescriptor(Object bean, String property, String description, int min,
				int max, String screenKey, boolean editableWhenProduction) {

			RangeIntegerPropertyDescriptor desc = new RangeIntegerPropertyDescriptor();
			desc.setMin(min);
			desc.setMax(max);

			addModelDescriptor(desc, bean, property, description, screenKey, editableWhenProduction);
		}

		public static void addModelDoubleDescriptor(Object bean, String property, String description, double min,
				double max, String screenKey, boolean editableWhenProduction) {

			RangeDoublePropertyDescriptor desc = new RangeDoublePropertyDescriptor();
			desc.setMin(min);
			desc.setMax(max);

			addModelDescriptor(desc, bean, property, description, screenKey, editableWhenProduction);
		}

		public static void addModelConstantDescriptor(Object bean, String property, String description,
				List<Object> possibleValues, String screenKey, boolean editableWhenProduction) {

			ConstantPropertyDescriptor desc = new ConstantPropertyDescriptor();
			desc.setPossibleValues(possibleValues);

			addModelDescriptor(desc, bean, property, description, screenKey, editableWhenProduction);
		}

		public static void addModelIpDescriptor(Object bean, String property, String description,
				List<Object> possibleValues, String screenKey, boolean editableWhenProduction) {

			addModelDescriptor(new IPPropertyDescriptor(), bean, property, description, screenKey,
					editableWhenProduction);
		}

		public static void addModelFreeTextDescriptor(Object bean, String property, String description,
				List<Object> possibleValues, String screenKey, boolean editableWhenProduction) {

			addModelDescriptor(new FreeTextDescriptor(), bean, property, description, screenKey, editableWhenProduction);
		}

		public static void addModelDateDescriptor(Object bean, String property, String description,
				List<Object> possibleValues, String screenKey, boolean editableWhenProduction) {

			addModelDescriptor(new DateDescriptor(), bean, property, description, screenKey, editableWhenProduction);
		}

		public static void addModelPortDescriptor(Object bean, String property, String description,
				List<Object> possibleValues, String screenKey, boolean editableWhenProduction) {

			addModelDescriptor(new PortDescriptor(), bean, property, description, screenKey, editableWhenProduction);
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
			public static void AddToMapping(String logicalName, String nameOnPlc) {
				Map<Object, Object> map = BeanProvider.getBean(BeansName.PLC_VAR_MAPPING);
				map.put(logicalName, nameOnPlc);
			}

			private static void addPlcParameterVariable(IPlcVariable<?> plcvar) {
				// parameters
				List<Object> params = BeanProvider.getBean(BeansName.PLC_PARAMETERS);
				params.add(plcvar);
			}

			public static void addPlcVariableDescriptor(String groupName, PlcVariableDescriptor<?> descriptor) {
				EditablePlcVariables plcVariables = BeanProvider.getBean(BeansName.PLC_EDITABLE_VARIABLES);
				PlcVariableGroup group = new PlcVariableGroup(descriptor);
				plcVariables.addGroup(group);
				descriptor.setPlcProvider((PlcProvider) BeanProvider.getBean(BeansName.PLC_PROVIDER));
			}

			public static void addPlcParameterVariableInt32(String groupName, String varName, int min, int max) {
				IPlcVariable<Integer> var = PlcVariable.createInt32Var(varName);
				addPlcParameterVariable(var);
				PlcIntegerVariableDescriptor desc = new PlcIntegerVariableDescriptor();
				desc.setMin(min);
				desc.setMax(max);
				desc.setVariable(var);
				addPlcVariableDescriptor(groupName, desc);
			}

			public static void addPlcParameterVariableBoolean(String groupName, String varName) {
				IPlcVariable<Boolean> var = PlcVariable.createBooleanVar(varName);
				addPlcParameterVariable(var);
				PlcBooleanVariableDescriptor desc = new PlcBooleanVariableDescriptor();
				desc.setVariable(var);
				addPlcVariableDescriptor(groupName, desc);
			}

			public static void addPlcParameterVariablePulse(String groupName, String varNameValue, String varNameUnit,
					int minpulse, int maxpulse, int minms, int maxms) {

				IPlcVariable<Boolean> varUnit = PlcVariable.createBooleanVar(varNameUnit);
				IPlcVariable<Integer> varValue = PlcVariable.createInt32Var(varNameValue);

				PlcPulseVariableDescriptor desc = new PlcPulseVariableDescriptor();
				desc.setMinMs(minms);
				desc.setMaxMs(maxms);
				desc.setMinPulse(minpulse);
				desc.setMaxPulse(maxpulse);
				desc.setUnitPlcVar(varUnit);
				desc.setVariable(varValue);

				addPlcVariableDescriptor(groupName, desc);

				addPlcParameterVariable(varUnit);
				addPlcParameterVariable(varValue);
			}
		}
	}

	public static abstract class View {
		public static void setSelectionFlowFactory(SelectionFlowViewFactory viewFactory) {
			MainFrameGetter getter = BeanProvider.getBean(BeansName.MAIN_FRAME);
			getter.setViewFactory(viewFactory);
		}
	}

	public static abstract class ImplementationProvider {
		public static void addAvailableBean(String beanName, Object bean) {
			SpringImplementationProvider implementationProvider = BeanProvider
					.getBean(BeansName.IMPLEMENTATION_PROVIDER);
			implementationProvider.addLocalImplementation(beanName, bean);
		}
	}
}
