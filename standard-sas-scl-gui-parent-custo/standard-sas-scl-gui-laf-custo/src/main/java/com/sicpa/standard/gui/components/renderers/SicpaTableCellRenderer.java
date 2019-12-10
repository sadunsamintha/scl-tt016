package com.sicpa.standard.gui.components.renderers;

import java.awt.Component;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.sicpa.standard.gui.I18n.GUIi18nManager;

public class SicpaTableCellRenderer extends SubstanceDefaultTableCellRenderer {

	public static final String I18N_DATE_FORMATTER = GUIi18nManager.SUFFIX+".renderers.table.formater.date";
	public static final String I18N_TIME_FORMATTER = GUIi18nManager.SUFFIX+".renderers.table.formater.time";
	public static final String I18N_DATE_TIME_FORMATTER = GUIi18nManager.SUFFIX+".renderers.table.formater.datetime";

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		SicpaTableCellRenderer res = (SicpaTableCellRenderer) super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		CellRenderersUtils.applyStripping(res, table, row, isSelected);
		return res;
	}

	public static class BooleanRenderer extends SubstanceDefaultTableCellRenderer.BooleanRenderer {

		public BooleanRenderer() {
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			SicpaTableCellRenderer.BooleanRenderer res = (SicpaTableCellRenderer.BooleanRenderer) super
					.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			res.setFont(table.getFont());
			CellRenderersUtils.prepareRendererForCheckBox(this, table, row, isSelected);
			return this;
		}
	}

	public static class BooleanEditor extends DefaultCellEditor {
		public BooleanEditor() {
			super(new JCheckBox());
			JCheckBox checkBox = (JCheckBox) getComponent();
			checkBox.setHorizontalAlignment(JCheckBox.CENTER);
		}

		@Override
		public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
				final int row, final int column) {
			JComponent res = (JComponent) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			CellRenderersUtils.prepareRendererForCheckBox(res, table, row, isSelected);
			return res;
		}
	}

	public static class IconRenderer extends SicpaTableCellRenderer {
		/**
		 * Creates a new renderer for icon columns.
		 */
		public IconRenderer() {
			super();
			this.setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public void setValue(final Object value) {
			this.setIcon((value instanceof Icon) ? (Icon) value : null);
			this.setText(null);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int col) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			CellRenderersUtils.applyStripping(res, table, row, isSelected);
			return res;
		}
	}

	/**
	 * Renderer for number columns.
	 * 
	 */
	public static class NumberRenderer extends SicpaTableCellRenderer {
		/**
		 * Creates a new renderer for number columns.
		 */
		public NumberRenderer() {
			super();
			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int col) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			CellRenderersUtils.applyStripping(res, table, row, isSelected);
			return res;
		}
	}

	/**
	 * Renderer for double columns.
	 * 
	 */
	public static class DoubleRenderer extends NumberRenderer {
		/**
		 * Number formatter for this renderer.
		 */
		NumberFormat formatter;

		/**
		 * Creates a new renderer for double columns.
		 */
		public DoubleRenderer() {
			super();
		}

		@Override
		public void setValue(final Object value) {
			if (this.formatter == null) {
				this.formatter = NumberFormat.getInstance();
			}
			this.setText((value == null) ? "" : this.formatter.format(value));
		}
	}

	public static abstract class AbstractDateRenderer extends SicpaTableCellRenderer {
		public AbstractDateRenderer() {
			super();
		}

		public abstract DateFormat getFormatter();

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int col) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			if (value instanceof Date) {
				String sdate = getFormatter().format((Date) value);
				((JLabel) res).setText(sdate);
			}
			CellRenderersUtils.applyStripping(res, table, row, isSelected);
			return res;
		}
	}

	public static class TimeRenderer extends AbstractDateRenderer {
		private DateFormat formatter;

		@Override
		public DateFormat getFormatter() {
			if (this.formatter == null) {
				this.formatter= new SimpleDateFormat(GUIi18nManager.get(SicpaTableCellRenderer.I18N_TIME_FORMATTER));
			}
			return this.formatter;
		}
	}

	public static class DateTimeRenderer extends AbstractDateRenderer {
		private DateFormat formatter;

		@Override
		public DateFormat getFormatter() {
			if (this.formatter == null) {
				this.formatter= new SimpleDateFormat(GUIi18nManager.get(SicpaTableCellRenderer.I18N_DATE_TIME_FORMATTER));
			}
			return this.formatter;
		}
	}

	public static class DateRenderer extends AbstractDateRenderer {
		private DateFormat formatter;

		@Override
		public DateFormat getFormatter() {
			if (this.formatter == null) {
				this.formatter= new SimpleDateFormat(GUIi18nManager.get(SicpaTableCellRenderer.I18N_DATE_FORMATTER));
			}
			return this.formatter;
		}
	}

}
