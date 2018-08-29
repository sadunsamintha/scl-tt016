package com.sicpa.tt016.devices.camera.alert;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.IPlcParamSender;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.messages.ActionEventWarning;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class TT016TrilightWarningCameraAlert {

    private static final Logger logger = LoggerFactory.getLogger(TT016TrilightWarningCameraAlert.class);

    private IPlcParamSender plcParamSender;

    private IPlcVariable<Integer> reqJavaErrorRegisterVar;

    private PlcProvider plcProvider;

    private final int TRILIGHT_BLINKING_YELLOW = 2, RESET_JAVA_WARNING = 0;


    @Subscribe
    public void handleActionEventWarning(ActionEventWarning warning) {
        if (warning.getKey().equals(MessageEventKey.Alert.TOO_MANY_CAMERA_WARNING)) {
            logger.debug("Process too many bad codes warning event {} with params {}.", warning.getKey(), Arrays.toString(warning.getParams()));
            activateTrilightWarning();
        }
    }

    @Subscribe
	public void handleActionEventWarningReset(ApplicationFlowStateChangedEvent event) {
		if (event.getPreviousState().equals(ApplicationFlowState.STT_STARTING)) {
			deActivateTrilightWarning();
		}
	}
    
    private void activateTrilightWarning() {
        try {
            reqJavaErrorRegisterVar.setValue(TRILIGHT_BLINKING_YELLOW);
            plcProvider.get().write(reqJavaErrorRegisterVar);
        } catch (PlcAdaptorException ex) {
            logger.error("Error sending plc variable value trilight warning ", ex.getMessage());
        }
    }
    
    private void deActivateTrilightWarning() {
    	try {
            reqJavaErrorRegisterVar.setValue(RESET_JAVA_WARNING);
            plcProvider.get().write(reqJavaErrorRegisterVar);
        } catch (PlcAdaptorException ex) {
            logger.error("Error sending plc variable value trilight warning ", ex.getMessage());
        }
    }

    // SETTERS
    public void setPlcParamSender(IPlcParamSender plcParamSender) {
        this.plcParamSender = plcParamSender;
    }

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setReqJavaErrorRegisterVar(IPlcVariable<Integer> reqJavaErrorRegisterVar) {
        this.reqJavaErrorRegisterVar = reqJavaErrorRegisterVar;
    }


}

