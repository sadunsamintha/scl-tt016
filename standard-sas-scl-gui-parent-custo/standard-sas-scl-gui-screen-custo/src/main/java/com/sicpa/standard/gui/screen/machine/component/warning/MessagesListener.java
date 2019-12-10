package com.sicpa.standard.gui.screen.machine.component.warning;

import java.util.EventListener;

import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEvent;
import com.sicpa.standard.gui.screen.machine.impl.SPL.stats.MessageEventUpdate;

public interface MessagesListener extends EventListener {

	void messageAdded(MessageEvent evt);

	void messageRemoved(MessageEvent evt);
	
	void reseted();
	
	void messageUpdated(MessageEventUpdate evt);

}
