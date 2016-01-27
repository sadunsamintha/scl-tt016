package com.sicpa.standard.sasscl.devices.brs.statistics;


public interface BrsStatisticsListener {


    /**
     * This method is call when a brs code is received.
     *
     * @param code  The brs code recieved.
     */
     void onBrsCodeReceived(String code);

    /**
     * This method is call when a camera code is received.
     *
     * @param code The code recieved.
     */
    void onCodeReceived(String code);



}
