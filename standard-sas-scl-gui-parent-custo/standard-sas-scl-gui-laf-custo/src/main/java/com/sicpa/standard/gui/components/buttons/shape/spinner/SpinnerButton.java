package com.sicpa.standard.gui.components.buttons.shape.spinner;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.GaussianBlurFilter;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;

import com.sicpa.standard.gui.components.buttons.shape.AbstractShapeButton;
import com.sicpa.standard.gui.utils.PaintUtils;

public abstract class SpinnerButton extends AbstractShapeButton {
	private static final long serialVersionUID = 1L;
	private BufferedImage blurBuff;

	public SpinnerButton() {
		this.withShadow = false;
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				SpinnerButton.this.blurBuff = null;
				repaint();
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				SpinnerButton.this.blurBuff = null;
				repaint();
			}
		});
	}

	private BufferedImage getBlur() {
		if (this.blurBuff == null) {
			this.blurBuff = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
			Graphics2D g2 = (Graphics2D) this.blurBuff.getGraphics();
			g2.setStroke(new BasicStroke(10));
			g2.setPaint(getPaint());
			g2.draw(this.shape);
			g2.dispose();
			new GaussianBlurFilter(20).filter(this.blurBuff, this.blurBuff);
		}
		return this.blurBuff;
	}

	protected abstract Paint getPaint();

	@Override
	protected void paintComponent(final Graphics g) {
		BufferedImage buff = GraphicsUtilities.createCompatibleTranslucentImage(getWidth(), getHeight());
		Graphics2D g2 = (Graphics2D) buff.getGraphics();
		PaintUtils.turnOnAntialias(g2);
		// if (isEnabled()) {
		// g2.setColor(getCurrentColor());
		// } else {
		// SubstanceColorScheme cs = SubstanceColorSchemeUtilities
		// .getColorScheme(this, ComponentState.DISABLED_ACTIVE);
		// g2.setColor(cs.getMidColor());
		// }

		SubstanceFillPainter painter = SubstanceLookAndFeel.getCurrentSkin().getFillPainter();

		SubstanceColorScheme defaultScheme = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this,
				ComponentState.ENABLED);

		SubstanceColorScheme armedScheme = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this,
				ComponentState.ARMED);

		if (!isEnabled()) {
			defaultScheme = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this,
					ComponentState.DISABLED_UNSELECTED);
			armedScheme = defaultScheme;
		}
		painter.paintContourBackground(g2, this, getWidth(), getHeight(), getShape(), false, defaultScheme, true);

		g2.setComposite(AlphaComposite.SrcOver.derive(getAnimProgress()));

		painter.paintContourBackground(g2, this, getWidth(), getHeight(), getShape(), false, armedScheme, true);

		// g2.fill(this.shape);
		g2.setComposite(AlphaComposite.SrcAtop.derive(0.55f));
		g2.drawImage(getBlur(), 0, 0, null);
		g2.dispose();

		g.drawImage(buff, 0, 0, null);
	}
}
