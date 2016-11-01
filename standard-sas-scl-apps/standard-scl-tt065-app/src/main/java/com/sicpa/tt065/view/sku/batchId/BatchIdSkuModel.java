package com.sicpa.tt065.view.sku.batchId;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;
import com.sicpa.standard.sasscl.model.SKU;

/**
 * Created by mjimenez on 26/10/2016.
 */
public class BatchIdSkuModel extends AbstractObservableModel {

    private String batchId;
    private SKU sku;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public SKU getSku() {
        return sku;
    }

    public void setSku(SKU sku) {
        this.sku = sku;
    }

}
