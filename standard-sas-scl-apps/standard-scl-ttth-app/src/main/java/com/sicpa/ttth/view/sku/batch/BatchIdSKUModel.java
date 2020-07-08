package com.sicpa.ttth.view.sku.batch;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class BatchIdSKUModel extends AbstractObservableModel {

    private String strBatchId;

    public BatchIdSKUModel() { }

    public String getStrBatchId() {
        return strBatchId;
    }

    public void setStrBatchId(String strBatchId) {
        this.strBatchId = strBatchId;
    }
}
