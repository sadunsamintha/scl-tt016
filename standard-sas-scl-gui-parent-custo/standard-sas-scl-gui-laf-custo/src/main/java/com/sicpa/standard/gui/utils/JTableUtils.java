package com.sicpa.standard.gui.utils;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.sicpa.standard.gui.components.table.CopyToClipboardDialog;
import com.sicpa.standard.gui.components.table.CopyToClipboardDialog.CopyToClipBoardConfig;
import com.sicpa.standard.gui.components.windows.WindowFadeInManager;

public class JTableUtils {
	public static void setHeaderTooltip(final JTable table, final Map<TableColumn, String> tooltips) {
		JTableHeader header = table.getTableHeader();
		ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
		tips.setToolTips(tooltips);
		header.addMouseMotionListener(tips);
	}

	private static class ColumnHeaderToolTips extends MouseMotionAdapter {
		// Current column whose tooltip is being displayed.
		// This variable is used to minimize the calls to setToolTipText().
		private TableColumn curCol;
		private Map<TableColumn, String> tooltips;

		// If tooltip is null, removes any tooltip text.
		public void setToolTips(final Map<TableColumn, String> tooltips) {
			this.tooltips = tooltips;
		}

		@Override
		public void mouseMoved(final MouseEvent evt) {
			TableColumn col = null;
			JTableHeader header = (JTableHeader) evt.getSource();
			JTable table = header.getTable();
			TableColumnModel colModel = table.getColumnModel();
			int column = colModel.getColumnIndexAtX(evt.getX());

			// Return if not clicked on any column header
			if (column >= 0) {
				col = colModel.getColumn(column);
			}

			if (col != this.curCol) {
				header.setToolTipText((String) this.tooltips.get(col));
				this.curCol = col;
			}
		}
	}

	public static void lockWidth(final TableColumn column, final int width) {
		column.setMinWidth(width);
		column.setMaxWidth(width);
		column.setPreferredWidth(width);
	}

	public static void lockWidth(final JTable table, final int column, final int width) {
		lockWidth(table.getColumnModel().getColumn(column), width);
	}

	private static CopyToClipBoardConfig copyConfig;

	public static void turnOnDialogCopyToClipboard(final JTable table) {
		ActionMap actionMap = table.getActionMap();
		actionMap.put("copy", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final CopyToClipboardDialog copyDialog = new CopyToClipboardDialog(copyConfig, table);
				copyDialog.setLocation(MouseInfo.getPointerInfo().getLocation());

				WindowFadeInManager.fadeIn(copyDialog);
				if (copyDialog.isOk()) {
					copyConfig = copyDialog.getConfig();
				}
			}
		});
	}

	// Returns the preferred height of a row.
	// The result is equal to the tallest cell in the row.
	public static int getPreferredRowHeight(JTable table, int rowIndex, int margin) {
		// Get the current default height for all rows
		int height = table.getRowHeight();

		// Determine highest cell in the row
		for (int c = 0; c < table.getColumnCount(); c++) {
			TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
			Component comp = table.prepareRenderer(renderer, rowIndex, c);
			int h = comp.getPreferredSize().height + 2 * margin;
			height = Math.max(height, h);
		}
		return height;
	}

	// The height of each row is set to the preferred height of the
	// tallest cell in that row.
	public static void packRows(JTable table, int margin) {
		packRows(table, 0, table.getRowCount(), margin);
	}

	// For each row >= start and < end, the height of a
	// row is set to the preferred height of the tallest cell
	// in that row.
	public static void packRows(JTable table, int start, int end, int margin) {
		for (int r = 0; r < table.getRowCount(); r++) {
			// Get the preferred height
			int h = getPreferredRowHeight(table, r, margin);

			// Now set the row height using the preferred height
			if (table.getRowHeight(r) != h) {
				table.setRowHeight(r, h);
			}
		}
	}
}
