package com.sicpa.standard.sasscl.controller.message;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.view.event.ErrorViewEvent;
import com.sicpa.standard.sasscl.messages.ActionEventError;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

public class MessageHandlerError {

	protected IFlowControl flowControl;

	@Subscribe
	public void handleMessage(ActionEventError error) {
		EventBusService.post(new ErrorViewEvent(error.getKey(), null, true, error.getParams()));
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.ERROR,
				SystemEventType.APPLICATION_MESSAGE, error.getSource() + ": " + error.getKey()));

		flowControl.notifyStopProduction();
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}
}
