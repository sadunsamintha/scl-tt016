package com.sicpa.ttth.view.sku.batch;

public interface IBatchJobIdSkuListener {

    void saveBatchJobId(String strBatchJobId);

    void saveBatchJobHist(String strBatchJobId);

    void generateBatchJobId(String batchJobSeq, String batchJobSkuId);

    void returnToSelection();

}
