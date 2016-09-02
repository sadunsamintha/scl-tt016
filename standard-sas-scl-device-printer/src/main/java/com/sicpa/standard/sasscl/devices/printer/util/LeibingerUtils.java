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

public class LeibingerUtils {

    private static final Logger logger = LoggerFactory.getLogger(LeibingerUtils.class);

    private static final String LEIBINGER_USER_LEVEL_KEY = "user.leibinger.level.%s";

    private static final int DEFAULT_LEIBINGER_USER_LEVEL = 1;

    public static int getPrinterUserLevelBySclUserName(String sclUsername) {
        Validate.notNull(sclUsername);

        String leibingerUserLevelValue = null;
        try {
            File globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();
            final Properties properties = new Properties();
            properties.load(new FileInputStream(globalPropertiesFile));
            String leibingerUserLevelKey = String.format(LEIBINGER_USER_LEVEL_KEY, sclUsername);
            leibingerUserLevelValue = properties.getProperty(leibingerUserLevelKey);
        } catch (IOException ex) {
            logger.error("Error reading the leibinger user level from global.properties ", ex);
            /* Let's process this error as non blocking and
               return the default leibinger user level
             */
            return DEFAULT_LEIBINGER_USER_LEVEL;
        }
        return Integer.parseInt(leibingerUserLevelValue);
    }


}
