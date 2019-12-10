package com.sicpa.standard.gui.components.panels.transition;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

public abstract class TransitionUI extends AbstractLayerUI<JComponent> {

	private BufferedImage nextImage;
	private BufferedImage previousImage;

	private float animProgress = 0;
	private int animDuration = 250;

	protected JXLayer<? extends JComponent> parent;
	protected boolean movingNext;

	public TransitionUI() {

	}

	@Override
	protected void paintLayer(final Graphics2D g, final JXLayer<? extends JComponent> parent) {
		super.paintLayer(g, parent);
		this.parent = parent;

		if (this.nextImage == null || this.previousImage == null) {
			return;
		}

		float anim = getAnimProgress();
		if (anim == 1) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g.create();
		paintImage(this.previousImage, this.nextImage, g2, anim);
		g2.dispose();
	}

	protected abstract void paintImage(final BufferedImage previous, final BufferedImage next, final Graphics2D g,
			final float animProgress);

	public void startAnim(final boolean movingNext) {
		if (this.parent == null && this.nextImage != null) {
			return;
		}
		this.movingNext = movingNext;

		Timeline timeline = new Timeline(this);
		timeline.setEase(new Spline(0.8f));
		timeline.setDuration(getAnimDuration());
		timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		timeline.play();
	}

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		if (this.parent != null) {
			this.parent.repaint();
		}
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	public int getAnimDuration() {
		Object o = System.getProperty("transitionUI.animDuration");
		if (o != null) {
			return (Integer.parseInt(o + ""));
		}
		o = UIManager.get("transitionUI.animDuration");
		if (o != null) {
			if (o instanceof Integer) {
				return ((Integer) o);
			} else if (o instanceof String) {
				return (Integer.parseInt(o + ""));
			}
		}
		return this.animDuration;
	}

	public void setAnimDuration(final int animDuration) {
		this.animDuration = animDuration;
	}

	public void setNextImage(final BufferedImage nextImage) {
		this.nextImage = nextImage;
	}

	public void setPreviousImage(final BufferedImage previousImage) {
		this.previousImage = previousImage;
	}
}
