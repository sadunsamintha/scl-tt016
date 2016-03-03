package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.listener.CoalescentChangeListener;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.utils.TextUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcByteVariableDescriptor;

@SuppressWarnings("serial")
public class PlcByteVariableRenderer extends JPanel implements IPlcVariableDescriptorListener {

	private static final Logger logger = LoggerFactory.getLogger(PlcByteVariableRenderer.class);
	protected JLabel labelVarName;
	protected JSpinner spinner;
	protected JLabel labelFormatedValue;
	protected PlcByteVariableDescriptor plcVar;

	public PlcByteVariableRenderer(final PlcByteVariableDescriptor plcVar) {
		this.plcVar = plcVar;
		plcVar.addListener(this);
		initGUI();
		valueChanged();
	}

	public static int LABEL_WIDTH = 250;

	private void initGUI() {
		setLayout(new MigLayout("ltr"));
		add(getLabelVarName(), "w " + LABEL_WIDTH + "!");
		add(getSpinner(), "");
		add(getLabelFormatedValue());
	}

	public JLabel getLabelVarName() {
		if (labelVarName == null) {
			labelVarName = new JLabel(plcVar.getShortVarName());
			labelVarName.setFont(SicpaFont.getFont(12));
			Font f = TextUtils.getOptimumFont(labelVarName.getText(), LABEL_WIDTH, labelVarName.getFont());
			if (f.getSize() < 8) {
				f = SicpaFont.getFont(8);
			}
			labelVarName.setFont(f);
		}
		return this.labelVarName;
	}

	public JLabel getLabelFormatedValue() {
		if (labelFormatedValue == null) {
			labelFormatedValue = new JLabel(plcVar.getFormattedValue());
			labelFormatedValue.setFont(SicpaFont.getFont(12));
		}
		return this.labelFormatedValue;
	}

	public JSpinner getSpinner() {
		if (spinner == null) {
			SpinnerNumberModel model = new SpinnerNumberModel(new Short(plcVar.getMin()), new Short(plcVar.getMin()),
					new Short(plcVar.getMax()), new Short((short) 1));

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
		plcVar.setValue((Short) getSpinner().getValue());
		getLabelFormatedValue().setText(plcVar.getFormattedValue());

		if (!StringUtils.isEmpty(plcVar.getFormattedValue())) {
			OperatorLogger.log("PLC Variables - {} = {}",
					new Object[] { plcVar.getVarName(), plcVar.getFormattedValue() });
		} else {
			OperatorLogger.log("PLC Variables - {} = {}", new Object[] { plcVar.getVarName(), plcVar.getValue() });
		}
	}

	public PlcByteVariableDescriptor getPlcVar() {
		return plcVar;
	}

	@Override
	public void formatedValueChanged() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabelFormatedValue().setText(plcVar.getFormattedValue());
			}
		});
	}

	@Override
	public void valueChanged() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				Object value = getPlcVar().getValue();
				if (value == null) {
					value = Short.valueOf((short) 0);
				}
				try {
					getSpinner().setValue(value);
				} catch (Exception e) {
					logger.error("error setting value for:" + plcVar.getVarName());
				}
			}
		});
	}

}
