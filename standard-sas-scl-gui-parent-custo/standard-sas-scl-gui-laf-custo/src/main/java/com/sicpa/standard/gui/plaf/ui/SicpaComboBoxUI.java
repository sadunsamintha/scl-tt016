package com.sicpa.standard.gui.plaf.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceComboBoxUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SicpaComboBoxUI extends SubstanceComboBoxUI {

	public SicpaComboBoxUI(final JComboBox combo) {
		super(combo);
	}

	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		SicpaComboBoxUI ui = new SicpaComboBoxUI((JComboBox) comp);
		ui.comboBox = (JComboBox) comp;
		return ui;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		SicpaLookAndFeelCusto.flagAsButton(this.comboBox);
		this.comboBox.setOpaque(false);
		// this.comboBox.setFocusable(false);

		this.comboBox.addPropertyChangeListener("editable", new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getNewValue() == Boolean.TRUE) {
					SicpaComboBoxUI.this.comboBox.setFocusable(true);

					SicpaLookAndFeelCusto.flagAsDefaultArea(SicpaComboBoxUI.this.comboBox);
				} else {
					SicpaComboBoxUI.this.comboBox.setFocusable(false);
					SicpaLookAndFeelCusto.flagAsButton(SicpaComboBoxUI.this.comboBox);
				}
			}
		});

		this.comboBox.addPropertyChangeListener("UI", new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (SicpaComboBoxUI.this.comboBox == null) {
					return;
				}

				if (evt.getNewValue() == Boolean.TRUE) {
					SicpaComboBoxUI.this.comboBox.setFocusable(true);

					SicpaLookAndFeelCusto.flagAsDefaultArea(SicpaComboBoxUI.this.comboBox);
				} else {
					SicpaComboBoxUI.this.comboBox.setFocusable(false);
					SicpaLookAndFeelCusto.flagAsButton(SicpaComboBoxUI.this.comboBox);
				}
			}
		});
	}
}
