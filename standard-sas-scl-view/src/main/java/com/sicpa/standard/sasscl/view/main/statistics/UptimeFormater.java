package com.sicpa.standard.sasscl.view.main.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sicpa.standard.common.util.Messages;

public class UptimeFormater {

	private final Calendar calendar;
	private final SimpleDateFormat formater;
	private String prefix;

	public UptimeFormater(String dateFormat) {
		formater = new SimpleDateFormat(dateFormat);
		calendar = Calendar.getInstance();
		prefix = Messages.get("statistics.uptime");
	}

	private void initCalendar() {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	}

	public String format(int seconds) {
		synchronized (calendar) {
			initCalendar();
			calendar.set(Calendar.SECOND, seconds);
			return prefix + " " + formater.format(calendar.getTime());
		}
	}
}
