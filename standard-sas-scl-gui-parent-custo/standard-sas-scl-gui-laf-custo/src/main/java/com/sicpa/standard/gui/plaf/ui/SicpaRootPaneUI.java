package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceRootPaneUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.ui.utils.SicpaTitlePane;

public class SicpaRootPaneUI extends SubstanceRootPaneUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaRootPaneUI();
	}

	@Override
	protected JComponent createTitlePane(final JRootPane root) {
		return new SicpaTitlePane(root, this);
	}
}
