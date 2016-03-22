package com.sicpa.tt018.scl.devices.plc.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.variable.EditablePlcVariables;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValue;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesForAllVar;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt018.interfaces.scl.master.constant.ProductPackagings;
import com.sicpa.tt018.scl.utils.AlbaniaUtilities;

public class AlbaniaPLC extends PlcAdaptor {
	private static final Logger logger = LoggerFactory.getLogger(PlcAdaptor.class);

	protected ProductionParameters productionParameters;
	private AlbaniaConfigFilePlcHolder configFileHolder;
	private IPlcVariable sensorTypeVariable;

	public AlbaniaPLC() {
		super();
	}

	public AlbaniaPLC(final IPlcController<?> controller) {
		super(controller);
	}
	
	protected void sendAllParameters() {
		for (IPlcVariable<?> var : parameters) {
			try {
				logger.info("write plc param: " + var.getVariableName() + " value:" + var.getValue());
				write(var);
			} catch (Exception e) {
				logger.error("failed to write plc param:" + var.getVariableName() + " value:" + var.getValue(), e);
			}
		}

		try {
			sendReloadPlcParametersRequest();
		} catch (PlcAdaptorException e) {
			logger.error("", e);
		}
	}

	protected void loadVariableFile(String fileName, int index) throws Exception {

		if (fileName != null && !fileName.isEmpty()) {

			List<IPlcVariable> params = new ArrayList<IPlcVariable>();

			for (IPlcVariable var : parameterLine) {
				params.add(PlcUtils.clone(var, index));
			}

			params.add(sensorTypeVariable);

			//extract main values
			PlcValuesForAllVar mainValues = loadValues(fileName); 

			//extract dedicated product values values
			int productPackage = getProductPackage();
			logger.info("set product type : " + productPackage);
			String productTypeConfigFile = configFileHolder.getConfigFileByProduct(productPackage);
			
			PlcValuesForAllVar productValues = loadValues(productTypeConfigFile);

			//remove product values from the main
			removeValue(mainValues, productValues);
			
			//merge the values so the loaded will be able to reconciliate parameters + values
			PlcValuesForAllVar mergedValues = mergeValue(mainValues, productValues);
			loader.load(params, mergedValues);
			parameters.addAll(params);
			
			//provide the list 
			List<PlcValuesForAllVar> listAllValues = new ArrayList<>();
			listAllValues.add(mainValues);
			listAllValues.add(productValues);
			generateLineEditableVariable(index, listAllValues);
		}
	}

	private PlcValuesForAllVar loadValues(String fileName) throws IOException, URISyntaxException {
		PlcValuesForAllVar mainValues = ConfigUtils.load(plcConfigFolder + "/" + fileName);
		 mainValues.setFileName(plcConfigFolder + "/" + fileName);
		return mainValues;
	}

	private PlcValuesForAllVar mergeValue(PlcValuesForAllVar mainValue, PlcValuesForAllVar productValue) {
		PlcValuesForAllVar merged = new PlcValuesForAllVar();

		for (PlcValue valuePlc : productValue.getListValues()) {
			merged.add(valuePlc);
		}

		for (PlcValue valuePlc : mainValue.getListValues()) {
			merged.add(valuePlc);
		}
		return merged;
	}

	private void removeValue(PlcValuesForAllVar mainValues, PlcValuesForAllVar productValues) {
		// jsut add the dedicated value of a specific variable
		for (PlcValue productValue : productValues.getListValues()) {
			PlcValue removeValue = mainValues.get(productValue.getVarName());
			mainValues.remove(removeValue);
		}
	}

	private void generateLineEditableVariable(int index, List<PlcValuesForAllVar> values) {
		// sent event to make param available on the gui
		try {
			PlcVariableGroup[] groups = transform(lineVarGroups, index);
			allActivePlcVarGroup.add(groups);

			EditablePlcVariables editablePlcVariables = new EditablePlcVariables(values, groups);
			PlcVariableGroupEvent evt = new PlcVariableGroupEvent(editablePlcVariables, "" + index);
			EventBusService.post(evt);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected int getProductPackage() {

		final int currentPackageType = AlbaniaUtilities.getProductPackage(productionParameters);
		if (currentPackageType == ProductPackagings.BOTTLE.getId() || currentPackageType == ProductPackagings.CAN.getId()) {
			return currentPackageType;
		} else {
			return ProductPackagings.OTHER.getId();
		}

	}

	public IPlcVariable getSensorTypeVariable() {
		return sensorTypeVariable;
	}

	public void setSensorTypeVariable(IPlcVariable sensorTypeVariable) {
		this.sensorTypeVariable = sensorTypeVariable;
	}

	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public AlbaniaConfigFilePlcHolder getConfigFileHolder() {
		return configFileHolder;
	}

	public void setConfigFileHolder(AlbaniaConfigFilePlcHolder configFileHolder) {
		this.configFileHolder = configFileHolder;
	}

}