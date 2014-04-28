package com.sicpa.standard.sasscl.view.report;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
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

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.time.DateUtils;
import org.divxdede.swing.busy.JBusyComponent;
import org.jdesktop.swingx.JXDatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.statistics.production.ProductionStatistics;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregator;

public class ReportScreen extends JPanel {

	private static Logger logger = LoggerFactory.getLogger(ReportScreen.class);

	private static final long serialVersionUID = 1L;
	protected JPanel mainPanel;
	protected JBusyComponent<JComponent> busyPanel;

	protected JToggleButton buttonDay;
	protected JToggleButton buttonWeek;
	protected JToggleButton buttonMonth;
	protected JToggleButton buttonGroupByProduct;
	protected JToggleButton buttonDailyDetailed;

	protected ReportTable table;
	protected JButton buttonPrint;
	protected JXDatePicker dateTo;
	protected JXDatePicker dateFrom;

	protected String dateFormatForKeyDailyDetailed;
	protected String dateFormatForKeyDay;
	protected String dateFormatForKeyMonth;
	protected String dateFormatForKeyWeek;

	public ReportScreen() {
		initGUI();

		setupDateformat();
	}

	protected void setupDateformat() {
		dateFormatForKeyDailyDetailed = initPattern("yyyy-MM-dd HH:mm:ss", "date.pattern.report.day.detail");
		dateFormatForKeyDay = initPattern("dd/MM/yy", "date.pattern.report.day.normal");
		dateFormatForKeyMonth = initPattern("dd/MM/yy", "date.pattern.report.day.month");
		dateFormatForKeyWeek = initPattern("dd/MM/yy", "date.pattern.report.day.week");
	}

	protected String initPattern(String defaultPattern, String langKey) {
		String pattern = null;
		try {
			pattern = Messages.get(langKey);
			// to test if the pattern is valid
			new SimpleDateFormat(pattern);
			return pattern;
		} catch (Exception e) {
			logger.error("invalid or pattern not found {}", pattern);
			return defaultPattern;
		}
	}

	public JXDatePicker getDateFrom() {
		if (this.dateFrom == null) {
			this.dateFrom = new JXDatePicker();
			this.dateFrom.setDate(new Date());
		}
		return this.dateFrom;
	}

	public JXDatePicker getDateTo() {
		if (this.dateTo == null) {
			this.dateTo = new JXDatePicker();
			this.dateTo.setDate(new Date());
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
			this.buttonDay.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonDayActionPerformed();
				}
			});
		}
		return this.buttonDay;
	}

	public JToggleButton getButtonDailyDetailed() {
		if (buttonDailyDetailed == null) {
			buttonDailyDetailed = new ToggleImageAndTextButton(Messages.get("production.report.detailed"));
			buttonDailyDetailed.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					buttonDailyDetailedActionPerformed();
				}
			});
		}
		return buttonDailyDetailed;
	}

	protected void buttonDailyDetailedActionPerformed() {
		buttonDayActionPerformed();
	}

	protected void buttonDayActionPerformed() {
		getButtonDailyDetailed().setEnabled(getButtonGroupByProduct().isSelected());
		startStatisticsRetreiving();
	}

	protected void startStatisticsRetreiving() {
		getBusyPanel().setBusy(true);
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					getAndShowReport();
				} catch (Exception e) {
					logger.error("", e);
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						getBusyPanel().setBusy(false);
					}
				});
			}
		});
	}

	protected void getAndShowReport() {
		ProductionStatisticsAggregator aggre = new ProductionStatisticsAggregator();
		aggre.setDateFormatForKeyDailyDetailed(dateFormatForKeyDailyDetailed);
		aggre.setDateFormatForKeyDay(dateFormatForKeyDay);
		aggre.setDateFormatForKeyMonth(dateFormatForKeyMonth);
		aggre.setDateFormatForKeyWeek(dateFormatForKeyWeek);

		Date from = DateUtils.round(getDateFrom().getDate(), Calendar.DAY_OF_MONTH);
		Date to = DateUtils.ceiling(getDateTo().getDate(), Calendar.DAY_OF_MONTH);// go to next day
		to = new Date(to.getTime() - 1);// remove 1 ms to come back

		aggre.aggregate(getProductionStatistics(from, to), getSelectedPeriod(), getButtonGroupByProduct().isSelected(),
				getButtonDailyDetailed().isSelected(), from, to);
		getTable().setData(aggre.getMapData(), getButtonGroupByProduct().isSelected(),
				getButtonDailyDetailed().isSelected());
	}

	protected List<ProductionStatistics> getProductionStatistics(final Date from, final Date to) {
		return MonitoringService.getProductionStatistics(from, to);
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
			this.buttonMonth.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonMonthActionPerformed();
				}
			});
		}
		return this.buttonMonth;
	}

	protected void buttonMonthActionPerformed() {
		getButtonDailyDetailed().setEnabled(false);
		getButtonDailyDetailed().setSelected(false);
		startStatisticsRetreiving();
	}

	public JToggleButton getButtonWeek() {
		if (this.buttonWeek == null) {
			this.buttonWeek = new ToggleImageAndTextButton(Messages.get("production.report.week"));
			this.buttonWeek.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonWeekActionPerformed();
				}
			});
		}
		return this.buttonWeek;
	}

	protected void buttonWeekActionPerformed() {
		getButtonDailyDetailed().setEnabled(false);
		getButtonDailyDetailed().setSelected(false);
		startStatisticsRetreiving();
	}

	public ReportTable getTable() {
		if (this.table == null) {
			this.table = new ReportTable();
		}
		return this.table;
	}

	public JToggleButton getButtonGroupByProduct() {
		if (this.buttonGroupByProduct == null) {
			this.buttonGroupByProduct = new ToggleImageAndTextButton(Messages.get("production.report.group"));
			this.buttonGroupByProduct.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonGroupByProductActionPerformed();
				}
			});
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
			this.mainPanel.add(getTable(), "grow , span,push");
			this.mainPanel.add(new PaddedButton(getButtonPrint()), "spanx ,split 2,  right");
		}
		return this.mainPanel;
	}

	public JBusyComponent<JComponent> getBusyPanel() {
		if (this.busyPanel == null) {
			this.busyPanel = new JBusyComponent<JComponent>(getMainPanel());
		}
		return this.busyPanel;
	}

	public JButton getButtonPrint() {
		if (buttonPrint == null) {
			buttonPrint = new JButton(Messages.get("production.report.print"));
			buttonPrint.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonPrintActionPerformed();
				}
			});
		}
		return buttonPrint;
	}

	protected void buttonPrintActionPerformed() {
		getTable().printReport();
	}

	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		removeAll();

		mainPanel = null;
		busyPanel = null;

		buttonDay = null;
		buttonWeek = null;
		buttonMonth = null;
		buttonGroupByProduct = null;
		buttonDailyDetailed = null;

		table = null;
		buttonPrint = null;
		dateTo = null;
		dateFrom = null;

		setupDateformat();

		initGUI();
		revalidate();
	}
}
