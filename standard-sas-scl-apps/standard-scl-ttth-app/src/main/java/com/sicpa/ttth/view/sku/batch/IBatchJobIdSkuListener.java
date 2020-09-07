package com.sicpa.ttth.view.sku.batch;

public interface IBatchJobIdSkuListener {

    void saveBatchJobId(String strBatchJobId);

    void saveBatchJobHist(String strBatchJobId);

    void generateBatchJobId(String batchJobSite, String batchJobSeq);

    void returnToSelection();

}
