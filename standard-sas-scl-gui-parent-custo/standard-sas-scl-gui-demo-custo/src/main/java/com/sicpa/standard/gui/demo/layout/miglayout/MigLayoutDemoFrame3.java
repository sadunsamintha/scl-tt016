package com.sicpa.standard.gui.demo.layout.miglayout;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class MigLayoutDemoFrame3 extends JFrame {

	private JPanel panel;

	public MigLayoutDemoFrame3() {
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
				MigLayoutDemoFrame3 test = new MigLayoutDemoFrame3();
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				test.setSize(500, 600);
				test.setVisible(true);

			}
		});
	}

	public JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();

			this.panel.setLayout(new MigLayout("fill", "[290][]", "[][]20[][]20[][]"));
			this.panel.add(new JLabel("CURRENT SITE"), "spanx");
			this.panel.add(new JTextField(), "spanx, grow");
			this.panel.add(new JLabel("CODE TYPE"), "");
			this.panel.add(new JLabel("DATE"), "wrap");
			this.panel.add(new JTextField(), "grow");
			this.panel.add(new JTextField(), "grow, wrap");
			this.panel.add(new JLabel("BOX ID"), "");
			this.panel.add(new JLabel("REEL QTY"), "wrap");
			this.panel.add(new JTextField(), "grow");
			this.panel.add(new JTextField(), "grow, wrap");
			this.panel.add(new JScrollPane(new JTable(new Object[][] {}, new Object[] { "TYPE", "REEL ID" })),
					"span, pushy, grow");
			this.panel.add(new JButton("DELETE"), "span,split 2,center,sizegroup grp1");
			this.panel.add(new JButton("CLOSE"), "sizegroup grp1");

		}
		return this.panel;
	}
}
