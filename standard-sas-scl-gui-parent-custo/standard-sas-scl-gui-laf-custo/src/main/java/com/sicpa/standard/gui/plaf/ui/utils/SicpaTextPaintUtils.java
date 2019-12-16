package com.sicpa.standard.gui.plaf.ui.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.pushingpixels.substance.internal.painter.BackgroundPaintingUtils;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceOutlineUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;
import org.pushingpixels.substance.internal.utils.border.SubstanceTextComponentBorder;

import com.sicpa.standard.gui.components.border.SicpaTextComponentBorder;
import com.sicpa.standard.gui.utils.PaintUtils;

public class SicpaTextPaintUtils {
	public static void paintTextFieldBackground(final Graphics g, final JComponent comp) {
		paintTextCompBackground(g, comp, SubstanceColorUtilities.getBackgroundFillColor(comp), SubstanceCoreUtilities
				.toDrawWatermark(comp)
				|| !comp.isOpaque());
	}

	private static void paintTextCompBackground(final Graphics g, final JComponent comp, final Color backgr,
			final boolean toOverlayWatermark) {
		Graphics2D g2d = (Graphics2D) g.create();
		BackgroundPaintingUtils.update(g, comp, false);

		// if (!comp.isEnabled()|| !(comp.getBackground() instanceof
		// UIResource))
		{
			int componentFontSize = SubstanceSizeUtils.getComponentFontSize(comp);
			int borderDelta = (int) Math.floor(1.5 * SubstanceSizeUtils.getBorderStrokeWidth(componentFontSize));
			Border compBorder = comp.getBorder();

			boolean isSubstanceBorder = compBorder instanceof SicpaTextComponentBorder
					|| compBorder instanceof SubstanceTextComponentBorder;
			if (!isSubstanceBorder) {
				Border border = compBorder;
				while (border instanceof CompoundBorder) {
					Border outer = ((CompoundBorder) border).getOutsideBorder();
					if (outer instanceof SicpaTextComponentBorder || compBorder instanceof SubstanceTextComponentBorder) {
						isSubstanceBorder = true;
						break;
					}
					Border inner = ((CompoundBorder) border).getInsideBorder();
					if (inner instanceof SicpaTextComponentBorder || compBorder instanceof SubstanceTextComponentBorder) {
						isSubstanceBorder = true;
						break;
					}
					border = inner;
				}
			}

			Shape contour;
			if (isSubstanceBorder) {
				contour = SubstanceOutlineUtilities.getBaseOutline(comp.getWidth(), comp.getHeight(), Math.max(0, 2.0f
						* SubstanceSizeUtils.getClassicButtonCornerRadius(componentFontSize) - borderDelta), null,
						borderDelta);
			} else {
				contour = new Rectangle(0, 0, comp.getWidth(), comp.getHeight());
			}

			g2d.setColor(backgr);
			PaintUtils.turnOnAntialias(g2d);
			g2d.fill(contour);
		}

		g2d.dispose();
	}
}
