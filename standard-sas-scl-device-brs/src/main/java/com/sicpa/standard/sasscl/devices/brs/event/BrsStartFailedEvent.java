package com.sicpa.standard.sasscl.devices.brs.event;


public class BrsStartFailedEvent {

    private String message;

    public BrsStartFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
