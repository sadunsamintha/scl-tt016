package com.sicpa.tt085.devices.remoteServer.dto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.sicpa.standard.sasscl.utils.ConfigUtilEx;


public class TT085CountryMessages {
    private final static Logger LOGGER = LoggerFactory.getLogger(TT085CountryMessages.class);

    public static final String ISO_8859_1 = "iso_8859-1";

    private static TT085CountryMessages sInstance = null;

    private ResourceBundle mCountryResource = null;


    /**
     * @return A static instance of SPLApplication.
     */
    public synchronized static TT085CountryMessages getInstance() {
        if (sInstance == null) {
            sInstance = new TT085CountryMessages();
        }

        return sInstance;
    }

    public TT085CountryMessages() {
    }

    public boolean isKnownIsoCode(String isoCode) {
        return !getCountryName(isoCode).equals(noTranslationFormatting(isoCode));
    }

    public String getCountryName(String key) {
        try{
            // Turkish is not supported by default jre8 translations - we need to take it from resource file
            if (mCountryResource == null) {
            	Properties properties = new Properties();
                File globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();
                properties.load(new FileInputStream(globalPropertiesFile));
                mCountryResource = ResourceBundle.getBundle("language/spl_iso_countries", new Locale(properties.getProperty("language")));
            }

            return mCountryResource.getString("country.iso." + key);
        } catch (MissingResourceException e) {
            LOGGER.error("Missing key in country ISO codes: {}" , key);
            return noTranslationFormatting(key);
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve text in language file", e);
            return noTranslationFormatting(key);
        }
    }

    private String noTranslationFormatting(String key) {
        return '!' + key + '!';
    }
}
