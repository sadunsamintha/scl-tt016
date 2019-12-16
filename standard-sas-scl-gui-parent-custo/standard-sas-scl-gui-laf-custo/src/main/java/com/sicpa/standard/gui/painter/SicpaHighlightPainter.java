package com.sicpa.standard.gui.painter;

import java.awt.Component;
import java.awt.Graphics2D;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.highlight.SubstanceHighlightPainter;

import com.sicpa.standard.gui.utils.PaintUtils;

public class SicpaHighlightPainter implements SubstanceHighlightPainter {
	@Override
	public String getDisplayName() {
		return "SicpaHighlightPainter";
	}

	@Override
	public void paintHighlight(final Graphics2D graphics, final Component comp, final int width, final int height,
			final SubstanceColorScheme colorScheme) {
		PaintUtils.paintHighlight(graphics, 0, 0, width, height);		
	}
}
