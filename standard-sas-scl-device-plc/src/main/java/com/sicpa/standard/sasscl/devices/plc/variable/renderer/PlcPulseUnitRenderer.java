package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.listener.CoalescentChangeListener;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;

public class PlcPulseUnitRenderer extends PlcIntegerVariableRenderer {
	private static final Logger logger = LoggerFactory.getLogger(PlcPulseUnitRenderer.class);

	private static final long serialVersionUID = 1L;
	protected JComboBox<PlcUnit> comboUnit;

	public PlcPulseUnitRenderer(PlcPulseVariableDescriptor desc) {
		super(desc);
		desc.setInit(true);
		initGUI();
		comboUnitActionPerformed();
		desc.setInit(false);
	}

	private void initGUI() {
		add(getComboUnit(), "growx , w 100");
	}

	private PlcPulseVariableDescriptor getPulseDescriptor() {
		return (PlcPulseVariableDescriptor) desc;
	}

	public JComboBox<PlcUnit> getComboUnit() {
		if (comboUnit == null) {
			comboUnit = new JComboBox<>();

			comboUnit.addItem(PlcUnit.MM);
			comboUnit.addItem(PlcUnit.MS);

			comboUnit.addActionListener(e -> comboUnitActionPerformed());
		}
		return this.comboUnit;
	}

	private void comboUnitActionPerformed() {
		selectionChanged();
	}

	@Override
	public JSpinner getSpinner() {
		if (spinner == null) {
			spinner = new JSpinner(new SpinnerNumberModel(0, 0, MAX_VALUE, 1));
			spinner.addChangeListener(new CoalescentChangeListener(1000) {
				@Override
				public void doAction() {
					spinnerChangeListener();
				}

				@Override
				public void eventReceived() {
					if (isShowing()) {
						super.eventReceived();
					}
				}
			});
		}
		return spinner;
	}

	protected void spinnerChangeListener() {
		selectionChanged();
	}

	private void selectionChanged() {
		desc.setValue("" + getSpinner().getValue() + getComboUnit().getSelectedItem());
	}

	@Override
	public void valueChanged() {
		ThreadUtils.invokeLater(() -> ValueChangedInEDT());
	}

	private void ValueChangedInEDT() {
		try {
			int valueInt = getValueOnly();
			getSpinner().setValue(valueInt);
			getComboUnit().setSelectedItem(getPulseDescriptor().getCurrentUnit());
		} catch (Exception e) {
			logger.error("error setting value for:" + desc.getVarName() + " value:" + desc.getValue(), e);
		}
	}

	private int getValueOnly() {
		String valueWithUnit = desc.getValue();
		String valueOnly = valueWithUnit.replace(getPulseDescriptor().getCurrentUnit().getSuffix(), "");
		return Integer.parseInt(valueOnly);
	}
}
