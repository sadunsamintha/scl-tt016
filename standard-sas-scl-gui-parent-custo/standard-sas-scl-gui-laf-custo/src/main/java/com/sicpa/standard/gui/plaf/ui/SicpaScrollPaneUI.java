package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceScrollPaneUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SicpaScrollPaneUI extends SubstanceScrollPaneUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaScrollPaneUI();
	}

	@Override
	protected void installDefaults(final JScrollPane scrollpane) {
		super.installDefaults(scrollpane);
	}
}
