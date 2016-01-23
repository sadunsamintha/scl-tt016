package com.sicpa.standard.sasscl.devices.brs.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class BrsStatisticsModel {

    private static final Logger logger = LoggerFactory.getLogger(BrsStatisticsModel.class);


    private AtomicLong totalNumberProductsCounted;

    private AtomicLong totalNumberBrsCodesCounted;

    private Map<String, AtomicLong> brsCodesCountedMap = Collections.synchronizedMap(new HashMap<>());

    public BrsStatisticsModel() {
        this.totalNumberProductsCounted = new AtomicLong(0L);
        this.totalNumberBrsCodesCounted = new AtomicLong(0L);
    }


    public void incrementValueTotalNumberProductsCounted() {
        logger.debug("atomically incrementing by one  totalNumberProductsCounted value {}.", totalNumberProductsCounted.get());
        totalNumberProductsCounted.incrementAndGet();
    }


    public synchronized void incrementValueNumberBrsCodesCounted(String brsCode) {
        logger.debug("atomically incrementing by one  NumberProductsCounted with value {} of brs code {}", totalNumberProductsCounted.get(), brsCode);
        AtomicLong counter = brsCodesCountedMap.get(brsCode);
        if (counter == null) {
            counter = new AtomicLong(0L);
            brsCodesCountedMap.put(brsCode, counter);
        }
        counter.incrementAndGet();
        totalNumberBrsCodesCounted.incrementAndGet();
    }

    public AtomicLong getTotalNumberProductsCounted() {
        return totalNumberProductsCounted;
    }

    public AtomicLong getTotalNumberBrsCodesCounted() {
        return totalNumberBrsCodesCounted;
    }

    public Map<String, AtomicLong> getBrsCodesCountedMap() {
        return brsCodesCountedMap;
    }

    @Override
    public String toString() {
        return "BrsStatisticsModel{" +
                "totalNumberProductsCounted=" + totalNumberProductsCounted +
                ", totalNumberBrsCodesCounted=" + totalNumberBrsCodesCounted +
                ", brsCodesCountedMap=" + brsCodesCountedMapToString() +
                '}';
    }

    private String brsCodesCountedMapToString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, AtomicLong> entry : brsCodesCountedMap.entrySet()) {
            sb.append(entry.getKey() + " : " + entry.getValue());
        }
        return sb.toString();
    }

}
