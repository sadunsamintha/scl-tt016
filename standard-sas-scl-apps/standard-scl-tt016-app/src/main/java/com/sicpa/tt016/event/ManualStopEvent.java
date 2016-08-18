package com.sicpa.tt016.event;


import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.security.User;

import java.util.Date;

/**
 * Event to be fired when a manual intervention stops the production
 */
public class ManualStopEvent {
    public final User user;
    public final Date date;
    public ManualStopEvent() {
        this.user = SecurityService.getCurrentUser();
        this.date = new Date();
    }
}
