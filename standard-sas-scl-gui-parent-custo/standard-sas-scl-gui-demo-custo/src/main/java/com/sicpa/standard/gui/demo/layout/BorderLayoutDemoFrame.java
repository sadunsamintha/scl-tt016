package com.sicpa.standard.gui.demo.layout;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class BorderLayoutDemoFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = -2895667505700626754L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BorderLayoutDemoFrame inst = new BorderLayoutDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public BorderLayoutDemoFrame() {
		super("BorderLayout demo");
		initGUI();
	}

	private void initGUI() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JButton("SOUTH"), BorderLayout.SOUTH);
		panel.add(new JButton("NORTH"), BorderLayout.NORTH);
		panel.add(new JButton("EAST"), BorderLayout.EAST);
		panel.add(new JButton("WEST"), BorderLayout.WEST);
		panel.add(new JButton("CENTER"), BorderLayout.CENTER);

		getContentPane().add(panel);
		setSize(400, 300);
	}
}
