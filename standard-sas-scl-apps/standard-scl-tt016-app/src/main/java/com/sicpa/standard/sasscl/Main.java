package com.sicpa.standard.sasscl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.tt016.scl.TT016Bootstrap;
import com.sicpa.tt016.scl.TT016MainAppWithProfile;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String GLOBAL_PROPERTIES_PATH = "profiles/TT016/config/global.properties";

    public static void main(final String[] args) {
        Properties prop = new Properties();
        TT016Bootstrap.addPlcVariableJavaEjectionCounter();
        try {
            prop.load(new FileInputStream(GLOBAL_PROPERTIES_PATH));
            if (Boolean.valueOf(prop.getProperty("wiper.enabled"))) {
                TT016Bootstrap.addWiperPlcVariable();
            }
            if (Boolean.valueOf(prop.getProperty("automated.beam.enabled"))) {
                TT016Bootstrap.addMotorizedBeamPlcVariables();
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        new TT016MainAppWithProfile().selectProfile();
    }
}