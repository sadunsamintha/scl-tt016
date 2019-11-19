package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.TreeCellRenderer;

import org.pushingpixels.substance.internal.ui.SubstanceTreeUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.components.renderers.SicpaTreeCellRenderer;

public class SicpaTreeUI extends SubstanceTreeUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaTreeUI();
	}

	@Override
	protected TreeCellRenderer createDefaultCellRenderer() {
		return new SicpaTreeCellRenderer();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
	}

}
