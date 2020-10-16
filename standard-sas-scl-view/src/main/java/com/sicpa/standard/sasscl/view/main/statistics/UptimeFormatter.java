package com.sicpa.standard.sasscl.view.main.statistics;

import com.sicpa.standard.client.common.i18n.Messages;

public class UptimeFormatter {

	private String prefix;
	
	private String UPTIME_FORMATTER_PATTERN = "%02dh %02dm %02ds";

	public UptimeFormatter() {
		prefix = Messages.get("statistics.uptime");
	}

	public String format(int seconds) {
		long diffHours = seconds / 3600;
		long diffMinutes = (seconds % 3600) / 60;
		long diffSeconds = seconds % 60;
		
		return prefix + " " + String.format(UPTIME_FORMATTER_PATTERN, diffHours, diffMinutes, diffSeconds);
	}
}