package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.listener.CoalescentChangeListener;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.utils.TextUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;

@SuppressWarnings("serial")
public class PlcIntegerVariableRenderer extends JPanel implements IPlcVariableDescriptorListener {

	private static final Logger logger = LoggerFactory.getLogger(PlcIntegerVariableRenderer.class);
	
	public static final int DEFAULT_FONT_SIZE = 18;
	public static final int MIN_FONT_SIZE = 8;
	public static final int LABEL_WIDTH = 370;

	private JLabel labelVarName;
	private JSpinner spinner;
	protected PlcIntegerVariableDescriptor desc;

	public PlcIntegerVariableRenderer(PlcIntegerVariableDescriptor desc) {
		this.desc = desc;
		desc.setInit(true);
		desc.addListener(this);
		initGUI();
		valueChanged();
		desc.setInit(false);
	}

	private void initGUI() {
		setLayout(new MigLayout("ltr"));
		add(getLabelVarName(), "w " + LABEL_WIDTH + "!");
		add(getSpinner(), "");
	}

	public JLabel getLabelVarName() {
		if (labelVarName == null) {
			labelVarName = new JLabel(desc.getVarName());
			labelVarName.setFont(SicpaFont.getFont(DEFAULT_FONT_SIZE));
			Font f = TextUtils.getOptimumFont(labelVarName.getText(), LABEL_WIDTH, labelVarName.getFont());
			if (f.getSize() < MIN_FONT_SIZE) {
				f = SicpaFont.getFont(MIN_FONT_SIZE);
			}
			labelVarName.setFont(f);
		}
		return labelVarName;
	}

	public JSpinner getSpinner() {
		if (spinner == null) {
			spinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
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
		desc.setValue("" + getSpinner().getValue());
	}

	@Override
	public void valueChanged() {
		ThreadUtils.invokeLater(() -> ValueChangedInEDT());
	}

	private void ValueChangedInEDT() {
		try {
			int value = Integer.parseInt(desc.getValue());
			getSpinner().setValue(value);
		} catch (Exception e) {
			logger.error("error setting value for:" + desc.getVarName() + " value:" + desc.getValue(), e);
		}
	}

}
