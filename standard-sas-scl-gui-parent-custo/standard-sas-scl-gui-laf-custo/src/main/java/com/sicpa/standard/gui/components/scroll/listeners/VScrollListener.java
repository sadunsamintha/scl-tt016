package com.sicpa.standard.gui.components.scroll.listeners;

import java.awt.MouseInfo;

import javax.swing.JScrollPane;

import com.sicpa.standard.gui.utils.ScrollUtils;
import com.sicpa.standard.gui.utils.ScrollUtils.ScrollDirection;

public  class VScrollListener extends ScrollListener {

		public VScrollListener(final JScrollPane scroll) {
			super(scroll);
		}

		@Override
		protected int getPosition() {
			return (int) MouseInfo.getPointerInfo().getLocation().getY();
		}

		@Override
		protected void scroll(final int amount) {
			if (amount != 0) {
				ScrollUtils.scroll(ScrollDirection.UP, amount, this.scroll);
			}
		}
	}
