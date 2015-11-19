package com.sicpa.standard.sasscl.devices.brs.reader;


import com.sicpa.common.device.reader.CodeReader;

import java.io.IOException;

public interface CodeReaderAdaptor extends CodeReader {

    void sendEnableReadingCommand() throws IOException;

    void sendDisableReadingCommand() throws IOException;

}
