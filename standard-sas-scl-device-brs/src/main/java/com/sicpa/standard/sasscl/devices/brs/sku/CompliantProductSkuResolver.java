package com.sicpa.standard.sasscl.devices.brs.sku;


import com.sicpa.standard.sasscl.model.SKU;

public class CompliantProductSkuResolver implements CompliantProduct<SKU> {

    @Override
    public boolean isCompliant(SKU sku) {
        return true;
    }
}
