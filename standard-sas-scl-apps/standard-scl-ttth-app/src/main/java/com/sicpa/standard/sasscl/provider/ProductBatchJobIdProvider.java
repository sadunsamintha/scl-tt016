package com.sicpa.standard.sasscl.provider;

import java.util.Date;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.ICustomProperty;

public interface ProductBatchJobIdProvider {
    ICustomProperty<String> productionBatchJobId = new CustomProperty<String>("productBatchJobId",String.class,null);
}
