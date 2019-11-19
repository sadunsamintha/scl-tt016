package com.sicpa.standard.gui.painter;

import java.awt.Component;
import java.awt.Graphics2D;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.painter.decoration.SubstanceDecorationPainter;

public class SicpaDecorationPainter implements SubstanceDecorationPainter {
	public SicpaDecorationPainter() {
	}

	@Override
	public void paintDecorationArea(final Graphics2D g, final Component comp,
			final DecorationAreaType decorationAreaType, final int width, final int height, final SubstanceSkin skin) {
		g.setColor(skin.getBackgroundColorScheme(decorationAreaType).getMidColor());
		g.fillRect(0, 0, width, height);
	}

	@Override
	public String getDisplayName() {
		return "sicpa decoration painter";
	}

}
