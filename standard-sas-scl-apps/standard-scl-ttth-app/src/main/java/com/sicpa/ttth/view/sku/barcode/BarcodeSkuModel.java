package com.sicpa.ttth.view.sku.barcode;

import java.util.List;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

/**
 * Model of the BatchId associated used in the new view
 */
public class BarcodeSkuModel extends AbstractObservableModel {

    private List<String>  skuBarcodes;

    public BarcodeSkuModel() {
    }

    public BarcodeSkuModel(List<String>  skuBarcodes) {
        this.skuBarcodes = skuBarcodes;
    }

    public List<String> getSkuBarcodes() {
        return skuBarcodes;
    }

    public void setSkuBarcodes(List<String> skuBarcodes) {
        this.skuBarcodes = skuBarcodes;
    }
}
