package com.sicpa.standard.gui.screen.machine.impl.SPL.stats;

import java.util.EventListener;

public interface StatisticsChangeListener extends EventListener {
	
	void validChanged(StatisticsChangeEvent evt);
	void invalidChanged(StatisticsChangeEvent evt);
	void lineSpeedChanged(StatisticsChangeEvent evt);

}
