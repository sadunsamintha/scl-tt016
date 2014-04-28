package com.sicpa.standard.sasscl.view.monitoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.time.DateUtils;
import org.divxdede.swing.busy.JBusyComponent;
import org.jdesktop.swingx.JXDatePicker;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

public abstract class AbstractMonitoringEventPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	protected BeanReaderJTable table;
	protected JXDatePicker dateFrom;
	protected JXDatePicker dateTo;
	protected JButton buttonRefresh;
	protected JPanel mainPanel;
	protected JBusyComponent<JComponent> busyPanel;
	protected JScrollPane scroll;

	public AbstractMonitoringEventPanel() {
	}

	protected void initGUI() {
		setLayout(new MigLayout("ltr,fill,inset 5 5 0 0 "));
		add(getBusyPanel(), "grow");
	}

	@SuppressWarnings("rawtypes")
	public abstract BeanReaderJTable getTable();

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

	public JButton getButtonRefresh() {
		if (this.buttonRefresh == null) {
			this.buttonRefresh = new JButton(Messages.get("label.refresh"));
			this.buttonRefresh.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonRefreshActionPerformed();
				}
			});

		}
		return this.buttonRefresh;
	}

	protected void buttonRefreshActionPerformed() {
		getBusyPanel().setBusy(true);
		getTable().clear();
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {

				Date from = DateUtils.round(getDateFrom().getDate(), Calendar.DAY_OF_MONTH);
				Date to = DateUtils.ceiling(getDateTo().getDate(), Calendar.DAY_OF_MONTH);

				requestAndHandleEvents(from, to);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						getBusyPanel().setBusy(false);
					}
				});
			}
		});
	}

	protected abstract void requestAndHandleEvents(Date from, Date to);

	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel();
			this.mainPanel.setLayout(new MigLayout("ltr,fill,inset 0 0 0 0 "));
			JLabel labelFrom = new JLabel(Messages.get("label.from"));
			labelFrom.setName("label.from");
			JLabel labelTo = new JLabel(Messages.get("label.to"));
			labelTo.setName("label.to");
			this.mainPanel.add(labelFrom, "span , split 4");
			this.mainPanel.add(getDateFrom());
			this.mainPanel.add(labelTo);
			this.mainPanel.add(getDateTo(), "");
			this.mainPanel.add(getButtonRefresh(), "wrap");
			this.mainPanel.add(SmallScrollBar.createLayerSmallScrollBar(getScroll()), "grow,push,span");
		}
		return this.mainPanel;
	}

	public JBusyComponent<JComponent> getBusyPanel() {
		if (this.busyPanel == null) {
			this.busyPanel = new JBusyComponent<JComponent>(getMainPanel());
		}
		return this.busyPanel;
	}

	protected void sortByDate() {
		this.table.getRowSorter().toggleSortOrder(0);
		this.table.getRowSorter().toggleSortOrder(0);
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getTable());
		}
		return this.scroll;
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		removeAll();

		table = null;
		dateFrom = null;
		dateTo = null;
		buttonRefresh = null;
		mainPanel = null;
		busyPanel = null;
		scroll = null;

		initGUI();
		revalidate();
	}
}
