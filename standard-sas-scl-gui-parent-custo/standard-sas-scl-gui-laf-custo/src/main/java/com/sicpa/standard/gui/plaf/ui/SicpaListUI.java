package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceListUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

public class SicpaListUI extends SubstanceListUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaListUI((JList) comp);
	}

	public SicpaListUI(final JList list) {
		super();
		// no idea why i can't set custom cell renderer here
		// during test: this code was called again (why ?) after setting the
		// custom cell renderer and thus overriding it
		// UIManager.put("List.cellRenderer",new SicpaListCellRenderer()); is
		// called in SicpaLookAndFeel to install the
		// default cell renderer
		// list.setCellRenderer(new SicpaListCellRenderer());
	}

	public void computeSize() {
		updateLayoutState();
	}

}
