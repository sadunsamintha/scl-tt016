package com.sicpa.standard.gui.painter;

import java.awt.Graphics2D;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXMultiSplitPane.DividerPainter;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

public class SicpaJXMultiSplitPaneDividerPainter extends DividerPainter {

	private JXMultiSplitPane splitpane;

	public SicpaJXMultiSplitPaneDividerPainter(final JXMultiSplitPane splitpane) {
		super();
		this.splitpane = splitpane;
	}

	@Override
	protected void doPaint(final Graphics2D g, final Divider divider, final int width, final int height) {
		SubstanceLookAndFeel.getCurrentSkin().getFillPainter().paintContourBackground(g, this.splitpane, width, height,
				this.splitpane.getBounds(), false,
				SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this.splitpane, ComponentState.DEFAULT), true);

	}

}
