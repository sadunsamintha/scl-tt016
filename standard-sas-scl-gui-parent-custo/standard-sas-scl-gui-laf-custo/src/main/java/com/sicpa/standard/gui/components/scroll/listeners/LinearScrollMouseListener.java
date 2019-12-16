package com.sicpa.standard.gui.components.scroll.listeners;

import java.awt.event.MouseAdapter;

import javax.swing.JScrollPane;

public  abstract class LinearScrollMouseListener extends MouseAdapter {
		protected int oldLoc;
		protected JScrollPane scroll;

		public LinearScrollMouseListener(final JScrollPane scroll) {
			this.scroll = scroll;
		}
	}
