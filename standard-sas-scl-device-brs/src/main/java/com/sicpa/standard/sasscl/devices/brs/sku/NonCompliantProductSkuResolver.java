package com.sicpa.standard.sasscl.devices.brs.sku;


import com.sicpa.standard.sasscl.model.SKU;

public class NonCompliantProductSkuResolver implements NonCompliantProduct<SKU> {

    @Override
    public boolean isCompliant(SKU sku) {
        return true;
    }
}
