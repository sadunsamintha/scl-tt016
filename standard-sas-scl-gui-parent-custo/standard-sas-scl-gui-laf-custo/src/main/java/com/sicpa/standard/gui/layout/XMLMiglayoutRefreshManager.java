package com.sicpa.standard.gui.layout;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.sicpa.standard.gui.utils.ThreadUtils;

public class XMLMiglayoutRefreshManager {

	private static ArrayList<XMLMiglayout> layouts = new ArrayList<XMLMiglayout>();

	private static boolean first = true;
	private static boolean autoRefresh = false;
	private static int autoRefreshTime = 5;

	private static boolean threadRuning = false;

	public static void manage(final XMLMiglayout layout) {

		layouts.add(layout);

		if (first) {
			if (autoRefresh) {
				new Thread(createChecker()).start();
			}
			setWithF5KeyRefreshListener(true);
			first = false;
		}
	}

	private static AWTEventListener keyRefreshListener = new AWTEventListener() {
		@Override
		public void eventDispatched(final AWTEvent event) {
			KeyEvent kevt = ((KeyEvent) event);
			if (kevt.getKeyCode() == KeyEvent.VK_F5 && kevt.getID() == KeyEvent.KEY_RELEASED) {
				refreshLayouts();
			}
		}
	};

	public static void setWithF5KeyRefreshListener(final boolean flag) {
		Toolkit.getDefaultToolkit().removeAWTEventListener(keyRefreshListener);
		if (flag) {
			Toolkit.getDefaultToolkit().addAWTEventListener(keyRefreshListener, AWTEvent.KEY_EVENT_MASK);
		}
	}

	public static void refreshLayouts() {
		for (XMLMiglayout layout : layouts) {
			try {

				layout.maybeUpdateLayout();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void remove(final XMLMiglayout layout) {
		layouts.remove(layout);
	}

	public static boolean isManaged(final XMLMiglayout layout) {
		return layouts.contains(layout);
	}

	private static Runnable createChecker() {
		return new Runnable() {
			public void run() {
				threadRuning = true;
				while (autoRefresh) {
					refreshLayouts();
					ThreadUtils.sleepQuietly(autoRefreshTime * 1000);
				}
				threadRuning = false;
			}
		};
	}

	public static void setAutoRefresh(final boolean autoRefresh) {
		XMLMiglayoutRefreshManager.autoRefresh = autoRefresh;
		if (!threadRuning) {
			new Thread(createChecker()).start();
		}
	}

	public static void setAutoRefreshTime(final int autoRefreshTime) {
		if (autoRefreshTime <= 0) {
			throw new IllegalArgumentException("auto refresh time must be >0");
		}
		XMLMiglayoutRefreshManager.autoRefreshTime = autoRefreshTime;
	}
}
