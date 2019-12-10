package com.sicpa.standard.gui.demo.paint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class GradientPaintDemoFrame extends javax.swing.JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GradientPaintDemoFrame inst = new GradientPaintDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public GradientPaintDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		JPanel p = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				int x1 = 0;
				int y1 = 0;
				int x2 = getWidth();
				int y2 = getHeight();

				Color c1 = Color.RED;
				Color c2 = Color.GREEN;

				GradientPaint gp = new GradientPaint(x1, y1, c1, x2, y2, c2);
				g2.setPaint(gp);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};
		add(p, BorderLayout.CENTER);

		setSize(400, 300);
	}
}
