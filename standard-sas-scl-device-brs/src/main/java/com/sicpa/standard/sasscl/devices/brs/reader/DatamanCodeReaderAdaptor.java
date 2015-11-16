package com.sicpa.standard.sasscl.devices.brs.reader;


import com.sicpa.common.device.reader.CodeReader;
import com.sicpa.common.device.reader.brs.commands.BrsCommands;

import java.io.IOException;

public class DatamanCodeReaderAdaptor extends AbstractCodeReaderAdaptor  {

    public DatamanCodeReaderAdaptor(CodeReader codeReader, String id) {
        super(codeReader, id);

    }

    @Override
    public void sendEnableReadingCommand() throws IOException {
        codeReader.sendData(BrsCommands.ENABLE_READING);
    }

    @Override
    public void sendDisableReadingCommand() throws IOException {
        codeReader.sendData(BrsCommands.DISABLE_READING);
    }

    @Override
    public String getId() {
        return codeReader.toString();
    }
}
