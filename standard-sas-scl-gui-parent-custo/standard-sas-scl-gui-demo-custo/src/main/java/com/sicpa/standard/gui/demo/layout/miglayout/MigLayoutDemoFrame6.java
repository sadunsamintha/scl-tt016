package com.sicpa.standard.gui.demo.layout.miglayout;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class MigLayoutDemoFrame6 extends JFrame {

	private JPanel panel;

	public MigLayoutDemoFrame6() {
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
				MigLayoutDemoFrame6 test = new MigLayoutDemoFrame6();
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				test.setSize(450, 450);
				test.setVisible(true);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	public JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();

			this.panel.setLayout(new MigLayout("fill"));

			this.panel.add(new JLabel("CURRENT SITE"), "spanx");

			this.panel.add(new JTextField(), "spanx, growx");

			this.panel.add(new JLabel("CURRENT DATE"));
			this.panel.add(new JLabel("PALLET DATE"), "wrap");

			this.panel.add(new JTextField(), "growx");
			this.panel.add(new JTextField(), "growx,wrap");

			this.panel.add(new JLabel("CODE TYPE"), "growx,span, split 2");
			this.panel.add(new JLabel("UNIT QTY"), "w 33%!,sizegroup sg1");

			this.panel.add(new JTextField(), "growx,span, split 2");
			this.panel.add(new JTextField(), "growx, sizegroup sg1");

			this.panel.add(new JLabel("UNIT TYPE"), "spanx");
			this.panel.add(new JTextField(), "span, wrap, growx");

			this.panel.add(new JLabel("PALLET ID"), "span, wrap");
			this.panel.add(new JTextField(), "spanx, growx");

			this.panel.add(new JButton("DISASSOCIATE"), "span, split 2,center, sizegroup sg2");
			this.panel.add(new JButton("CLOSE"), "sizegroup sg2");

		}
		return this.panel;
	}
}
