package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import java.text.NumberFormat;

import javax.swing.JComponent;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter.IPulseToMMConverter;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event.PulseConversionChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcPulseUnitRenderer;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValueWithUnit;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PlcPulseVariableDescriptor extends PlcIntegerVariableDescriptor {

	protected IPlcVariable<Boolean> unitPlcVar;

	protected PlcUnit currentUnit;

	protected PlcPulseVariableDescriptor[] greaterThan;
	protected PlcPulseVariableDescriptor[] lowerThan;

	protected int minPulse;
	protected int maxPulse;
	protected int minMs;
	protected int maxMs;
	protected NumberFormat mmFormat;

	protected IPulseToMMConverter pulseConverter;

	public PlcPulseVariableDescriptor() {
	}

	public void validatePulseRange(final Integer value) throws ValidatorException {
		if (minPulse > value || value > maxPulse) {

			throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_RANGE, variable.getVariableName(),
					variable.getVariableName(), value, minPulse, maxPulse);
		}
	}

	public void validateMSRange(final Integer value) throws ValidatorException {
		if (minMs > value || value > maxMs) {

			throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_RANGE, variable.getVariableName(),
					variable.getVariableName(), value, minMs, maxMs);
		}
	}

	@Override
	public void validate() throws ValidatorException {

		Integer value = variable.getValue();
		if (value == null) {
			throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_NULL, variable.getVariableName(),
					this.variable.getVariableName());
		}

		if (currentUnit == PlcUnit.PULSE) {
			validatePulseRange(value);
		} else if (currentUnit == PlcUnit.MS) {
			validateMSRange(value);
		}

		checkLower();
		checkGreater();
	}

	protected void checkLower() throws ValidatorException {
		Integer current = (Integer) getValue();
		if (lowerThan != null && lowerThan.length != 0) {
			for (PlcPulseVariableDescriptor d : lowerThan) {
				if (currentUnit == d.currentUnit) {
					Integer compareValue = (Integer) d.getValue();
					if (compareValue != null && current != null) {
						if (current > compareValue) {

							throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_LOWER, getVarName(), current,
									d.getVarName(), compareValue);
						}
					}
				}
			}
		}
	}

	protected void checkGreater() throws ValidatorException {
		Integer current = (Integer) getValue();
		if (greaterThan != null && greaterThan.length != 0) {
			for (PlcPulseVariableDescriptor d : greaterThan) {
				if (currentUnit == d.currentUnit) {
					Integer compareValue = (Integer) d.getValue();
					if (compareValue != null && current != null) {
						if (current < compareValue) {
							throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_HIGHER, getVarName(), current,
									d.getVarName(), compareValue);
						}
					}
				}
			}
		}
	}

	@Override
	protected void setFormattedValue() {
		if (currentUnit == PlcUnit.MS) {
			formattedValue = getValue() + " ms";
		} else if (pulseConverter != null) {
			formattedValue = convertPulseToMM((Integer) getValue()) + " mm";
		}
		fireFormatedValueChanged();
	}

	protected void setupMMFormater() {
		mmFormat = NumberFormat.getInstance();
		mmFormat.setMinimumFractionDigits(2);
		mmFormat.setMaximumFractionDigits(2);
	}

	protected String convertPulseToMM(int value) {
		if (mmFormat == null) {
			setupMMFormater();
		}
		return mmFormat.format(pulseConverter.convertToMM(value));
	}

	public void setCurrentUnit(final PlcUnit currentUnit) {
		this.currentUnit = currentUnit;
		if (currentUnit == null) {
			return;
		}
		unitPlcVar.setValue(currentUnit == PlcUnit.PULSE);
		setFormattedValue();

		PlcValueWithUnit v = (PlcValueWithUnit) editablePlcVariables.getPlcValues().get(getVarName());
		v.setUnit(currentUnit);

		sendValueToPlc(unitPlcVar);
	}

	public String getUnitVarName() {
		return unitPlcVar.getVariableName();
	}

	@Override
	public JComponent getRenderer() {
		if (renderer == null) {
			renderer = new PlcPulseUnitRenderer(this);
		}
		return renderer;
	}

	public void setGreaterThan(final PlcPulseVariableDescriptor[] greaterThan) {
		this.greaterThan = greaterThan;
	}

	public void setLowerThan(PlcPulseVariableDescriptor[] lowerThan) {
		this.lowerThan = lowerThan;
	}

	public int getMinPulse() {
		return minPulse;
	}

	public void setMinPulse(int minPulse) {
		this.minPulse = minPulse;
	}

	public int getMaxPulse() {
		return maxPulse;
	}

	public void setMaxPulse(int maxPulse) {
		this.maxPulse = maxPulse;
	}

	public int getMinMs() {
		return minMs;
	}

	public void setMinMs(int minMs) {
		this.minMs = minMs;
	}

	public int getMaxMs() {
		return maxMs;
	}

	public void setMaxMs(int maxMs) {
		this.maxMs = maxMs;
	}

	@Override
	public void setMin(int min) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMax(int max) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMin() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMax() {
		throw new UnsupportedOperationException();
	}

	public void setUnitPlcVar(final IPlcVariable<Boolean> unitPlcVar) {
		this.unitPlcVar = unitPlcVar;
	}

	public IPlcVariable<Boolean> getUnitPlcVar() {
		return unitPlcVar;
	}

	@Subscribe
	public void handlePulseConversionChanged(PulseConversionChangedEvent evt) {
		if (getVarName().startsWith(evt.getLine()) || getVarName().startsWith(".ExtGbl_")) {
			pulseConverter = evt.getConverter();
			setFormattedValue();
		}
	}

	@Override
	public PlcPulseVariableDescriptor clone() {
		PlcPulseVariableDescriptor descriptor = new PlcPulseVariableDescriptor();
		descriptor.plcProvider = plcProvider;
		descriptor.editablePlcVariables = editablePlcVariables;
		descriptor.variable = variable;

		descriptor.currentUnit = currentUnit;
		descriptor.unitPlcVar = unitPlcVar;

		descriptor.greaterThan = greaterThan;
		descriptor.lowerThan = lowerThan;

		descriptor.minMs = minMs;
		descriptor.maxMs = maxMs;

		descriptor.minPulse = minPulse;
		descriptor.maxPulse = maxPulse;
		return descriptor;
	}

	public PlcUnit getCurrentUnit() {
		return currentUnit;
	}
}
