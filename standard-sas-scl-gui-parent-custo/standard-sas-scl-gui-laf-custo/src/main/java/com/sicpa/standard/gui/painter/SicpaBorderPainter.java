package com.sicpa.standard.gui.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.painter.border.StandardBorderPainter;
import org.pushingpixels.substance.internal.utils.SubstanceInternalArrowButton;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaSkin;

public class SicpaBorderPainter extends StandardBorderPainter {
	@Override
	public String getDisplayName() {
		return "SicpaBorderPainter";
	}

	@Override
	public void paintBorder(final Graphics g, final Component c, final int width, final int height,
			final Shape contour, final Shape innerContour, final SubstanceColorScheme borderScheme) {
		if (c instanceof JProgressBar) {

		} else {
			paintBorderInternal(g, c, width, height, contour, innerContour, borderScheme);
		}
	}

	public void paintBorderInternal(final Graphics g, final Component c, final int width, final int height,
			final Shape contour, final Shape innerContour, final SubstanceColorScheme colorScheme1) {
		if (contour == null)
			return;

		Graphics2D graphics = (Graphics2D) g.create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

		Color borderColor;

		if (SubstanceLookAndFeel.getDecorationType(c) == SicpaSkin.WORK_DECORATION_AREA_TYPE || c instanceof JScrollBar
				|| c instanceof JComboBox) {
			// if on ultra light blue back ground
			// or if jscrollbar
			// or if jcombobox, even if the combo is on a panel flag as
			// WORK_DECORATION_AREA_TYPE it's needed...
			borderColor = SicpaColor.BLUE_DARK;// colorScheme1.getMidColor();
		} else {
			borderColor = SicpaColor.BLUE_LIGHT;// colorScheme1.getLightColor();
		}

		if (borderColor != null) {
			float strokeWidth = SubstanceSizeUtils.getBorderStrokeWidth(SubstanceSizeUtils.getComponentFontSize(c));
			boolean isSpecialButton = c instanceof SubstanceInternalArrowButton;
			int joinKind = isSpecialButton ? BasicStroke.JOIN_MITER : BasicStroke.JOIN_ROUND;
			int capKind = isSpecialButton ? BasicStroke.CAP_SQUARE : BasicStroke.CAP_BUTT;
			graphics.setStroke(new BasicStroke(strokeWidth, capKind, joinKind));
			graphics.setColor(borderColor);
			graphics.draw(contour);
		}
		graphics.dispose();
	}
}
