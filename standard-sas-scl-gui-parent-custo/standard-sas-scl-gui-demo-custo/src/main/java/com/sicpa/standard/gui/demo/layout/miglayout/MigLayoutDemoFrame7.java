package com.sicpa.standard.gui.demo.layout.miglayout;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class MigLayoutDemoFrame7 extends JFrame {

	private JPanel panel;

	public MigLayoutDemoFrame7() {
		initGUI();
	}

	private void initGUI() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPanel(), BorderLayout.CENTER);
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MigLayoutDemoFrame7 test = new MigLayoutDemoFrame7();
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				test.setSize(600, 550);
				test.setVisible(true);
			}
		});
	}

	public JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();
			this.panel.setLayout(new MigLayout("fill", "", ""));

			JPanel panelButton = new JPanel(new MigLayout(("fill")));
			panelButton.add(new JButton("YES"), "grow");
			panelButton.add(new JButton("NO"), "grow");

			for (int i = 0; i < 5; i++) {
				this.panel.add(new JLabel("some label"));
				this.panel.add(new JLabel("some label"));
				this.panel.add(new JLabel("some label"));
				this.panel.add(new JLabel("some label"), "wrap");
			}
			this.panel.add(panelButton, "pushy,bottom,south");
		}
		return this.panel;
	}
}
