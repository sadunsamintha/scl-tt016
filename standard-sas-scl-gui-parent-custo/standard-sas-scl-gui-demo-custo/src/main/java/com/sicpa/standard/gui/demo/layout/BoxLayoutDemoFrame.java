package com.sicpa.standard.gui.demo.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class BoxLayoutDemoFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = -4106001466150308680L;
	private JPanel panelX;
	private JPanel panelY;
	private JPanel panelGlue;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BoxLayoutDemoFrame inst = new BoxLayoutDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public BoxLayoutDemoFrame() {
		super("BoxLayout demo");

		setLayout(new BorderLayout(20,20));
		getContentPane().add(getXScrollPane(), BorderLayout.SOUTH);
		getContentPane().add(getYScrollPane(), BorderLayout.EAST);
		getContentPane().add(getPanelGlue(), BorderLayout.NORTH);

		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			setSize(800, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JPanel getXScrollPane() {
		if (this.panelX == null) {
			this.panelX = new JPanel();
			this.panelX.setLayout(new BoxLayout(this.panelX, BoxLayout.LINE_AXIS));
			for (int i = 0; i < 15; i++) {
				JButton b = new JButton(String.valueOf(i));
				this.panelX.add(b);
			}
			this.panelX.setPreferredSize(new Dimension(0, 100));
		}
		return this.panelX;
	}

	public JPanel getYScrollPane() {
		if (this.panelY == null) {
			this.panelY = new JPanel();
			BoxLayout layout = new BoxLayout(this.panelY, BoxLayout.PAGE_AXIS);

			this.panelY.setLayout(layout);
			for (int i = 0; i < 10; i++) {
				JButton b = new JButton(String.valueOf(i));
				b.setAlignmentX(Component.RIGHT_ALIGNMENT);
				this.panelY.add(b);
				this.panelY.add(Box.createVerticalStrut(5));
			}
			this.panelY.setPreferredSize(new Dimension(150, 0));
		}
		return this.panelY;
	}

	public JPanel getPanelGlue() {
		if (this.panelGlue == null) {
			this.panelGlue = new JPanel();
			BoxLayout layout = new BoxLayout(this.panelGlue, BoxLayout.LINE_AXIS);
			this.panelGlue.setLayout(layout);

			this.panelGlue.add(Box.createGlue());
			this.panelGlue.add(new JButton("Yes"));
			this.panelGlue.add(Box.createHorizontalStrut(10));
			this.panelGlue.add(new JButton("No"));
			this.panelGlue.add(Box.createGlue());
			this.panelGlue.add(new JButton("Cancel"));
		}
		return this.panelGlue;
	}
}
