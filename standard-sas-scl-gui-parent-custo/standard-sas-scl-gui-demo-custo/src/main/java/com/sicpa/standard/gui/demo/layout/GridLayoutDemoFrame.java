package com.sicpa.standard.gui.demo.layout;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class GridLayoutDemoFrame extends javax.swing.JFrame {

	private JPanel panel1;
	private JPanel panel2;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GridLayoutDemoFrame inst = new GridLayoutDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public GridLayoutDemoFrame() {
		super("GridLayout demo");
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getContentPane().setLayout(new FlowLayout());
			getContentPane().add(getPanel1());
			getContentPane().add(getPanel2());
			setSize(800, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JPanel getPanel1() {
		if (this.panel1 == null) {
			this.panel1 = new JPanel();
			GridLayout layout = new GridLayout(5, 0, 10, 10);
			this.panel1.setLayout(layout);
			for (int i = 0; i < 15; i++) {
				this.panel1.add(new JButton(String.valueOf(i)));
			}
			this.panel1.setPreferredSize(new Dimension(350, 300));
		}
		return this.panel1;
	}

	public JPanel getPanel2() {
		if (this.panel2 == null) {
			this.panel2 = new JPanel();
			GridLayout layout = new GridLayout(0, 2, 0, 10);
			this.panel2.setLayout(layout);
			for (int i = 0; i < 15; i++) {
				this.panel2.add(new JButton(String.valueOf(i)));
			}
			this.panel2.setPreferredSize(new Dimension(350, 300));
		}
		return this.panel2;
	}
}
