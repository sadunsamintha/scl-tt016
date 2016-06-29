package com.sicpa.tt016.devices.plc;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.plc.IPlcListener;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

import java.util.List;

public class PlcCameraProductStatusListener implements IPlcListener {

	private PlcProvider plcProvider;
	private String plcCameraProductStatusNtfVarName;

	public void init() {
		plcProvider.addChangeListener(evt -> plcProvider.get().addPlcListener(this));
	}

	@Override
	public void onPlcEvent(PlcEvent event) {
		EventBusService.post(PlcCameraResultParser.getPlcCameraResultEvent((String) event.getValue()));
	}

	@Override
	public List<String> getListeningVariables() {
		return PlcLineHelper.getLinesVariableName(plcCameraProductStatusNtfVarName);
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setPlcCameraProductStatusNtfVarName(String plcCameraProductStatusNtfVarName) {
		this.plcCameraProductStatusNtfVarName = plcCameraProductStatusNtfVarName;
	}
}
