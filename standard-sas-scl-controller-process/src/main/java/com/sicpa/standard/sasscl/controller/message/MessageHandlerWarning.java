package com.sicpa.standard.sasscl.controller.message;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.view.event.WarningViewEvent;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

public class MessageHandlerWarning {

	@Subscribe
	public void handleMessage(ActionEventWarning warning) {
		EventBusService.post(new WarningViewEvent(warning.getKey(), null, true, warning.getParams()));
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.WARNING,
				SystemEventType.APPLICATION_MESSAGE, warning.getSource() + ": " + warning.getKey()));
	}
}
