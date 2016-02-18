package com.sicpa.standard.sasscl.devices.brs.event;


import java.util.List;

public class BrsWrongBarcodeEvent {

    private final List<String> barcodesExpected;

    private final String barcodeRead;

    public BrsWrongBarcodeEvent(List<String> barcodesExpected,  String barcodeRead) {
        this.barcodesExpected = barcodesExpected;
        this.barcodeRead = barcodeRead;
    }

    public List<String> getBarcodesExpected() {
        return barcodesExpected;
    }

    public String getBarcodeRead() {
        return barcodeRead;
    }

}
