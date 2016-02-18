package com.sicpa.standard.sasscl.devices.brs.barcodeCheck;


public interface BrsWindow  {

    /**
     * Increments by one the size of the window and clean up items outside the window size
     * and then return the count of items still in the window.
     *
     * @return A count of items still inside the time window.
     */
    int incrementAndGetWindowCount();

    /**
     * Increments by one the size of the window.
     */
    void incrementWindowCount();

    /**
     * Clean up items outside the window size and then return the count of items still in the window.
     *
     *  @return A count of items still inside the time window.
     */
    int getWindowCount();

}
