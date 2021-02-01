package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.gui.listener.CoalescentChangeListener;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.utils.TextUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public abstract class AbstractPlcNumberVariableRenderer<TYPE extends Number> extends JPanel implements
		IPlcVariableDescriptorListener {

	public static final int DEFAULT_FONT_SIZE = 18;
	public static final int MIN_FONT_SIZE = 8;
	public static final int LABEL_WIDTH = 340;

	private static final Logger logger = LoggerFactory.getLogger(AbstractPlcNumberVariableRenderer.class);
	private JLabel labelVarName;
	private JSpinner spinner;
	protected PlcVariableDescriptor desc;

	public AbstractPlcNumberVariableRenderer(PlcVariableDescriptor desc) {
		this.desc = desc;
		desc.setInit(true);
		desc.addListener(this);
		initGUI();
		valueChanged();
		desc.setInit(false);
		EventBusService.register(this);
	}

	private void initGUI() {
		setLayout(new MigLayout("ltr"));
		add(getLabelVarName(), "");
		
		int lineSize = SwingUtilities.computeStringWidth(getLabelVarName().getFontMetrics(getLabelVarName().getFont()), getLabelVarName().getText());
		int infoWidth = Math.abs(LABEL_WIDTH - lineSize);
		
		add(new PlcVariableInfoRenderer(desc), "gap right " + infoWidth);
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
		return this.labelVarName;
	}

	public JSpinner getSpinner() {
		if (spinner == null) {
			SpinnerNumberModel model = createSpinnerNumberModel();
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

	public void valueChanged(){
		ThreadUtils.invokeLater(() -> {
			try {
				getSpinner().setValue(parseValue(desc.getValue()));
			} catch (Exception e) {
				logger.error("error setting value for:" + desc.getVarName());
			}
		});
	};

	protected abstract SpinnerNumberModel createSpinnerNumberModel();

	protected void spinnerChangeListener() {
		desc.setValue("" + getSpinner().getValue());
	}


	protected abstract TYPE parseValue(String value);
}
