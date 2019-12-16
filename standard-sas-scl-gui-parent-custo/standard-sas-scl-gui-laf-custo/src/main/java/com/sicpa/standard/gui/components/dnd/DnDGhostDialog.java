package com.sicpa.standard.gui.components.dnd;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.utils.WindowsUtils;

public class DnDGhostDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private BufferedImage image;

	private static int failureFeedbackDuration = 100;
	private static int successFeedbackDuration = 250;
	private boolean playingFailureFeedback = false;

	private float animProgress;

	private float alpha = 0.6f;

	public DnDGhostDialog() {
		setAlwaysOnTop(true);
		WindowsUtils.setOpaque(this, false);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(this.panel);
		this.panel.setOpaque(false);
		// RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
	}

	public void setImage(final BufferedImage dragged) {
		this.image = dragged;
		if (dragged != null) {
			setSize(dragged.getWidth(), dragged.getHeight());
		}
		repaint();
	}

	public void setPoint(final Point location) {
		this.animProgress = 0;
		if (this.image != null) {
			setLocation(new Point(location.x - this.image.getWidth() / 2, location.y - this.image.getHeight() / 2));
		}
	}

	private JPanel panel = new JPanel() {
		@Override
		protected void paintComponent(final Graphics g) {
			// super.paintComponent(g);

			if (DnDGhostDialog.this.image == null && DnDGhostDialog.this.image == null) {
				return;
			}

			Graphics2D g2 = (Graphics2D) g;
			// success feedback
			if (getAnimProgress() > 0 && DnDGhostDialog.this.image != null) {
				g2.setComposite(AlphaComposite.SrcOver.derive(getAlpha() - getAnimProgress() * getAlpha()));

				// image fade out
				int w = (int) (DnDGhostDialog.this.image.getWidth() * (1f - getAnimProgress() * getAlpha()));
				int h = (int) (DnDGhostDialog.this.image.getHeight() * (1f - getAnimProgress() * getAlpha()));
				g2.drawImage(DnDGhostDialog.this.image, 0, 0, w, h, null);

			} else {
				if (DnDGhostDialog.this.image != null) {

					// do not paint the pixel where the cursor is
					// this allow the drop to work
					// else the drop target do not get the drop event
					Shape oldClip = g2.getClip();
					if (!DnDGhostDialog.this.playingFailureFeedback) {
						Area a1 = new Area(getBounds());
						// remove the center point (where the cursor is)
						a1.subtract(new Area(new Rectangle(getWidth() / 2, getHeight() / 2, 1, 1)));
						g2.setClip(a1);
					}
					g2.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));
					g2.drawImage(DnDGhostDialog.this.image, 0, 0, null);
					g2.setClip(oldClip);
				}
			}
		}

		@Override
		public boolean contains(final int x, final int y) {
			return false;
		}

		@Override
		public boolean contains(final Point p) {
			return false;
		}
	};

	public void showSuccessFeedback() {
		Timeline timeline = new Timeline(this);
		timeline.setDuration(getSuccessFeedbackDuration());
		timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					setImage(null);
					setAnimProgress(0);
					setVisible(false);
					DnDLock.release();
				}
			}
		});
		timeline.play();
	}

	public void showFailureFeedback(final Point startDragLocation, final Point dropLocation) {
		Timeline timeline = new Timeline(this);
		timeline.setEase(new Spline(0.7f));

		startDragLocation.x += this.image.getWidth() / 2;
		startDragLocation.y += this.image.getHeight() / 2;

		double delta = Math.sqrt(Math.pow(dropLocation.x - startDragLocation.x, 2)
				+ Math.pow(dropLocation.y - startDragLocation.y, 2));
		int duration = (int) ((delta / 100) * getFailureFeedbackDuration());
		timeline.setDuration(duration);
		timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					setImage(null);
					setVisible(false);
					DnDGhostDialog.this.playingFailureFeedback = false;
					DnDLock.release();
				}
			}
		});

		timeline.addPropertyToInterpolate("point", dropLocation, startDragLocation);
		setLocationGoingBack(dropLocation);
		this.playingFailureFeedback = true;
		timeline.play();
	}

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		repaint();
	}

	public void setLocationGoingBack(final Point locationGoingBack) {
		setLocation(locationGoingBack);
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public static int getSuccessFeedbackDuration() {
		return successFeedbackDuration;
	}

	public static int getFailureFeedbackDuration() {
		return failureFeedbackDuration;
	}

	public static void setSuccessFeedbackDuration(final int successFeedbackDuration) {
		DnDGhostDialog.successFeedbackDuration = successFeedbackDuration;
	}

	public static void setFailureFeedbackDuration(final int failureFeedbackDuration) {
		DnDGhostDialog.failureFeedbackDuration = failureFeedbackDuration;
	}

	public float getAlpha() {
		return this.alpha;
	}

	public float getAnimProgress() {
		return this.animProgress;
	}
}
