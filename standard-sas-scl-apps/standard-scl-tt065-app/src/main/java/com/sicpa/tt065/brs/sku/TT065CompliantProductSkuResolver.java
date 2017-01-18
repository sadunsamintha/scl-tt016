package com.sicpa.tt065.brs.sku;

import com.sicpa.standard.sasscl.devices.brs.sku.CompliantProductSkuResolver;
import com.sicpa.standard.sasscl.model.SKU;

import static com.sicpa.tt065.model.TT065CustomProperties.skuCompliant;

public class TT065CompliantProductSkuResolver extends CompliantProductSkuResolver {

    @Override
    public boolean isCompliant(SKU sku) {
        return sku.getProperty(skuCompliant);
    }
}
