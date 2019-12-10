package com.sicpa.standard.gui.components.loggers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LogLevelPanelModel {

	protected List<ILoggerLevelChangeListener> listeners;

	public LogLevelPanelModel() {
		this.listeners = new ArrayList<ILoggerLevelChangeListener>();
	}

	public List<Logger> getLoggers() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		return loggerContext.getLoggerList();
	}

	public void addPropertyChangeListener(final ILoggerLevelChangeListener lis) {
		this.listeners.add(lis);
	}

	public void removePropertyChangeListener(final ILoggerLevelChangeListener lis) {
		this.listeners.remove(lis);
	}

	protected void firePropertyChange(final Logger logger, final Level level) {
		LoggerLevelChangeEvent evt = new LoggerLevelChangeEvent(logger, level);
		for (ILoggerLevelChangeListener l : this.listeners) {
			l.loggerLevelChanged(evt);
		}
	}

	public void setLevel(final Logger logger, final Level level) {
		if (level != null && (logger.getLevel() == null || level.levelInt != logger.getLevel().levelInt)) {
			logger.setLevel(level);
			firePropertyChange(logger, level);
		}
	}
}
