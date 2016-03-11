package com.sicpa.standard.sasscl.devices.plc.variable;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;

public class PlcVariableGroup {

	private final List<PlcVariableDescriptor> plcVars = new ArrayList<>();
	private String description;

	public PlcVariableGroup(final PlcVariableDescriptor... descriptors) {
		for (PlcVariableDescriptor descriptor : descriptors) {
			addDescriptor(descriptor);
		}
	}

	public void addDescriptor(PlcVariableDescriptor descriptor) {
		plcVars.add(descriptor);
	}

	public List<PlcVariableDescriptor> getPlcVars() {
		return plcVars;
	}

	@Override
	public String toString() {
		return "PlcVariableGroup [description=" + description + "]";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
