package com.sicpa.standard.gui.components.renderers;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/*
 *	Use a JTable as a renderer for row numbers of a given main table.
 *  This table must be added to the row header of the scrollpane that
 *  contains the main table.
 */
public class RowNumberTable extends JTable implements ChangeListener, PropertyChangeListener {
	// TODO more work needed
	private JTable main;

	public RowNumberTable(final JTable table) {
		this.main = table;
		this.main.addPropertyChangeListener(this);

		setFocusable(false);
		setAutoCreateColumnsFromModel(false);
		// setModel(this.main.getModel());
		// setSelectionModel(this.main.getSelectionModel());

		TableColumn column = new TableColumn();
		column.setHeaderValue(" ");
		addColumn(column);
		// column.setCellRenderer(new RowNumberRenderer());

		getColumnModel().getColumn(0).setPreferredWidth(50);
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	@Override
	public void addNotify() {
		super.addNotify();

		Component c = getParent();

		// Keep scrolling of the row table in sync with the main table.

		if (c instanceof JViewport) {
			JViewport viewport = (JViewport) c;
			viewport.addChangeListener(this);
		}
	}

	/*
	 * Delegate method to main table
	 */
	@Override
	public int getRowCount() {
		if (this.main != null) {
			return this.main.getRowCount();
		} else {
			return 0;
		}
	}

	@Override
	public int getRowHeight(final int row) {
		return this.main.getRowHeight(row);
	}

	/*
	 * This table does not use any data from the main TableModel, so just return
	 * a value based on the row parameter.
	 */
	@Override
	public Object getValueAt(final int row, final int column) {
		return Integer.toString(row + 1);
	}

	/*
	 * Don't edit data in the main TableModel by mistake
	 */
	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	//
	// Implement the ChangeListener
	//
	public void stateChanged(final ChangeEvent e) {
		// Keep the scrolling of the row table in sync with main table

		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane) viewport.getParent();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}

	//
	// Implement the PropertyChangeListener
	//
	public void propertyChange(final PropertyChangeEvent e) {
		// Keep the row table in sync with the main table

		if ("selectionModel".equals(e.getPropertyName())) {
			// setSelectionModel(this.main.getSelectionModel());
		}

		if ("model".equals(e.getPropertyName())) {
			// setModel(this.main.getModel());
		}
	}

	private static class RowNumberRenderer// extends DefaultTableCellRenderer
			implements TableCellRenderer {
		JLabel label;

		public RowNumberRenderer() {
			this.label = new JLabel();
			// this.label.setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {

			this.label.setForeground(Color.RED);
			this.label.setBackground(Color.GRAY);
			this.label.setText((value == null) ? "" : value.toString());

			return this.label;
		}
	}
}
