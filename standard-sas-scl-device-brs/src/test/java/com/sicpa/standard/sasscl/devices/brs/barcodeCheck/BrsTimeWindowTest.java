package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;


import com.sicpa.standard.common.util.ThreadUtils;
import org.junit.Test;
import org.testng.Assert;

public class BrsTimeWindowTest {

    private BrsTimeWindow window ;

    @Test
    public void getW1indowCount() {
        long windowSize = 5000; // 5s
         window = new BrsTimeWindow(windowSize);
        incrementWindowCountMultipleTimes(1, 0);
        Assert.assertEquals(1, window.getWindowCount());
    }

    @Test
    public void get5WindowCount() {
        long windowSize = 5000; // 5s
        window = new BrsTimeWindow(windowSize);
        incrementWindowCountMultipleTimes(5, 0);
        Assert.assertEquals(5, window.getWindowCount());
    }

    @Test
    public void get1WindowCountOutsideWindow() {
        long windowSize = 1500; // 1.5s
        window = new BrsTimeWindow(windowSize);
        incrementWindowCountMultipleTimes(2, 1000);
        Assert.assertEquals(1, window.getWindowCount());
    }

    @Test
    public void get0WindowCountOutsideWindow() {
        long windowSize = 500; // 0.5s
        window = new BrsTimeWindow(windowSize);
        incrementWindowCountMultipleTimes(2, 1000);
        Assert.assertEquals(0, window.getWindowCount());
    }


    private void incrementWindowCountMultipleTimes(int numberOfTimes, long sleepTimeBetweenCalls) {
        for(int i = 0; i < numberOfTimes; i++) {
            window.incrementWindowCount();
            ThreadUtils.sleepQuietly(sleepTimeBetweenCalls);
        }
    }


}
