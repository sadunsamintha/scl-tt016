package com.sicpa.tt016.view.startstop;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.tt016.event.ManualStopEvent;
import com.sicpa.standard.sasscl.view.startstop.StartStopViewController;


public class TT016StartStopViewController extends StartStopViewController {
    @Override
    public void stop() {
        EventBusService.post(new ManualStopEvent());
        super.stop();
    }

}
