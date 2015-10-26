package com.sicpa.standard.sasscl.devices.brs.utils;


import org.junit.Assert;
import org.junit.Test;

public class ReseteableAtomicCounterTest {


    @Test
    public void getNextValue()  {

        ResettableAtomicCounter atomicCounter = new ResettableAtomicCounter();
        atomicCounter.getNextValue();
        Assert.assertEquals(1, atomicCounter.getValue());
    }

    @Test
    public void getPreviousValue()  {

        ResettableAtomicCounter atomicCounter = new ResettableAtomicCounter();
        atomicCounter.getNextValue();
        atomicCounter.getPreviousValue();
        Assert.assertEquals(0, atomicCounter.getValue());
    }

    @Test
    public void reset() {
        ResettableAtomicCounter atomicCounter = new ResettableAtomicCounter();
        atomicCounter.getNextValue();
        atomicCounter.getNextValue();
        atomicCounter.reset();
        Assert.assertEquals(0, atomicCounter.getValue());
    }

}