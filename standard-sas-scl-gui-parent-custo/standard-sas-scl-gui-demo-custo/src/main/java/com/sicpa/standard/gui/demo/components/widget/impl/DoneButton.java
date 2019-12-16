package com.sicpa.standard.gui.demo.components.widget.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.components.flipapble.ReversableComponent;
import com.sicpa.standard.gui.utils.PaintUtils;

public class DoneButton extends AbstractButton {
	private ReversableComponent reversibleComp;

	public DoneButton(final ReversableComponent reversibleComp) {
		this.reversibleComp = reversibleComp;
		setText("Done");
		setOpaque(false);
		setFont(new Font("Helvetica", Font.BOLD, 11));
		setForeground(new Color(240, 240, 240, 200));
		setPreferredSize(new Dimension(38, 16));
		setModel(new DefaultButtonModel());

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				DoneButton.this.reversibleComp.flip();
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				getModel().setRollover(true);
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				getModel().setRollover(false);
			}
		});
	}

	@Override
	protected void paintComponent(final Graphics g) {

		Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);
		Shape s = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

		if (getModel().isRollover()) {
			g2.setColor(new Color(240, 240, 240, 80));
		} else {
			g2.setColor(new Color(240, 240, 240, 40));
		}
		g2.fill(s);

		g2.setColor(new Color(240, 240, 240, 200));
		g2.setStroke(new BasicStroke(1.5f));
		g2.draw(s);

		g2.setColor(new Color(255, 255, 255, 255));
		int x = (getWidth() / 2) - (SwingUtilities.computeStringWidth(g.getFontMetrics(), getText()) / 2);
		int y = (getHeight() / 2) + (g.getFontMetrics().getHeight() / 2) - 2;
		g2.drawString(getText(), x, y);
	}
}
