package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;

public class PlcPulseUnitRenderer extends AbstractPlcNumberVariableRenderer<Float> {
	private static final Logger logger = LoggerFactory.getLogger(PlcPulseUnitRenderer.class);

	private static final long serialVersionUID = 1L;
	private JComboBox<PlcUnit> comboUnit;

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
		return comboUnit;
	}

	private void comboUnitActionPerformed() {
		selectionChanged();
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
			getComboUnit().setSelectedItem(getPulseDescriptor().getCurrentUnit());
		} catch (Exception e) {
			logger.error("error setting value for:" + desc.getVarName() + " value:" + desc.getValue(), e);
		}
	}
	
	@Override
	protected SpinnerNumberModel createSpinnerNumberModel() {
		return new SpinnerNumberModel(new Float(0), new Float(0f), new Float(Short.MAX_VALUE), new Float(0.1f));
	}

	@Override
	protected Float parseValue(String value) {
		return Float.parseFloat(value);
	}
}
