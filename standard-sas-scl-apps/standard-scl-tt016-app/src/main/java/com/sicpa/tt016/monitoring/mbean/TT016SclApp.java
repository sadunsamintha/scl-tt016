package com.sicpa.tt016.monitoring.mbean;

import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.mbean.scl.SclApp;
import com.sicpa.tt016.model.statistics.TT016StatisticsKey;

import java.util.Map;
import java.util.Set;

public class TT016SclApp extends SclApp implements TT016SclAppMBean {

    @Override
    public int getNbInkDetectedProducts() {
        Set<Map.Entry<StatisticsKey, Integer>> statisticsKeys = stats.getProductsStatistics().getValues().entrySet();
        int numInkDetectedProducts = 0;

        for (Map.Entry<StatisticsKey, Integer> entry : statisticsKeys) {
            if (isInkDetectedKey(entry.getKey())) {
                numInkDetectedProducts += entry.getValue();
            }
        }

        return numInkDetectedProducts;
    }

    @Override
    public int getNbProducerEjectedProducts() {
        Set<Map.Entry<StatisticsKey, Integer>> statisticsKeys = stats.getProductsStatistics().getValues().entrySet();
        int numEjectedProducerProducts = 0;

        for (Map.Entry<StatisticsKey, Integer> entry : statisticsKeys) {
            if (isProducerEjectedKey(entry.getKey())) {
                numEjectedProducerProducts += entry.getValue();
            }
        }

        return numEjectedProducerProducts;
    }

    private boolean isInkDetectedKey(StatisticsKey key) {
        return key.getDescription().equals(TT016StatisticsKey.INK_DETECTED.getDescription());
    }

    private boolean isProducerEjectedKey(StatisticsKey key) {
        return key.getDescription().equals(TT016StatisticsKey.EJECTED_PRODUCER.getDescription());
    }
}
