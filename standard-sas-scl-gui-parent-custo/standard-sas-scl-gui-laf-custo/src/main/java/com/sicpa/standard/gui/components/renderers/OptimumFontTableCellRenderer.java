package com.sicpa.standard.gui.components.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import com.sicpa.standard.gui.utils.TextUtils;

/**
 * A table cell renderer that make the text always smaller than the given max
 * width by decreasing the font size
 * 
 * @author DIelsch
 * 
 */
public class OptimumFontTableCellRenderer extends SicpaTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private int maxWidth;

	public OptimumFontTableCellRenderer(final int maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		l.setFont(TextUtils.getOptimumFont(l.getText(), this.maxWidth, l.getFont()));

		return l;
	}
}
