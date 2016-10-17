package com.sicpa.tt016.monitoring.mbean;

import com.sicpa.standard.sasscl.monitoring.mbean.scl.SclApp;

import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.EJECTED_PRODUCER;
import static com.sicpa.tt016.model.statistics.TT016StatisticsKey.INK_DETECTED;

public class TT016SclApp extends SclApp implements TT016SclAppMBean {

    @Override
    public int getNbInkDetectedProducts() {
        return getNbProductsByType(INK_DETECTED);
    }

    @Override
    public int getNbProducerEjectedProducts() {
        return getNbProductsByType(EJECTED_PRODUCER);
    }
}
