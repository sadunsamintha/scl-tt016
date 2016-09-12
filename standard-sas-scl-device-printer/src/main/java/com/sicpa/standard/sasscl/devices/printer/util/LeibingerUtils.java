package com.sicpa.standard.sasscl.devices.printer.util;


import org.apache.commons.lang.Validate;

import java.util.Properties;

public class LeibingerUtils {

    private static final String LEIBINGER_USER_LEVEL_KEY = "leibinger.user.level.%s";

    public static int getPrinterUserLevelBySclUserName(Properties properties, String sclUsername) {
        Validate.notNull(sclUsername);
        String leibingerUserLevelKey = String.format(LEIBINGER_USER_LEVEL_KEY, sclUsername);
        String leibingerUserLevelValue = properties.getProperty(leibingerUserLevelKey);
        return Integer.parseInt(leibingerUserLevelValue);
    }

}
