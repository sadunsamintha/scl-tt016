package com.sicpa.tt016.scl.remote.simulator;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;

public class TT016RemoteServerSimulator extends RemoteServerSimulator {

    private boolean refeedAvailable;

    public TT016RemoteServerSimulator(RemoteServerSimulatorModel model) {
        super(model);
    }

    public TT016RemoteServerSimulator(String configFile) throws RemoteServerException {
        super(configFile);
    }

    public void setRefeedAvailable(boolean refeedAvailable) {
        this.refeedAvailable = refeedAvailable;
    }
}
