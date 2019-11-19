package com.sicpa.standard.gui.components.loggers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.gui.utils.TextUtils;

public class LogLevelPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private LogLevelPanelModel model;

	// all the logger panels
	private List<LoggerPanel> loggerPanels;

	private JCheckBox checkDebugAll;
	private JCheckBox checkInfoAll;
	private JCheckBox checkWarningAll;
	private JCheckBox checkErrorAll;
	private JButton buttonShowAllLogger;

	// the panel that contains all the loggerpanel
	private JPanel panelAllLoggers;

	// list that contains all the log panel not displayed by default
	private Map<String, Pair<LoggerPanel, JComponent>> listLogPanelChildren = new HashMap<String, Pair<LoggerPanel, JComponent>>();
	private Map<String, Pair<LoggerPanel, JComponent>> listLogPanelMain = new HashMap<String, Pair<LoggerPanel, JComponent>>();

	// are all the logger panels shown, by default only those that have a level set are shown
	private boolean AllLoggerShown = false;

	private JButton buttonSave;
	private JButton buttonLoad;

	public LogLevelPanel(final LogLevelPanelModel model) {
		this.model = model;
		this.model.addPropertyChangeListener(new ILoggerLevelChangeListener() {

			@Override
			public void loggerLevelChanged(final LoggerLevelChangeEvent evt) {
				modelLoggerLevelChanged(evt);
			}
		});
		this.loggerPanels = new ArrayList<LoggerPanel>();
		initGUI();
	}

	public LogLevelPanel() {
		this(new LogLevelPanelModel());
	}

	private void modelLoggerLevelChanged(final LoggerLevelChangeEvent evt) {

		Pair<LoggerPanel, JComponent> pair = this.listLogPanelChildren.get(evt.getLogger().getName());

		if (pair == null) {
			pair = this.listLogPanelMain.get(evt.getLogger().getName());
		}

		if (pair != null) {
			pair.getValue1().setLevel(evt.getLevel());
		}

	}

	private void initGUI() {
		setLayout(new MigLayout("ltr,fill,hidemode 3"));
		add(new JLabel("Log level"), "span ,split 2");
		add(new JSeparator(), "growx");

		JComponent comp = SmallScrollBar.createLayerSmallScrollBar(new JScrollPane(getPanelAllLoggers()));
		comp.setBorder(new LineBorder(new Color(0, 31, 55, 55)));
		add(comp, "span,grow,wrap,push");

		add(new JLabel("Change level for all loggers"));

		this.checkDebugAll = createCheckBox("DEBUG", Level.DEBUG);
		this.checkInfoAll = createCheckBox("INFO", Level.INFO);
		this.checkWarningAll = createCheckBox("WARN", Level.WARN);
		this.checkErrorAll = createCheckBox("ERROR", Level.ERROR);

		add(this.checkDebugAll, "spanx,split 4");
		add(this.checkInfoAll);
		add(this.checkWarningAll);
		add(this.checkErrorAll);

		add(getButtonShowAllLogger(), "spanx , split 3");
		add(getButtonLoad());
		add(getButtonSave());
	}

	public JPanel getPanelAllLoggers() {
		if (this.panelAllLoggers == null) {
			this.panelAllLoggers = new JPanel();
			this.panelAllLoggers.setLayout(new MigLayout("ltr,hidemode 3"));

			Map<String, Logger> loggerNames = new TreeMap<String, Logger>();
			for (Logger logger : getModel().getLoggers()) {
				loggerNames.put(logger.getName(), logger);
			}

			for (Logger log : loggerNames.values()) {
				LoggerPanel p = new LoggerPanel(log);
				JLabel label = new JLabel();
				if (log.getLevel() == null) {
					this.listLogPanelChildren.put(log.getName(), new Pair<LoggerPanel, JComponent>(p, label));
					p.setVisible(false);
					label.setVisible(false);
				} else {
					this.listLogPanelMain.put(log.getName(), new Pair<LoggerPanel, JComponent>(p, label));
				}

				this.loggerPanels.add(p);

				label.setFont(label.getFont().deriveFont(12f));
				String text = TextUtils.getWrappedHTLMText(log.getName().replaceAll("\\.", " "), 250, label.getFont());
				label.setText(text);
				this.panelAllLoggers.add(label);
				this.panelAllLoggers.add(p, "grow,wrap");
			}
		}
		return this.panelAllLoggers;
	}

	public JButton getButtonShowAllLogger() {
		if (this.buttonShowAllLogger == null) {
			this.buttonShowAllLogger = new JButton("Show all loggers");
			this.buttonShowAllLogger.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonShowAllLoggerActionPerformed();
				}
			});
		}
		return this.buttonShowAllLogger;
	}

	private void buttonShowAllLoggerActionPerformed() {
		if (this.AllLoggerShown) {
			getButtonShowAllLogger().setText("Show all loggers");
		} else {
			getButtonShowAllLogger().setText("Only show main loggers");
		}

		this.AllLoggerShown = !this.AllLoggerShown;
		for (Pair<LoggerPanel, JComponent> pair : this.listLogPanelChildren.values()) {
			pair.getValue1().setVisible(this.AllLoggerShown);
			pair.getValue2().setVisible(this.AllLoggerShown);
		}
	}

	private JCheckBox createCheckBox(final String text, final Level level) {
		final JCheckBox check = new JCheckBox(text);
		check.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				checkboxActionPerformed(check, level);
			}
		});
		initCheckWithLevel(check, level);
		return check;
	}

	private void checkboxActionPerformed(final JCheckBox check, final Level level) {

		if (check.isSelected()) {
			this.checkDebugAll.setSelected(this.checkDebugAll == check);
			this.checkInfoAll.setSelected(this.checkInfoAll == check);
			this.checkWarningAll.setSelected(this.checkWarningAll == check);
			this.checkErrorAll.setSelected(this.checkErrorAll == check);
		}

		for (LoggerPanel panel : this.loggerPanels) {
			panel.setLevel(level);
		}
	}

	private class LoggerPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		Logger logger;
		JCheckBox checkDebug;
		JCheckBox checkInfo;
		JCheckBox checkWarning;
		JCheckBox checkError;

		public LoggerPanel(final Logger log) {
			this.logger = log;
			initGUI();
		}

		private void initGUI() {
			setLayout(new MigLayout("ltr,fill,inset 0 0 0 0"));

			Level currentLevel = this.logger.getLevel();
			this.checkDebug = createCheckBox("DEBUG", Level.DEBUG, currentLevel);
			this.checkInfo = createCheckBox("INFO", Level.INFO, currentLevel);
			this.checkWarning = createCheckBox("WARN", Level.WARN, currentLevel);
			this.checkError = createCheckBox("ERROR", Level.ERROR, currentLevel);

			add(this.checkDebug);
			add(this.checkInfo);
			add(this.checkWarning);
			add(this.checkError);
		}

		ButtonGroup grp = new ButtonGroup();

		JCheckBox createCheckBox(final String text, final Level level, final Level currentLevel) {
			final JCheckBox check = new JCheckBox(text);
			if (currentLevel != null) {
				check.setSelected(level.levelInt == currentLevel.levelInt);
			}
			check.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					checkboxActionPerformed(check, level, true);
				}
			});
			this.grp.add(check);
			initCheckWithLevel(check, level);
			return check;
		}

		void checkboxActionPerformed(final JCheckBox check, final Level level, final boolean userClick) {

			if (check.isSelected()) {
				getModel().setLevel(this.logger, level);
			}

			if (userClick) {
				LogLevelPanel.this.checkDebugAll.setSelected(false);
				LogLevelPanel.this.checkInfoAll.setSelected(false);
				LogLevelPanel.this.checkWarningAll.setSelected(false);
				LogLevelPanel.this.checkErrorAll.setSelected(false);
			}
		}

		private void setLevel(final Level level) {
			this.checkDebug.setSelected(level.levelInt == Level.DEBUG_INT);
			this.checkInfo.setSelected(level.levelInt == Level.INFO_INT);
			this.checkWarning.setSelected(level.levelInt == Level.WARN_INT);
			this.checkError.setSelected(level.levelInt == Level.ERROR_INT);

			checkboxActionPerformed(this.checkDebug, Level.DEBUG, false);
			checkboxActionPerformed(this.checkInfo, Level.INFO, false);
			checkboxActionPerformed(this.checkWarning, Level.WARN, false);
			checkboxActionPerformed(this.checkError, Level.ERROR, false);
		}
	}

	public static void initCheckWithLevel(final JCheckBox check, final Level level) {
		Color c = null;
		if (Level.DEBUG_INT == level.levelInt) {
			c = SicpaColor.BLUE_MEDIUM;
		} else if (Level.INFO_INT == level.levelInt) {
			c = SicpaColor.GREEN_LIGHT;
		} else if (Level.WARN_INT == level.levelInt) {
			c = SicpaColor.ORANGE;
		} else if (Level.ERROR_INT == level.levelInt) {
			c = SicpaColor.RED;
		}
		check.setForeground(c);
	}

	private JFileChooser chooser;

	public JFileChooser getChooser() {
		if (this.chooser == null) {
			this.chooser = new JFileChooser();
			this.chooser.setMultiSelectionEnabled(false);
			this.chooser.setSelectedFile(new File(".").getAbsoluteFile());
		}
		return this.chooser;
	}

	private void saveConfig() {
		if (getChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = getChooser().getSelectedFile();
			LogLevelUtils.saveConfig(file);
		}
	}

	public JButton getButtonSave() {
		if (this.buttonSave == null) {
			this.buttonSave = new JButton("Save");
			this.buttonSave.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonSaveActionPerformed();
				}
			});
		}
		return this.buttonSave;
	}

	private void buttonSaveActionPerformed() {
		saveConfig();
	}

	public JButton getButtonLoad() {
		if (this.buttonLoad == null) {
			this.buttonLoad = new JButton("Load");
			this.buttonLoad.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonLoadActionPerformed();
				}
			});
		}
		return this.buttonLoad;
	}

	private void buttonLoadActionPerformed() {
		if (getChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = getChooser().getSelectedFile();
			LogLevelUtils.loadConfig(this.model, file);
		}
	}

	public LogLevelPanelModel getModel() {
		return this.model;
	}
}
