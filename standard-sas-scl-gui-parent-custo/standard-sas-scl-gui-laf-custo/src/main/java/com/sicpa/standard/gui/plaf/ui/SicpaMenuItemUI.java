package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceMenuItemUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SicpaMenuItemUI extends SubstanceMenuItemUI {
	public static ComponentUI crehahaateUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaMenuItemUI((JMenuItem) comp);
	}

	public SicpaMenuItemUI(final JMenuItem item) {
		super(item);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		// SicpaLookAndFeel.FlagAsHeaderOrFooter(this.menuItem);
	}
}
