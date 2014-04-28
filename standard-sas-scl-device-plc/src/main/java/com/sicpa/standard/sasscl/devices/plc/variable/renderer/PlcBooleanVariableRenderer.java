package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcBooleanVariableDescriptor;

public class PlcBooleanVariableRenderer extends JPanel implements IPlcVariableDescriptorListener {

	protected static final long serialVersionUID = 1L;
	protected PlcBooleanVariableDescriptor plcVarDesc;
	protected JCheckBox check;
	protected JLabel label;

	public PlcBooleanVariableRenderer(final PlcBooleanVariableDescriptor plcVarDesc) {
		this.plcVarDesc = plcVarDesc;
		plcVarDesc.addListener(this);
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("ltr"));
		add(getCheck(), "");
		add(getLabel());
	}

	public JCheckBox getCheck() {
		if (check == null) {
			check = new JCheckBox(plcVarDesc.getShortVarName());
			valueChanged();
			check.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					checkActionPerformed();
				}
			});
		}
		return this.check;
	}

	protected void checkActionPerformed() {
		plcVarDesc.setValue(getCheck().isSelected());
		OperatorLogger.log("PLC Variables - {} = {}", new Object[] { plcVarDesc.getVarName(), plcVarDesc.getValue() });
	}

	@Override
	public void formatedValueChanged() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabel().setText(plcVarDesc.getFormattedValue());
			}
		});
	}

	public JLabel getLabel() {
		if (this.label == null) {
			this.label = new JLabel(plcVarDesc.getFormattedValue());
		}
		return this.label;
	}

	@Override
	public void valueChanged() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				Boolean value = (Boolean) plcVarDesc.getValue();
				if (value == null) {
					value = false;
				}
				getCheck().setSelected(value);
			}
		});

	}
}
