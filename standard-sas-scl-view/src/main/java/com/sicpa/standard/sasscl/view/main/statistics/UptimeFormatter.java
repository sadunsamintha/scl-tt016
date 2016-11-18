package com.sicpa.standard.sasscl.view.main.statistics;

import com.sicpa.standard.client.common.i18n.Messages;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UptimeFormatter {

	private final Calendar calendar;
	private final SimpleDateFormat formatter;
	private String prefix;

	public UptimeFormatter(String dateFormat) {
		formatter = new SimpleDateFormat(dateFormat);
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
			return prefix + " " + formatter.format(calendar.getTime());
		}
	}
}