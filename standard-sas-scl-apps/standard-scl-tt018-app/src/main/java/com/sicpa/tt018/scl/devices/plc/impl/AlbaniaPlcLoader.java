package com.sicpa.tt018.scl.devices.plc.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.utils.StringMap;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.devices.plc.PlcValuesLoader;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.config.plc.PlcVariablesPanelGetter;

public class AlbaniaPlcLoader extends PlcValuesLoader {

	private static final Logger logger = LoggerFactory.getLogger(AlbaniaPlcLoader.class);
	private static final int LINE_INDEX_USED = 1;

	private ProductionParameters productionParameters;
	private final Collection<String> varnameProductTypeSpecific = new ArrayList<>();
	private final FileByPackageTypeMapping fileByPackageType = new FileByPackageTypeMapping();
	private String currentProductTypeSpecificFileUse;
	private PlcVariablesPanelGetter plcView;

	public AlbaniaPlcLoader() {
	}

	@Override
	protected void loadProperties() {
		super.loadProperties();
		loadProductTypeFile();
	}

	public void loadProductTypeFile() {
		currentProductTypeSpecificFileUse = fileByPackageType.getFileName(productionParameters);
		if (StringUtils.isNotBlank(currentProductTypeSpecificFileUse)) {
			loadFile(currentProductTypeSpecificFileUse);
		}
	}

	@Subscribe
	public void handleProductionParameterChanged(ProductionParametersEvent evt) {
		loadProductTypeFile();
		refreshValueConfigScreen();
	}

	private void refreshValueConfigScreen() {
		for (String var : varnameProductTypeSpecific) {
			refreshValueConfigScreen(var);
		}
	}

	private void refreshValueConfigScreen(String var) {
		String value = valuesByLines.get(LINE_INDEX_USED).get(var);
		PlcVariableDescriptor desc = findVarDescriptor(var);
		desc.setValueForScreenRefresh(value);
	}

	private PlcVariableDescriptor findVarDescriptor(String var) {
		List<PlcVariableGroup> groups = plcView.getComponent().getPanelsLines().get(LINE_INDEX_USED + "").getGroups();
		for (PlcVariableGroup grp : groups) {
			for (PlcVariableDescriptor desc : grp.getPlcVars()) {
				if (desc.getVarName().equals(var)) {
					return desc;
				}
			}
		}
		return null;
	}

	private void loadFile(String name) {
		try {
			logger.info("loading product type : " + name);
			StringMap prop = loadProperties(name);
			prop = replaceLinePlaceholderInProperties(1, prop);
			valuesByLines.get(LINE_INDEX_USED).putAll(prop);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	@Override
	public void saveLineNewValue(String varName, String value, int lineIndex) {
		if (isProductTypeSpecific(varName)) {
			saveProductSpecificVar(varName, value);
		} else {
			super.saveLineNewValue(varName, value, lineIndex);
		}
	}

	private void saveProductSpecificVar(String var, String value) {
		if (StringUtils.isNotBlank(currentProductTypeSpecificFileUse)) {
			valuesByLines.get(LINE_INDEX_USED).put(var, value);
			StringMap map = createPropertiesToSaveProductSpecific();
			map.put(var, value);
			save(currentProductTypeSpecificFileUse, map);
		}
	}

	private StringMap createPropertiesToSaveProductSpecific() {
		StringMap res = new StringMap();
		for (String var : varnameProductTypeSpecific) {
			String value = valuesByLines.get(LINE_INDEX_USED).get(var);
			res.put(var, value);
		}
		return res;
	}

	private boolean isProductTypeSpecific(String varname) {
		return varnameProductTypeSpecific.contains(varname);
	}

	public void setVarnameProductTypeSpecific(Collection<String> varnameProductTypeSpecific) {
		this.varnameProductTypeSpecific.addAll(varnameProductTypeSpecific);
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setPlcView(PlcVariablesPanelGetter plcView) {
		this.plcView = plcView;
	}
}
