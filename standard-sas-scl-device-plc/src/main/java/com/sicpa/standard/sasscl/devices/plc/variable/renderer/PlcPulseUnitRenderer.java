package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.sicpa.standard.gui.listener.CoalescentChangeListener;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;

public class PlcPulseUnitRenderer extends PlcIntegerVariableRenderer {

	private static final long serialVersionUID = 1L;
	protected JComboBox comboUnit;

	public PlcPulseUnitRenderer(final PlcPulseVariableDescriptor plcVar) {
		super(plcVar);
		plcVar.setInit(true);
		initGUI();
		valueChanged();
		comboUnitActionPerformed();
		plcVar.setInit(false);
	}

	private void initGUI() {
		add(getComboUnit(), "growx , w 100");
	}

	@Override
	public PlcPulseVariableDescriptor getPlcVar() {
		return (PlcPulseVariableDescriptor) super.getPlcVar();
	}

	public JComboBox getComboUnit() {
		if (comboUnit == null) {
			comboUnit = new JComboBox();

			comboUnit.addItem(PlcUnit.PULSE);
			comboUnit.addItem(PlcUnit.MS);

			comboUnit.setSelectedItem(getPlcVar().getCurrentUnit());
			comboUnit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					comboUnitActionPerformed();
				}
			});
		}
		return this.comboUnit;
	}

	protected void comboUnitActionPerformed() {
		getPlcVar().setCurrentUnit((PlcUnit) getComboUnit().getSelectedItem());
		Integer value = (Integer) getSpinner().getValue();
		getSpinner().setModel(createSpinnerModel());
		getSpinner().setValue(value);
		getLabelFormatedValue().setText(getPlcVar().getFormattedValue());

	}

	@Override
	public JSpinner getSpinner() {
		if (spinner == null) {
			spinner = new JSpinner(createSpinnerModel());
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

	protected SpinnerNumberModel createSpinnerModel() {
		SpinnerNumberModel model = null;

		if (getPlcVar().getCurrentUnit() == null || getPlcVar().getCurrentUnit().equals(PlcUnit.MS)) {
			model = new SpinnerNumberModel(getPlcVar().getMinPulse(), getPlcVar().getMinMs(), getPlcVar().getMaxMs(), 1);
		} else {
			model = new SpinnerNumberModel(getPlcVar().getMinPulse(), getPlcVar().getMinPulse(), getPlcVar()
					.getMaxPulse(), 1);
		}
		return model;
	}
}
