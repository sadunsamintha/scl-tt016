package com.sicpa.standard.sasscl.devices.remote.impl;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;

/**
 * NOTICE:  All information and intellectual property contained herein is the confidential property of SICPA Security Solutions SA,
 * and may be subject to patent, copyright, trade secret, and other intellectual property and contractual protections.
 * Reproduction or dissemination of the information or intellectual property contained herein is strictly forbidden,
 * unless separate written permission has been obtained from SICPA Security Solutions SA.
 *
 * Copyright © 2015 SICPA Security Solutions SA. All rights reserved.
 *
 * Created by lclaudon on 29.06.2015.
 */
 

public class RemoteServerLifeChecker {
    protected IRemoteServer target;

    protected int lifeCheckSleep = 20;

    Thread lifeCheker;

    public RemoteServerLifeChecker() {
    }

    public void setTarget(IRemoteServer target) {
        this.target = target;
    }

    public void setLifeCheckSleep(int lifeCheckSleep) {
        this.lifeCheckSleep = lifeCheckSleep;
    }

    protected void start() {
        if (lifeCheker == null && target != null) {
            lifeCheker = new Thread(new Runnable() {
                @Override
                public void run() {
                    // is a daemon + server is supposed to be up at all time
                    // => while true is ok
                    while (true) {
                        target.lifeCheckTick();
                        if (lifeCheckSleep > 0) {
                            ThreadUtils.sleepQuietly(1000L * lifeCheckSleep);
                        } else {
                            ThreadUtils.sleepQuietly(10 * 1000);
                        }
                    }
                }
            });
            lifeCheker.setDaemon(true);
            lifeCheker.setName("RemoteServerLifeChecker");
            lifeCheker.start();
        }
    }
}
