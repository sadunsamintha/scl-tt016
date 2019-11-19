package com.sicpa.standard.gui.screen.DMS.log;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.screen.DMS.log.UserLog.EStatusLog;

public class UserLogCellRenderer extends SicpaTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value instanceof EStatusLog) {
			label.setIcon(((EStatusLog) value).getIcon());
			label.setText("");
		} else {
			label.setIcon(null);
		}
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		EStatusLog type = (EStatusLog) dtm.getValueAt(row, 0);
		if (type == EStatusLog.failure) {
			label.setForeground(SicpaColor.RED);
		} else {
			label.setForeground(SicpaColor.BLUE_MEDIUM);
		}

		if (row == 0) {
			label.setFont(getFont().deriveFont(22f));
		} else {
			label.setFont(getFont());
		}

		return label;
	}
}
