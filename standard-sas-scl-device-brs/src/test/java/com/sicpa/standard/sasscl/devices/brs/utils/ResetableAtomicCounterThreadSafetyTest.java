package com.sicpa.standard.sasscl.devices.brs.utils;


import org.testng.Assert;
import org.testng.annotations.Test;

public class ResetableAtomicCounterThreadSafetyTest {

    private ResettableAtomicCounter counter = new ResettableAtomicCounter();

    private static final int COUNT_LIMIT = 1000;




    @Test(threadPoolSize = 3, invocationCount = 9)
    public void getNextValue()  {
        int lastValue = counter.getNextValue();
        for (int i = 0; i < COUNT_LIMIT ; i++) {
            int currentValue = counter.getNextValue();

            // verify the counter is incrementing the value correctly in a multithread enviroment
            Assert.assertTrue(currentValue > lastValue);

            lastValue = currentValue;
        }
    }

    @Test(threadPoolSize = 3, invocationCount = 9)
    public void getPreviousValue()  {
        int lastValue = counter.getPreviousValue();
        for (int i = 0; i < COUNT_LIMIT ; i++) {
            int currentValue = counter.getPreviousValue();

            // verify the counter is decrementing the value correctly in a multithread enviroment
            Assert.assertTrue(currentValue < lastValue);

            lastValue = currentValue;
        }
    }



}
