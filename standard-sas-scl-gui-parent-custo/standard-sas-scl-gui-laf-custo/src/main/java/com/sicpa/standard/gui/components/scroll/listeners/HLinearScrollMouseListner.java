package com.sicpa.standard.gui.components.scroll.listeners;

import java.awt.MouseInfo;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;

import com.sicpa.standard.gui.utils.ScrollUtils;
import com.sicpa.standard.gui.utils.ScrollUtils.ScrollDirection;

public  class HLinearScrollMouseListner extends LinearScrollMouseListener {
		public HLinearScrollMouseListner(final JScrollPane scroll) {
			super(scroll);
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			this.oldLoc = (int) MouseInfo.getPointerInfo().getLocation().getX();
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			int current = (int) MouseInfo.getPointerInfo().getLocation().getX();
			int offset = this.oldLoc - current;
			ScrollUtils.scroll(ScrollDirection.RIGHT, offset, this.scroll);
			this.oldLoc = current;
		}
	}
