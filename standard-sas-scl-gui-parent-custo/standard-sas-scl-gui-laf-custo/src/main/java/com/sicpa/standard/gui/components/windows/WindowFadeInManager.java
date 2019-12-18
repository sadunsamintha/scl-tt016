package com.sicpa.standard.gui.components.windows;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class WindowFadeInManager {
	private static int FADE_IN_DURATION = 500;

	public static void fadeIn(final Window window, final int duration) {
		TimelineCallback tt = new TimelineCallback() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						WindowsUtils.setOpacity(window, 1.0f);
					}
				});
			}

			public void onTimelineStateChanged(final Timeline.TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (newState == TimelineState.DONE) {
							WindowsUtils.setOpacity(window, 1.0f);
						}
					}
				});
			}
		};
		WindowsUtils.setOpacity(window, 1.0f);
		Timeline anim = new Timeline();
		anim.setDuration(duration);
		anim.addCallback(tt);

		anim.play();
		window.setVisible(true);
	}

	public static void fadeIn(final Window window) {
		fadeIn(window, FADE_IN_DURATION);
	}

	public static void fadeOut(final Window window) {
		fadeOut(window, FADE_IN_DURATION);
	}

	public static void fadeOut(final Window window, final int duration) {
		TimelineCallback tt = new TimelineCallback() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						WindowsUtils.setOpacity(window, 1.0f);
					}
				});
			}

			public void onTimelineStateChanged(final Timeline.TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (newState == TimelineState.DONE) {
							WindowsUtils.setOpacity(window, 1.0f);
							window.setVisible(false);
							window.dispose();
						}
					}
				});
			}
		};
		Timeline anim = new Timeline();
		anim.setDuration(duration);
		anim.addCallback(tt);
		anim.play();
	}

	public static int fadeIn(final JOptionPane optionPane, final String title, final Component parent) {
		return fadeIn(optionPane, title, parent, FADE_IN_DURATION);
	}

	public static int fadeIn(final JOptionPane optionPane, final String title, final Component parent,
			final int duration) {
		JDialog dialog = optionPane.createDialog(parent, title);
		fadeIn(dialog, duration);
		return (Integer) optionPane.getValue();
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
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								System.out.println("start");
								JOptionPane optionpane = new JOptionPane("My question message",
										JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
								int response = WindowFadeInManager.fadeIn(optionpane, "my title", f);
								System.out.println(response);
							}
						});
					}
				}).start();
			}
		});
	}
}
