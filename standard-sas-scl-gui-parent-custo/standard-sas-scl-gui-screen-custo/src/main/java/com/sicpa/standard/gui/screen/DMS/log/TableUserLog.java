package com.sicpa.standard.gui.screen.DMS.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sicpa.standard.gui.I18n.GUIi18nManager;
import com.sicpa.standard.gui.screen.DMS.log.UserLog.EStatusLog;
import com.sicpa.standard.gui.utils.JTableUtils;

public class TableUserLog extends TableLog {
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:m a");

	public static final String I18N_TYPE = GUIi18nManager.SUFFIX+"DMS.TableUserLog.logtype";
	public static final String I18N_DATE = GUIi18nManager.SUFFIX+"DMS.TableUserLog.date";
	public static final String I18N_ITEM_CODE = GUIi18nManager.SUFFIX+"DMS.TableUserLog.code";
	public static final String I18N_OPERATION = GUIi18nManager.SUFFIX+"DMS.TableUserLog.operation";
	public static final String I18N_RESULT = GUIi18nManager.SUFFIX+"DMS.TableUserLog.result";

	public TableUserLog() {
		this.model.addColumn(GUIi18nManager.get(I18N_TYPE));
		this.model.addColumn(GUIi18nManager.get(I18N_DATE));
		this.model.addColumn(GUIi18nManager.get(I18N_ITEM_CODE));
		this.model.addColumn(GUIi18nManager.get(I18N_OPERATION));
		this.model.addColumn(GUIi18nManager.get(I18N_RESULT));

		this.table.getColumnModel().getColumn(0).setPreferredWidth(50);
		JTableUtils.lockWidth(this.table.getColumnModel().getColumn(0), 100);

		// DATE
		JTableUtils.lockWidth(this.table.getColumnModel().getColumn(1), 220);

		this.table.getColumnModel().getColumn(2).setPreferredWidth(120);
		this.table.getColumnModel().getColumn(3).setPreferredWidth(150);
		this.table.getColumnModel().getColumn(4).setPreferredWidth(400);

		for (int i = 0; i < this.model.getColumnCount(); i++) {
			this.table.getColumnModel().getColumn(i).setCellRenderer(new UserLogCellRenderer());
		}

		// Map<TableColumn, String> tooltips = new HashMap<TableColumn,
		// String>();
		// tooltips.put(this.table.getColumnModel().getColumn(0),
		// this.table.getColumnModel
		// ().getColumn(0).getHeaderValue().toString());
		// tooltips.put(this.table.getColumnModel().getColumn(1),
		// this.table.getColumnModel
		// ().getColumn(1).getHeaderValue().toString());
		// tooltips.put(this.table.getColumnModel().getColumn(2),
		// this.table.getColumnModel
		// ().getColumn(2).getHeaderValue().toString());
		// tooltips.put(this.table.getColumnModel().getColumn(3),
		// this.table.getColumnModel
		// ().getColumn(3).getHeaderValue().toString());
		// tooltips.put(this.table.getColumnModel().getColumn(4),
		// this.table.getColumnModel
		// ().getColumn(4).getHeaderValue().toString());
		// JTableUtils.setHeaderTooltip(this.table, tooltips);

		setCellTooltipVisible(false);

	}

	public void addLineLast(final EStatusLog status, final Date date, final String itemCode, final String operation,
			final String result) {
		Object[] o = new Object[5];

		o[0] = status;
		o[1] = this.sdf.format(date);
		o[2] = itemCode;
		o[3] = operation;
		o[4] = result;

		this.model.addRow(o);
	}

	public void addLineFirst(final EStatusLog status, final Date date, final String itemCode, final String operation,
			final String result) {
		Object[] o = new Object[5];

		o[0] = status;
		o[1] = this.sdf.format(date);
		o[2] = itemCode;
		o[3] = operation;
		o[4] = result;

		this.model.insertRow(0, o);
	}
}
