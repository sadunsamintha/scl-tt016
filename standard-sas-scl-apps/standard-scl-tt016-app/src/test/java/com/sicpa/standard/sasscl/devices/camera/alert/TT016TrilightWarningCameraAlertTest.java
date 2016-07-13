package com.sicpa.standard.sasscl.devices.camera.alert;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.IPlcParamSender;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.tt016.devices.camera.alert.TT016TrilightWarningCameraAlert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TT016TrilightWarningCameraAlertTest {

    @Mock
    private IPlcParamSender plcParamSender;

    @Mock
    private IPlcVariable<Integer> reqJavaErrorRegisterVar;

    @Mock
    private PlcProvider plcProvider;

    @Mock
    private IPlcAdaptor plcAdaptor;

    @InjectMocks
    private TT016TrilightWarningCameraAlert alert = new TT016TrilightWarningCameraAlert();

    private final int TRILIGHT_BLINKING_YELLOW = 2;

    @Test
    public void handleActionEventWarning() throws PlcAdaptorException {
        when(plcProvider.get()).thenReturn(plcAdaptor);

        ActionEventWarning warning = new ActionEventWarning();
        warning.setKey(MessageEventKey.Alert.TOO_MANY_CAMERA_ERROR);
        alert.handleActionEventWarning(warning);

        verify(reqJavaErrorRegisterVar, times(1)).setValue(TRILIGHT_BLINKING_YELLOW);
        verify(plcAdaptor, times(1)).write(reqJavaErrorRegisterVar);
    }


    @Test
    public void handleNoActionEventWarning() throws PlcAdaptorException {
        ActionEventWarning warning = new ActionEventWarning();
        warning.setKey(MessageEventKey.Alert.TOO_MANY_CAMERA_WARNING);
        alert.handleActionEventWarning(warning);

        verify(reqJavaErrorRegisterVar, times(0)).setValue(TRILIGHT_BLINKING_YELLOW);
        verify(plcAdaptor, times(0)).write(reqJavaErrorRegisterVar);
    }


}
