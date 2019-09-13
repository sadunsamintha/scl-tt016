package com.sicpa.standard.sasscl.provider;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.ICustomProperty;

public interface ProductBatchIdProvider {

    ICustomProperty<String> productionBatchId = new CustomProperty<>("productBatchId",String.class,"");
}
