package com.sicpa.standard.gui.components.scroll;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.sicpa.standard.gui.components.scroll.listeners.HLinearScrollMouseListner;
import com.sicpa.standard.gui.components.scroll.listeners.HScrollListener;
import com.sicpa.standard.gui.components.scroll.listeners.LinearScrollMouseListener;
import com.sicpa.standard.gui.components.scroll.listeners.ScrollListener;
import com.sicpa.standard.gui.components.scroll.listeners.VLinearScrollMouseListner;
import com.sicpa.standard.gui.components.scroll.listeners.VScrollListener;

public abstract class SmoothScrolling {

	/**
	 * enable easy scrolling for touchscreen<br>
	 * the view of the scrollpane should not changed after this is called else the behavior is lost
	 * 
	 * @param scroll
	 */

	public static void enableFullScrolling(final JScrollPane scroll) {
		addHorizontalSmoothScrolling(scroll);
		addVerticalSmoothScrolling(scroll);
		addHorizontalLinearScroll(scroll);
		addVerticalLinearScroll(scroll);
	}

	// public static void enableSmoothScrolling(final JScrollPane scroll) {
	// addHorizontalSmoothScrolling(scroll);
	// addVerticalSmoothScrolling(scroll);
	// }

	public static void addVerticalSmoothScrolling(final JScrollPane scroll) {
		addScrollListener(scroll, new VScrollListener(scroll));
	}

	public static void addHorizontalSmoothScrolling(final JScrollPane scroll) {
		addScrollListener(scroll, new HScrollListener(scroll));
	}

	public static void addVerticalLinearScroll(final JScrollPane scroll) {
		addLinearScrolling(scroll, new VLinearScrollMouseListner(scroll));
	}

	public static void addHorizontalLinearScroll(final JScrollPane scroll) {
		addLinearScrolling(scroll, new HLinearScrollMouseListner(scroll));
	}

	private static void addLinearScrolling(final JScrollPane scroll, final LinearScrollMouseListener listener) {
		Component view = scroll.getViewport().getView();
		view.addMouseListener(listener);
		view.addMouseMotionListener(listener);

		if (scroll.getViewport().getView() instanceof JComponent) {
			for (Component comp : ((JComponent) scroll.getViewport().getView()).getComponents()) {
				if (comp instanceof JComponent) {
					addLinearScrollListener((JComponent) comp, listener);
				}
			}
		}
	}

	private static void addLinearScrollListener(final JComponent comp, final LinearScrollMouseListener listener) {
		comp.addMouseListener(listener);
		comp.addMouseMotionListener(listener);
		for (Component child : comp.getComponents()) {
			if (child instanceof JComponent) {
				addLinearScrollListener((JComponent) child, listener);
			}
		}
	}

	


	private static void addScrollListener(final JScrollPane scroll, final ScrollListener listener) {
		scroll.getViewport().getView().addMouseListener(listener);
		scroll.getViewport().getView().addMouseMotionListener(listener);
		if (scroll.getViewport().getView() instanceof JComponent) {
			for (Component comp : ((JComponent) scroll.getViewport().getView()).getComponents()) {
				if (comp instanceof JComponent) {
					addScrollListener((JComponent) comp, listener);
				}
			}
		}
	}

	private static void addScrollListener(final JComponent comp, final ScrollListener listener) {
		comp.addMouseListener(listener);
		comp.addMouseMotionListener(listener);
		for (Component child : comp.getComponents()) {
			if (child instanceof JComponent) {
				addScrollListener((JComponent) child, listener);
			}
		}
	}

	

	

	
}
