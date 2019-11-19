package com.sicpa.standard.gui.components.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;

import com.sicpa.standard.gui.components.renderers.CellRenderersUtils;
import com.sicpa.standard.gui.components.renderers.IconTableCellRenderer;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.utils.ReflectionUtils;

public class BeanReaderJTable<T> extends JTable {

	protected static final long serialVersionUID = 1L;

	protected GenericTableModel model;
	protected String[] fields;
	protected String[] title;

	// to know if a row has been already added or not.
	// during the first call to addRow,
	protected boolean first;

	protected Map<Class<?>, TableCellRenderer> renderers;

	public BeanReaderJTable(final String[] fields) {
		this(fields, fields);
	}

	public BeanReaderJTable(final String[] fields, final String[] title) {
		if (fields.length != title.length) {
			throw new IllegalArgumentException("field length must match title length: " + fields.length + "!="
					+ title.length);
		}

		this.first = true;
		this.fields = fields;
		this.title = title;

		initRenderers();

		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null && !fields[i].isEmpty()) {
				String tmpFull = "";
				for (String field : fields[i].split("\\.")) {
					String tmpfield = String.valueOf(field.charAt(0)).toUpperCase();
					if (field.length() > 1) {
						tmpfield += field.substring(1);
					}
					tmpFull += tmpfield + ".";
				}
				tmpFull = tmpFull.substring(0, tmpFull.length() - 1);
				fields[i] = tmpFull;
			}
		}
		setModel(getModel());
		setAutoCreateRowSorter(true);
	}

	protected void initRenderers() {
		TableCellRenderer booleanRenderer = new SicpaTableCellRenderer.BooleanRenderer();
		getRenderers().put(boolean.class, booleanRenderer);
		getRenderers().put(Boolean.class, booleanRenderer);

		TableCellRenderer colorRenderer = new ColorTableCellRenderer();
		getRenderers().put(Color.class, colorRenderer);
		getRenderers().put(ColorUIResource.class, colorRenderer);

		IconTableCellRenderer iconRenderer = new IconTableCellRenderer();
		getRenderers().put(BufferedImage.class, iconRenderer);
		getRenderers().put(ImageIcon.class, iconRenderer);

		SicpaTableCellRenderer.AbstractDateRenderer dateRenderer = new SicpaTableCellRenderer.DateTimeRenderer();
		getRenderers().put(Date.class, dateRenderer);
		getRenderers().put(java.sql.Date.class, dateRenderer);
		getRenderers().put(java.sql.Timestamp.class, dateRenderer);

	}

	public Map<Class<?>, TableCellRenderer> getRenderers() {
		if (this.renderers == null) {
			this.renderers = new HashMap<Class<?>, TableCellRenderer>();
		}
		return this.renderers;
	}

	@Override
	public GenericTableModel getModel() {
		if (this.model == null) {
			this.model = new GenericTableModel();
		}
		return this.model;
	}

	public void clear() {
		this.model.clear();
	}

	public void replace(final int row, final T t) {
		getModel().replace(convertRowIndexToModel(row), t);
	}

	public void addRow(final T t) {
		this.model.addRow(t);
		initRendererAtFirstObject();

	}

	protected void initRendererAtFirstObject() {
		if (this.first&&model.getRowCount()>0) {
			this.first = false;
			for (int i = 0; i < this.fields.length; i++) {
				if (this.fields[i] != null && !this.fields[i].equals("")) {

					Object value = model.getValueAt(0, i);
					if (value != null) {
						Class<?> returnType = value.getClass();

						TableCellRenderer renderer = getRenderers().get(returnType);

						if (renderer != null) {
							getColumnModel().getColumn(i).setCellRenderer(renderer);
						}
					}
				}
			}
		}
	}

	public void addRow(final Collection<T> list) {
		model.addRow(list);
		initRendererAtFirstObject();
	}

	public void addRow(final T[] list) {
		model.addRow(list);
		initRendererAtFirstObject();
	}

	public void removeRow(final T t) {
		getModel().removeRow(t);
	}

	public T getObjectAtRow(final int row) {
		int modelIndex = convertRowIndexToModel(row);
		return getModel().getObjects().get(modelIndex);
	}

	public T getSelectedObject() {
		int[] rows = getSelectedRows();
		if (rows != null && rows.length > 0) {
			return getObjectAtRow(getSelectedRows()[0]);
		} else {
			return null;
		}
	}

	public List<T> getAllObjects() {
		return getModel().getObjects();
	}

	@SuppressWarnings("unchecked")
	public T[] getSelectedObjects() {
		int[] rows = getSelectedRows();
		if (rows != null && rows.length > 0) {
			int[] selected = getSelectedRows();
			Object[] tab = new Object[selected.length];
			for (int i = 0; i < selected.length; i++) {
				tab[i] = getObjectAtRow(selected[i]);
			}
			return (T[]) tab;
		} else {
			return (T[]) new Object[] {};
		}
	}

	protected ReflectionUtils relfecUtils = new ReflectionUtils();

	public class GenericTableModel extends AbstractTableModel {
		protected static final long serialVersionUID = 1L;
		protected ArrayList<T> objects;

		protected ArrayList<T> getObjects() {
			if (this.objects == null) {
				this.objects = new ArrayList<T>();
			}
			return this.objects;
		}

		protected void addRow(final T t) {
			getObjects().add(t);
			fireTableDataChanged();
		}

		protected void addRow(final T[] ts) {
			for (T t : ts) {
				getObjects().add(t);
			}
			fireTableDataChanged();
		}

		protected void addRow(Collection<T> ts) {
			for (T t : ts) {
				getObjects().add(t);
			}
			fireTableDataChanged();
		}

		protected void clear() {
			getObjects().clear();
			fireTableDataChanged();
		}

		protected void removeRow(final T t) {
			getObjects().remove(t);
			fireTableDataChanged();
		}

		protected void replace(final int row, final T t) {
			getObjects().remove(row);
			getObjects().add(row, t);
		}

		@Override
		public String getColumnName(final int column) {
			return BeanReaderJTable.this.title[column];
		}

		@Override
		public boolean isCellEditable(final int row, final int column) {
			return BeanReaderJTable.this.fields[column] == null || BeanReaderJTable.this.fields[column] == "";
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			try {
				return BeanReaderJTable.this.relfecUtils.getValue(BeanReaderJTable.this.fields[column],
						this.objects.get(row));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public int getColumnCount() {
			if (BeanReaderJTable.this.fields != null) {
				return BeanReaderJTable.this.fields.length;
			} else {
				return 0;
			}
		}

		@Override
		public int getRowCount() {
			return getObjects().size();
		}
	}

	public static class ColorTableCellRenderer extends SubstanceDefaultTableCellRenderer// DefaultTableCellRenderer
	{
		protected static final long serialVersionUID = 1L;
		JLabel label = new JLabel();
		Color c;
		JPanel panel = new JPanel() {
			protected static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(final Graphics g) {
				super.paintComponent(g);

				g.setColor(ColorTableCellRenderer.this.c);
				g.fillRect(5, 5, getWidth() - 10, getHeight() - 10);
			}
		};

		public ColorTableCellRenderer() {
			this.panel.setLayout(new MigLayout(""));
			this.panel.add(this.label, "gap left 5");
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			this.c = ((Color) value);
			this.label.setText(this.c.getRed() + "," + this.c.getGreen() + "," + this.c.getBlue());
			this.label.setForeground(SubstanceColorUtilities.invertColor(this.c));
			CellRenderersUtils.applyStripping(this.panel, table, row, isSelected);
			return this.panel;
		}
	}
}
