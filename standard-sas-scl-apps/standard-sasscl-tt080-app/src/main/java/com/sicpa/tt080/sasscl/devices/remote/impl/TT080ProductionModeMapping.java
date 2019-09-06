package com.sicpa.tt080.sasscl.devices.remote.impl;

import com.sicpa.standard.sasscl.devices.remote.mapping.ProductionModeMapping;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt080.sasscl.model.TT080ProductionMode;



public class TT080ProductionModeMapping extends ProductionModeMapping {

  public TT080ProductionModeMapping() {
    //Default Modes
    this.add(ProductionMode.STANDARD, 1);
    this.add(ProductionMode.EXPORT, 5);
    this.add(ProductionMode.COUNTING, 6);
    this.add(ProductionMode.COUNTING, 7);
    this.add(ProductionMode.EXPORT_CODING, 8);

    //Custo mapping
    this.add(TT080ProductionMode.FREEZONE, 2); //IMPORT
  }
}
