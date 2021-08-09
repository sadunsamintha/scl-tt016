package com.sicpa.tt016.monitoring.mbean.mapping;

import com.sicpa.standard.sasscl.monitoring.mbean.StandardMonitoringMBeanConstants;

import java.util.HashMap;
import java.util.Map;

public class LegacyDevicesStatusIdProvider {

    private static Map<Integer, Integer> deviceStatusToDeviceStatusIdLegacy = new HashMap<>();

    static {
        deviceStatusToDeviceStatusIdLegacy.put(StandardMonitoringMBeanConstants.CONNECTED, 1);
        deviceStatusToDeviceStatusIdLegacy.put(StandardMonitoringMBeanConstants.DISCONNECTED, 4);
        deviceStatusToDeviceStatusIdLegacy.put(StandardMonitoringMBeanConstants.UNKNOWN, 0);
    }

    public static int getDeviceStatusId(int deviceStatus) {
        return deviceStatusToDeviceStatusIdLegacy.getOrDefault(deviceStatus, -1);
    }
}
