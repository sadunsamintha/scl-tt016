package com.sicpa.standard.sasscl.view.config.plc;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class PlcVariablesPanelGetter extends SecuredComponentGetter {

	protected PlcVariablesPanelGetter() {
		super(SasSclPermission.EDIT_PLC_VARIABLES, "label.plc.variables");
	}

	protected MultiEditablePlcVariablesSet component;

	@Override
	public MultiEditablePlcVariablesSet getComponent() {
		if (component == null) {
			component = new MultiEditablePlcVariablesSet();
			EventBusService.register(component);
		}
		return component;
	}

	@Subscribe
	public void handleNewPlcGroupEditable(PlcVariableGroupEvent evt) {
		ThreadUtils.invokeLater(() -> getComponent().handleNewPlcGroupEditable(evt));
	}

}
