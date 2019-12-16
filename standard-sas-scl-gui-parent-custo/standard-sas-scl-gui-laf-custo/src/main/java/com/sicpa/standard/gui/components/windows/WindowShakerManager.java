package com.sicpa.standard.gui.components.windows;

import java.awt.Point;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class WindowShakerManager {
	private static final int SHAKE_DISTANCE = 20;
	private static final int SHAKE_CYCLE = 25;
	private static final int SHAKE_DURATION = 1000;

	private static class WindowShaker {
		private Window dialog;
		private Point naturalLocation;

		public WindowShaker(final Window dialog) {
			this.dialog = dialog;
		}

		public void startShake() {
			this.naturalLocation = this.dialog.getLocation();

			TimelineCallback tt = new TimelineCallback() {
				@Override
				public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
					int shakenX = (int) (timelinePosition * SHAKE_DISTANCE) + WindowShaker.this.naturalLocation.x;
					int shakenY = (int) (timelinePosition * SHAKE_DISTANCE) + WindowShaker.this.naturalLocation.y;
					WindowShaker.this.dialog.setLocation(shakenX, shakenY);
					WindowShaker.this.dialog.repaint();
				}

				@Override
				public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
						final float durationFraction, final float timelinePosition) {
					if (newState == TimelineState.DONE) {
						WindowShaker.this.dialog.setLocation(WindowShaker.this.naturalLocation);
					}
				}
			};

			Timeline anim = new Timeline();
			anim.setDuration(SHAKE_DURATION / SHAKE_CYCLE);
			anim.addCallback(tt);
			anim.setEase(new Spline(0.5f));
			anim.playLoop(SHAKE_CYCLE, RepeatBehavior.REVERSE);
		}
	}

	public static void shake(final Window window) {
		new WindowShaker(window).startShake();
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				final JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(1024, 768);
				f.setVisible(true);

				new Thread(new Runnable() {
					@Override
					public void run() {
						ThreadUtils.sleepQuietly(1000);
						WindowShakerManager.shake(f);
						ThreadUtils.sleepQuietly(200);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showConfirmDialog(f, "....................");
							}
						});
					}
				}).start();
			}
		});
	}
}
