package com.sicpa.standard.gui.screen.machine.component.warning;

import java.util.Date;



public class Error extends Message {

	public Error() {
		super();
	}

	public Error(final String code, final String message, final Date time) {
		super(code, message, time);
		setRemoveable(false);
	}

	public Error(final String code, final String message) {
		super(code, message);
		setRemoveable(false);
	}

}
