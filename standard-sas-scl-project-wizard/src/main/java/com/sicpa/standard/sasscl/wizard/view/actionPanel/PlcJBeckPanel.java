package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import java.util.Arrays;
import java.util.List;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.configaction.SetPLCJBeckAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.DeviceBox;

public class PlcJBeckPanel extends DeviceBox implements IActionProvider, IReplaceableComponent {

	private static final long serialVersionUID = 1L;

	public PlcJBeckPanel() {
		text = "PLC jbeck";
	}

	@Override
	public IConfigAction provide() {
		return new SetPLCJBeckAction();
	}

	@Override
	public List<Class> getReplaceableClasses() {
		Class c = ((Class<?>) PlcBeckHoffPanel.class);
		return Arrays.asList(c);
	}
	
	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}
	
}
