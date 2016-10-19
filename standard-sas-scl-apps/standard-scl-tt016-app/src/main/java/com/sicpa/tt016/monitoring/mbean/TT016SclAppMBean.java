package com.sicpa.tt016.monitoring.mbean;

import com.sicpa.standard.sasscl.monitoring.mbean.sas.SasAppMBean;

public interface TT016SclAppMBean extends SasAppMBean {

    int getNbInkDetectedProducts();

    int getNbProducerEjectedProducts();
}
