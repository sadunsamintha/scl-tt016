package com.sicpa.standard.sasscl.devices.plc.variable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValue;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValueWithUnit;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesForAllVar;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class EditablePlcVariables {

	private static final Logger logger = LoggerFactory.getLogger(EditablePlcVariables.class);

	protected PlcValuesForAllVar listValues;

	protected final List<PlcVariableGroup> plcGroup = new ArrayList<>();

	// where to save the var when they changed
	protected String file;

	public EditablePlcVariables(final PlcValuesForAllVar plcValues, final PlcVariableDescriptor<?>... descriptors) {
		this.listValues = plcValues;
		for (PlcVariableDescriptor<?> descriptor : descriptors) {
			addVariableDescriptor(descriptor);
		}
	}

	public EditablePlcVariables(final PlcValuesForAllVar plcValues, final PlcVariableGroup... group) {
		this.listValues = plcValues;
		for (PlcVariableGroup descriptor : group) {
			addGroup(descriptor);
		}
	}

	/**
	 * Add additional plc values into the list, if the list already has the plc value, skip it
	 * 
	 * @param listValues
	 */
	public void addAdditionalPlcValuesForAllVar(PlcValuesForAllVar plcValues) {
		for (PlcValue plcValue : plcValues.getListValues()) {
			if (listValues.getValue(plcValue.getVarName()) == null) {
				listValues.add(plcValue);
			}
		}
	}

	public void addGroup(final PlcVariableGroup group) {
		boolean groupFound = false;
		for (PlcVariableGroup grp : plcGroup) {
			if (grp.description.equals(group.description)) {
				group.plcVars.addAll(grp.plcVars);
				groupFound = true;
				break;
			}
		}
		if (!groupFound) {
			plcGroup.add(group);
		}
		for (PlcVariableDescriptor<?> var : group.getPlcVars()) {
			addVariableDescriptor(var);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addVariableDescriptor(final PlcVariableDescriptor descriptor) {
		descriptor.setEditablePlcVariables(this);
		PlcValue value = listValues.get(descriptor.getVarName());
		if (value == null) {
			if (descriptor instanceof PlcPulseVariableDescriptor) {
				value = new PlcValueWithUnit(null, descriptor.getVarName(), null,
						((PlcPulseVariableDescriptor) descriptor).getUnitVarName());
			} else {
				value = new PlcValue(null, descriptor.getVarName());
			}
			listValues.add(value);
		}
		descriptor.setValue(value.getValue());
		if (descriptor instanceof PlcPulseVariableDescriptor) {
			if (value instanceof PlcValueWithUnit) {
				((PlcPulseVariableDescriptor) descriptor).setCurrentUnit(((PlcValueWithUnit) value).getUnit());
			}
		}
		descriptor.setInit(false);
	}

	public List<PlcVariableGroup> getGroups() {
		return plcGroup;
	}

	public PlcValuesForAllVar getPlcValues() {
		return listValues;
	}

	public void update(final String varName, final Object value) {
		PlcValue plcvalue = listValues.get(varName);
		if (plcvalue != null) {
			plcvalue.setValue(value);
			validate();
			save();
		}
	}

	public void validate() {
		for (PlcVariableGroup grp : getGroups()) {
			for (PlcVariableDescriptor<?> d : grp.getPlcVars()) {
				try {
					d.validate();
				} catch (final ValidatorException e) {
					logger.error(e.getMessage());
					EventBusService.post(new MessageEvent(e.getSource(), e.getLangKey(), e.getParams()));
				}
			}
		}
	}

	protected void save() {
		try {
			ConfigUtils.save(listValues, new File(file));
		} catch (final Exception e) {
			logger.error("", e);
			EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.EXCEPTION_FAIL_SAVE_VARIABLE_TO_FILE, e));
		}
	}

	public void setFile(String file) {
		this.file = file;
	}
}
