package com.sicpa.standard.sasscl.provider;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.ICustomProperty;

public interface ProductShipmentIdProvider {
    // KEN-UG integration
    ICustomProperty<String> productionShipmentId = new CustomProperty<String>("productionShipmentId",String.class,null);
    
}
