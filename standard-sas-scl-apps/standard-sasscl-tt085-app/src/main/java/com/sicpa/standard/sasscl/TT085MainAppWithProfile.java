package com.sicpa.standard.sasscl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.app.profile.Profile;
import com.sicpa.tt085.scl.TT085Bootstrap;

public class TT085MainAppWithProfile extends MainAppWithProfile{
	
	private static final Logger logger = LoggerFactory.getLogger(TT085MainAppWithProfile.class);
	
	@Override
	protected void initFromProfile(Profile profile, List<String> filesToLoad) {
		super.initFromProfile(profile, filesToLoad);
		Properties prop = new Properties();
		try (FileInputStream fileInputStream =  new FileInputStream(profile.getPath() + "/config/global.properties")) {
            prop.load(fileInputStream);
            if (Boolean.valueOf(prop.getProperty("wiper.enabled"))) {
                TT085Bootstrap.addWiperPlcVariable();
            }
		}catch (IOException e) {
            logger.error("", e);
        }
	}

}
