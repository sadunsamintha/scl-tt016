package com.sicpa.ttth.scl.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import com.sicpa.ttth.storage.TTTHFileStorage;

public class TTTHDailyBatchJobUtils {

    public static boolean isBatchJobIdDated(Date batchJobEndDate) {
        Calendar batchCal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        batchCal.setTime(batchJobEndDate);
        return now.getTime().compareTo(batchCal.getTime()) > 0;
    }

    public static boolean isBatchJobHistoryDated(Date batchJobEndDate) throws IOException {
        Calendar batchCal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        Properties prop = new Properties();
        prop.load(new FileInputStream(TTTHFileStorage.GLOBAL_PROPERTIES_PATH));

        batchCal.setTime(batchJobEndDate);
        batchCal.add(Calendar.DATE,
            Integer.parseInt(prop.getProperty("sku.batch.job.history.length")));
        return now.getTime().compareTo(batchCal.getTime()) > 0;
    }
}
