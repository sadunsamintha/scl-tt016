package com.sicpa.tt077.scl.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TT077CalendarUtils {

    public static final String timeZoneTZ = "GMT+3";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String convertToTZCal(String date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(date));
        SimpleDateFormat sdfTZ = new SimpleDateFormat(sdf.toPattern());
        sdfTZ.setTimeZone(TimeZone.getTimeZone(timeZoneTZ));
        return sdfTZ.format(cal.getTime());
    }

}
