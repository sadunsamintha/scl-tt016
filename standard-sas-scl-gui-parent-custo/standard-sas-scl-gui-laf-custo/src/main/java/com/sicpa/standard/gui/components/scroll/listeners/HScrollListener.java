package com.sicpa.standard.gui.components.scroll.listeners;

import java.awt.MouseInfo;

import javax.swing.JScrollPane;

import com.sicpa.standard.gui.utils.ScrollUtils;
import com.sicpa.standard.gui.utils.ScrollUtils.ScrollDirection;

public class HScrollListener extends ScrollListener {

		public HScrollListener(final JScrollPane scroll) {
			super(scroll);
		}

		@Override
		protected int getPosition() {
			return (int) MouseInfo.getPointerInfo().getLocation().getX();
		}

		@Override
		protected void scroll(final int amount) {
			if (amount != 0) {
				ScrollUtils.scroll(ScrollDirection.RIGHT, amount, this.scroll);
			}
		}
	}
