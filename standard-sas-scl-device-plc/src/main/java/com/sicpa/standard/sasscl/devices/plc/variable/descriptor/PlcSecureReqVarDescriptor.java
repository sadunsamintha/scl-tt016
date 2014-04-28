package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcSecureAdaptor;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcSecureAdaptor.SecurePlcRequest;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcSecureReqVarRenderer;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PlcSecureReqVarDescriptor extends PlcVariableDescriptor<Boolean> {
	
	private SecurePlcRequest request;

	public PlcSecureReqVarDescriptor(SecurePlcRequest request) {
		super();
		this.request = request;
	}
	
	@Override
	public String getVarName() {
		return request.name();
	}
	
	@Override
	public void setValue(Boolean value) {		
	}

	@Override
	public void validate() throws ValidatorException {
	}

	@Override
	public JComponent getRenderer() {
		if (renderer == null) {
			renderer = new PlcSecureReqVarRenderer(this);
		}
		return renderer;
	}

	@Override
	public PlcVariableDescriptor<Boolean> clone() {
		PlcSecureReqVarDescriptor descriptor=new PlcSecureReqVarDescriptor(null);
		descriptor.plcProvider=plcProvider;
		descriptor.editablePlcVariables=editablePlcVariables;
		descriptor.variable=variable;
		return descriptor;
	}
	
	public void sendRequestToPlc(boolean value) {
		if (init) {
			return;
		}
		
		IPlcAdaptor plc = plcProvider != null ? plcProvider.get() : null;
		if (plc != null) {
			try {
				if (plc.isConnected()) {
					((PlcSecureAdaptor)plc).executeRequest(request, true);
				}
			} catch (final Exception e) {
				logger.error("", e);
				EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.ERROR_SENDING_PARAM, e, variable.getVariableName()));
			}
		}
	}
}
