package com.sicpa.standard.sasscl.provider.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.event.ApplicationVersionEvent;

public class AppVersionProvider extends AbstractProvider<String> {

    public AppVersionProvider() {
        super("AppVersion");
    }

    @Subscribe
    public void handleAppVersionEvent(ApplicationVersionEvent event) {
        set(event.getVersion());
    }
}
