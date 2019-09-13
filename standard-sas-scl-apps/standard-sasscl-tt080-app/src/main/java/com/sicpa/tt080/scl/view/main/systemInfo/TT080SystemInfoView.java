package com.sicpa.tt080.scl.view.main.systemInfo;

import com.sicpa.standard.gui.components.label.DateTimeLabel;
import com.sicpa.standard.sasscl.view.main.systemInfo.SystemInfoView;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TT080SystemInfoView extends SystemInfoView {

	@Override
	public DateTimeLabel getLabelDateValue() {
		if (labelDateValue == null) {
			labelDateValue = new DateTimeLabel();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			sd.setTimeZone(TimeZone.getTimeZone("America/Santiago"));
			labelDateValue.setDateFormat(sd);
		}
		return labelDateValue;
	}

	@Override
    protected void updateDateFormat() {
        getLabelDateValue();
    }
}
