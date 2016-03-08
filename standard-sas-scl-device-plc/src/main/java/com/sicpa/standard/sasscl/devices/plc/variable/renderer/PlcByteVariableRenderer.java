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
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcByteVariableDescriptor;

@SuppressWarnings("serial")
public class PlcByteVariableRenderer extends JPanel implements IPlcVariableDescriptorListener {

	private static final Logger logger = LoggerFactory.getLogger(PlcByteVariableRenderer.class);
	private JLabel labelVarName;
	private JSpinner spinner;
	private PlcByteVariableDescriptor desc;

	public PlcByteVariableRenderer(PlcByteVariableDescriptor desc) {
		this.desc = desc;
		desc.setInit(true);
		desc.addListener(this);
		initGUI();
		valueChanged();
		desc.setInit(false);
	}

	public static int LABEL_WIDTH = 250;

	private void initGUI() {
		setLayout(new MigLayout("ltr"));
		add(getLabelVarName(), "w " + LABEL_WIDTH + "!");
		add(getSpinner(), "");
	}

	public JLabel getLabelVarName() {
		if (labelVarName == null) {
			labelVarName = new JLabel(desc.getVarName());
			labelVarName.setFont(SicpaFont.getFont(12));
			Font f = TextUtils.getOptimumFont(labelVarName.getText(), LABEL_WIDTH, labelVarName.getFont());
			if (f.getSize() < 8) {
				f = SicpaFont.getFont(8);
			}
			labelVarName.setFont(f);
		}
		return this.labelVarName;
	}

	public JSpinner getSpinner() {
		if (spinner == null) {
			SpinnerNumberModel model = new SpinnerNumberModel(new Short((short) 0), new Short((short) 0), new Short(
					Short.MAX_VALUE), new Short((short) 1));

			spinner = new JSpinner(model);

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
		desc.setValue("" + (Short) getSpinner().getValue());
	}

	@Override
	public void valueChanged() {
		ThreadUtils.invokeLater(() -> {
			Short value = Short.parseShort(desc.getValue());
			try {
				getSpinner().setValue(value);
			} catch (Exception e) {
				logger.error("error setting value for:" + desc.getVarName());
			}
		});
	}
}
