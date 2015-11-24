package com.sicpa.standard.sasscl.devices.brs.sku;

import com.sicpa.standard.sasscl.model.SKU;

/**
 * A non compliant product SKU  represents products that do not have a barcode or are known
 * to have mostly unreadable barcodes due to package reutilisation process.
 */
public interface CompliantProduct<T extends SKU> {

    boolean isCompliant(T sku);

}
