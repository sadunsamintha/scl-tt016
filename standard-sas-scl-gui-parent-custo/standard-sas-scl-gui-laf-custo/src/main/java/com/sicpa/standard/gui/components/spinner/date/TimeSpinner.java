package com.sicpa.standard.gui.components.spinner.date;

import java.util.Date;

public class TimeSpinner extends AbstractTimeSpinner {

	private static final long serialVersionUID = 1L;

	public TimeSpinner() {
		addUnit(SpinnerDateUnit.HOUR, ":");
		addUnit(SpinnerDateUnit.MINUTE, ":");
		addUnit(SpinnerDateUnit.SECOND, "");
		setDate(new Date());
	}

}
