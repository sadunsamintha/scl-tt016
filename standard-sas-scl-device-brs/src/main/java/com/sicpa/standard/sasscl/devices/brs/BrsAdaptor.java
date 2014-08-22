package com.sicpa.standard.sasscl.devices.brs;

import com.sicpa.standard.sasscl.devices.AbstractDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOTICE:  All information and intellectual property contained herein is the confidential property of SICPA Security Solutions SA,
 * and may be subject to patent, copyright, trade secret, and other intellectual property and contractual protections.
 * Reproduction or dissemination of the information or intellectual property contained herein is strictly forbidden,
 * unless separate written permission has been obtained from SICPA Security Solutions SA.
 *
 * Copyright © 2014 SICPA Security Solutions SA. All rights reserved.
 *
 * Created by lclaudon on 22.08.14.
 */
 

public class BrsAdaptor  extends AbstractDevice implements IStartableDevice {

    private static final Logger logger = LoggerFactory.getLogger(BrsAdaptor.class);

    protected boolean shouldBeConnected;

    protected boolean reallyConnected;

    public BrsAdaptor() {
        super();
        setName("BRS");
    }

    @Override
    protected void doConnect() throws DeviceException {

        shouldBeConnected = true;
        if (reallyConnected) {
            fireDeviceStatusChanged(DeviceStatus.CONNECTED);
        }
    }

    @Override
    protected void doDisconnect() throws DeviceException {

        shouldBeConnected = false;
        fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
    }

    public void onBrsConnected(boolean connected) {

        reallyConnected = connected;
        if (!shouldBeConnected) return;

        if (connected) {
            logger.debug("BRS CONNECTED");
            fireDeviceStatusChanged(DeviceStatus.CONNECTED);
        }
        else {
            logger.debug("BRS DISCONNECTED");
            fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
        }
    }

    @Override
    public void start() throws DeviceException {
        fireDeviceStatusChanged(DeviceStatus.STARTED);
    }

    @Override
    public void stop() throws DeviceException {
        fireDeviceStatusChanged(DeviceStatus.STOPPED);
    }

    @Override
    public boolean isBlockProductionStart() {
        return true;
    }
}
