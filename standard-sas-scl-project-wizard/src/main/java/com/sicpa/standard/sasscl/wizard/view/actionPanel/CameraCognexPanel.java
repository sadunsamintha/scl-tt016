package com.sicpa.standard.sasscl.wizard.view.actionPanel;

import java.util.Arrays;
import java.util.List;

import com.sicpa.standard.sasscl.wizard.ApplicationType;
import com.sicpa.standard.sasscl.wizard.configaction.IConfigAction;
import com.sicpa.standard.sasscl.wizard.configaction.SetCameraCognexAction;
import com.sicpa.standard.sasscl.wizard.view.IActionProvider;
import com.sicpa.standard.sasscl.wizard.view.box.DeviceBox;

public class CameraCognexPanel extends DeviceBox implements IActionProvider, IReplaceableComponent {

	private static final long serialVersionUID = -918107985152956750L;

	public CameraCognexPanel() {
		text = "Camera Cognex";
	}

	@Override
	public List<Class> getReplaceableClasses() {
		Class c = ((Class<?>) CameraDrsPanel.class);
		return Arrays.asList(c);
	}

	@Override
	public IConfigAction provide() {
		return new SetCameraCognexAction();
	}

	@Override
	public boolean isAvailable(ApplicationType type) {
		return true;
	}

}
