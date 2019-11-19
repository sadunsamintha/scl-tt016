package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstancePopupMenuUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SicpaPopupMenuUI extends SubstancePopupMenuUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaPopupMenuUI();
	}

	@Override
	public void installDefaults() {
		super.installDefaults();
		SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this.popupMenu);
	}
}
