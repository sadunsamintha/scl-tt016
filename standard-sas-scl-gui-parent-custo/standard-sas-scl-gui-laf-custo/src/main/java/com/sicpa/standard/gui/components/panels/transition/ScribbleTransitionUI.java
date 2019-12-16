package com.sicpa.standard.gui.components.panels.transition;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.bric.image.transition.Transition2D;
import com.bric.image.transition.spunk.ScribbleTransition2D;

public class ScribbleTransitionUI extends TransitionUI {

	Transition2D transition;

	public ScribbleTransitionUI() {
		this.transition = new ScribbleTransition2D(false);
	}

	@Override
	protected void paintImage(final BufferedImage previous, final BufferedImage next, final Graphics2D g,
			final float animProgress) {
		this.transition.paint(g, previous, next, animProgress);
	}
}
