package com.sicpa.standard.sasscl.view.monitoring;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.utils.ComparatorUtils;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class SystemEventPanel extends AbstractMonitoringEventPanel implements ISecuredComponentGetter {

	private static final long serialVersionUID = 1L;

	public static final Logger logger = LoggerFactory.getLogger(SystemEventPanel.class);

	public SystemEventPanel() {
		initGUI();
	}

	@Override
	@SuppressWarnings("unchecked")
	public BeanReaderJTable<BasicSystemEvent> getTable() {
		if (this.table == null) {
			this.table = new BeanReaderJTable<BasicSystemEvent>(new String[] { "date", "type", "message" },
					new String[] { Messages.get("label.date"), Messages.get("label.type"),
							Messages.get("label.message") }) {

				private static final long serialVersionUID = 1L;

				@Override
				public boolean contains(final int x, final int y) {
					return false;
				}

				@Override
				public boolean contains(final Point p) {
					return false;
				}
			};

			this.table.getColumnModel().getColumn(0).setPreferredWidth(85);
			this.table.getColumnModel().getColumn(1).setPreferredWidth(80);
			this.table.getColumnModel().getColumn(2).setPreferredWidth(420);

			BasicSystemEventTableCellRenderer renderer = new BasicSystemEventTableCellRenderer();
			this.table.getColumnModel().getColumn(0).setCellRenderer(renderer);
			this.table.getColumnModel().getColumn(1).setCellRenderer(renderer);
			this.table.getColumnModel().getColumn(2).setCellRenderer(renderer);

			((DefaultRowSorter<?, ?>) this.table.getRowSorter()).setComparator(0, new ComparatorUtils.DateComparator());

			this.table.getRenderers().clear();

			sortByDate();
		}
		return this.table;
	}

	@Override
	protected void requestAndHandleEvents(final Date from, final Date to) {
		final List<BasicSystemEvent> list = getSystemEvents(from, to);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTable().addRow(list);
			}
		});
	}

	protected List<BasicSystemEvent> getSystemEvents(final Date from, final Date to) {
		return MonitoringService.getSystemEventList(from, to);
	}

	public static class BasicSystemEventTableCellRenderer extends SicpaTableCellRenderer {

		protected SimpleDateFormat dateFormatter;
		private static final long serialVersionUID = 1L;

		{
			String pattern = null;
			try {
				pattern = Messages.get("date.pattern.system.event");
				this.dateFormatter = new SimpleDateFormat(pattern);
			} catch (Exception e) {
				logger.error("invalid or pattern not found {}", pattern);
				this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {

			BeanReaderJTable<BasicSystemEvent> t = (BeanReaderJTable<BasicSystemEvent>) table;

			JLabel label = (JLabel) super
					.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setFont(label.getFont().deriveFont(10f));

			if (value instanceof Date) {
				label.setText(this.dateFormatter.format((Date) value));
			}

			BasicSystemEvent data = t.getObjectAtRow(row);
			if (data != null && data.getType() != null) {
				if (data.getLevel().equals(SystemEventLevel.ERROR)) {
					label.setForeground(Color.RED);
				} else if (data.getLevel().equals(SystemEventLevel.WARNING)) {
					label.setForeground(Color.ORANGE.darker());
				}
			}
			return label;
		}
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Permission getPermission() {
		return SasSclPermission.MONITORING_SYSTEM_EVENT;
	}

	@Override
	public String getTitle() {
		return "label.monitoring.system.event";
	}
}
