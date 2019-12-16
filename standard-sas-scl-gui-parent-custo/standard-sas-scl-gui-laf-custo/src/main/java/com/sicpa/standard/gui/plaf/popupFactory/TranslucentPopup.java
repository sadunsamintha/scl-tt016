package com.sicpa.standard.gui.plaf.popupFactory;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import org.pushingpixels.lafwidget.utils.ShadowPopupBorder;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class TranslucentPopup extends Popup {
	private JWindow popupWindow;

	// NOT working with jxdatepicker, as soon as the JXMonthView get a clicked
	// it dimiss the popup, no idea why
	// =>can t select another month

	public TranslucentPopup(final Component contents, final int ownerX, final int ownerY) {
		this.popupWindow = new JWindow();
		// determine the popup location
		this.popupWindow.setLocation(ownerX, ownerY);
		// add the contents to the popup
		this.popupWindow.getContentPane().add(contents, BorderLayout.CENTER);
		contents.invalidate();
		JComponent parent = (JComponent) contents.getParent();
		// set the shadow border
		parent.setBorder(new ShadowPopupBorder());
	}

	private Timeline timeline;
	private float animProgress;

	@Override
	public void show() {
		if (this.timeline != null) {
			this.timeline.cancel();
		}

		// mark the window as non-opaque, so that the
		// shadow border pixels take on the per-pixel
		// translucency
		WindowsUtils.setOpacity(this.popupWindow, 0f);
		this.timeline = new Timeline(this);
		this.timeline.setDuration(500);
		this.timeline.addPropertyToInterpolate("animProgress", 0f, 0.95f);

		this.popupWindow.pack();

		WindowsUtils.setOpaque(this.popupWindow, false);
		this.popupWindow.setVisible(true);
		this.timeline.play();
	}

	@Override
	public void hide() {
		// need the sleep else it doesn t select the value ...
		ThreadUtils.sleepQuietly(100);

		if (this.timeline != null) {
			this.timeline.cancel();
		}

		this.timeline = new Timeline(this);
		this.timeline.addPropertyToInterpolate("animProgress", getAnimProgress(), 0f);
		this.timeline.addCallback(new TimelineCallbackAdapter() {
			@Override
			public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
					final float durationFraction, final float timelinePosition) {
				if (newState == TimelineState.DONE) {
					TranslucentPopup.this.popupWindow.setVisible(false);
					TranslucentPopup.this.popupWindow.removeAll();
				}

			}
		});
		this.timeline.play();
	}

	public static void install() {
		PopupFactory.setSharedInstance(new TranslucentPopupFactory());
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		WindowsUtils.setOpacity(this.popupWindow, animProgress);
		this.popupWindow.repaint();
	}
}
