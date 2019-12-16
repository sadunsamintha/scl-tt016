package com.sicpa.standard.gui.screen.machine.impl.SPL.stats;

import com.sicpa.standard.gui.screen.machine.component.warning.Message;

public class MessageEvent {

	private Message message;
	
	public MessageEvent(final Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return this.message;
	}

}
