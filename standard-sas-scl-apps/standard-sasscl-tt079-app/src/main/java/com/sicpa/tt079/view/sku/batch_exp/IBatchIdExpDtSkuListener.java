package com.sicpa.tt079.view.sku.batch_exp;

import java.util.Date;

/**
 * Interface to manage the event saveBatchId and Expiration date in the new window
 *
 *
 */
public interface IBatchIdExpDtSkuListener {

    void saveBatchIdAndExpDt(String strBatchId, Date expDt);

}
