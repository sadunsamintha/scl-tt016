package com.sicpa.standard.gui.components.preview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.pushingpixels.lafwidget.LafWidgetUtilities;

import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.TextUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class Preview extends JComponent {
	private JComponent comp;
	private BufferedImage image;
	private String title;

	public Preview() {
	}

	public void refreshPreview() {
		Map<Component, Boolean> dbSnapshot = new HashMap<Component, Boolean>();
		LafWidgetUtilities.makePreviewable(this.comp, dbSnapshot);

		this.image = GraphicsUtilities.createCompatibleTranslucentImage(this.comp.getWidth(), this.comp.getHeight());
		Graphics2D g = (Graphics2D) this.image.getGraphics();
		final Graphics2D gtemp = g;

		ThreadUtils.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				Preview.this.comp.validate();
				Preview.this.comp.paint(gtemp);
			}
		});

		g.dispose();

		int w = getWidth() - 5;
		int h = getHeight() - 5;
		if (w <= 0) {// hack for cover flow scroller
			h = w = 250;
		}

		LafWidgetUtilities.restorePreviewable(this.comp, dbSnapshot);

		double coef = Math.min((double) w / (double) this.comp.getWidth(), (double) h / (double) this.comp.getHeight());

		if (coef < 1.0) {
			w = (int) (coef * this.comp.getWidth());
			h = (int) (coef * this.comp.getHeight());
		}

		this.image = GraphicsUtilities.createThumbnail(this.image, w, h);
		g = (Graphics2D) this.image.getGraphics();
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, w - 1, h - 1);
		g.setFont(TextUtils.getOptimumFont(this.title, w, g.getFont()));
		g.drawImage(getImage(), 0, 0, null);
		PaintUtils.turnOnAntialias(g);
		g.setClip(0, 0, w - 5, h);
		PaintUtils.drawHighLightText(g, this.title, 5, 20, Color.BLACK, Color.white);
		g.dispose();

		this.image = ImageUtils.getShadowedImage(this.image);
	}

	public BufferedImage getImage() {
		// if (this.image == null)
		// {
		// refreshPreview();
		// }
		return this.image;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (getImage() == null) {
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
		g2.drawImage(getImage(), 0, 0, null);
	}

	public JComponent getComp() {
		return this.comp;
	}

	public void setComp(final JComponent comp) {
		this.comp = comp;
	}

	public void setImage(final BufferedImage image) {
		this.image = image;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}
}
