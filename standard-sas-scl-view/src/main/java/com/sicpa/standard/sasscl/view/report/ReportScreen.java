package com.sicpa.standard.sasscl.view.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.time.DateUtils;
import org.divxdede.swing.busy.JBusyComponent;
import org.jdesktop.swingx.JXDatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.monitoring.IProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.monitoring.IProductionStatisticsAggregatorFactory;

import net.miginfocom.swing.MigLayout;

public class ReportScreen extends JPanel implements ISecuredComponentGetter {

	private static Logger logger = LoggerFactory.getLogger(ReportScreen.class);

	private static final long serialVersionUID = 1L;
	protected JPanel mainPanel;
	protected JBusyComponent<JComponent> busyPanel;

	protected JToggleButton buttonDay;
	protected JToggleButton buttonWeek;
	protected JToggleButton buttonMonth;
	protected JToggleButton buttonGroupByProduct;
	protected JToggleButton buttonDailyDetailed;

	protected IReportTable table;
	protected JButton buttonPrint;
	protected JXDatePicker dateTo;
	protected JXDatePicker dateFrom;

	protected IProductionStatisticsAggregatorFactory productionStatisticsAggregatorFactory;

    public ReportScreen() {
        initGUI();
    }

    @Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		removeAll();
		this.busyPanel = null;
		this.mainPanel = null;
		this.buttonDay = null;
		this.buttonWeek = null;
		this.buttonMonth = null;
		this.buttonGroupByProduct = null;
		this.buttonDailyDetailed = null;
		this.buttonPrint = null;
		this.dateFrom = null;
		this.dateTo   = null;
		getMainPanel();
		initGUI();
		revalidate();
	}
    
    public JXDatePicker getDateFrom() {
		if (this.dateFrom == null) {
			this.dateFrom = new JXDatePicker();
			this.dateFrom.setDate(new Date());
			
			this.dateFrom.addPropertyChangeListener("date", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Date selectedDate = dateFrom.getDate();
                    dateTo.getMonthView().setLowerBound(selectedDate);
                    dateTo.setDate(selectedDate);
                }
            });
		}
		return this.dateFrom;
	}

	public JXDatePicker getDateTo() {
		if (this.dateTo == null) {
			this.dateTo = new JXDatePicker();
			this.dateTo.setDate(new Date());
			this.dateTo.getMonthView().setLowerBound(new Date());
		}
		return this.dateTo;
	}

	protected void initGUI() {
		setLayout(new BorderLayout());
		add(getBusyPanel(), BorderLayout.CENTER);
		ButtonGroup grp = new ButtonGroup();
		grp.add(getButtonDay());
		grp.add(getButtonWeek());
		grp.add(getButtonMonth());
	}

	public JToggleButton getButtonDay() {
		if (this.buttonDay == null) {
			this.buttonDay = new ToggleImageAndTextButton(Messages.get("production.report.day"));
			this.buttonDay.addActionListener(e -> buttonDayActionPerformed());
		}
		return this.buttonDay;
	}

	public JToggleButton getButtonDailyDetailed() {
		if (buttonDailyDetailed == null) {
			buttonDailyDetailed = new ToggleImageAndTextButton(Messages.get("production.report.detailed"));
			buttonDailyDetailed.addActionListener(a -> buttonDailyDetailedActionPerformed());
		}
		return buttonDailyDetailed;
	}

	protected void buttonDailyDetailedActionPerformed() {
		buttonDayActionPerformed();
	}

	protected void buttonDayActionPerformed() {
		getButtonDailyDetailed().setEnabled(getButtonGroupByProduct().isSelected());
		startStatisticsRetrieving();
	}

	protected void startStatisticsRetrieving() {
		getBusyPanel().setBusy(true);
		TaskExecutor.execute(() -> {
            try {
                getAndShowReport();
            } catch (Exception e) {
                logger.error("", e);
            }

            SwingUtilities.invokeLater(() -> getBusyPanel().setBusy(false));
        });
	}

	protected void getAndShowReport() {
		if (getDateFrom().getDate() == null || getDateTo().getDate() == null) {
			return;
		}
		
		IProductionStatisticsAggregator aggre = productionStatisticsAggregatorFactory.getInstance();

		Date from = DateUtils.round(getDateFrom().getDate(), Calendar.DAY_OF_MONTH);
		Date to = DateUtils.ceiling(getDateTo().getDate(), Calendar.DAY_OF_MONTH);// go to next day
		to = new Date(to.getTime() - 1);// remove 1 ms to come back

		aggre.aggregate(getProductionStatistics(from, to), getSelectedPeriod(), getButtonGroupByProduct().isSelected(),
				getButtonDailyDetailed().isSelected(), from, to);
		getTable().setData(aggre.getMapData(), getButtonGroupByProduct().isSelected(),
				getButtonDailyDetailed().isSelected());
		getTable().getComponent().setVisible(true);
	}

	protected List<IncrementalStatistics> getProductionStatistics(final Date from, final Date to) {
		return MonitoringService.getIncrementalStatistics(from, to);
	}

	protected ReportPeriod getSelectedPeriod() {
		if (getButtonDay().isSelected()) {
			return ReportPeriod.DAY;
		} else if (getButtonWeek().isSelected()) {
			return ReportPeriod.WEEK;
		} else if (getButtonMonth().isSelected()) {
			return ReportPeriod.MONTH;
		}
		return ReportPeriod.DAY;
	}

	public JToggleButton getButtonMonth() {
		if (this.buttonMonth == null) {
			this.buttonMonth = new ToggleImageAndTextButton(Messages.get("production.report.month"));
			this.buttonMonth.addActionListener(e -> buttonMonthActionPerformed());
		}
		return this.buttonMonth;
	}

	protected void buttonMonthActionPerformed() {
		getButtonDailyDetailed().setEnabled(false);
		getButtonDailyDetailed().setSelected(false);
		startStatisticsRetrieving();
	}

	public JToggleButton getButtonWeek() {
		if (this.buttonWeek == null) {
			this.buttonWeek = new ToggleImageAndTextButton(Messages.get("production.report.week"));
			this.buttonWeek.addActionListener(e -> buttonWeekActionPerformed());
		}
		return this.buttonWeek;
	}

	protected void buttonWeekActionPerformed() {
		getButtonDailyDetailed().setEnabled(false);
		getButtonDailyDetailed().setSelected(false);
		startStatisticsRetrieving();
	}

	public IReportTable getTable() {
		if (table == null) {
            table = new ReportTable();
		}

		return table;
	}

	public JToggleButton getButtonGroupByProduct() {
		if (this.buttonGroupByProduct == null) {
			this.buttonGroupByProduct = new ToggleImageAndTextButton(Messages.get("production.report.group"));
			this.buttonGroupByProduct.addActionListener(e -> buttonGroupByProductActionPerformed());
		}
		return this.buttonGroupByProduct;
	}

	protected void buttonGroupByProductActionPerformed() {

		if (getButtonGroupByProduct().isSelected()) {
			getButtonDailyDetailed().setEnabled(true);
		} else {
			getButtonDailyDetailed().setEnabled(false);
			getButtonDailyDetailed().setSelected(false);
		}

		if (getButtonDay().isSelected()) {
			buttonDayActionPerformed();
		} else if (getButtonWeek().isSelected()) {
			buttonWeekActionPerformed();
		} else if (getButtonMonth().isSelected()) {
			buttonMonthActionPerformed();
		}
	}

	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel();
			this.mainPanel.setLayout(new MigLayout("fill"));
			this.mainPanel.add(new JLabel(Messages.get("label.from")), "span , split 4");
			this.mainPanel.add(getDateFrom());
			this.mainPanel.add(new JLabel(Messages.get("label.to")));
			this.mainPanel.add(getDateTo(), "");
			this.mainPanel.add(getButtonDay(), "grow,w 30%");
			this.mainPanel.add(getButtonWeek(), "grow,w 30%");
			this.mainPanel.add(getButtonMonth(), "grow,w 30%,wrap");
			this.mainPanel.add(getButtonGroupByProduct(), "grow");
			this.mainPanel.add(getButtonDailyDetailed(), "wrap,grow");
			this.mainPanel.add(getTable().getComponent(), "grow , span,push");
			this.mainPanel.add(new PaddedButton(getButtonPrint()), "spanx ,split 2,  right");
		}
		return this.mainPanel;
	}

	public JBusyComponent<JComponent> getBusyPanel() {
		if (this.busyPanel == null) {
			this.busyPanel = new JBusyComponent<>(getMainPanel());
		}
		return this.busyPanel;
	}

	public JButton getButtonPrint() {
		if (buttonPrint == null) {
			buttonPrint = new JButton(Messages.get("production.report.print"));
			buttonPrint.addActionListener(e -> buttonPrintActionPerformed());
		}
		return buttonPrint;
	}

	protected void buttonPrintActionPerformed() {
		getTable().printReport();
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Permission getPermission() {
		return SasSclPermission.PRODUCTION_REPORT;
	}

	@Override
	public String getTitle() {
		return "report.production";
	}

	public void setProductionStatisticsAggregatorFactory(
			IProductionStatisticsAggregatorFactory productionStatisticsAggregatorFactory) {
		this.productionStatisticsAggregatorFactory = productionStatisticsAggregatorFactory;
	}
}
