package com.sicpa.standard.sasscl.view.config.misc;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.loggers.ILoggerLevelChangeListener;
import com.sicpa.standard.gui.components.loggers.LogLevelPanel;
import com.sicpa.standard.gui.components.loggers.LoggerLevelChangeEvent;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;

public class MiscConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected LogLevelPanel logLevel;
	protected SwitchLangPanel switchLangPanel;

	public MiscConfigPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("ltr,fill"));
		add(getLogLevel(), "grow,wrap,push");
		add(getSwitchLangPanel(), "grow");
	}

	public LogLevelPanel getLogLevel() {
		if (this.logLevel == null) {
			this.logLevel = new LogLevelPanel();
			this.logLevel.getModel().addPropertyChangeListener(new ILoggerLevelChangeListener() {
				
				@Override
				public void loggerLevelChanged(LoggerLevelChangeEvent evt) {
					OperatorLogger.log("Debug Config Log Level - {} = {}", evt.getLogger().getName(), evt.getLevel());
					
				}
			});
		}
		return this.logLevel;
	}

	public SwitchLangPanel getSwitchLangPanel() {
		if (this.switchLangPanel == null) {
			this.switchLangPanel = new SwitchLangPanel();
		}
		return this.switchLangPanel;
	}
}
