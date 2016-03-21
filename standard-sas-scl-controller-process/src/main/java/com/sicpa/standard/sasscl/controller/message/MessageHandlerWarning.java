package com.sicpa.standard.sasscl.controller.message;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.view.event.WarningViewEvent;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

public class MessageHandlerWarning {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandlerWarning.class);

	@Subscribe
	public void handleMessage(ActionEventWarning warning) {
		logger.info(warning.getKey() + Arrays.toString(warning.getParams()));
		EventBusService.post(new WarningViewEvent(warning.getKey(), null, true, warning.getParams()));
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.WARNING,
				SystemEventType.APPLICATION_MESSAGE, warning.getSource() + ": " + warning.getKey()));
	}
}
