package com.sicpa.standard.sasscl.view.utils;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

import com.sicpa.standard.sasscl.utils.ConfigUtilEx;

import org.springframework.core.io.ClassPathResource;

public class TimeZoneUtil {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static TimeZone getTimeZone() {
        try {
            Properties prop = new Properties();
            File globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();
            prop.load(new FileInputStream(globalPropertiesFile));
            String timeZoneStr = prop.getProperty("gui.display.timezone");
            if (!timeZoneStr.isEmpty() && timeZoneStr != null) {
                return TimeZone.getTimeZone(timeZoneStr);
            } else {
                return TimeZone.getDefault();
            }
        } catch (Exception e) {
            return TimeZone.getDefault();
        }
    }

    public static String covertToConfigTimeZone(String date) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(date));
        SimpleDateFormat sdfTZ = new SimpleDateFormat(sdf.toPattern());
        sdfTZ.setTimeZone(getTimeZone());
        return sdfTZ.format(cal.getTime());
    }

}
