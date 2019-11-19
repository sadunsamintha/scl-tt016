package com.sicpa.standard.gui.components.panels.transition;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class RectanglesTransitionUI extends TransitionUI {

	@Override
	protected void paintImage(final BufferedImage previous, final BufferedImage next, final Graphics2D g, final float animProgress) {
		g.drawImage(next, 0, 0, null);
		g.setClip(getShape(animProgress));
		g.drawImage(previous, 0, 0, null);
	}

	public Shape getShape(final float animProgress) {
		final int stepSize = 50;
		final int numRect = 1 + this.parent.getHeight() / stepSize;

		final Area toHide = new Area();

		final Rectangle[] rects = new Rectangle[numRect];
		for (int n = 0; n < numRect; n++) {
			rects[n] = new Rectangle(0, (int) (stepSize * n - stepSize / 2 * animProgress), this.parent.getWidth(),
					(int) (stepSize * animProgress));
			toHide.add(new Area(rects[n]));
		}

		final Area all = new Area(new Rectangle(0, 0, this.parent.getWidth(), this.parent.getHeight()));
		all.subtract(toHide);

		return all;
	}

}
