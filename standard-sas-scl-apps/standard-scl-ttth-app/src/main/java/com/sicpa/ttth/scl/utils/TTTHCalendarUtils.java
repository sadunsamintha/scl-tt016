package com.sicpa.ttth.scl.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TTTHCalendarUtils {

    public static final String timeZoneTH = "GMT+7";

    public static final int TH_YEAR_DIFF = 543;

    private static final int fiscalEndMonth = 8;

    private static final int fiscalEndDay = 30;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy");

    private static final SimpleDateFormat sdfDetailed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat sdfTH = new SimpleDateFormat("dd/MM/yyyy");

    private static final SimpleDateFormat sdfTHDetailed = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String convertToBuddhistCal(String date, boolean isDetailed) throws ParseException {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfThTimeZone;

        if (isDetailed)
            cal.setTime(sdfDetailed.parse(date));
        else
            cal.setTime(sdf.parse(date));


        cal.add(Calendar.YEAR, TH_YEAR_DIFF);

        if (isDetailed) {
            sdfThTimeZone = new SimpleDateFormat(sdfTHDetailed.toPattern());
            sdfThTimeZone.setTimeZone(TimeZone.getTimeZone(timeZoneTH));
            return sdfThTimeZone.format(cal.getTime());
        }
        else {
            sdfThTimeZone = new SimpleDateFormat(sdfTH.toPattern());
            sdfThTimeZone.setTimeZone(TimeZone.getTimeZone(timeZoneTH));
            return sdfThTimeZone.format(cal.getTime());
        }
    }

    public static String getFiscalYear(Date date) {
        Calendar fiscalEnd = Calendar.getInstance();
        fiscalEnd.add(Calendar.YEAR, TH_YEAR_DIFF);
        fiscalEnd.set(Calendar.MONTH, fiscalEndMonth);
        fiscalEnd.set(Calendar.DAY_OF_MONTH, fiscalEndDay);

        if (date.after(fiscalEnd.getTime())) {
            fiscalEnd.add(Calendar.YEAR, 1);
            return new SimpleDateFormat("yy").format(fiscalEnd.getTime());
        }
        return new SimpleDateFormat("yy").format(fiscalEnd.getTime());
    }

}
