package com.sicpa.standard.gui.plaf.ui;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.SicpaSkin;

public class SicpaPanelUI extends org.pushingpixels.substance.swingx.SubstancePanelUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaPanelUI();
	}

	@Override
	protected void installDefaults(final JPanel p) {
		super.installDefaults(p);

		if (SubstanceLookAndFeel.getDecorationType(p) == null
				|| SubstanceLookAndFeel.getDecorationType(p) == DecorationAreaType.NONE) {
			SicpaLookAndFeelCusto.flagAsWorkArea(p);
		}
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		super.update(g, c);
		if (c.getBackground() != null) {
			if (SubstanceLookAndFeel.getDecorationType(c) == SicpaSkin.WORK_DECORATION_AREA_TYPE) {
				if (!(c.getBackground() instanceof ColorUIResource)) {
					g.setColor(c.getBackground());
					g.fillRect(0, 0, c.getWidth(), c.getHeight());
				}
			}
		}
	}
}
