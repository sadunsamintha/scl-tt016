package com.sicpa.standard.gui.screen.machine.component.warning;

import java.util.Date;



public class Warning extends Message {

	public Warning() {
		super();
	}

	public Warning(final String code, final String message, final Date time) {
		super(code, message, time);
	}

	public Warning(final String code, final String message, final boolean removeable) {
		super(code, message, removeable);
	}

	public Warning(final String code, final String message) {
		super(code, message);
	}

}
