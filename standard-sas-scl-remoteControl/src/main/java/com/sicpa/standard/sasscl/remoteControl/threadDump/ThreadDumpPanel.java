package com.sicpa.standard.sasscl.remoteControl.threadDump;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;

public class ThreadDumpPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected RemoteControlSasMBean controlBean;
	protected JButton buttonRefresh;
	protected JTextArea textDump;

	public ThreadDumpPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getButtonRefresh(), "wrap");
		add(new JScrollPane(getTextDump()), "grow,push");
	}

	public void setControlBean(RemoteControlSasMBean controlBean) {
		this.controlBean = controlBean;
	}

	public JButton getButtonRefresh() {
		if (buttonRefresh == null) {
			buttonRefresh = new JButton("refresh");
			buttonRefresh.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonRefreshActionPerformed();
				}
			});
		}
		return buttonRefresh;
	}

	protected void buttonRefreshActionPerformed() {
		textDump.setText(controlBean.createThreadsDump());
	}

	public JTextArea getTextDump() {
		if (textDump == null) {
			textDump = new JTextArea();
			textDump.setEditable(false);
		}
		return textDump;
	}
}
