package com.sicpa.standard.sasscl.devices.brs.reader;


import com.sicpa.common.device.reader.CodeReader;

import java.io.IOException;

public interface CodeReaderAdaptor extends CodeReader {

    /**
     * Send an enable reading command to BRS device.
     *
     * @throws IOException if some other I/O error occurs
     */
    void sendEnableReadingCommand() throws IOException;

    /**
     * Send an disable reading command to BRS device.
     *
     * @throws IOException if some other I/O error occurs
     */
    void sendDisableReadingCommand() throws IOException;
}
