package com.sicpa.tt084.sasscl.model;

import com.sicpa.standard.sasscl.model.ProductionMode;

import static com.sicpa.standard.sasscl.model.ProductionMode.STANDARD;

public class TT084ProductionMode {

  //Custo Production Mode
  public static final ProductionMode IMPORT = new ProductionMode(2000, "productionmode.import", STANDARD.isWithSicpaData());

}
