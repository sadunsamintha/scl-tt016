package com.sicpa.standard.gui.components.scroll;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.internal.utils.HashMapKey;

import com.sicpa.standard.gui.components.scroll.listeners.HLinearScrollMouseListner;
import com.sicpa.standard.gui.components.scroll.listeners.HScrollListener;
import com.sicpa.standard.gui.components.scroll.listeners.VLinearScrollMouseListner;
import com.sicpa.standard.gui.components.scroll.listeners.VScrollListener;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.lazyMap.LazyResettableHashMap;

public class SmallScrollBar {

	private static LazyResettableHashMap<BufferedImage> cache;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JLabel label = new JLabel(new ImageIcon(ImageUtils.createRandomStrippedImage(700, 700, 50)));
				f.getContentPane().setLayout(new BorderLayout());

				final JScrollPane scroll = new JScrollPane(label);
				// SmoothScrolling.enableFullScrolling(scroll);
				f.getContentPane().add(SmallScrollBar.createLayerSmallScrollBar(scroll));

				f.setSize(300, 300);
				f.setVisible(true);

			}
		});
	}

	public static JXLayer<JScrollPane> createLayerSmallScrollBar(final JScrollPane scroll) {
		return createLayerSmallScrollBar(scroll, true, true, true, true);
	}

	public static JXLayer<JScrollPane> createLayerSmallScrollBar(final JScrollPane scroll, final boolean withHbar,
			final boolean withVbar, final boolean withSmoothScrolling, boolean display) {
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		JXLayer<JScrollPane> layer = new JXLayer<JScrollPane>(scroll);
		ScrollBarLayerUI ui = new ScrollBarLayerUI(scroll, withHbar, withVbar, withSmoothScrolling, display);
		layer.setUI(ui);
		return layer;
	}

	private static final int barWidth = 5;

	@SuppressWarnings("serial")
	public static class ScrollBarLayerUI extends AbstractLayerUI<JScrollPane> {
		private JScrollPane scroll;
		private boolean withVbar;
		private boolean withHbar;

		HScrollListener hScrollListener;
		HLinearScrollMouseListner hlinear;
		VScrollListener vScrollListener;
		VLinearScrollMouseListner vlinear;

		boolean display = true;

		@Override
		public void eventDispatched(final AWTEvent event, final JXLayer<? extends JScrollPane> arg1) {
			super.eventDispatched(event, arg1);
			if (this.hScrollListener == null) {
				return;
			}

			if (event instanceof MouseEvent) {
				disptachToScroll((MouseEvent) event);
			}
		}

		private void disptachToScroll(final MouseEvent evt) {
			switch (evt.getID()) {
			case MouseEvent.MOUSE_DRAGGED:
				this.hScrollListener.mouseDragged(evt);
				this.vScrollListener.mouseDragged(evt);
				this.hlinear.mouseDragged(evt);
				this.vlinear.mouseDragged(evt);
				break;
			case MouseEvent.MOUSE_PRESSED:
				this.hScrollListener.mousePressed(evt);
				this.vScrollListener.mousePressed(evt);
				this.hlinear.mousePressed(evt);
				this.vlinear.mousePressed(evt);
				break;
			case MouseEvent.MOUSE_RELEASED:
				this.hScrollListener.mouseReleased(evt);
				this.vScrollListener.mouseReleased(evt);
				this.hlinear.mouseReleased(evt);
				this.vlinear.mouseReleased(evt);
				break;
			}
		}

		public ScrollBarLayerUI(final JScrollPane scroll, final boolean withHbar, final boolean withVbar,
				final boolean withSmoothScrolling, boolean display) {
			this.scroll = scroll;
			this.withHbar = withHbar;
			this.withVbar = withVbar;
			if (withSmoothScrolling) {
				this.hScrollListener = new HScrollListener(scroll);
				this.hlinear = new HLinearScrollMouseListner(scroll);
				this.vScrollListener = new VScrollListener(scroll);
				this.vlinear = new VLinearScrollMouseListner(scroll);
			}
			this.display = display;
		}

		@Override
		protected void paintLayer(final Graphics2D g, final JXLayer<? extends JScrollPane> layer) {
			super.paintLayer(g, layer);
			if (!display) {
				return;
			}
			JComponent view = (JComponent) this.scroll.getViewport().getView();
			if (layer == null || view == null) {
				return;
			}
			if (this.withVbar && layer.getHeight() < view.getHeight()) {
				JScrollBar bar = this.scroll.getVerticalScrollBar();
				double current = bar.getValue();
				double visible = bar.getVisibleAmount();
				double h = layer.getHeight();
				double barY = current / view.getHeight() * h;
				double barH = visible / view.getHeight() * h;
				barH = Math.max(barH, 25);
				paintBar(g, layer.getWidth() - barWidth - 2, (int) barY, barWidth, (int) barH);
			}

			if (this.withHbar && layer.getWidth() < view.getWidth()) {
				JScrollBar bar = this.scroll.getHorizontalScrollBar();
				double current = bar.getValue();
				double visible = bar.getVisibleAmount();
				double w = layer.getWidth();
				double barX = current / view.getWidth() * w;
				double barW = visible / view.getWidth() * w;
				barW = Math.max(barW, 25);
				paintBar(g, (int) barX, layer.getHeight() - barWidth - 2, (int) barW, barWidth);
			}
		}
	}

	private static void paintBar(final Graphics2D g, final int x, final int y, final int w, final int h) {
		if (h <= 0 || w <= 0) {
			return;
		}

		if (cache == null) {
			cache = new LazyResettableHashMap<BufferedImage>();
		}
		HashMapKey key = new HashMapKey(w, h);
		BufferedImage cachedImage = cache.get(key);
		if (cachedImage == null) {
			cachedImage = GraphicsUtilities.createCompatibleTranslucentImage(w, h);
			Graphics2D gBuff = cachedImage.createGraphics();

			gBuff.setColor(Color.BLACK);
			PaintUtils.turnOnQualityRendering(gBuff);
			gBuff.fillRoundRect(0, 0, w, h, 5, 5);

			gBuff.setComposite(AlphaComposite.DstIn);
			gBuff.setColor(new Color(0, 0, 0, 150));
			gBuff.fillRoundRect(1, 1, w - 2, h - 2, 2, 2);

			gBuff.dispose();
			cache.put(key, cachedImage);
		}

		g.drawImage(cachedImage, x, y, null);
	}
}
