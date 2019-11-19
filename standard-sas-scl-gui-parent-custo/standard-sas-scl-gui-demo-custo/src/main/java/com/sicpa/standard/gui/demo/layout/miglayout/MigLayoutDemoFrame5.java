package com.sicpa.standard.gui.demo.layout.miglayout;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class MigLayoutDemoFrame5 extends JFrame {

	private JPanel panel;

	public MigLayoutDemoFrame5() {
		initGUI();
	}

	private void initGUI() {

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPanel(), BorderLayout.CENTER);
	}

	public static void main(final String[] args) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				MigLayoutDemoFrame5 test = new MigLayoutDemoFrame5();
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				test.setSize(400, 450);
				test.setVisible(true);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	public JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();

			this.panel.setLayout(new MigLayout("fill"));

			this.panel.add(new JLabel("PRODUCTION HISTORY"), "spanx, center");

			this.panel.add(new JLabel("PACKS"), "skip 1,center");
			this.panel.add(new JLabel("SHEETS"), "wrap,center");

			this.panel.add(new JLabel("VALID"));
			this.panel.add(new JLabel("0"), "center");
			this.panel.add(new JLabel("0"), "center,wrap");

			this.panel.add(new JLabel("CANCELLED"));
			this.panel.add(new JLabel("0"), "center");
			this.panel.add(new JLabel("0"), "center,wrap");

			this.panel.add(new JLabel("EJECTED SHEETS G1"), "gaptop 20 ,spanx 2");
			this.panel.add(new JLabel("0"), "wrap,right");

			this.panel.add(new JLabel("EJECTED SHEETS G1"), "spanx 2");
			this.panel.add(new JLabel("0"), "wrap,right");

			this.panel.add(new JLabel("TOTAL SCRAPPED SHEETS"), "gaptop 20, spanx 2");
			this.panel.add(new JLabel("0"), "wrap,right");

			this.panel.add(new JLabel("CODING TIME:"), "gaptop 20,spanx 2");
			this.panel.add(new JLabel("00:00:00"), "wrap");

			this.panel.add(new JButton("RESET"), "gaptop 20,h 50,w 100");

		}
		return this.panel;
	}
}
