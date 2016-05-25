package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.devices.plc.event.PlcVarValueChangeEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.IPlcVariableDescriptorListener;

public abstract class PlcVariableDescriptor {

	protected String varName;
	protected String value;

	protected boolean init;
	protected final List<IPlcVariableDescriptorListener> listeners = new ArrayList<>();
	protected JComponent renderer;
	protected int lineIndex;

	public PlcVariableDescriptor() {
		init = true;
	}

	public void initValue(String value) {
		this.value = value;
	}

	public void setValue(String value) {
		if (init) {
			return;
		}
		if (!value.equals(value)) {
			return;
		}
		setValueForScreenRefresh(value);
		OperatorLogger.log("PLC Variables - {} = {}", new Object[] { getVarName(), value });
		saveAndSendValueToPlc();
	}

	public void setValueForScreenRefresh(String value) {
		this.value = value;
		fireValueChanged();
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getVarName() {
		return varName;
	}

	protected void saveAndSendValueToPlc() {
		EventBusService.post(new PlcVarValueChangeEvent(varName, value, lineIndex));
	}

	protected String getFormattedValueForSending() {
		return value;
	}

	public String getValue() {
		return value;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public void addListener(IPlcVariableDescriptorListener lis) {
		listeners.add(lis);
	}

	public void removeListener(IPlcVariableDescriptorListener lis) {
		listeners.remove(lis);
	}

	protected void fireValueChanged() {
		for (IPlcVariableDescriptorListener l : listeners) {
			l.valueChanged();
		}
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	public abstract JComponent getRenderer();

	public abstract PlcVariableDescriptor clone();

}
