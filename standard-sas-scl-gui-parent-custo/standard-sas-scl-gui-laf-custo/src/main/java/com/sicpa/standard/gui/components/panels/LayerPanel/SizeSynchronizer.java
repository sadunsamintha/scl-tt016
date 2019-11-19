package com.sicpa.standard.gui.components.panels.LayerPanel;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class SizeSynchronizer extends ComponentAdapter implements HierarchyListener {
	protected JComponent src;
	protected JComponent dst;

	public SizeSynchronizer(final JComponent src, final JComponent dst) {
		this.src = src;
		this.dst = dst;
	}

	@Override
	public void componentResized(final ComponentEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Point p = src.getLocation();
				dst.setBounds(p.x, p.y, src.getWidth(), src.getHeight());
				if (dst.getParent() != null) {
					dst.getParent().validate();
				}
			}
		});
	}

	@Override
	public void componentShown(final ComponentEvent e) {
		componentResized(e);
	}

	@Override
	public void hierarchyChanged(final HierarchyEvent e) {
		long changeFlags = e.getChangeFlags();
		if (changeFlags > 0) {
			if ((changeFlags & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
				componentResized(null);
			}
		}
	}

}
