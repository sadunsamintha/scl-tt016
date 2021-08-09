package com.sicpa.tt016.monitoring.mbean.mapping;

import java.util.HashMap;
import java.util.Map;

import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.SENT_TO_REMOTE_SERVER_ERROR;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.SENT_TO_REMOTE_SERVER_OK;

public class LegacyRemoteServerSendingStatusIdProvider {

    private static Map<String, Integer> sendingStatusIdToSendingStatusIdLegacy = new HashMap<>();

    static {
        sendingStatusIdToSendingStatusIdLegacy.put(SENT_TO_REMOTE_SERVER_OK.toString(), 0);
        sendingStatusIdToSendingStatusIdLegacy.put(SENT_TO_REMOTE_SERVER_ERROR.toString(), 1);
    }

    public static int getSendingStatusId(String sendingStatus) {
        return sendingStatusIdToSendingStatusIdLegacy.getOrDefault(sendingStatus, -1);
    }

}
