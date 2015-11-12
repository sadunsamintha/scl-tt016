package com.sicpa.standard.sasscl.devices.printer.event;


import com.sicpa.standard.sasscl.devices.printer.IPrinterAdaptor;

/**
 * Event notifiying that the printer has been stopped.
 */
public class PrinterStoppedEvent {

    private IPrinterAdaptor source;


    public PrinterStoppedEvent(){
    }

    public PrinterStoppedEvent(IPrinterAdaptor source){
        this.source = source;
    }

    public IPrinterAdaptor getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "PrinterStoppedEvent{" +
                "source=" + source +
                '}';
    }
}
