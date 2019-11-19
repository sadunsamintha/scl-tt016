package com.sicpa.standard.gui.plaf.ui.utils;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JRootPane;

import org.pushingpixels.lafwidget.animation.effects.GhostPaintingUtils;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.internal.painter.BackgroundPaintingUtils;
import org.pushingpixels.substance.internal.ui.SubstanceRootPaneUI;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceTextUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceTitlePane;

public class SicpaTitlePane extends SubstanceTitlePane {
	public SicpaTitlePane(final JRootPane root, final SubstanceRootPaneUI ui) {
		super(root, ui);
	}

	@Override
	public void paintComponent(final Graphics g) {
		// long start = System.nanoTime();
		// As state isn't bound, we need a convenience place to check
		// if it has changed. Changing the state typically changes the
		if (this.getFrame() != null) {
			// this.setState(this.getFrame().getExtendedState());
		}
		final JRootPane rootPane = this.getRootPane();
		Window window = this.window;
		boolean leftToRight = (window == null) ? rootPane.getComponentOrientation().isLeftToRight() : window
				.getComponentOrientation().isLeftToRight();
		int width = this.getWidth();
		int height = this.getHeight();

		SubstanceSkin skin = SubstanceCoreUtilities.getSkin(rootPane);
		if (skin == null) {
			SubstanceCoreUtilities.traceSubstanceApiUsage(this,
					"Substance delegate used when Substance is not the current LAF");
		}
		SubstanceColorScheme scheme = skin.getActiveColorScheme(DecorationAreaType.PRIMARY_TITLE_PANE);

		int xOffset = 0;
		String theTitle = this.getTitle();

		if (theTitle != null) {
			Rectangle titleTextRect = this.getTitleTextRectangle();
			FontMetrics fm = rootPane.getFontMetrics(g.getFont());
			int titleWidth = titleTextRect.width - 20;
			String clippedTitle = SubstanceCoreUtilities.clipString(fm, titleWidth, theTitle);
			// show tooltip with full title only if necessary
			if (theTitle.equals(clippedTitle)) {
				this.setToolTipText(null);
			} else {
				this.setToolTipText(theTitle);
			}
			theTitle = clippedTitle;
			if (leftToRight)
				xOffset = titleTextRect.x;
			else
				xOffset = titleTextRect.x + titleTextRect.width - fm.stringWidth(theTitle);
		}

		Graphics2D graphics = (Graphics2D) g.create();
		Font font = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null).getWindowTitleFont();
		graphics.setFont(font);

		BackgroundPaintingUtils.update(graphics, SicpaTitlePane.this, false);

		// draw the title (if needed)
		if (theTitle != null) {
			// FontMetrics fm = rootPane.getFontMetrics(graphics.getFont());
			// int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent();

			// SubstanceTextUtilities.paintTextWithDropShadow(this, graphics,
			// SubstanceColorUtilities.getForegroundColor(scheme), theTitle,
			// width, height,
			// xOffset, yOffset);
			SubstanceTextUtilities.paintText(graphics, this, new Rectangle(xOffset, 0, width, height), theTitle, -1,
					font, SubstanceColorUtilities.getForegroundColor(scheme), null);

		}

		GhostPaintingUtils.paintGhostImages(this, graphics);

		// long end = System.nanoTime();
		// System.out.println(end - start);
		graphics.dispose();
	}

	private String getTitle() {
		Window w = this.window;

		if (w instanceof Frame) {
			return ((Frame) w).getTitle();
		}
		if (w instanceof Dialog) {
			return ((Dialog) w).getTitle();
		}
		return null;
	}

	private Frame getFrame() {
		Window window = this.window;

		if (window instanceof Frame) {
			return (Frame) window;
		}
		return null;
	}
}
