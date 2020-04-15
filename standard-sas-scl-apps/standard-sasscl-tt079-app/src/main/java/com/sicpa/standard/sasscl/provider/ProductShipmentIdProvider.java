package com.sicpa.standard.sasscl.provider;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.ICustomProperty;

public interface ProductShipmentIdProvider {

    ICustomProperty<String> productionShipmentId = new CustomProperty<>("productionShipmentId",String.class,"");
    
}
