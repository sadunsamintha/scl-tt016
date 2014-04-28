package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.variable.EditablePlcVariables;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.IPlcVariableDescriptorListener;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public abstract class PlcVariableDescriptor<T> {

	protected static final Logger logger = LoggerFactory.getLogger(PlcVariableDescriptor.class);

	protected IPlcVariable<T> variable;
	protected transient String formattedValue;
	protected transient PlcProvider plcProvider;
	protected EditablePlcVariables editablePlcVariables;
	protected transient boolean init;

	protected transient List<IPlcVariableDescriptorListener> listeners;

	public PlcVariableDescriptor() {
		listeners = new ArrayList<IPlcVariableDescriptorListener>();
		init = true;
	}

	public String getFormattedValue() {
		return formattedValue;
	}

	public void setValue(final T value) {
		variable.setValue(value);
		fireValueChanged();
		setFormattedValue();
		sendValueToPlc(variable);
	}

	protected void setFormattedValue() {
		formattedValue = "";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setVariable(final IPlcVariable variable) {
		this.variable = variable;
		setFormattedValue();
	}

	public IPlcVariable<T> getVariable() {
		return variable;
	}

	protected final Collection<String> toFilter = new ArrayList<String>(Arrays.asList(".com.stLine[1].stParameters.",
			".com.stLine[2].stParameters.", ".com.stLine[3].stParameters.", ".com.stCabinet.stParameters."));

	public String getVarName() {

		return variable.getVariableName();
	}

	public String getShortVarName() {
		String shortName = getVarName();
		for (String pattern : toFilter) {
			shortName = shortName.replace(pattern, "");
		}
		return shortName;
	}

	public void sendValueToPlc() {
		this.sendValueToPlc(variable);
	}

	public void sendValueToPlc(final IPlcVariable<?> variable) {
		if (init) {
			return;
		}
		// save to a file
		this.editablePlcVariables.update(variable.getVariableName(), variable.getValue());
		// send to plc if production is running
		IPlcAdaptor plc = plcProvider != null ? plcProvider.get() : null;
		if (plc != null) {
			try {
				if (plc.isConnected()) {
					plc.reloadPlcParameter(variable);
				}
			} catch (final PlcAdaptorException e) {
				logger.error("", e);
				EventBusService.post(new MessageEvent(this, MessageEventKey.PLC.ERROR_SENDING_PARAM, e, variable
						.getVariableName()));
			}
		}
	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public Object getValue() {
		return variable.getValue();
	}

	public void setEditablePlcVariables(final EditablePlcVariables editablePlcVariables) {
		this.editablePlcVariables = editablePlcVariables;
	}

	public abstract void validate() throws ValidatorException;

	public void setInit(final boolean init) {
		this.init = init;
	}

	public void addListener(final IPlcVariableDescriptorListener lis) {
		if (listeners == null) {
			listeners = new ArrayList<IPlcVariableDescriptorListener>();
		}
		listeners.add(lis);
	}

	public void removeListener(final IPlcVariableDescriptorListener lis) {
		listeners.remove(lis);
	}

	protected void fireValueChanged() {
		for (IPlcVariableDescriptorListener l : listeners) {
			l.valueChanged();
		}
	}

	protected void fireFormatedValueChanged() {
		for (IPlcVariableDescriptorListener l : listeners) {
			l.formatedValueChanged();
		}
	}

	protected transient JComponent renderer;

	public abstract JComponent getRenderer();

	public abstract PlcVariableDescriptor<T> clone();
}
