package com.sicpa.standard.gui.components.panels.transition;

import java.awt.ComponentOrientation;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.bric.image.transition.Transition2D;
import com.bric.image.transition.vanilla.PushTransition2D;

public class SlideTransitionUI extends TransitionUI {

	protected Transition2D transitionMoveNext;
	protected Transition2D transitionMovePrevious;

	public SlideTransitionUI() {
		this.transitionMoveNext = new PushTransition2D(Transition2D.LEFT);
		this.transitionMovePrevious = new PushTransition2D(Transition2D.RIGHT);
	}

	@Override
	protected void paintImage(final BufferedImage previous, final BufferedImage next, final Graphics2D g,
			final float animProgress) {
		if (this.movingNext) {
			this.transitionMoveNext.paint(g, previous, next, animProgress);
		} else {
			this.transitionMovePrevious.paint(g, previous, next, animProgress);
		}
	}

	public void setTransitionMoveNext(final Transition2D transitionMoveNext) {
		this.transitionMoveNext = transitionMoveNext;
	}

	public void setTransitionMovePrevious(final Transition2D transitionMovePrevious) {
		this.transitionMovePrevious = transitionMovePrevious;
	}

	public void setComponentOrientation(ComponentOrientation orientation) {
		if (orientation.isLeftToRight()) {
			this.transitionMoveNext = new PushTransition2D(Transition2D.LEFT);
			this.transitionMovePrevious = new PushTransition2D(Transition2D.RIGHT);
		} else {
			this.transitionMoveNext = new PushTransition2D(Transition2D.RIGHT);
			this.transitionMovePrevious = new PushTransition2D(Transition2D.LEFT);
		}
	}
}
