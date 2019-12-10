package com.sicpa.standard.gui.screen.DMS.log;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JTable;

public class UserLogi18nCellRenderer extends UserLogCellRenderer {

	private static final long serialVersionUID = 284697921266141064L;
	private Properties lang;

	public UserLogi18nCellRenderer(final Properties lang) {
		this.lang = lang;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value != null) {
			String res = this.lang.getProperty(value.toString());
			if (res != null) {
				label.setText(res);
			}
			// else
			// {
			// label.setText("no translation");
			// }
		}

		return label;

	}
}
