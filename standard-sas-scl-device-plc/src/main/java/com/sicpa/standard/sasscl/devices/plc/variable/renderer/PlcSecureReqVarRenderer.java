package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcSecureReqVarDescriptor;

public class PlcSecureReqVarRenderer extends JPanel {

	protected static final long serialVersionUID = 1L;
	protected PlcSecureReqVarDescriptor plcVarDesc;
	protected JButton button;
	protected JLabel label;

	public PlcSecureReqVarRenderer(final PlcSecureReqVarDescriptor plcVarDesc) {
		this.plcVarDesc = plcVarDesc;
//		plcVarDesc.addListener(this);
		initGUI();
	}

	protected void initGUI() {
		setLayout(new MigLayout("ltr"));
		add(getButton(), "");
		add(getLabel());
	}

	public JButton getButton() {
		if (button == null) {
			button = new JButton("SEND");
//			valueChanged();
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					sendRequest();
				}
			});
		}
		return this.button;
	}

	protected void sendRequest() {
		plcVarDesc.sendRequestToPlc(true);
//		OperatorLogger.log("PLC Variables - {} = {}", new Object[] { plcVarDesc.getVarName(), plcVarDesc.getValue() });
	}

//	@Override
//	public void formatedValueChanged() {
//		ThreadUtils.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				getLabel().setText(plcVarDesc.getFormattedValue());
//			}
//		});
//	}

	public JLabel getLabel() {
		if (this.label == null) {
			this.label = new JLabel(plcVarDesc.getVarName());
		}
		return this.label;
	}

//	@Override
//	public void valueChanged() {
//		ThreadUtils.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				Boolean value = (Boolean) plcVarDesc.getValue();
//				if (value == null) {
//					value = false;
//				}
//				getCheck().setSelected(value);
//			}
//		});
//	}
	
}
