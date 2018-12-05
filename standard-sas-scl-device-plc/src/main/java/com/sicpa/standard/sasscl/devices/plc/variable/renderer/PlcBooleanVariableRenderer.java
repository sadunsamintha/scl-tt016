package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcBooleanVariableDescriptor;

public class PlcBooleanVariableRenderer extends JPanel implements IPlcVariableDescriptorListener {

	private static final long serialVersionUID = 1L;
	private PlcBooleanVariableDescriptor desc;
	private JCheckBox check;
	private JLabel label;

	public PlcBooleanVariableRenderer(final PlcBooleanVariableDescriptor desc) {
		this.desc = desc;
		desc.addListener(this);
		desc.setInit(true);
		initGUI();
		valueChanged();
		desc.setInit(false);
	}

	protected void initGUI() {
		setLayout(new MigLayout("ltr"));
		add(getCheck(), "");
		add(getLabel(), "");
	}

	public JCheckBox getCheck() {
		if (check == null) {
			check = new JCheckBox();
			check.addActionListener((e) -> checkActionPerformed());
		}
		return this.check;
	}
	
	public JLabel getLabel() {
		if (label == null) {
			label = new JLabel(desc.getVarName());
		}
		return this.label;
	}

	protected void checkActionPerformed() {
		desc.setValue("" + getCheck().isSelected());
	}

	@Override
	public void valueChanged() {
		ThreadUtils.invokeLater(() -> {
			Boolean value = Boolean.parseBoolean(desc.getValue());
			getCheck().setSelected(value);
		});
	}
}
