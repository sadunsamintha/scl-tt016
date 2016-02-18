package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;


import com.sicpa.standard.sasscl.business.alert.task.IAlertTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.PriorityQueue;

public class BrsTimeWindow implements BrsWindow, IAlertTask {

    private static final Logger logger = LoggerFactory.getLogger(BrsBarcodeCheck.class);

    /**
     * The size of the windows in milliseconds
     */
    private final long windowSize ;

    private PriorityQueue<Instant> window = new PriorityQueue<>();

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
        Instant currentTime = Instant.now();
        cleanoutOldTimestamps();
        final int  windowCount = window.size();
        logger.debug("The Size of the BRS window is {} ", windowCount);
        return windowCount;
    }

    @Override
    public void incrementWindowCount() {
        window.add(Instant.now());
    }

    private void cleanoutOldTimestamps() {

        while(window.peek() == null ? false : Duration.between(window.peek(), Instant.now()).toMillis() > windowSize){
           window.remove();
        }
    }

    private void resetWindow() {
        logger.debug("reseting BRS windows");
        window.clear();
    }


}
