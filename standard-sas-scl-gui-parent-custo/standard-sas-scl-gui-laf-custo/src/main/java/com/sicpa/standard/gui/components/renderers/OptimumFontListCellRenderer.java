package com.sicpa.standard.gui.components.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;

import com.sicpa.standard.gui.utils.TextUtils;

/**
 * A list cell renderer that make the text always smaller than the given max
 * width by decreasing the font size
 * 
 * @author DIelsch
 * 
 */
public class OptimumFontListCellRenderer extends SicpaListCellRenderer {
	private static final long serialVersionUID = 1L;
	private int maxWidth;

	public OptimumFontListCellRenderer(final int maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		l.setFont(TextUtils.getOptimumFont(l.getText(), this.maxWidth, l.getFont()));
		return l;
	}
}
