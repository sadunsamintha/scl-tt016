package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import java.util.Arrays;
import java.util.List;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.configaction.SetCameraDrsAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.DeviceBox;

public class CameraDrsPanel extends DeviceBox implements IActionProvider, IReplaceableComponent {

	private static final long serialVersionUID = 7609804626734158205L;

	public CameraDrsPanel() {
		text = "Camera DRS";
	}

	@Override
	public List<Class> getReplaceableClasses() {
		Class c = ((Class<?>) CameraCognexPanel.class);
		return Arrays.asList(c);
	}

	@Override
	public IConfigAction provide() {
		return new SetCameraDrsAction();
	}

	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}

}
