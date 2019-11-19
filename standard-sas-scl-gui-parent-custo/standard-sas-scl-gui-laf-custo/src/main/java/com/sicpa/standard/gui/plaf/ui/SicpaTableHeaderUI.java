package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import org.pushingpixels.substance.internal.ui.SubstanceTableHeaderUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.components.renderers.SicpaTableHeaderCellRenderer;

public class SicpaTableHeaderUI extends SubstanceTableHeaderUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaTableHeaderUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		if (this.defaultHeaderRenderer instanceof UIResource) {
			this.header.setDefaultRenderer(new SicpaTableHeaderCellRenderer());
		}
	}
}
