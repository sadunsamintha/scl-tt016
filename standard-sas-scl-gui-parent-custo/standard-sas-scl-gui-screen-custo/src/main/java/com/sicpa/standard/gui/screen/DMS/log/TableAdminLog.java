package com.sicpa.standard.gui.screen.DMS.log;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class TableAdminLog extends TableLog {
	private static TableAdminLog INSTANCE;

	public static final String I18N_DATE = GUIi18nManager.SUFFIX+"DMS.TableAdminLog.date";
	public static final String I18N_THREAD = GUIi18nManager.SUFFIX+"DMS.TableAdminLog.thread";
	public static final String I18N_FILE = GUIi18nManager.SUFFIX+"DMS.TableAdminLog.file";
	public static final String I18N_LOG = GUIi18nManager.SUFFIX+"DMS.TableAdminLog.log";

	public TableAdminLog() {
		this.model.addColumn(GUIi18nManager.get(I18N_DATE));
		this.model.addColumn(GUIi18nManager.get(I18N_THREAD));
		this.model.addColumn(GUIi18nManager.get(I18N_FILE));
		this.model.addColumn(GUIi18nManager.get(I18N_LOG));
		// this.table.getColumnModel().getColumn(0).setCellRenderer(new
		// OptimumFontTableCellRenderer(1200));
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.table.getColumnModel().getColumn(0).setPreferredWidth(150);
		this.table.getColumnModel().getColumn(1).setPreferredWidth(150);
		this.table.getColumnModel().getColumn(2).setPreferredWidth(200);
		this.table.getColumnModel().getColumn(3).setPreferredWidth(900);
		this.table.setFont(SicpaFont.getFont(10));
		this.table.setShowGrid(false);

		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				tableMouseClicked(e);
			}
		});

		// Map<TableColumn, String> tooltips=new HashMap<TableColumn, String>();
		// tooltips.put(this.table.getColumnModel().getColumn(0),this.table.
		// getColumnModel().getColumn(0).getHeaderValue(
		// ).toString());
		// tooltips.put(this.table.getColumnModel().getColumn(1),this.table.
		// getColumnModel().getColumn(1).getHeaderValue(
		// ).toString());
		// tooltips.put(this.table.getColumnModel().getColumn(2),this.table.
		// getColumnModel().getColumn(2).getHeaderValue(
		// ).toString());
		// tooltips.put(this.table.getColumnModel().getColumn(3),this.table.
		// getColumnModel().getColumn(3).getHeaderValue(
		// ).toString());
		// JTableUtils.setHeaderTooltip(this.table, tooltips);
		setCellTooltipVisible(true);
	}

	private void tableMouseClicked(final MouseEvent evt) {
		int col = this.table.columnAtPoint(evt.getPoint());
		String text = this.table.getValueAt(this.table.getSelectedRow(), this.table.getSelectedColumn()) + "";
		String header = this.table.getColumnModel().getColumn(col).getHeaderValue().toString();

		int wText = SwingUtilities.computeStringWidth(this.table.getFontMetrics(this.table.getFont()), text);
		int wHeader = SwingUtilities.computeStringWidth(this.table.getFontMetrics(this.table.getFont()), header);

		int currentwidth = this.table.getColumnModel().getColumn(col).getWidth();

		if (wText > wHeader && wText > currentwidth) {
			this.table.getColumnModel().getColumn(col).setPreferredWidth(wText + 20);
		} else if (wHeader > currentwidth) {
			this.table.getColumnModel().getColumn(col).setPreferredWidth(wHeader * 2);
		}
	}

	public static void addLine(final String date, final String thread, final String file, final String log) {
		while (getINSTANCE().model.getRowCount() > getINSTANCE().getMaxRow()) {
			getINSTANCE().model.removeRow(getINSTANCE().model.getRowCount() - 1);
		}
		getINSTANCE().model.addRow(new Object[] { date, thread, file, log });
	}

	protected int getMaxRow() {
		return 2000;
	}

	public static TableAdminLog getINSTANCE() {
		if (INSTANCE == null) {
			ThreadUtils.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					INSTANCE = new TableAdminLog();
				}
			});
		}
		return INSTANCE;
	}
}
