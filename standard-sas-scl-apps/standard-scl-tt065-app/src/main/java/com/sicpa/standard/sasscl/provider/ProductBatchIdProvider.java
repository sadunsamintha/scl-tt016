package com.sicpa.standard.sasscl.provider;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.ICustomProperty;

/**
 * Created by mjimenez on 01/11/2016.
 */
public interface ProductBatchIdProvider {

    ICustomProperty<String> productionBatchId = new CustomProperty<>("productBatchId",String.class,"");
}
