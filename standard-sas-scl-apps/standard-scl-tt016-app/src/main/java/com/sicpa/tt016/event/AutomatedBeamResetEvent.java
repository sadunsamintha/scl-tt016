package com.sicpa.tt016.event;

import java.util.Date;

public class AutomatedBeamResetEvent {
    public final String message;
    public final Date date;
    public AutomatedBeamResetEvent() {
        this.message = "Beam Reset By Operator to SKU Height";
        this.date = new Date();
    }
}
