package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceMenuUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SicpaMenuUI extends SubstanceMenuUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaMenuUI((JMenu) comp);
	}

	public SicpaMenuUI(final JMenu menu) {
		super(menu);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		// SicpaLookAndFeel.FlagAsHeaderOrFooter(this.menuItem);
	}
}
