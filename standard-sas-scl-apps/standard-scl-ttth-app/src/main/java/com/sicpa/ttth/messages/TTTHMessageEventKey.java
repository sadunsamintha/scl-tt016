package com.sicpa.ttth.messages;

public interface TTTHMessageEventKey {

    interface SKUSELECTION {
        String BARCODE_VERIFIED_MSG_CODE = "[TTTH_02]";
        String BARCODE_VERIFIED = "sku.barcode.validated";
        String DAILY_BATCH_EXCEEDED_MSG_CODE ="[TTTH_03]";
        String DAILY_BATCH_EXCEEDED ="sku.daily.batch.exceeded";
        String DAILY_BATCH_DATED_MSG_CODE ="[TTTH_04]";
        String DAILY_BATCH_DATED ="sku.daily.batch.dated";
    }

    interface EJECTOR {
        String PLC_EJECTOR_CAPACITY_WARNING_MSG_CODE = "[EJT_01]";
        String PLC_EJECTOR_CAPACITY_WARNING = "PLC.EJECTOR.SENSOR.CAPACITY.WARNING";
        String PLC_EJECTOR_CAPACITY_ERROR_MSG_CODE = "[EJT_02]";
        String PLC_EJECTOR_CAPACITY_ERROR = "PLC.EJECTOR.SENSOR.CAPACITY.ERROR";
        String PLC_EJECTOR_CONFIRMATION_ERROR_MSG_CODE = "[EJT_03]";
        String PLC_EJECTOR_CONFIRMATION_ERROR = "PLC.EJECTOR.CONFIRMATION.ERROR";
    }
}
