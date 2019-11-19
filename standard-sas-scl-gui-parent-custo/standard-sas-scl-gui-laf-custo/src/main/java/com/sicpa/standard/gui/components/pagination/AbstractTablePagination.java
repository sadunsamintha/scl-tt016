package com.sicpa.standard.gui.components.pagination;

import java.util.List;

import com.sicpa.standard.gui.components.table.BeanReaderJTable;

public abstract class AbstractTablePagination<ITEM> extends AbstractPagination<ITEM> {

	private static final long serialVersionUID = 1L;
	protected BeanReaderJTable<ITEM> table;

	@Override
	protected void initGUI() {
		super.initGUI();
		getScroll().setViewportView(getTable());
	}

	@Override
	protected void displayPage(List<ITEM> items, int pageNumber) {
		getTable().clear();
		super.displayPage(items, pageNumber);
	}

	@Override
	protected void displayItem(ITEM item) {
		getTable().addRow(item);
	}

	public BeanReaderJTable<ITEM> getTable() {
		if (table == null) {
			table = new BeanReaderJTable<ITEM>(getFields(), getHeaders());
		}
		return table;
	}

	/**
	 * 
	 * @return the fields to display in the table
	 */
	protected abstract String[] getFields();

	/**
	 * 
	 * @return the table column header text to display
	 */
	protected abstract String[] getHeaders();
}
