package com.sicpa.standard.gui.components.dialog.dropShadow;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import com.jhlabs.image.GaussianFilter;
import com.jidesoft.swing.Resizable;
import com.jidesoft.utils.PortingUtils;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.PaintUtils;

@SuppressWarnings("serial")
public class DropShadowContentPane extends JPanel {
	private BufferedImage image;
	private Paint paintBackground;
	private final ResizePanelManager resizeManager;

	public DropShadowContentPane() {
		this.resizeManager = new ResizePanelManager();
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				DropShadowContentPane.this.image = null;
				repaint();
			}
		});
	}

	private class ResizePanelManager extends Resizable {
		public ResizePanelManager() {
			super(DropShadowContentPane.this);
		}

		@Override
		public void resizing(final int resizeDir, final int newX, final int newY, final int newW, final int newH) {
			final Container container = DropShadowContentPane.this;
			PortingUtils.setPreferredSize(container, new Dimension(newW, newH));
			getTopLevelAncestor().setBounds(newX, newY, newW, newH);
		}

		@Override
		public boolean isTopLevel() {
			return true;
		}

		@Override
		public void installListeners() {
			super.installListeners();
		}
	}

	public void setResizable(final boolean flag) {
		if (flag) {
			this.resizeManager.uninstallListeners();
			this.resizeManager.installListeners();
		} else {
			this.resizeManager.uninstallListeners();

		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		PaintUtils.turnOnAntialias(g2);
		g2.drawImage(getImage(), 0, 0, null);
		g2.dispose();
	}

	// the shadow image
	private BufferedImage getImage() {
		if (this.image == null) {
			this.image = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());

			// ---create shadow
			Graphics2D g = this.image.createGraphics();
			PaintUtils.turnOnAntialias(g);
			g.setColor(new Color(0, 0, 0, 125));
			g.fillRoundRect(10, 10, getWidth() - 12, getHeight() - 12, 20, 20);
			g.dispose();
			new GaussianFilter(5).filter(this.image, this.image);

			// --background
			g = this.image.createGraphics();
			PaintUtils.turnOnAntialias(g);
			g.drawImage(getImage(), 0, 0, null);

			if (this.paintBackground != null) {
				g.setPaint(this.paintBackground);
			} else if (getBackground() instanceof ColorUIResource && SubstanceLookAndFeel.isCurrentLookAndFeel()) {
				final SubstanceColorScheme scheme = SubstanceLookAndFeel.getCurrentSkin().getBackgroundColorScheme(
						SubstanceLookAndFeel.getDecorationType(this));
				g.setColor(scheme.getBackgroundFillColor());
			} else {
				g.setColor(getBackground());
			}
			g.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
			g.setColor(SicpaColor.BLUE_DARK);
			g.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
			g.dispose();
		}
		return this.image;
	}

	public void setPaintBackground(final Paint paintBackground) {
		this.paintBackground = paintBackground;
		this.image = null;
		repaint();
	}

	@Override
	public void setBackground(final Color bg) {
		this.image = null;
		super.setBackground(bg);
	}
}
