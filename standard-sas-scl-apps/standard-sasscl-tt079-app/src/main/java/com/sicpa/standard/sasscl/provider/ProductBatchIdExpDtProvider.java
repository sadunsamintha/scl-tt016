package com.sicpa.standard.sasscl.provider;

import java.util.Date;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.ICustomProperty;

public interface ProductBatchIdExpDtProvider {

    ICustomProperty<String> productionBatchId = new CustomProperty<>("productBatchId",String.class,"");
    ICustomProperty<Date> productionExpdt = new CustomProperty<>("productionExpdt",Date.class);
    
}
