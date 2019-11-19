package com.sicpa.standard.gui.utils;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ScrollUtils {
	public static enum ScrollDirection {
		UP, DOWN, LEFT, RIGHT
	}

	public static void scroll(final ScrollDirection direction, final int offset, final JScrollPane scroll) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				int d = 1;
				switch (direction) {
				case DOWN:
					d = -1;
					break;
				case UP:
					d = 1;
					break;
				case LEFT:
					d = -1;
					break;
				case RIGHT:
					d = 1;
					break;
				}
				int oldValue;
				int newValue;

				switch (direction) {
				case DOWN:
				case UP:
					oldValue = scroll.getVerticalScrollBar().getValue();
					newValue = oldValue + d * offset;
					scroll.getVerticalScrollBar().setValue(newValue);
					break;

				case LEFT:
				case RIGHT:
					oldValue = scroll.getHorizontalScrollBar().getValue();
					newValue = oldValue + d * offset;
					scroll.getHorizontalScrollBar().setValue(newValue);
					break;
				}

			}
		});
	}

	public static boolean isScrollToMax(final JScrollBar bar) {
		return (bar.getVisibleAmount() + bar.getValue() == bar.getMaximum());
	}
}
