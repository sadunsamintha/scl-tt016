package com.sicpa.tt016.monitoring.mbean.mapping;

import java.util.HashMap;
import java.util.Map;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.*;

public class LegacyApplicationStatusIdProvider {

    private static Map<String, Integer> appFlowStateToAppStatusId = new HashMap<>();

    static {
        appFlowStateToAppStatusId.put(STT_NO_SELECTION.getName(), 0);
        appFlowStateToAppStatusId.put(STT_CONNECTED.getName(), 3);
        appFlowStateToAppStatusId.put(STT_CONNECTING.getName(), 11);
        appFlowStateToAppStatusId.put(STT_EXIT.getName(), 15);
        appFlowStateToAppStatusId.put(STT_SELECT_NO_PREVIOUS.getName(), 8);
        appFlowStateToAppStatusId.put(STT_SELECT_WITH_PREVIOUS.getName(), 8);
        appFlowStateToAppStatusId.put(STT_STARTED.getName(), 7);
        appFlowStateToAppStatusId.put(STT_STARTING.getName(), 12);
        appFlowStateToAppStatusId.put(STT_STOPPING.getName(), 13);
        appFlowStateToAppStatusId.put(STT_RECOVERING.getName(), 4);
        appFlowStateToAppStatusId.put(STT_DISCONNECTING_ON_PARAM_CHANGED.getName(), 14);
    }

    public static int getApplicationStatusId(String state) {
        return appFlowStateToAppStatusId.getOrDefault(state, -1);
    }
}
