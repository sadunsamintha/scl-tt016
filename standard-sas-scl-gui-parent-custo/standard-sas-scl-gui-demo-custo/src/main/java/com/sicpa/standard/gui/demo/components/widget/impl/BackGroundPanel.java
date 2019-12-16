package com.sicpa.standard.gui.demo.components.widget.impl;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.sicpa.standard.gui.utils.PaintUtils;

public class BackGroundPanel extends JPanel {
	private static final long serialVersionUID = 1153377478988624160L;

	public BackGroundPanel() {
		setOpaque(false);
		setLayout(new BorderLayout());

	}

	@Override
	protected void paintComponent(final Graphics g) {
		int arc = 25;
		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);

		g2.setColor(Color.BLACK);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

		GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 140), 0, (int) (getHeight() * 0.5),
				new Color(255, 255, 255, 0));
		g2.setPaint(gp);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

		g2.setColor(Color.LIGHT_GRAY);
		g2.setStroke(new BasicStroke(3f));
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
	}
}
