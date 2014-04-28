package com.sicpa.standard.sasscl.view.component.question;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TranslucentPanel extends JPanel {

	protected Color color;
	protected float alpha;

	public TranslucentPanel(final Color c, final float alpha) {
		setColor(c);
		this.alpha = alpha;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
		g2.setPaint(this.color);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.dispose();
	}

	public void setColor(Color color) {
		if (color != null) {
			this.color = color;
		}
	}
}