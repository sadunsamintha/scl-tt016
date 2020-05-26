package com.sicpa.tt065.view.main.systemInfo;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.label.DateTimeLabel;
import com.sicpa.standard.sasscl.view.main.systemInfo.SystemInfoView;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TT065SystemInfoView extends SystemInfoView {

	@Override
	public DateTimeLabel getLabelDateValue() {
		if (labelDateValue == null) {
			labelDateValue = new DateTimeLabel();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			sd.setTimeZone(TimeZone.getTimeZone("America/Guayaquil"));
			labelDateValue.setDateFormat(sd);
		}
		return labelDateValue;
	}

	@Override
    protected void updateDateFormat() {
        getLabelDateValue();
    }
}
