package com.sicpa.standard.gui.components.renderers;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultComboBoxRenderer;

public class BasicComboBoxRendererWithTooltip extends SubstanceDefaultComboBoxRenderer {
	private String[] tooltips;

	public BasicComboBoxRendererWithTooltip(final String[] tooltips, final JComboBox combo) {
		super(combo);
		this.tooltips = tooltips;
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		Component res = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (-1 < index) {
			list.setToolTipText(this.tooltips[index]);
		} else {
			list.setToolTipText(null);
		}
		return res;
	}
}
