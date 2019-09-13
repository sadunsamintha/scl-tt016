package com.sicpa.tt080.scl.view.sku.batchId;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;

/**
 * Model of the BatchId associated used in the new view
 *
 * @author mjimenez
 *
 */
public class BatchIdSkuModel extends AbstractObservableModel implements ProductBatchIdProvider {

    private ProductionParameters productionParameters;

    public BatchIdSkuModel(){
    }

    public ProductionParameters getProductionParameters() {
        return productionParameters;
    }

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }
}
