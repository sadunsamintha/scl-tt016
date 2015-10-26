package com.sicpa.standard.sasscl.devices.brs.reader;


import com.sicpa.common.device.reader.CodeReader;
import com.sicpa.common.device.reader.brs.commands.BrsCommands;
import com.sicpa.common.device.reader.sick.commands.SickCommands;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CodeReaderAdaptorTest {


    @Mock private CodeReader codeReader;

    private CodeReaderAdaptor datamanAdaptor;

    private CodeReaderAdaptor sickAdaptor;

    @Before
    public void setUp() {
        datamanAdaptor = new DatamanCodeReaderAdaptor(codeReader);
        sickAdaptor = new SickCodeReaderAdaptor(codeReader);

    }

    @Test
    public void disableReadingDataman() throws IOException {
        doNothing().when(codeReader).sendData(BrsCommands.DISABLE_READING);
        datamanAdaptor.sendDisableReadingCommand();
        verify(codeReader, times(1)).sendData(BrsCommands.DISABLE_READING);
    }

    @Test
    public void enableReadingDataman() throws IOException {
        doNothing().when(codeReader).sendData(BrsCommands.ENABLE_READING);
        datamanAdaptor.sendEnableReadingCommand();
        verify(codeReader, times(1)).sendData(BrsCommands.ENABLE_READING);
    }


    @Test(expected = IOException.class)
    public void throwIOExceptionWhenEnableReadingDataman() throws IOException {
        doThrow(new IOException()).when(codeReader).sendData(BrsCommands.ENABLE_READING);
        datamanAdaptor.sendEnableReadingCommand();
        verify(codeReader, times(1)).sendData(BrsCommands.ENABLE_READING);
    }

    @Test
    public void disableReadingSick() throws IOException {
        doNothing().when(codeReader).sendData(SickCommands.DISABLE_READING);
        sickAdaptor.sendDisableReadingCommand();
        verify(codeReader, times(1)).sendData(SickCommands.DISABLE_READING);
    }

    @Test
    public void enableReadingSick() throws IOException {
        doNothing().when(codeReader).sendData(SickCommands.ENABLE_READING);
        sickAdaptor.sendEnableReadingCommand();
        verify(codeReader, times(1)).sendData(SickCommands.ENABLE_READING);
    }


    @Test(expected = IOException.class)
    public void throwIOExceptionWhenEnableReadingSick() throws IOException {
        doThrow(new IOException()).when(codeReader).sendData(SickCommands.ENABLE_READING);
        sickAdaptor.sendEnableReadingCommand();
        verify(codeReader, times(1)).sendData(SickCommands.ENABLE_READING);
    }



}
