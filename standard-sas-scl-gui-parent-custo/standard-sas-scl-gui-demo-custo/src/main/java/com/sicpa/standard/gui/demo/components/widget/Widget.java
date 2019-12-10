package com.sicpa.standard.gui.demo.components.widget;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.graphics.ReflectionRenderer;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.components.flipapble.ReversableComponent;
import com.sicpa.standard.gui.components.transition.impl.PinchTransition;
import com.sicpa.standard.gui.utils.WindowsUtils;

public abstract class Widget extends javax.swing.JDialog {
	protected static final int OFFSET = 15;
	protected Dimension widgetSize;
	private CloseButton buttonClose;
	private PropertiesButton buttonProperties;
	private Timeline timeline;
	private ReversableComponent flip;

	public Widget() {
		super();
		RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
		WindowsUtils.setOpaque(this, false);
		setAlwaysOnTop(true);
	}

	protected void initGUI() {
		getContentPane().setLayout(new MigLayout("fill,inset " + OFFSET + " " + OFFSET + " 0 0"));
		getContentPane().add(getFlip());

		getContentPane().add(this.reflec, "push,top,newline,grow, h 50!");

		getLayeredPane().add(getButtonClose());
		getLayeredPane().add(getButtonProperties());
		setSize(300, 200);

		getBack().addMouseListener(this.mouseListener);
		getBack().addMouseMotionListener(this.mouseListener);

		getFront().addMouseListener(this.mouseListener);
		getFront().addMouseMotionListener(this.mouseListener);
	}

	private JComponent reflec = new JComponent() {
		private ReflectionRenderer rr = new ReflectionRenderer();
		{
			this.rr.setBlurEnabled(true);
		}
		private BufferedImage buffReflect;
		private BufferedImage buffClock;

		@Override
		protected void paintComponent(final Graphics g) {
			if (getFlip().getWidth() > 0 && getFlip().getHeight() > 0) {
				if (this.buffClock == null) {
					this.buffClock = GraphicsUtilities.createCompatibleTranslucentImage(getFlip().getWidth(), getFlip()
							.getHeight());
				}
				if (this.buffReflect == null) {
					this.buffReflect = GraphicsUtilities.createCompatibleTranslucentImage(getFlip().getWidth(),
							getFlip().getHeight());
				}

				Graphics2D gtmp = (Graphics2D) this.buffClock.getGraphics();
				// clear the old image , clear rect not working(give an opaque
				// area)
				gtmp.setComposite(AlphaComposite.SrcOver);
				gtmp.fillRect(0, 0, this.buffClock.getWidth(), this.buffClock.getHeight());
				gtmp.setComposite(AlphaComposite.SrcOut);
				gtmp.fillRect(0, 0, this.buffClock.getWidth(), this.buffClock.getHeight());

				// paint the clock
				gtmp.setComposite(AlphaComposite.SrcOver);
				getFlip().paint(gtmp);
				gtmp.dispose();

				this.buffReflect = this.rr.createReflection(this.buffClock);

				g.drawImage(this.buffReflect, 0, 0, null);
			}
		}
	};

	private void startShowAnim() {
		if (getButtonClose().isVisible()) {
			return;
		}
		if (this.timeline != null && this.timeline.getState() == TimelineState.PLAYING_FORWARD) {
			this.timeline.abort();
		}
		this.timeline = new Timeline(this);
		this.timeline.setDuration(200);
		this.timeline.setEase(new Spline(0.7f));
		this.timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				getButtonClose().setAlpha(timelinePosition);
				getButtonProperties().setAlpha(timelinePosition);
			}

			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					getButtonClose().setAlpha(timelinePosition);
					getButtonProperties().setAlpha(timelinePosition);
				} else if (newState == TimelineState.READY) {
					getButtonClose().setVisible(true);
					getButtonProperties().setVisible(true);
					getButtonClose().setAlpha(0f);
					getButtonProperties().setAlpha(0f);
				}
			}
		});

		this.timeline.play();
	}

	private void startHideAnim() {
		if (!getButtonClose().isVisible()) {
			return;
		}
		if (this.timeline != null && this.timeline.getState() == TimelineState.PLAYING_FORWARD) {
			this.timeline.abort();
		}
		this.timeline = new Timeline(this);
		this.timeline.setDuration(200);
		this.timeline.setEase(new Spline(0.7f));

		this.timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelinePulse(final float durationFraction, final float timelinePosition) {
				getButtonClose().setAlpha(1 - timelinePosition);
				getButtonProperties().setAlpha(1 - timelinePosition);
			}

			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					getButtonClose().setAlpha(0);
					getButtonProperties().setAlpha(0);
					getButtonClose().setVisible(false);
					getButtonProperties().setVisible(false);
				}
			};
		});
		this.timeline.play();
	}

	public ReversableComponent getFlip() {
		if (this.flip == null) {
			this.flip = new ReversableComponent();
			this.flip.setSize(this.widgetSize);
			this.flip.setFront(getFront());
			this.flip.setBack(getBack());
			this.flip.setOpaque(false);
			this.flip.setAnimDuration(1000);
		}
		return this.flip;
	}

	protected abstract JComponent getFront();

	protected abstract JComponent getBack();

	protected MouseAdapter mouseListener = new MouseAdapter() {
		private Point location;

		@Override
		public void mousePressed(final MouseEvent me) {
			this.location = me.getPoint();
		}

		@Override
		public void mouseDragged(final MouseEvent me) {
			Point p = me.getPoint();
			SwingUtilities.convertPointToScreen(p, ((JComponent) me.getSource()));
			p.translate(-this.location.x, -this.location.y);
			p.translate(-Widget.this.flip.getX(), -Widget.this.flip.getY());
			setLocation(p);
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (Widget.this.flip.isFrontShowing()) {
				startShowAnim();
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {

			Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getFront());
			boolean flag = getFront().contains(p);
			if (!flag) {
				startHideAnim();
			}
		}
	};

	public CloseButton getButtonClose() {
		if (this.buttonClose == null) {
			this.buttonClose = new CloseButton();
			this.buttonClose.setVisible(false);
			this.buttonClose.setBounds(5, 5, 25, 25);
			this.buttonClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					new PinchTransition(Widget.this).startCloseTransition();
				}
			});
		}
		return this.buttonClose;
	}

	public PropertiesButton getButtonProperties() {
		if (this.buttonProperties == null) {
			this.buttonProperties = new PropertiesButton();
			this.buttonProperties.setVisible(false);
			this.buttonProperties.setBounds(190, 60, 15, 15);
			this.buttonProperties.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					Widget.this.flip.flip();
					startHideAnim();
				}
			});
		}
		return this.buttonProperties;
	}
}
