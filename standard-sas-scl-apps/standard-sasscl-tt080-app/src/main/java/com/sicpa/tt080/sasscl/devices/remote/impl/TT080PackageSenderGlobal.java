package com.sicpa.tt080.sasscl.devices.remote.impl;

import com.sicpa.standard.sasscl.devices.remote.impl.PackageSenderGlobal;
import com.sicpa.tt080.sasscl.model.TT080ProductStatus;

public class TT080PackageSenderGlobal extends PackageSenderGlobal {

  public TT080PackageSenderGlobal() {
    super();
    addToActivatedPackager(TT080ProductStatus.DECLARED);
  }

}
