package com.sicpa.standard.gui.demo.layout;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class FlowLayoutDemoFrame extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FlowLayoutDemoFrame inst = new FlowLayoutDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public FlowLayoutDemoFrame() {
		super("FlowLayout demo");
		initGUI();
	}

	private void initGUI() {
		new JRootPane();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER,10,20));
		for (int i = 0; i < 12; i++) {
			panel.add(new JButton("button:" + i));
		}

		getContentPane().add(panel);
		setSize(280, 300);
	}
}
