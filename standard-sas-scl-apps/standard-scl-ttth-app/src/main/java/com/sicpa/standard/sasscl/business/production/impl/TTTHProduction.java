package com.sicpa.standard.sasscl.business.production.impl;

import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchJobIdProvider;
import com.sicpa.standard.sasscl.view.startstop.IStartStopViewListener;

public class TTTHProduction extends Production implements ProductBatchJobIdProvider, IStartStopViewListener {

    private ProductionParameters pp;

    @Override
    protected void prepareProduct(Product product) {
        super.prepareProduct(product);
        CustoBuilder.addPropertyToClass(Product.class, productionBatchJobId);
        product.setProperty(productionBatchJobId,pp.getProperty(productionBatchJobId));
    }

    public void setPp(ProductionParameters pp) {
        this.pp = pp;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        sendAllProductionData();
    }

}
