package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import java.util.Arrays;
import java.util.List;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.configaction.SetPlcBeckHoffAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.DeviceBox;

public class PlcBeckHoffPanel extends DeviceBox implements IActionProvider, IReplaceableComponent {

	private static final long serialVersionUID = 1L;

	public PlcBeckHoffPanel() {
		text = "PLC beckhoff";
	}

	@Override
	public IConfigAction provide() {
		return new SetPlcBeckHoffAction();
	}

	@Override
	public List<Class> getReplaceableClasses() {
		Class c = ((Class<?>) PlcJBeckPanel.class);
		return Arrays.asList(c);
	}
	
	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}

}
