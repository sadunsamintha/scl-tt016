package com.sicpa.standard.sasscl.view.encoder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

@SuppressWarnings("serial")
public class EncodersInfoView extends JPanel implements ISecuredComponentGetter {

	private static final Logger logger = LoggerFactory.getLogger(EncodersInfoView.class);
	private static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private IStorage storage;

	private JButton buttonRefreshNow;
	private JCheckBox autoRefresh;
	private JSpinner refreshTime;
	private QuarantineEncoderView quarantineEncoderView;
	private JPanel tablePanel;
	private BeanReaderJTable<EncoderInfo> table;
	private Timer timer = new Timer(1000, e -> timerTick());

	public EncodersInfoView() {
		initGUI();
		setupDateFormatter();
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		removeAll();
		buttonRefreshNow = null;
		autoRefresh = null;
		refreshTime = null;
		quarantineEncoderView = null;
		tablePanel = null;
		table = null;
		setupDateFormatter();
		initGUI();
		revalidate();
	}

	private void setupDateFormatter() {
		try {
			dateFormater = new SimpleDateFormat(Messages.get("encoderview.datepattern"));
		} catch (Exception e) {
			logger.error("invalid or missing pattern for encoderview.datepattern");
		}
	}

	public QuarantineEncoderView getQuarantineEncoderView() {
		if (quarantineEncoderView == null) {
			quarantineEncoderView = new QuarantineEncoderView();
		}
		return quarantineEncoderView;
	}

	public BeanReaderJTable<EncoderInfo> getTable() {
		if (table == null) {
			table = new BeanReaderJTable<EncoderInfo>(new String[] { "encoderId", "codeTypeId", "sequence",
					"firstCodeDate", "lastCodeDate", "finished" }, new String[] {
					Messages.get("encoderview.encoderId"), Messages.get("encoderview.codeTypeId"),
					Messages.get("encoderview.sequence"), Messages.get("encoderview.firstCodeDate"),
					Messages.get("encoderview.lastCodeDate"), Messages.get("encoderview.finished") });
			table.setFont(SicpaFont.getFont(10));
			table.getTableHeader().setFont(SicpaFont.getFont(12));

			table.getColumnModel().getColumn(3).setCellRenderer(renderer);
			table.getColumnModel().getColumn(4).setCellRenderer(renderer);
		}
		return table;
	}

	private TableCellRenderer renderer = new SicpaTableCellRenderer() {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			JLabel res = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value != null) {
				try {
					res.setText(dateFormater.format(value));
				} catch (Exception e) {
					System.out.println(value + " => " + value.getClass());
				}
			}
			return res;
		}
	};

	public void start() {
		timer.start();
		getAutoRefresh().setSelected(true);
	}

	public void stop() {
		timer.stop();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getButtonRefreshNow(), "spanx , right, pushx ,split 3");
		add(getAutoRefresh());
		add(getRefreshTime(), "wrap,h 35!");

		add(new JLabel(Messages.get("encoderview.quarantine")), "span,split 2");
		add(new JSeparator(), "growx");

		JScrollPane scroll = new JScrollPane(getQuarantineEncoderView());
		add(SmallScrollBar.createLayerSmallScrollBar(scroll), "grow,span,h 300");

		add(new JLabel(Messages.get("encoderview.info")), "span,split 2");
		add(new JSeparator(), "growx");

		add(getTablePanel(), "grow,push,span");
	}

	public JButton getButtonRefreshNow() {
		if (buttonRefreshNow == null) {
			buttonRefreshNow = new JButton(Messages.get("encoderview.button.refresh"));
			buttonRefreshNow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					OperatorLogger.log("Encoder View Auto Refresh Clicked");
					refresh();
				}
			});
		}
		return buttonRefreshNow;
	}

	private void updateEncoderView(List<EncoderInfo> encoders, String type) {
		getTable().clear();
		getTable().addRow(encoders.toArray(new EncoderInfo[encoders.size()]));
	}

	private void refresh() {
		updateEncoderView(storage.getAllEndodersInfo(), "typetodefine");
		getQuarantineEncoderView().refresh();
		revalidate();
		repaint();
	}

	int tickcounter = 0;

	private void timerTick() {
		tickcounter++;
		if (tickcounter >= (Integer) refreshTime.getValue()) {
			if (isShowing()) {
				refresh();
			}
			tickcounter = 0;
		}
	}

	public JCheckBox getAutoRefresh() {
		if (autoRefresh == null) {
			autoRefresh = new JCheckBox(Messages.get("encoderview.autorefresh"));
			// autoRefresh.setSelected(true);
			autoRefresh.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					OperatorLogger.log("Encoder View Auto Refresh - selected = {}", getAutoRefresh().isSelected());
					if (getAutoRefresh().isSelected()) {
						timer.start();
					} else {
						timer.stop();
					}
				}
			});
		}
		return autoRefresh;
	}

	public JSpinner getRefreshTime() {
		if (refreshTime == null) {
			refreshTime = new JSpinner(new SpinnerNumberModel(5, 1, 999, 1));
			refreshTime.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					OperatorLogger.log("Encoder View Auto Refresh - time = {}", refreshTime.getValue());
				}
			});
		}
		return refreshTime;
	}

	public JPanel getTablePanel() {
		if (tablePanel == null) {
			tablePanel = new JPanel(new BorderLayout());
			tablePanel.add(SmallScrollBar.createLayerSmallScrollBar(new JScrollPane(getTable())));
		}
		return tablePanel;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}



	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Permission getPermission() {
		return SasSclPermission.DISPLAY_ENCODERS_VIEW;
	}

	@Override
	public String getTitle() {
		return "view.encoder.title";
	}
}
