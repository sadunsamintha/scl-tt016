package com.sicpa.standard.gui.screen.machine.component.warning;

import java.util.Date;



public class Info extends Message {

	public Info() {
		super();
	}

	public Info(final String code, final String message, final Date time) {
		super(code, message, time);
	}

	public Info(final String code, final String message) {
		super(code, message);
	}

}
