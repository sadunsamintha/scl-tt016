package com.sicpa.standard.sasscl.devices.printer.util;


import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.sicpa.standard.client.common.utils.PropertiesUtils.extractAndDo;
import static java.lang.Boolean.parseBoolean;

public class LeibingerUtils {

    private static final Logger logger = LoggerFactory.getLogger(LeibingerUtils.class);

    private static final String LEIBINGER_USER_LEVEL_KEY = "user.leibinger.level.%s";

    public static int getPrinterUserLevelBySclUserName(Properties properties, String sclUsername) {
        Validate.notNull(sclUsername);
        String leibingerUserLevelKey = String.format(LEIBINGER_USER_LEVEL_KEY, sclUsername);
        String leibingerUserLevelValue = properties.getProperty(leibingerUserLevelKey);
        return Integer.parseInt(leibingerUserLevelValue);
    }

}
