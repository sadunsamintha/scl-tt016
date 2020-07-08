package com.sicpa.ttth.messages;

public interface TTTHMessageEventKey {

    interface SKUSELECTION {
        //TODO: Add messages for automated batch id.
        String BARCODE_VERIFIED_MSG_CODE = "[SLC_02]";
        String BARCODE_VERIFIED = "sku.barcode.validated";
    }
}
