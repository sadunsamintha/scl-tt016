package com.sicpa.standard.gui.screen.machine.impl.SPL.stats;

import com.sicpa.standard.gui.model.BasicMapLikeModel;
import com.sicpa.standard.gui.utils.TextUtils;

public class StatisticsModel extends BasicMapLikeModel {

	public static final String PROPERTY_VALID = "valid";
	public static final String PROPERTY_INVALID = "invalid";
	public static final String PROPERTY_LINESPEED = "linespeed";
	public static final String PROPERTY_CODEFREQ = "codefreq";
	public static final String PROPERTY_UPTIME = "uptime";

	public StatisticsModel() {
		addAvailableProperty(PROPERTY_INVALID);
		addAvailableProperty(PROPERTY_LINESPEED);
		addAvailableProperty(PROPERTY_VALID);
		addAvailableProperty(PROPERTY_CODEFREQ);
		addAvailableProperty(PROPERTY_UPTIME);
	}

	public int getValid() {
		String s = TextUtils.objectToString(getAllProperties().get(PROPERTY_VALID));
		if (s != null && s.length() > 0) {
			return Integer.parseInt(s);
		} else {
			return 0;
		}
	}

	public void setValid(final int valid) {
		setProperty(PROPERTY_VALID, valid + "");
	}

	public int getInvalid() {
		String s = TextUtils.objectToString(getAllProperties().get(PROPERTY_INVALID));
		if (s != null && s.length() > 0) {
			return Integer.parseInt(s);
		} else {
			return 0;
		}
	}

	public void setInvalid(final int invalid) {
		setProperty(PROPERTY_INVALID, invalid + "");
	}

	public void setCodeFreq(final int freq) {
		setProperty(PROPERTY_CODEFREQ, freq + "");
	}

	public void startUptime() {
		setProperty(PROPERTY_UPTIME, "start");
	}

	public void stopUptime() {
		setProperty(PROPERTY_UPTIME, "stop");
	}

	public int getTotal() {
		return getValid() + getInvalid();
	}

	public int getLineSpeed() {
		String s = TextUtils.objectToString(getAllProperties().get(PROPERTY_LINESPEED));
		if (s != null && s.length() > 0) {
			return Integer.parseInt(s);
		} else {
			return 0;
		}
	}

	public int getCodeFreq() {
		String s = TextUtils.objectToString(getAllProperties().get(PROPERTY_CODEFREQ));
		if (s != null && s.length() > 0) {
			return Integer.parseInt(s);
		} else {
			return 0;
		}
	}

	public boolean isUptimeRunning() {
		String s = TextUtils.objectToString(getAllProperties().get(PROPERTY_UPTIME));
		if (s != null && s.length() > 0) {
			return Boolean.parseBoolean(s);
		} else {
			return false;
		}
	}

	public void setLineSpeed(final int lineSpeed) {
		setProperty(PROPERTY_LINESPEED, lineSpeed + "");
	}

	public float getPercentValid() {
		return (float) getValid() / (getTotal()) * 100;
	}

	public float getPercentInvalid() {
		return (float) getInvalid() / (getTotal()) * 100;
	}

	public void addInvalid() {
		addInvalid(1);
	}

	public void addInvalid(final long number) {
		setProperty(PROPERTY_INVALID, (getInvalid() + number) + "");
	}

	public void addValidCodes(final long number) {
		setProperty(PROPERTY_VALID, (getValid() + number) + "");
	}

	public void addValid() {
		addValidCodes(1);
	}

	public void reset() {
		setInvalid(0);
		setValid(0);
		setLineSpeed(0);
	}
}
