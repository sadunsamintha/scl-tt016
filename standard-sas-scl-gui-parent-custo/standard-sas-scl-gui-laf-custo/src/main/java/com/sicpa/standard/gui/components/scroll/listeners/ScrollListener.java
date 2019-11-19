package com.sicpa.standard.gui.components.scroll.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

public  abstract class ScrollListener extends MouseAdapter {

		protected JScrollPane scroll;

		public ScrollListener(final JScrollPane scroll) {
			this.scroll = scroll;
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			this.oldy = getPosition();
			this.posPressed = this.oldy;
			this.timePressed = System.currentTimeMillis();
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			this.timeReleased = System.currentTimeMillis();
			this.posReleased = getPosition();

			long deltaDrag = this.timeReleased - this.timeLastDragged;
			if (deltaDrag < 100) {
				// fast movement start the smooth anim
	          
				long deltaTimeMove = this.timeReleased - this.timePressed;
                int deltaDistance = Math.abs(this.posReleased - this.posPressed);
				float speed = (float) deltaDistance / ((float) deltaTimeMove);
				// System.out.println(deltaTimeMove + "ms " + deltaDistance + "px  " + speed + "px/ms");

				float oldspeed = 0;
				if (this.timeline != null && this.timeline.getState() == TimelineState.PLAYING_FORWARD) {
					oldspeed = speed;
					this.timeline.abort();
				}

				this.timeline = new Timeline(this);
				this.timeline.setEase(new Spline(.8f));
				this.timeline.setDuration(this.animDuarion);
				this.timeline.addPropertyToInterpolate("speed", speed + oldspeed, 0f);
				this.timeline.addCallback(new TimelineCallbackAdapter() {
					@Override
					public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
							final float durationFraction, final float timelinePosition) {
						if (newState == TimelineState.PLAYING_FORWARD) {
							ScrollListener.this.timer.start();
							// System.out.println("start timer");
						} else if (newState == TimelineState.DONE) {
							setSpeed(0f);
						}
					}
				});
				this.timeline.play();
			} else {
//				System.out.println("too slow");
			}
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			this.timeLastDragged = System.currentTimeMillis();
			int current = getPosition();
			int offset = this.oldy - current;
			if(offset==0) {
			  return;
			}
			this.oldy = current;
			this.goingUp = offset > 0;
		}

		private int oldy;

		private long timeLastDragged = -1;
		private long timeReleased = -1;
		private long timePressed = -1;
		private int posPressed = -1;
		private int posReleased = -1;
		private int animDuarion = 500;
		private Timeline timeline;

		protected abstract int getPosition();

		private boolean goingUp;

		private float resolution = 20;

		private float speed = -1;

		public void setSpeed(final float speed) {
			this.speed = speed;
		}

		Timer timer = new Timer(5, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				timerActionPerformed();
			}
		});

		private void timerActionPerformed() {
			if (this.speed <= 0) {
				this.timer.stop();
				// System.out.println("stop timer");
				return;
			}

			int move = (int) (this.speed * this.resolution);
			if (!this.goingUp) {
				move = -move;
			}
			scroll(move);
		}

		protected abstract void scroll(int amount);
	}
