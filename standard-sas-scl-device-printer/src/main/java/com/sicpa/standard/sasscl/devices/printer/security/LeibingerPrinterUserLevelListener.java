package com.sicpa.standard.sasscl.devices.printer.security;


import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.sasscl.devices.printer.util.LeibingerUtils;
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
        final int printerUserLevel = LeibingerUtils.getPrinterUserLevelBySclUserName(userLoggedIn);
        logger.debug("The  printer user level of the current user loggedIn is {}", printerUserLevel);
        return printerUserLevel;
    }



}
