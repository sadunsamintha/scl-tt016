package com.sicpa.standard.sasscl.devices.brs;

public interface BrsReaderListener {

    void onBrsReaderConnected(boolean isConnected, String readerId);

}
