package com.sicpa.standard.gui.components.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

public class SicpaListCellRenderer extends SubstanceDefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		JLabel res = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		CellRenderersUtils.prepareRenderer(res, list, index, isSelected);
		return res;
	}
}
