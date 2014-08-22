package com.sicpa.standard.sasscl.devices.brs;

import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
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
 

public class BrsAdaptorSimulator  extends BrsAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(BrsAdaptorSimulator.class);

    @Override
    protected void doConnect() throws DeviceException {

        shouldBeConnected = true;
        fireDeviceStatusChanged(DeviceStatus.CONNECTED);
    }
}
