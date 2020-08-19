package com.sicpa.ttth.view.sku.batch;

public interface IBatchJobIdSkuListener {

    void saveBatchJobId(String strBatchId);

    void generateBatchJobId(String batchJobSite, String batchJobSeq);

    void returnToSelection();

}
