package com.sicpa.standard.sasscl.remoteControl.connector.manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.sasscl.remoteControl.RemoteControlMain;

public class ConnectionManager extends JFrame {

	private static final long serialVersionUID = 1L;

	protected static ConnectionManager INSTANCE;

	public static void showManager(final String ip, final String port) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (INSTANCE == null) {
					INSTANCE = new ConnectionManager();
				}
				INSTANCE.getTextIp().setText(ip);
				INSTANCE.getTextPort().setText(port);
				INSTANCE.setVisible(true);
			}
		});
	}

	public static void showManager() {
		showManager("", "");
	}

	public static void main(String[] args) {
		SicpaLookAndFeel.install();

		showManager();
	}

	protected JButton buttonStart;

	protected JTextField textIp;
	protected JTextField textPort;

	private ConnectionManager() {
		initGUI();
	}

	private void initGUI() {
		getContentPane().setLayout(new MigLayout("fill"));
		getContentPane().add(new JLabel("ip"));
		getContentPane().add(getTextIp(), "growx");
		getContentPane().add(new JLabel("port"));
		getContentPane().add(getTextPort(), "growx");
		getContentPane().add(getButtonStart());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450, 100);
	}

	public JTextField getTextIp() {
		if (textIp == null) {
			textIp = new JTextField(15);
		}
		return textIp;
	}

	public JTextField getTextPort() {
		if (textPort == null) {
			textPort = new JTextField(8);
		}
		return textPort;
	}

	public JButton getButtonStart() {
		if (buttonStart == null) {
			buttonStart = new JButton("Connect");
			buttonStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonStartActionPerformed();
				}
			});
		}
		return buttonStart;
	}

	protected void buttonStartActionPerformed() {

		final String ip = getTextIp().getText();
		final String port = getTextPort().getText();
		getButtonStart().setEnabled(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					RemoteControlMain.start(ip, port);
					setVisible(false);
				} finally {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							getButtonStart().setEnabled(true);
						}
					});
				}
			}
		}).start();
	}
}
