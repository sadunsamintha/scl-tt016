package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceCheckBoxMenuItemUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SicpaCheckBoxMenuItemUI extends SubstanceCheckBoxMenuItemUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaCheckBoxMenuItemUI((JCheckBoxMenuItem) comp);
	}

	public SicpaCheckBoxMenuItemUI(final JCheckBoxMenuItem check) {
		super(check);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.menuItem);
	}
}
