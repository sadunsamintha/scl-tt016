package com.sicpa.standard.sasscl.devices.camera.d900.sku;

import com.sicpa.standard.sasscl.model.SKU;

public interface D900CompliantProduct<T extends SKU> {

    boolean isCompliant(T sku);

}

