package com.sicpa.standard.gui.demo.layout.miglayout;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class MigLayoutDemoFrame4 extends JFrame {

	private JPanel panel;

	public MigLayoutDemoFrame4() {
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
				SicpaLookAndFeelCusto.install();
				MigLayoutDemoFrame4 test = new MigLayoutDemoFrame4();
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				test.setSize(600, 250);
				test.setVisible(true);
			}
		});
	}

	public JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();
			this.panel.setLayout(new MigLayout("", "[][][][][]50[]", ""));

			this.panel.add(new JLabel("MODULES:"), "spanx 2");
			this.panel.add(new JRadioButton("0"));
			this.panel.add(new JRadioButton("1"));
			this.panel.add(new JRadioButton("2"));
			this.panel.add(new JCheckBox("IMAGES"), "wrap");
			this.panel.add(new JCheckBox("both"), "span y2");
			this.panel.add(new JLabel("PRINTER[mm]"));
			this.panel.add(new JSpinner(), "spanx 3 , center");
			this.panel.add(new JButton(), "wrap,h 150 , w 150,spany 3");
			this.panel.add(new JLabel("CAMERA[mm]"));
			this.panel.add(new JSpinner(), "spanx 3 , center,wrap");

		}
		return this.panel;
	}
}
