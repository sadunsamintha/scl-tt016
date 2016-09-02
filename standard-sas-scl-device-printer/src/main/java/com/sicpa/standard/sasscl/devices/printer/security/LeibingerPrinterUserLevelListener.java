package com.sicpa.standard.sasscl.devices.printer.security;


import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.sasscl.event.ChangePrinterUserLevelEvent;
import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LeibingerPrinterUserLevelListener {

    private static final Logger logger = LoggerFactory.getLogger(LeibingerPrinterUserLevelListener.class);

    private static final String LEIBINGER_USER_LEVEL_KEY = "user.leibinger.level.%s";

    private static final int DEFAULT_LEIBINGER_USER_LEVEL = 1;

    public void initLoginListenerLogger() {

        SecurityService.addLoginListener(new ILoginListener() {

            @Override
            public void loginSucceeded(String login) {
                triggerChangePrinterUserLevelEvent(login);
            }

            @Override
            public void logoutCompleted(String login) {
                triggerChangePrinterUserLevelEvent(login);
            }
        });
    }

    private void triggerChangePrinterUserLevelEvent(String login) {
        logger.debug("Triggering getPrinterUserLevelUserLoggedIn for the user {}", login);
        final int userLevel = getPrinterUserLeveLoggedIn();
        EventBusService.post(new ChangePrinterUserLevelEvent(userLevel));
    }

    public int getPrinterUserLeveLoggedIn() {
        final String userLoggedIn = SecurityService.getCurrentUser().getLogin().toLowerCase();
        final int printerUserLevel = readUserLeibingerLevel(userLoggedIn);
        logger.debug("The  printer user level of the current user loggedIn is {}", printerUserLevel);
        return printerUserLevel;
    }

    private int readUserLeibingerLevel(String userLoggedIn) {
        String leibingerUserLevelValue = null;
        try {
            File globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();
            final Properties properties = new Properties();
            properties.load(new FileInputStream(globalPropertiesFile));
            String leibingerUserLevelKey = String.format(LEIBINGER_USER_LEVEL_KEY, userLoggedIn);
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
