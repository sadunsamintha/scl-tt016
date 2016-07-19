package com.sicpa.tt016.scl.remote.simulator;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.tt016.scl.remote.RemoteServerRefeedAvailability;

public class TT016RemoteServerSimulator extends RemoteServerSimulator implements RemoteServerRefeedAvailability {

    private boolean isRefeedAvailable;

    public TT016RemoteServerSimulator(RemoteServerSimulatorModel model) {
        super(model);
    }

    public TT016RemoteServerSimulator(String configFile) throws RemoteServerException {
        super(configFile);
    }

    @Override
    public boolean isRemoteRefeedAvailable() {
        return isRefeedAvailable;
    }

    public void setIsRefeedAvailable(boolean isRefeedAvailable) {
        this.isRefeedAvailable = isRefeedAvailable;
    }

}
