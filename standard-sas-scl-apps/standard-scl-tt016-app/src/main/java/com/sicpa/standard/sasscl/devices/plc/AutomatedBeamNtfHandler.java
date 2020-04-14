package com.sicpa.standard.sasscl.devices.plc;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.device.plc.IPLCVariableMappping;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.messages.IssueSolvedMessage;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.LINE_INDEX_PLACEHOLDER;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_ADJUST_HEIGHT;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_AWAITING_RESET;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_ERROR_STATE;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_HEAD_TO_HOME;
import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.AUTOMATED_BEAM_SAFETY_SENSOR_TRIG;

public class AutomatedBeamNtfHandler {
    private static final Logger logger = LoggerFactory.getLogger(AutomatedBeamNtfHandler.class);

    private PlcProvider plcProvider;
    private IPLCVariableMappping plcVariableMap;

    private PlcBeamAlertDetectedNotificationListener alertDetectedNotificationListener = new PlcBeamAlertDetectedNotificationListener();

    private final static List<IPlcListener> eventContainer = new ArrayList<>();

    public void setPlcProvider(PlcProvider plcProvider) {
        this.plcProvider = plcProvider;
    }

    public void setPlcVariableMap(IPLCVariableMappping plcVariableMap) {
        this.plcVariableMap = plcVariableMap;
    }

    public void init() {
        logger.info("Initializing...");

        plcProvider.addChangeListener(evt -> {
            List<IPlcVariable<Boolean>> beamResetDetectedNtfVars =
                createNotificationVars(plcVariableMap
                    .getPhysicalVariableName(AutomatedBeamPlcEnums.REQUEST_SAFETY_SENSOR_TRIG.toString()));

            List<IPlcVariable<Boolean>> beamAdjustingHeightNtfVars =
                createNotificationVars(plcVariableMap
                    .getPhysicalVariableName(AutomatedBeamPlcEnums.REQUEST_BEAM_ADJUSTING_HEIGHT.toString()));

            List<IPlcVariable<Boolean>> beamErrorStateNtfVars =
                createNotificationVars(plcVariableMap
                    .getPhysicalVariableName(AutomatedBeamPlcEnums.REQUEST_ERROR_STATE_TRIG.toString()));

            registerNtfVarsToListeners(beamResetDetectedNtfVars, alertDetectedNotificationListener);
            registerNtfVarsToListeners(beamAdjustingHeightNtfVars, alertDetectedNotificationListener);
            registerNtfVarsToListeners(beamErrorStateNtfVars, alertDetectedNotificationListener);

            logger.info("Successfully registered >>>> " + alertDetectedNotificationListener);

        });
    }

    private List<IPlcVariable<Boolean>> createNotificationVars(String plcNtfVars) {
        List<IPlcVariable<Boolean>> notificationVars = new ArrayList<>();
        for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
            notificationVars.add(PlcVariable
                .createBooleanVar(plcNtfVars
                    .replace(LINE_INDEX_PLACEHOLDER, lineIndex.toString())));
        }

        return notificationVars;
    }

    private void registerNtfVarsToListeners(List<IPlcVariable<Boolean>> ntfVars,
                                            PlcBeamAlertDetectedNotificationListener listener) {
        ntfVars.forEach((var) -> {
            plcProvider.get().registerNotification(var);
            plcProvider.get().addPlcListener(listener);
        });
    }

    private class PlcBeamAlertDetectedNotificationListener implements IPlcListener {

        private boolean isHeadingHome = false;
        private boolean isSafetySensorAlertTriggered = false;
        private boolean isResetNeeded = false;

        @Override
        public synchronized void onPlcEvent(PlcEvent event) {
            for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
                String adjustingHeight = AutomatedBeamPlcEnums.REQUEST_BEAM_ADJUSTING_HEIGHT
                    .getNameOnPlc().replace(LINE_INDEX_PLACEHOLDER, lineIndex.toString());
                String safetySensorTrig = AutomatedBeamPlcEnums.REQUEST_SAFETY_SENSOR_TRIG
                    .getNameOnPlc().replace(LINE_INDEX_PLACEHOLDER, lineIndex.toString());
                String errorStateTrig = AutomatedBeamPlcEnums.REQUEST_ERROR_STATE_TRIG
                    .getNameOnPlc().replace(LINE_INDEX_PLACEHOLDER, lineIndex.toString());

                if (adjustingHeight.equals(event.getVarName())) {
                    handleHeightAdjustmentNtf((Boolean) event.getValue());
                } else if (safetySensorTrig.equals(event.getVarName())) {
                    handleSafetySensorTrigNtf((Boolean) event.getValue());
                } else if (errorStateTrig.equals(event.getVarName())) {
                    handleErrorStateNtf((Boolean) event.getValue());
                }
            }
        }

        private synchronized void handleHeightAdjustmentNtf(boolean isAdjusting) {
            if (isAdjusting) {
                if (!eventContainer.contains(this)) {
                    eventContainer.add(this);

                    if (isSafetySensorAlertTriggered) {
                        isSafetySensorAlertTriggered = false;
                    } else {
                        EventBusService.post(new MessageEvent(plcProvider.get(), AUTOMATED_BEAM_ADJUST_HEIGHT));
                    }
                }
            } else {
                if (isHeadingHome) {
                    isHeadingHome = false;
                    isResetNeeded = true;
                    EventBusService.post(new MessageEvent(plcProvider.get(), AUTOMATED_BEAM_AWAITING_RESET));
                } else {
                    isResetNeeded = false;
                    EventBusService.post(new IssueSolvedMessage(AUTOMATED_BEAM_SAFETY_SENSOR_TRIG, plcProvider.get()));
                    EventBusService.post(new IssueSolvedMessage(AUTOMATED_BEAM_ADJUST_HEIGHT, plcProvider.get()));
                }
            }
        }

        private synchronized void handleSafetySensorTrigNtf(boolean isTriggered) {
            if (isTriggered) {
                if (!eventContainer.contains(this)) {
                    eventContainer.add(this);
                    if (!isSafetySensorAlertTriggered && !isResetNeeded) {
                        isSafetySensorAlertTriggered = true;
                        isHeadingHome = true;
                        EventBusService.post(new MessageEvent(plcProvider.get(), AUTOMATED_BEAM_SAFETY_SENSOR_TRIG));
                        EventBusService.post(new MessageEvent(plcProvider.get(), AUTOMATED_BEAM_HEAD_TO_HOME));
                    }
                }
            } else {
                eventContainer.remove(this);
            }
        }

        private synchronized void handleErrorStateNtf(boolean isErrorState) {
            if (isErrorState) {
                if (!eventContainer.contains(this)) {
                    eventContainer.add(this);
                    EventBusService.post(new MessageEvent(plcProvider.get(), AUTOMATED_BEAM_ERROR_STATE));
                }
            } else {
                EventBusService.post(new IssueSolvedMessage(AUTOMATED_BEAM_ERROR_STATE, plcProvider.get()));
            }
        }

        @Override
        public List<String> getListeningVariables() {
            List<String> listVars = new ArrayList<>();
            for (Integer lineIndex : PlcLineHelper.getLineIndexes()) {
                listVars.add(AutomatedBeamPlcEnums.REQUEST_BEAM_ADJUSTING_HEIGHT
                    .getNameOnPlc().replace(LINE_INDEX_PLACEHOLDER, lineIndex.toString()));
                listVars.add(AutomatedBeamPlcEnums.REQUEST_SAFETY_SENSOR_TRIG
                    .getNameOnPlc().replace(LINE_INDEX_PLACEHOLDER, lineIndex.toString()));
            }

            return listVars;
        }
    }
}
