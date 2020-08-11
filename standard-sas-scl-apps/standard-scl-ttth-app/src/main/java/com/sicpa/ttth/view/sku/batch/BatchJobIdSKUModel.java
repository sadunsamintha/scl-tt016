package com.sicpa.ttth.view.sku.batch;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class BatchJobIdSKUModel extends AbstractObservableModel {

    private String strBatchId;

    public BatchJobIdSKUModel() { }

    public String getStrBatchJobId() {
        return strBatchId;
    }

    public void setStrBatchJobId(String strBatchId) {
        this.strBatchId = strBatchId;
    }
}
