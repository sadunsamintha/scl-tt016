package com.sicpa.standard.sasscl.provider;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.ICustomProperty;
import com.sicpa.tt080.scl.view.sku.batchId.ProductionBatch;

public interface ProductBatchIdProvider {

    ICustomProperty<ProductionBatch> productionBatchId = new CustomProperty<>("productBatchId", ProductionBatch.class, ProductionBatch.EMPTY);
}
