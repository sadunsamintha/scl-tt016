package com.sicpa.tt080.sasscl.model;

import com.sicpa.standard.sasscl.model.ProductionMode;

import static com.sicpa.standard.sasscl.model.ProductionMode.STANDARD;

public class TT080ProductionMode {
  //Custo Production Mode
  public static final ProductionMode FREEZONE = new ProductionMode(1000, "productionmode.freezone", STANDARD.isWithSicpaData());
}
