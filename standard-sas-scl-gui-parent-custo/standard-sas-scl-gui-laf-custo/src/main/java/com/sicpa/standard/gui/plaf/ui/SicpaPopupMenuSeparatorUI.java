package com.sicpa.standard.gui.plaf.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SicpaPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaSeparatorUI();
	}

	public SicpaPopupMenuSeparatorUI() {
		super();
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		SubstanceColorScheme cs = SubstanceColorSchemeUtilities.getColorScheme(c, ColorSchemeAssociationKind.SEPARATOR,
				ComponentState.DEFAULT);
		Color color = cs.getMidColor();
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(color);
		g2.fillRect(0, 0, c.getWidth(), c.getHeight());
		g2.dispose();
	}
}
