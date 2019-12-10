package com.sicpa.standard.gui.components.layeredComponents.oldValue;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.utils.PaintUtils;

public class OldValueUI extends AbstractLayerUI<JComponent> {
	private static class TrackedComponent {
		private int x;
		private int y;
		private OldValueWrapper tracked;
		private JComponent compLocation;
		private Font font;
		private Color inner;
		private Color border;

		public TrackedComponent(final JComponent compLocation, final OldValueWrapper tracked, final int x, final int y) {
			super();
			this.compLocation = compLocation;
			this.tracked = tracked;
			this.x = x;
			this.y = y;
			this.border = Color.WHITE;
			this.inner = Color.BLACK;
			this.font = SicpaFont.getFont(14);
		}

		public TrackedComponent(final JComponent compLocation, final OldValueWrapper tracked, final int x, final int y,
				final Font font, final Color in, final Color out) {
			super();
			this.compLocation = compLocation;
			this.tracked = tracked;
			this.x = x;
			this.y = y;
			this.inner = in;
			this.border = out;
			this.font = font;
		}
	}

	private ArrayList<TrackedComponent> list = new ArrayList<TrackedComponent>();

	@Override
	protected void paintLayer(final Graphics2D g, final JXLayer<? extends JComponent> layer) {
		super.paintLayer(g, layer);
		g.setComposite(AlphaComposite.SrcOver.derive(0.8f));
		for (TrackedComponent tc : this.list) {
			if (tc.tracked.isShowing() && tc.tracked.isChanged()) {
				paintAValue(g, tc, layer);
			}
		}
	}

	private void paintAValue(final Graphics2D g, final TrackedComponent tracked, final JXLayer<? extends JComponent> layer) {
		g.setFont(tracked.font);
		Point p = new Point(tracked.compLocation.getX(), tracked.compLocation.getY());
		p = SwingUtilities.convertPoint(tracked.tracked.getParent(), p, layer);
		PaintUtils.drawHighLightText(g, 1, 0.1f, tracked.tracked.getFormatedValue(), p.x + tracked.x, p.y + tracked.y,
				tracked.inner, tracked.border);
	}

	public void addTrackedComponent(final OldValueWrapper wrapper, final int xOffset, final int yOffset,
			final JComponent compRelativ) {
		this.list.add(new TrackedComponent(compRelativ, wrapper, xOffset, yOffset));
	}

	public void addTrackedComponent(final OldValueWrapper wrapper, final int xOffset, final int yOffset,
			final JComponent compRelativ, final Font font, final Color inner, final Color border) {
		this.list.add(new TrackedComponent(compRelativ, wrapper, xOffset, yOffset, font, inner, border));
	}

	public void storeAllOldValue() {
		for (TrackedComponent tc : this.list) {
			tc.tracked.setOldValue();
		}
	}
}
