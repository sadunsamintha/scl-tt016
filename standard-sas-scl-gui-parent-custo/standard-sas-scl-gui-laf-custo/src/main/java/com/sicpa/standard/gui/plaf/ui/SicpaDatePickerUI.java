package com.sicpa.standard.gui.plaf.ui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.swingx.SubstanceDatePickerUI;

public class SicpaDatePickerUI extends SubstanceDatePickerUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaDatePickerUI();
	}

	@Override
	protected JButton createPopupButton() {
		super.createPopupButton();
		this.substancePopupButton.setPreferredSize(new Dimension(this.substancePopupButton.getFont().getSize()+7,30));

		this.substancePopupButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				SicpaDatePickerUI.this.datePicker.setLinkDay(new Date(System.currentTimeMillis()));
			}
		});
		return this.substancePopupButton;
	}

	// protected JFormattedTextField createEditor()
	// {
	// JFormattedTextField field=super.createEditor();
	// field.setForeground(Color.WHITE);
	// return field;
	// }
}
