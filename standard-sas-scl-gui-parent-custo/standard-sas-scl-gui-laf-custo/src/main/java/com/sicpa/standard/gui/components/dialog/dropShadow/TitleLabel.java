package com.sicpa.standard.gui.components.dialog.dropShadow;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.internal.utils.SubstanceTextUtilities;

public class TitleLabel extends JComponent {

	private JDialog diag;
	private JFrame frame;

	public TitleLabel(final JFrame f) {
		this.frame = f;
	}

	public TitleLabel(final JDialog d) {
		this.diag = d;
	}

	@Override
	protected void paintComponent(final Graphics g) {

		final SubstanceSkin skin = SubstanceLookAndFeel.getCurrentSkin();

		final SubstanceColorScheme scheme = skin.getColorScheme(this, ComponentState.ENABLED);

		int xOffset = 0;
		String theTitle = this.diag == null ? this.frame.getTitle() : this.diag.getTitle();

		if (theTitle != null) {

			final int w = this.diag == null ? this.frame.getWidth() : this.diag.getWidth();

			final Rectangle titleTextRect = new Rectangle(0, 0, w - 50, getHeight());
			final FontMetrics fm = getFontMetrics(g.getFont());
			final int titleWidth = titleTextRect.width - 20;
			final String clippedTitle = clipString(fm, titleWidth, theTitle);
			// show tooltip with full title only if necessary
			if (theTitle.equals(clippedTitle)) {
				this.setToolTipText(null);
			} else {
				this.setToolTipText(theTitle);
			}
			theTitle = clippedTitle;

			xOffset = titleTextRect.x;
		}

		final Graphics2D graphics = (Graphics2D) g.create();
		final Font font = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null).getWindowTitleFont();
		graphics.setFont(font.deriveFont(30f));

		if (theTitle != null) {
			SubstanceTextUtilities.paintText(graphics, this, new Rectangle(xOffset, 0, getWidth(), getHeight()),
					theTitle, -1, font, scheme.getForegroundColor(), null);
		}
		graphics.dispose();
	}

	public static String clipString(final FontMetrics metrics, final int availableWidth, final String fullText) {

		if (metrics.stringWidth(fullText) <= availableWidth)
			return fullText;

		String ellipses = "...";
		int ellipsesWidth = metrics.stringWidth(ellipses);
		if (ellipsesWidth > availableWidth)
			return "";

		String starter = "";
		String ender = "";

		int w = fullText.length();
		int w2 = (w / 2) + (w % 2);
		String prevTitle = "";
		for (int i = 0; i < w2; i++) {
			String newStarter = starter + fullText.charAt(i);
			String newEnder = ender;
			if ((w - i) > w2)
				newEnder = fullText.charAt(w - i - 1) + newEnder;
			String newTitle = newStarter + ellipses + newEnder;
			if (metrics.stringWidth(newTitle) <= availableWidth) {
				starter = newStarter;
				ender = newEnder;
				prevTitle = newTitle;
				continue;
			}
			return prevTitle;
		}
		return fullText;
	}
}
