package com.sicpa.standard.gui.screen.machine.impl.SPL.systemInfo;

import com.sicpa.standard.gui.model.BasicMapLikeModel;
import com.sicpa.standard.gui.utils.TextUtils;

public class SystemInfoModel extends BasicMapLikeModel {

	public static final String PROPERTY_VERSION = "version";
	public static final String PROPERTY_DATE_FORMAT = "dateFormat";

	public SystemInfoModel() {
		addAvailableProperty(PROPERTY_VERSION, PROPERTY_DATE_FORMAT);
		setProperty(PROPERTY_VERSION, "unknown version");
		setProperty(PROPERTY_DATE_FORMAT, "yyyy/MM/dd HH:mm:ss");
	}

	public String getVersion() {
		return TextUtils.objectToString(getAllProperties().get(PROPERTY_VERSION));
	}

	public void setVersion(final String version) {
		setProperty(PROPERTY_VERSION, version);
	}

	public void setDateFormat(final String dateFormat) {
		setProperty(PROPERTY_DATE_FORMAT, dateFormat);
	}

	public String getDateFormat() {
		return TextUtils.objectToString(getAllProperties().get(PROPERTY_DATE_FORMAT));
	}
}
