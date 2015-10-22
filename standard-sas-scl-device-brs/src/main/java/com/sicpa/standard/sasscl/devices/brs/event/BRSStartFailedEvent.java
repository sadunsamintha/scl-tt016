package com.sicpa.standard.sasscl.devices.brs.event;


public class BRSStartFailedEvent {

    private String message;

    public BRSStartFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
