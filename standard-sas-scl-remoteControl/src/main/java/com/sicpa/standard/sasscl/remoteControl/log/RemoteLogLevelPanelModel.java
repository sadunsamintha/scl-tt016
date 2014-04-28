package com.sicpa.standard.sasscl.remoteControl.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.sicpa.standard.gui.components.loggers.LogLevelPanelModel;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;

public class RemoteLogLevelPanelModel extends LogLevelPanelModel {

	protected List<Logger> loggers;
	protected RemoteControlSasMBean controlBean;

	public RemoteLogLevelPanelModel(final RemoteControlSasMBean controlBean) {
		this.loggers = new ArrayList<Logger>();
		this.controlBean = controlBean;
	}

	@Override
	public List<Logger> getLoggers() {
		return this.loggers;
	}

	public void setLoggersListName(final Map<String, Level> mapLogger) {
		for (Entry<String, Level> entry : mapLogger.entrySet()) {
			Logger logger = (Logger) LoggerFactory.getLogger(entry.getKey());
			logger.setLevel(entry.getValue());
			this.loggers.add(logger);
		}
	}

	@Override
	public void setLevel(final Logger logger, final Level level) {
		if (level != null && (logger.getLevel() == null || level.levelInt != logger.getLevel().levelInt)) {
			logger.setLevel(level);
			firePropertyChange(logger, level);
			this.controlBean.setLoggerLevel(logger.getName(), level);
		}
	}
}
