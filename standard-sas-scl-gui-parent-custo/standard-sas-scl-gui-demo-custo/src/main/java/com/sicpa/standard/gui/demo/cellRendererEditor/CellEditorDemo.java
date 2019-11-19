package com.sicpa.standard.gui.demo.cellRendererEditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class CellEditorDemo extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				CellEditorDemo inst = new CellEditorDemo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JTable table;
	private DefaultTableModel dtm;

	public CellEditorDemo() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("fill"));
		getContentPane().add(new JScrollPane(getTable()), "grow");

		setSize(600, 300);
	}

	public JTable getTable() {
		if (this.table == null) {
			this.dtm = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(final int row, final int column) {
					return true;
				}
			};
			this.dtm.addColumn("col 1");
			this.dtm.addColumn("multiple of col1");
			this.table = new JTable(this.dtm);

			ComboEditorColumn1 editor1 = new ComboEditorColumn1();
			CellEditorListener l = new CellEditorListener() {// What s happening
																// when the user
																// select a
																// value in the
																// editor in the
																// column 1
				@Override
				public void editingStopped(final ChangeEvent e) {
					int row = ((ComboEditorColumn1) e.getSource()).row;
					int value = (Integer) ((ComboEditorColumn1) e.getSource()).getCellEditorValue();

					int multiple = (Integer) CellEditorDemo.this.dtm.getValueAt(row, 1);
					if (value == 0) {
						CellEditorDemo.this.dtm.setValueAt(0, row, 1);
						CellEditorDemo.this.table.repaint();
					} else if (multiple % value != 0) {
						CellEditorDemo.this.dtm.setValueAt(value * 2, row, 1);
						CellEditorDemo.this.table.repaint();
					}
				}

				@Override
				public void editingCanceled(final ChangeEvent e) {
				}
			};

			editor1.addCellEditorListener(l);

			this.table.getColumnModel().getColumn(1).setCellEditor(new ComboEditorColumn2());
			this.table.getColumnModel().getColumn(0).setCellEditor(editor1);

			for (int i = 0; i < 20; i++) {
				this.dtm.addRow(new Object[] { i, i * 2 });
			}
		}
		return this.table;
	}

	// Editor that show the multiple of the selected value in the column 1
	private static class ComboEditorColumn2 extends AbstractCellEditor implements TableCellEditor {
		private JComboBox combo;

		public ComboEditorColumn2() {
			this.combo = new JComboBox();
			this.combo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					stopCellEditing();
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
				final int row, final int column) {
			// Setup the editor here
			this.combo.removeAllItems();
			for (int i = 0; i < 10; i++) {
				this.combo.addItem(i * ((Integer) table.getValueAt(row, column - 1)));
			}

			this.combo.setSelectedItem(value);
			return this.combo;
		}

		@Override
		public Object getCellEditorValue() {
			return this.combo.getSelectedItem();
		}
	}

	// editor for the column 1 , allow to choose from 0 to 20
	private static class ComboEditorColumn1 extends AbstractCellEditor implements TableCellEditor {
		private JComboBox combo;
		private int row;

		public ComboEditorColumn1() {
			this.combo = new JComboBox();
			for (int i = 0; i < 20; i++) {
				this.combo.addItem(i);
			}

			this.combo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					stopCellEditing();
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return this.combo.getSelectedItem();
		}

		@Override
		public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
				final int row, final int column) {
			// setup the editor here
			this.row = row;
			this.combo.setSelectedItem(value);
			return this.combo;
		}
	}
}
