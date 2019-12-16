package com.sicpa.standard.gui.components.panels.transition;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class FadeTransitionUI extends TransitionUI {

	@Override
	protected void paintImage(final BufferedImage previous, final BufferedImage next, final Graphics2D g,
			final float animProgress) {
		g.setComposite(AlphaComposite.SrcOver.derive(animProgress));
		g.drawImage(next, 0, 0, null);

		g.setComposite(AlphaComposite.SrcOver.derive(1f - animProgress));
		g.drawImage(previous, 0, 0, null);
	}
}
