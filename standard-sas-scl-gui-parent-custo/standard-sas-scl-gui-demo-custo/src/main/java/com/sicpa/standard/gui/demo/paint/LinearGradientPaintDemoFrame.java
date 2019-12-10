package com.sicpa.standard.gui.demo.paint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class LinearGradientPaintDemoFrame extends javax.swing.JFrame {

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LinearGradientPaintDemoFrame inst = new LinearGradientPaintDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public LinearGradientPaintDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		JPanel p = new JPanel() {
			@Override
			protected void paintComponent(final Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();

				Point p1 = new Point(0, 0);
				Point p2 = new Point(getWidth(), getHeight());
				float[] fraction = { 0f, .5f, 1f };
				Color[] colors = { Color.RED, Color.BLUE, Color.GREEN };

				LinearGradientPaint lgp = new LinearGradientPaint(p1, p2, fraction, colors);
				g2.setPaint(lgp);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};
		add(p, BorderLayout.CENTER);
		setSize(400, 300);
	}

}
