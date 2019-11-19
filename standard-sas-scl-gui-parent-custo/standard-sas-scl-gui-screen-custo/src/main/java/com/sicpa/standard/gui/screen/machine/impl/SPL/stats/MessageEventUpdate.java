package com.sicpa.standard.gui.screen.machine.impl.SPL.stats;

import com.sicpa.standard.gui.screen.machine.component.warning.Message;

public class MessageEventUpdate extends MessageEvent {

	private int index;

	public MessageEventUpdate(final Message message, int index) {
		super(message);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
