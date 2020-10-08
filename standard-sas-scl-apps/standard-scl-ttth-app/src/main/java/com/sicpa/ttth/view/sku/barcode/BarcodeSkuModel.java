package com.sicpa.ttth.view.sku.barcode;

import java.util.List;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

/**
 * Model of the BatchId associated used in the new view
 */
public class BarcodeSkuModel extends AbstractObservableModel {

    private List<String>  skuBarcodes;
    private String skuName;

    public BarcodeSkuModel() {
    }

    public BarcodeSkuModel(String skuName, List<String>  skuBarcodes) {
        this.skuName = skuName;
        this.skuBarcodes = skuBarcodes;
    }

    public List<String> getSkuBarcodes() {
        return skuBarcodes;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuBarcodes(List<String> skuBarcodes) {
        this.skuBarcodes = skuBarcodes;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
}
