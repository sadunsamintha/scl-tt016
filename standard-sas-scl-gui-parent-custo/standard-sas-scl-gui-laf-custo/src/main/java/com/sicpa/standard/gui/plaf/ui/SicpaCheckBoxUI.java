package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceCheckBoxUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SicpaCheckBoxUI extends SubstanceCheckBoxUI {
	
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaCheckBoxUI((JCheckBox) comp);
	}

	public SicpaCheckBoxUI(final JToggleButton button) {
		super(button);
	}
}
