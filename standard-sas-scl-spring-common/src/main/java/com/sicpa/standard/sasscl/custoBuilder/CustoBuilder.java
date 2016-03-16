package com.sicpa.standard.sasscl.custoBuilder;

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
import com.sicpa.standard.sasscl.controller.productionconfig.mapping.IProductionConfigMapping;
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
import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyDefinition;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.sicpa.standard.sasscl.model.custom.ICustomizable;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory;
import com.sicpa.standard.sasscl.productionParameterSelection.ISelectionModelFactory.IConfigFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.SelectionModel;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.view.MainFrameGetter;

import java.util.List;
import java.util.Map;

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
			IProductionConfigMapping mapping = BeanProvider.getBean(BeansName.PRODUCTION_CONFIG_MAPPING);
			mapping.put(productionMode, configId);
			addToRemoteMapping(productionMode, idOnRemoteServer);
		}

		/**
		 * Add or replace in the mapping: ProductionMode, on scl app <---> market type id, on server <br/>
		 * this mapping is used when converting the tree of production parameter
		 */
		public static void addToRemoteMapping(ProductionMode mode, int idOnRemoteServer) {
			IProductionModeMapping mapping = BeanProvider.getBean(BeansName.PRODUCTION_MODE_MAPPING);
			mapping.add(mode, idOnRemoteServer);
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
			public static void addToMapping(String logicalName, String nameOnPlc) {
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

	public static abstract class Model {

		/**
		 * This method allows a property to be added to a class. After adding the property, an instance implementing
		 * the ICustomizable interface, can set the value of the added property. Setting the value of a property
		 * without previously adding it to the class, will result in an exception.
		 * @param classToCustomize class to add property to
		 * @param property custom property to add
		 * @param <T> the type of custom property to ad
		 * @see CustoBuilderTest.testModelAddCustomPropertySuccess
		 *
		 */
		public static <T> void addPropertyToClass(Class<? extends ICustomizable> classToCustomize, CustomProperty<T>
				property) {
			CustomizablePropertyDefinition definition = new CustomizablePropertyDefinition();
			definition.addProperty(classToCustomize, property);
			CustomizablePropertyFactory.setCustomizablePropertyDefinition(definition);
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
