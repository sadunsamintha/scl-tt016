package com.sicpa.standard.sasscl.devices.brs.reader;

public interface CodeReaderListener {

    /**
     * This method is call when the reader is either connected or disconnected.
     * @param isConnected <code>true</code> if the code reader is connected otherwise <code>false</code>.
     * @param readerId The id of the code reader.
     */
    void onReaderConnected(boolean isConnected, String readerId);

    /**
     * This method is call when the code reader receive a code.
     *
     * @param code The code recieved.
     */
    void onCodeReceived(String code);

}
