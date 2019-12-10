package com.sicpa.standard.gui.demo.layout.miglayout;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class MigLayoutDemoFrame2 extends JFrame {

	private JPanel panel;

	public MigLayoutDemoFrame2() {
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
				MigLayoutDemoFrame2 test = new MigLayoutDemoFrame2();
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				test.setSize(400, 300);
				test.setVisible(true);

			}
		});
	}

	public JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();

			this.panel.setLayout(new MigLayout("fill", "", ""));
			this.panel.add(new JTextArea(), "grow,pushy,spanx");
			this.panel.add(new JButton("OK"), "spanx, split 2, center, h 50, sizegroup grp1");
			this.panel.add(new JButton("CANCEL"), "sizegroup grp1");

		}
		return this.panel;
	}
}
