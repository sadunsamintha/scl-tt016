package com.sicpa.tt065.event;


import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.security.User;
import com.sicpa.standard.sasscl.model.ProductionParameters;

import java.util.Date;

/**
 * Event to be fired when a manual intervention stops the production
 */
public class BatchIdViewEvent {
    public final User user;
    public final Date date;
    private ProductionParameters pp;

    public BatchIdViewEvent(ProductionParameters pp) {
        this.pp = pp;
        this.user = SecurityService.getCurrentUser();
        this.date = new Date();
    }

    public ProductionParameters getPp() {
        return pp;
    }
}
