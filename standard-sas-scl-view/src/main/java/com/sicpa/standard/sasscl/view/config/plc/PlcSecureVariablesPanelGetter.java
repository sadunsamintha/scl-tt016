package com.sicpa.standard.sasscl.view.config.plc;

import java.util.List;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.devices.plc.variable.EditablePlcVariables;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class PlcSecureVariablesPanelGetter extends SecuredComponentGetter {

	protected PlcSecureVariablesPanelGetter() {
		super(SasSclPermission.EDIT_PLC_SECURE_VARIABLES, "label.plc.secure.variables");
	}

	protected MultiEditablePlcVariablesSet component;
	protected List<PlcVariableGroup> groupsVarCabinet;

	@Override
	public MultiEditablePlcVariablesSet getComponent() {
		if (component == null) {
			component = new MultiEditablePlcVariablesSet(groupsVarCabinet);
			EventBusService.register(component);
		}
		return component;
	}

	public void setCabinetVariables(final EditablePlcVariables e) {
		this.groupsVarCabinet = e.getGroups();
	}

//	@Subscribe
//	public void handleNewPlcGroupEditable(final PlcVariableGroupEvent evt) {
//		ThreadUtils.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				getComponent().handleNewPlcGroupEditable(evt);
//			}
//		});
//	}

}
