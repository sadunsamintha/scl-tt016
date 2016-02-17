package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;


import com.sicpa.standard.sasscl.business.alert.task.IAlertTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.PriorityQueue;

public class BrsTimeWindow implements BrsWindow, IAlertTask {

    private static final Logger logger = LoggerFactory.getLogger(BrsBarcodeCheck.class);


    /**
     * The size of the windows in milliseconds
     */
    private final long windowSize ;

    private PriorityQueue<Long> window = new PriorityQueue<>();

    public BrsTimeWindow(long windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void stop() {
        resetWindow();
    }

    @Override
    public void reset() {
        // do nothing
    }


    @Override
    public int incrementAndGetWindowCount() {
        incrementWindowCount();
        return getWindowCount();
    }

    @Override
    public synchronized int getWindowCount(){
        long currentTime = Instant.now().toEpochMilli();
        cleanoutOldTimestamps(currentTime);
        final int  windowCount = window.size();
        logger.debug("The Size of the BRS window is {} ", windowCount);
        return windowCount;
    }

    @Override
    public void incrementWindowCount() {
        window.add(Instant.now().toEpochMilli());
    }

    private void cleanoutOldTimestamps(long currentTime) {

        while(window.peek() == null ? false : currentTime -  window.peek().longValue() > windowSize){
           window.remove();
        }
    }

    private void resetWindow() {
        logger.debug("reseting BRS windows");
        window.clear();
    }



}
