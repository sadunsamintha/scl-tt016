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
}
