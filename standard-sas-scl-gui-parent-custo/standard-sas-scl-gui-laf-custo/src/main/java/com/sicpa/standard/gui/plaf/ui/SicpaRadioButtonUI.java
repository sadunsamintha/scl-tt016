package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceRadioButtonUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SicpaRadioButtonUI extends SubstanceRadioButtonUI {


	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaRadioButtonUI((JToggleButton) comp);
	}

	public SicpaRadioButtonUI(final JToggleButton button) {
		super(button);
	}

}
