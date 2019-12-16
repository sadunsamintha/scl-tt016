package com.sicpa.standard.gui.components.scroll.listeners;

import java.awt.MouseInfo;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;

import com.sicpa.standard.gui.utils.ScrollUtils;
import com.sicpa.standard.gui.utils.ScrollUtils.ScrollDirection;

public  class VLinearScrollMouseListner extends LinearScrollMouseListener {
		public VLinearScrollMouseListner(final JScrollPane scroll) {
			super(scroll);
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			this.oldLoc = (int) MouseInfo.getPointerInfo().getLocation().getY();
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			int current = (int) MouseInfo.getPointerInfo().getLocation().getY();
			int offset = this.oldLoc - current;
			ScrollUtils.scroll(ScrollDirection.UP, offset, this.scroll);
			this.oldLoc = current;
		}
	}
