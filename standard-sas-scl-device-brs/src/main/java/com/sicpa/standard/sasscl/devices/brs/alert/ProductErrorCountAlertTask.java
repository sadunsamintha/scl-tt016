package com.sicpa.standard.sasscl.devices.brs.alert;

import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class ProductErrorCountAlertTask extends AbstractBrsProductCountAlertTask  {

    private int unreadBarcodesErrorThreshold;

    private static final String ALERT_NAME = "Brs Product Error Count Alert";


    @Override
    public MessageEvent getAlertMessage() {
        return new MessageEvent(MessageEventKey.BRS.BRS_TOO_MANY_UNREAD_BARCODES_ERROR);
    }

    @Override
    public int getUnreadBarcodesThreshold() {
        return unreadBarcodesErrorThreshold;
    }

    @Override
    public String getAlertName() {
        return ALERT_NAME;
    }

    public int getUnreadBarcodesErrorThreshold() {
        return unreadBarcodesErrorThreshold;
    }

    public void setUnreadBarcodesErrorThreshold(int unreadBarcodesErrorThreshold) {
        this.unreadBarcodesErrorThreshold = unreadBarcodesErrorThreshold;
    }

}
