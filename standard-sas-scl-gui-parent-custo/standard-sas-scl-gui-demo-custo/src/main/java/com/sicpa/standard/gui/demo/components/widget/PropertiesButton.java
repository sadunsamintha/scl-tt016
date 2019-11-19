package com.sicpa.standard.gui.demo.components.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.utils.PaintUtils;

public class PropertiesButton extends JButton {

	float alpha = 1f;

	public PropertiesButton() {
		super();
		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 13));
	}

	protected Shape getShape() {
		return new Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);

		Shape disc = getShape();

		// background
		if (getModel().isRollover()) {
			g2.setColor(new Color(1f, 1f, 1f, .2f));
			g2.fill(disc);
		}

		// symbol
		g2.setFont(getFont());
		g2.setColor(new Color(1f, 1f, 1f, this.alpha));
		int iw = SwingUtilities.computeStringWidth(g2.getFontMetrics(), "i");
		g2.drawString("i", (getWidth() / 2) - (iw / 2), (getHeight() / 2) + g2.getFontMetrics().getHeight() / 2 - 4);
		g2.dispose();
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
		repaint();
	}
}
