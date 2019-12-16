package com.sicpa.standard.gui.components.loggers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class LoggerLevelChangeEvent {

	private Logger logger;
	private Level level;

	public LoggerLevelChangeEvent(final Logger logger, final Level level) {
		super();
		this.logger = logger;
		this.level = level;
	}

	public Logger getLogger() {
		return this.logger;
	}

	public Level getLevel() {
		return this.level;
	}

}
