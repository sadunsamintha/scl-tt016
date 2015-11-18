package com.sicpa.standard.sasscl.devices.brs.reader;


import com.sicpa.common.device.reader.CodeReader;
import com.sicpa.common.device.reader.sick.commands.SickCommands;

import java.io.IOException;

public class SickCodeReaderAdaptor extends AbstractCodeReaderAdaptor implements CodeReaderAdaptor {

    public SickCodeReaderAdaptor(CodeReader codeReader) {
        super(codeReader);

    }

    @Override
    public void sendEnableReadingCommand() throws IOException {
        codeReader.sendData(SickCommands.ENABLE_READING);

    }

    @Override
    public void sendDisableReadingCommand() throws IOException {
        codeReader.sendData(SickCommands.DISABLE_READING);

    }
}

