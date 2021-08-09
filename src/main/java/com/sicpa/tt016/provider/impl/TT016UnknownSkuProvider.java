package com.sicpa.tt016.provider.impl;

import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;

public class TT016UnknownSkuProvider extends AbstractProvider<SKU> {

    private SKU unknownSkuDomestic;
    private SKU unknownSkuExport;

    private ProductionParameters productionParameters;

    public TT016UnknownSkuProvider(int unknownSkuIdDomestic, int unknownSkuIdExport) {
        super("unknownSku");

        unknownSkuDomestic = new SKU(unknownSkuIdDomestic, "Unknown");
        unknownSkuExport = new SKU(unknownSkuIdExport, "Unknown");
    }

    @Override
    public SKU get() {
        return productionParameters.getProductionMode().equals(ProductionMode.EXPORT)
                ? unknownSkuExport : unknownSkuDomestic;
    }

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }

    public void setCodeType(CodeType codeType) {
        unknownSkuDomestic.setCodeType(codeType);
        unknownSkuExport.setCodeType(codeType);
    }
}
