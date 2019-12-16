package com.sicpa.standard.gui.plaf.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SicpaSeparatorUI extends BasicSeparatorUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaSeparatorUI();
	}

	public SicpaSeparatorUI() {
		super();
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		SubstanceColorScheme cs = SubstanceColorSchemeUtilities.getColorScheme(null,
				ColorSchemeAssociationKind.SEPARATOR, ComponentState.DEFAULT);
		Color color = cs.getMidColor();
		if (!(c.getBackground() instanceof ColorUIResource)) {
			color = c.getBackground();
		}
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(color);
		g2.fillRect(0, 0, c.getWidth(), c.getHeight());
		g2.dispose();
	}
}
