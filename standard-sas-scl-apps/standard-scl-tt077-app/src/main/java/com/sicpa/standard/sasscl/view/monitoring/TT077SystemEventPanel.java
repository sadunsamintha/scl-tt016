package com.sicpa.standard.sasscl.view.monitoring;

import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.swing.DefaultRowSorter;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.sasscl.common.utils.ComparatorUtils;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.tt077.scl.utils.TT077CalendarUtils;

public class TT077SystemEventPanel extends SystemEventPanel {

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

            TT077BasicSystemEventTableCellRenderer renderer = new TT077BasicSystemEventTableCellRenderer();
            this.table.getColumnModel().getColumn(0).setCellRenderer(renderer);
            this.table.getColumnModel().getColumn(1).setCellRenderer(renderer);
            this.table.getColumnModel().getColumn(2).setCellRenderer(renderer);

            ((DefaultRowSorter<?, ?>) this.table.getRowSorter()).setComparator(0, new ComparatorUtils.DateComparator());

            this.table.getRenderers().clear();

            sortByDate();
        }
        return this.table;
    }

    public static class TT077BasicSystemEventTableCellRenderer extends BasicSystemEventTableCellRenderer {
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
            this.dateFormatter.setTimeZone(TimeZone.getTimeZone(TT077CalendarUtils.timeZoneTZ));
        }
    }

}
