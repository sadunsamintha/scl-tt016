package com.sicpa.standard.gui.screen.machine.component.config;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.components.buttons.shape.CheckButton;
import com.sicpa.standard.gui.utils.PaintUtils;

public class BlinkingCheckButton extends CheckButton {

	private static final long serialVersionUID = 1L;
	private Timeline timeline;
	private float animProgress;

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (this.timeline != null
				&& (this.timeline.getState() == TimelineState.PLAYING_FORWARD || this.timeline.getState() == TimelineState.PLAYING_REVERSE)) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setComposite(AlphaComposite.SrcOver.derive(this.animProgress));
			PaintUtils.turnOnAntialias(g2);
			g2.setColor(Color.RED);
			g2.fill(this.shape);
			g2.dispose();
		}
	}

	public void startBlinking() {
		if (this.timeline == null
				|| (this.timeline.getState() != TimelineState.PLAYING_FORWARD && this.timeline.getState() != TimelineState.PLAYING_REVERSE)) {
			this.timeline = new Timeline(this);
			this.timeline.setDuration(1000);
			this.timeline.setEase(new Spline(0.7f));
			this.timeline.playLoop(RepeatBehavior.REVERSE);
		}
	}

	public void stopBlinking() {
		if (this.timeline != null) {
			this.timeline.abort();
		}
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	@Override
	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		repaint();
	}
}
