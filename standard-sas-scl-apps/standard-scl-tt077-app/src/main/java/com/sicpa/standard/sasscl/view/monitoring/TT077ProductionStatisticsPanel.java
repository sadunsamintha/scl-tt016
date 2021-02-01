package com.sicpa.standard.sasscl.view.monitoring;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.sasscl.common.utils.ComparatorUtils;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.tt077.scl.utils.TT077CalendarUtils;

public class TT077ProductionStatisticsPanel extends ProductionStatisticsPanel {

    private static final Logger logger = LoggerFactory.getLogger(TT077ProductionStatisticsPanel.class);

    @Override
    @SuppressWarnings("unchecked")
    public BeanReaderJTable<IncrementalStatistics> getTable() {
        if (table == null) {

            table = new BeanReaderJTable<IncrementalStatistics>(new String[] { "startTime", "stopTime",
                "productsStatistics", "errors", "productionParameters" }, new String[] {
                Messages.get("label.start"), Messages.get("label.stop"), Messages.get("label.statistics"),
                Messages.get("label.errors"), Messages.get("label.parameters") }) {

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

            table.getColumnModel().getColumn(0).setPreferredWidth(45);
            table.getColumnModel().getColumn(1).setPreferredWidth(45);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(150);
            table.getColumnModel().getColumn(4).setPreferredWidth(150);

            TT077ProductionStatisticsTableCellRenderer renderer = new TT077ProductionStatisticsTableCellRenderer();
            table.getColumnModel().getColumn(0).setCellRenderer(renderer);
            table.getColumnModel().getColumn(1).setCellRenderer(renderer);
            table.getColumnModel().getColumn(2).setCellRenderer(renderer);
            table.getColumnModel().getColumn(3).setCellRenderer(renderer);
            table.getColumnModel().getColumn(4).setCellRenderer(renderer);

            ((DefaultRowSorter<?, ?>) table.getRowSorter()).setComparator(0, new ComparatorUtils.DateComparator());
            ((DefaultRowSorter<?, ?>) table.getRowSorter()).setComparator(1, new ComparatorUtils.DateComparator());

            table.getRenderers().clear();

            sortByDate();
        }
        return table;
    }

    public static class TT077ProductionStatisticsTableCellRenderer extends ProductionStatisticsTableCellRenderer {
        private static final long serialVersionUID = 1L;
        {
            String pattern = null;
            try {
                pattern = Messages.get("date.pattern.statistics.production");
                this.dateFormatter = new SimpleDateFormat(pattern);
            } catch (Exception e) {
                logger.error("invalid or pattern not found {}", pattern);
                this.dateFormatter = new SimpleDateFormat("'<html>'yyyy-MM-dd'<br>'HH:mm:ss");
            }
            this.dateFormatter.setTimeZone(TimeZone.getTimeZone(TT077CalendarUtils.timeZoneTZ));
        }
    }

}
