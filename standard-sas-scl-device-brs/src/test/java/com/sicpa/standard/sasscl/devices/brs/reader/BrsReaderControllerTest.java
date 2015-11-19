package com.sicpa.standard.sasscl.devices.brs.reader;

import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.brs.model.BrsReaderModel;
import com.sicpa.standard.sasscl.devices.brs.model.BrsType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BrsReaderControllerTest {

    @Mock
    private CodeReaderAdaptor codeReaderAdaptor;

    @Mock
    private CodeReaderListener codeReaderListener;

    private BrsReaderModel brsReaderModel;

    private BrsReaderController brsReaderController;

    @Before
    public void setUp() {
        // do the injection manually
        brsReaderController = new BrsReaderController(codeReaderListener, null);
        brsReaderController.setCodeReader(codeReaderAdaptor);
    }


    @Test
    public void onCodeReceived() {
        String code = "123345";
        //stub
        doNothing().when(codeReaderListener).onCodeReceived(code);

        brsReaderController.onCodeReceived(code);

        //verify
        verify(codeReaderListener, times(1)).onCodeReceived(code);
    }

    @Test
    public void start() throws IOException, DeviceException {
        //stub

        doNothing().when(codeReaderAdaptor).sendEnableReadingCommand();

        brsReaderController.start();

        //verify
        verify(codeReaderAdaptor, times(1)).sendEnableReadingCommand();
    }

    @Test(expected = DeviceException.class)
    public void startWithExeption() throws IOException, DeviceException {
        //stub

        doThrow(new IOException()).when(codeReaderAdaptor).sendEnableReadingCommand();

        brsReaderController.start();

        //verify
        verify(codeReaderAdaptor, times(1)).sendEnableReadingCommand();
    }


    @Test(expected = DeviceException.class)
    public void stopWithExeption() throws IOException, DeviceException {
        //stub

        doThrow(new IOException()).when(codeReaderAdaptor).sendDisableReadingCommand();

        brsReaderController.stop();

        //verify
        verify(codeReaderAdaptor, times(1)).sendDisableReadingCommand();
    }

    @Test
    public void stop() throws IOException, DeviceException {
        //stub

        doNothing().when(codeReaderAdaptor).sendDisableReadingCommand();

        brsReaderController.stop();

        //verify
        verify(codeReaderAdaptor, times(1)).sendDisableReadingCommand();
    }

    @Test
    public void disconnect() throws DeviceException {
        //stub

        doNothing().when(codeReaderAdaptor).stop();

        brsReaderController.disconnect();

        //verify
        verify(codeReaderAdaptor, times(1)).stop();
    }



}
