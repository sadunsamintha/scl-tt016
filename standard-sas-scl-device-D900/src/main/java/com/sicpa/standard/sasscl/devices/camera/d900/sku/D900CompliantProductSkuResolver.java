package com.sicpa.standard.sasscl.devices.camera.d900.sku;

import com.sicpa.standard.sasscl.model.SKU;

public class D900CompliantProductSkuResolver implements D900CompliantProduct<SKU> {

    @Override
    public boolean isCompliant(SKU sku) {
        return true;
    }
}
