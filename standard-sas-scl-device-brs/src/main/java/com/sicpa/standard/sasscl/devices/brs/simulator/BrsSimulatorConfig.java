package com.sicpa.standard.sasscl.devices.brs.simulator;


import java.io.Serializable;

public class BrsSimulatorConfig implements Serializable {

    private static final long serialVersionUID = -2432103731183714506L;

    /**
     * if the value is set to true then the BrsAdaptorSimulator will receive wrong barcodes not maching any barcodes
     * from the SKU selected
     **/
    private boolean sendWrongBarcode = false;

    /**
     * if the value is set to true then the BrsAdaptorSimulator will not receive some barcodes reaching
     * the unread barcodes thresholds.
     **/
    private boolean reachUnreadBarcodesThresholds = false;




    public boolean isReachUnreadBarcodesThresholds() {
        return reachUnreadBarcodesThresholds;
    }

    public void setReachUnreadBarcodesThresholds(boolean reachUnreadBarcodesThresholds) {
        this.reachUnreadBarcodesThresholds = reachUnreadBarcodesThresholds;
    }


    public boolean isSendWrongBarcode() {
        return sendWrongBarcode;
    }

    public void setSendWrongBarcode(boolean sendWrongBarcode) {
        this.sendWrongBarcode = sendWrongBarcode;
    }


}
