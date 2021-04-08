package com.sicpa.ttth.view.main.systemInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.sicpa.standard.gui.components.label.DateTimeLabel;
import com.sicpa.standard.sasscl.view.main.systemInfo.SystemInfoView;

public class TTTHSystemInfoView extends SystemInfoView {

	@Override
	public DateTimeLabel getLabelDateValue() {
		if (labelDateValue == null) {
			labelDateValue = new DateTimeLabel();
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sd.setTimeZone(TimeZone.getTimeZone("GMT+7"));
			labelDateValue.setDateFormat(sd);
			Calendar thaiCalendar = Calendar.getInstance();
			thaiCalendar.add(Calendar.YEAR, 543);
			labelDateValue.setCalendar(thaiCalendar);
		}
		return labelDateValue;
	}

	@Override
    protected void updateDateFormat() {
        getLabelDateValue();
    }
}
