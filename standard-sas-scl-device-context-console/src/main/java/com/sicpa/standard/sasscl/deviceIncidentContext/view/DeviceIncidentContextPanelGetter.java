package com.sicpa.standard.sasscl.deviceIncidentContext.view;

import java.awt.Component;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.repository.errors.IErrorsRepository;

public class DeviceIncidentContextPanelGetter extends SecuredComponentGetter {

	public static final Permission DEVICE_CONTEXT_CONSOLE = new Permission("DEVICE_CONTEXT_CONSOLE");

	protected IErrorsRepository errorsRepository;

	protected DeviceIncidentContextPanel panel;

	public DeviceIncidentContextPanelGetter() {
		super(DEVICE_CONTEXT_CONSOLE, "device.context.console");
	}

	@Override
	public Component getComponent() {
		if (panel == null) {
			panel = new DeviceIncidentContextPanel(errorsRepository);
		}
		return panel;
	}

	public void setErrorsRepository(IErrorsRepository errorsRepository) {
		this.errorsRepository = errorsRepository;
	}

}
