package com.sicpa.standard.sasscl.devices.brs.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class ResettableAtomicCounter {

    private static final Logger logger = LoggerFactory.getLogger(ResettableAtomicCounter.class);

    private final AtomicInteger counter;

    public ResettableAtomicCounter() {
        counter = new AtomicInteger(0);
    }

    public ResettableAtomicCounter(int initialValue) {
        counter = new AtomicInteger(initialValue);
    }

    public int getValue(){
        return counter.get();
    }

    public int getNextValue(){
        logger.debug("atomically incrementing by one the current value {}.", counter.get());
        return counter.incrementAndGet();
    }

    public int getPreviousValue(){
        logger.debug("atomically decrementing by one the current value {}.", counter.get());
        return counter.decrementAndGet();
    }

    public void reset() {
        logger.debug("Resetting the atomic counter");
        counter.set(0);
    }
}
